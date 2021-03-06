package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.SingletoneCountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;
    private Diary diary;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        this.diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (c) -> {
            diary.setR2D2Terminate(System.currentTimeMillis());
            terminate();
        });
        subscribeEvent(DeactivationEvent.class, (DeactivationEvent c) -> {
            try {
                Thread.sleep(duration);
                diary.setR2D2Deactivate(System.currentTimeMillis());
                complete(c, true);
            } catch (InterruptedException e) {
                System.out.println("Error with this R2 unit. Should have gotten the orange droid.");
                sendBroadcast(new TerminationBroadcast());
            }
        });
        SingletoneCountDownLatch.getInstance().countDown();
    }
}
