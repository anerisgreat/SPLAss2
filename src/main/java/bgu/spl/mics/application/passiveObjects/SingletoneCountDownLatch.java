package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.CountDownLatch;

public class SingletoneCountDownLatch {
    private static class SingletonHolder {
        private static CountDownLatch instance = new CountDownLatch(4);
    }
    public static CountDownLatch getInstance() {
        return SingletonHolder.instance;
    }
}
