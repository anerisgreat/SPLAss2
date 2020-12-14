package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.SingletoneCountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;
    private Diary diary;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        this.diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (c) -> {
            diary.setLandoTerminate(System.currentTimeMillis());
            terminate();
        });
        subscribeEvent(BombDestroyerEvent.class, (c) -> {
            try {
                Thread.sleep(duration);
                complete(c, true);
            } catch (InterruptedException e) {
                //not sure...
            }
        });
        SingletoneCountDownLatch.getInstance().countDown();
    }
}
