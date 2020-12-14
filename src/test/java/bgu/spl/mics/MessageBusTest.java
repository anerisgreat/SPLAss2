package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.*;


import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;


public class MessageBusTest {
    private class BCast implements Broadcast {}
    private MessageBus msgBus;
    private MicroService ms;
    private MicroService OtherMs;
    private AttackEvent ae;
    private AttackEvent otherAe;
    private BCast bc;

    @BeforeEach
    public void setUp(){
        Diary d = new Diary();
        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        msgBus = MessageBusImpl.getInstance();
        ms = new C3POMicroservice();
        OtherMs = new HanSoloMicroservice();
        ae = new AttackEvent(new Attack(list,100));
        bc = new BCast();
        otherAe = new AttackEvent(new Attack(list, 200));

    }

    @Test
    public void testRegister(){
        //We check if a simple register and sending of event
        //works
        Message m;
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);

        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
    }


    @Test
    public void testSubscribeEvent(){
        //Check if, despite two registered microservices,
        //correct microservice collects the intended message.
        msgBus.register(ms);
        msgBus.register(OtherMs);
        Message m;
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(ae, m);
        } catch (Exception e) {
            assert true;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testSubscribeBroadcast() {
        //Checking if two microservices get same broadcast
        //event
        msgBus.register(ms);
        msgBus.register(OtherMs);
        Message m;
        msgBus.subscribeBroadcast(bc.getClass(), ms);
        msgBus.subscribeBroadcast(bc.getClass(), OtherMs);
        msgBus.sendBroadcast(bc);
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, bc);
            m = msgBus.awaitMessage(OtherMs);
            assertEquals(m, bc);
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testComplete(){
        //Testing complete
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        Future<Boolean> f = msgBus.sendEvent(ae);
        assert(!f.isDone());
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            msgBus.complete((AttackEvent)m, true);
            assertEquals(ae, m);
            assert(f.isDone());
            assert(f.get());
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
    }

    @Test
    public void testSendBroadcast(){
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeBroadcast(bc.getClass(), ms);
        msgBus.subscribeBroadcast(bc.getClass(), OtherMs);
        msgBus.sendBroadcast(bc);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, bc);
            m = msgBus.awaitMessage(OtherMs);
            assertEquals(m, bc);
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
        msgBus.unregister(OtherMs);
    }

    @Test
    public void testSendEvent(){
        msgBus.register(ms);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.sendEvent(ae);
        Message m;
        try {
            m = msgBus.awaitMessage(ms);
            assertEquals(m, ae);
        } catch (Exception e) {
            assert false;
        }
        msgBus.unregister(ms);
    }



    @Test
    public void testAwaitMessage() {
        msgBus.register(ms);
        msgBus.register(OtherMs);
        msgBus.subscribeEvent(ae.getClass(), ms);
        msgBus.subscribeBroadcast(bc.getClass(), OtherMs);
        msgBus.sendEvent(ae);
        msgBus.sendBroadcast(bc);
        Message m;
       try {
          m = msgBus.awaitMessage(ms);
          assertEquals(m, ae);
          m = msgBus.awaitMessage(OtherMs);
          assertEquals(m, bc);
       }
       catch (Exception e) {
           assert false;
       }
    }
}
