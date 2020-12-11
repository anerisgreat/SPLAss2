package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    Diary diary;

    public HanSoloMicroservice(Diary diary) {
        super("Han");
        this.diary = diary;
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (c) -> {
            try {
                Thread.sleep(c.getDuration());
            } catch (InterruptedException e) {
                //not sure...
            }
        });
    }
}
