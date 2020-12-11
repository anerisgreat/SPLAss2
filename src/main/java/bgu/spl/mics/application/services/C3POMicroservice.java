package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    Diary diary;

    public C3POMicroservice(Diary diary) {
        super("C3PO");
        this.diary = diary;
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (c) -> {
            try {
                Thread.sleep(c.getDuration());//need to get the duration from the attack
            } catch (InterruptedException e) {
                //not sure...
            }
        });
    }
}
