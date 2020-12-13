package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Diary diary;
    private Ewoks ewoks;

    public HanSoloMicroservice(Diary diary) {
        super("Han");
        this.diary = diary;
        ewoks = Ewoks.getInstance();
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (c) -> {
            terminate();
        });
        subscribeEvent(AttackEvent.class, (c) -> {
            try {
                for(int i : c.getSerials()) {
                    ewoks.acquire(i);
                }
                Thread.sleep(c.getDuration());
                complete(c, true);
                for(int i : c.getSerials()) {
                    ewoks.release(i);
                }
            } catch (InterruptedException e) {
                //not sure...
            }
        });
    }
}
