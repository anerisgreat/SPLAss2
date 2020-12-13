package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBus msgBus;

    //Members
    //Used to map each MicroService to their message queue.
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceToQueue;
    //Used to map each event to their respective future.
    private ConcurrentHashMap<Message, Object> eventToFutureHashMap;

    //Used to map each event to the queue containing all subscribed microservices.
    private ConcurrentHashMap<Class, ConcurrentLinkedQueue<MicroService>> eventToMicroServicesQueues;
    //Used to map each broadcast to the queue containing all subscribed microservices.
    private ConcurrentHashMap<Class, List<MicroService>> broadcastToMicroServicesList;

    //Used to remember to which events each microservice subscribed.
    private ConcurrentHashMap<MicroService, List<Class>> microServiceToEventList;
    //Used to remember to which boradcasts each microservice subscribed.
    private ConcurrentHashMap<MicroService, List<Class>> microServiceToBroadcastList;

	public static MessageBus getInstance() {
		if (msgBus == null) {
			msgBus = new MessageBusImpl();
		}
		return msgBus;
 	}

    private MessageBusImpl(){
        microServiceToQueue = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
        eventToFutureHashMap = new ConcurrentHashMap<Message, Object>();

        eventToMicroServicesQueues = new ConcurrentHashMap<Class, ConcurrentLinkedQueue<MicroService>>();
        broadcastToMicroServicesList = new ConcurrentHashMap<Class, List<MicroService>>();

        microServiceToEventList = new ConcurrentHashMap<MicroService, List<Class>>();
        microServiceToBroadcastList = new ConcurrentHashMap<MicroService, List<Class>>();
    }
    
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if(!eventToMicroServicesQueues.containsKey(type)){
            eventToMicroServicesQueues.put(type, new ConcurrentLinkedQueue<MicroService>());
        }

        //Adding it to queue for receiving messages
        ConcurrentLinkedQueue<MicroService> microServiceQueue = eventToMicroServicesQueues.get(type);
        synchronized(microServiceQueue ){
            if(!microServiceQueue.contains(m)){
                //Add it to round robin queue, so it may get messages of this type.
                microServiceQueue.add(m);
                //In case an event was being sent with no one, notify that there is a new subscriber.
                microServiceQueue.notifyAll();
            }
        }

        //Writing that this microservice is subscribed to this event, for erasing later.
        List<Class> eventList = microServiceToEventList.get(m);
        synchronized(eventList){
            if(!eventList.contains(type)){
                //Making sure we remember that microservice is subscribed to this type, for unsub.
                eventList.add(type);
            }
        }

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if(!broadcastToMicroServicesList.containsKey(type)){
            broadcastToMicroServicesList.put(type, new LinkedList<MicroService>());
        }

        List<MicroService> broadcastList = broadcastToMicroServicesList.get(type);
        synchronized(broadcastList){
            //Adding microservice to round-robin queue so it may get new messages.
            if(!broadcastList.contains(m)){
                broadcastList.add(m);
            }
        }

        List<Class> broadcastMessageList = microServiceToBroadcastList.get(m);
        synchronized(broadcastMessageList){
            //Making sure we remember this microservice subscribed to this type of broadcast.
            if(!broadcastMessageList.contains(type)){
                broadcastMessageList.add(type);
            }
        }
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
        //Making sure this event is known to us.
        if(eventToFutureHashMap.containsKey(e)){
            //Retrieving future.
            Future<T> messageFuture = (Future<T>)(eventToFutureHashMap.get(e));
            messageFuture.resolve(result);
            //Removing from our collection.
            eventToFutureHashMap.remove(e);
        }
	}

	@Override
	public void sendBroadcast(Broadcast b) {
        //Get list of subscribed
        List<MicroService> mServiceList = broadcastToMicroServicesList.get(b.getClass());
        synchronized(mServiceList){
            for(MicroService mService : mServiceList){
                ConcurrentLinkedQueue<Message> messageQueue = microServiceToQueue.get(mService);
                synchronized(messageQueue){
                    //Pushing into queue.
                    messageQueue.add(b);
                    //Notifying so the processing thread may be released.
                    messageQueue.notify();
                }
            }
        }
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
        MicroService mService;
        //Get round-robin queue.
        ConcurrentLinkedQueue<MicroService> mServiceQueue = eventToMicroServicesQueues.get(e.getClass());
        synchronized(mServiceQueue){
            //Retrieving microservice and adding to back.
            mService = mServiceQueue.poll();
            mServiceQueue.add(mService);
        }

        //Getting microservice message queue.
        ConcurrentLinkedQueue<Message> messageQueue = microServiceToQueue.get(mService);

        //Creating future
        Future<T> newFuture = new Future<T>();
        eventToFutureHashMap.put(e, newFuture);

        synchronized(messageQueue){
            //Pushing into queue
            messageQueue.add(e);
            //Notifying processing thread.
            messageQueue.notify();
        }

        return newFuture;
	}

	@Override
	public void register(MicroService m) {

        //Many checks so we don't 'register twice'
        if(!microServiceToQueue.containsKey(m)){
            microServiceToQueue.put(m, new ConcurrentLinkedQueue<Message>());
        }
        if(!microServiceToEventList.containsKey(m)){
            microServiceToEventList.put(m, new LinkedList<Class>());
        }
        if(!microServiceToBroadcastList.containsKey(m)){
            microServiceToBroadcastList.put(m, new LinkedList<Class>());
        }
	}

	@Override
	public void unregister(MicroService m) {
        for(Class t: microServiceToEventList.get(m)){
            ConcurrentLinkedQueue<MicroService> eventQueue = eventToMicroServicesQueues.get(t);
            synchronized(eventQueue){
                eventQueue.remove(m);
            }
        }

        microServiceToEventList.remove(m);

        for(Class t: microServiceToBroadcastList.get(m)){
            List<MicroService> broadcastList = broadcastToMicroServicesList.get(t);
            synchronized(broadcastList){
                broadcastList.remove(m);
            }
        }

        microServiceToBroadcastList.remove(m);

        microServiceToQueue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
        ConcurrentLinkedQueue<Message> mServiceQueue = microServiceToQueue.get(m);
        synchronized(mServiceQueue){
            while(mServiceQueue.isEmpty()){
                mServiceQueue.wait();
            }

            return mServiceQueue.poll();
        }
	}
}
