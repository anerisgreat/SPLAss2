package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    private Diary diary;
    private Ewoks ewoks;

    public C3POMicroservice(Diary diary) {
        super("C3PO");
        this.diary = diary;
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (c) -> {
            diary.setC3POTerminate(System.currentTimeMillis());
            terminate();
        });
        subscribeEvent(AttackEvent.class, (c) -> {
            try {
                //this part needs to be fixed
                ewoks.acquire(c.getSerials());
                //
                Thread.sleep(c.getDuration());
                diary.setTotalAttacks();
                complete(c, true);
                diary.setC3POFinish(System.currentTimeMillis());
                ewoks.release(c.getSerials());
            } catch (InterruptedException e) {
                //not sure...
            }
        });
    }
}
