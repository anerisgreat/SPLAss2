package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }
    public static Diary getInstance() {
        return SingletonHolder.instance;
    }

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }

    public void setTotalAttacks() {
        totalAttacks.addAndGet(1);
    }
    public AtomicInteger getTotalAttacks() { return totalAttacks;}
    public void setHanSoloFinish(long HanSoloFinish) {
        this.HanSoloFinish = HanSoloFinish;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public void setC3POFinish(long C3POFinish) {
        this.C3POFinish = C3POFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public void setC3POTerminate(long C3POTerminate) {
        this.C3POTerminate = C3POTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }
    
}
