package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
        this.diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        List<Future> attackFutures = new LinkedList<>();
        Future shieldDeactivation;
        Future destroyed;
        subscribeBroadcast(TerminationBroadcast.class, (c) -> {
            diary.setLeiaTerminate(System.currentTimeMillis());
            terminate();
        });
        //This is just for setup
        //Otherwise, two events might be sent to
        //same place.

        //send attack events and store its future
    	for (Attack a : attacks) {
    	    attackFutures.add(sendEvent(new AttackEvent(a)));
        }
    	//wait for all futures to be resolved
        for (Future f : attackFutures) {
            f.get();
        }
        //send deactivation event
        shieldDeactivation = sendEvent(new DeactivationEvent());
        //wait for shield to deactivate
        shieldDeactivation.get();

        //send bomb destroyer event
        destroyed = sendEvent((new BombDestroyerEvent()));
        //wait for future to be resolved
        destroyed.get();
        //send broadcast to all to terminate
        sendBroadcast(new TerminationBroadcast());
    }
}
