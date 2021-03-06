package bgu.spl.mics.application.passiveObjects;


import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static int numEwoks;
    private Ewok[] ewokArr;//need to make sure list is ordered by serialNumber

    public static void setNumEwoks(int n){
        numEwoks = n;
    }

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }
    public static Ewoks getInstance() {
        return SingletonHolder.instance;
    }


    private Ewoks(){
        ewokArr = new Ewok[numEwoks];
        for(int i = 0; i < numEwoks; ++i){
            ewokArr[i] = new Ewok(i+1);
        }
    }

    public void acquire(Collection<Integer> toAcquire){
        synchronized(ewokArr){
            boolean isAvailable = true;
            int cSize = toAcquire.size();
            do{
                isAvailable = true;

                for(int index: toAcquire){
                    isAvailable = ewokArr[index - 1].getAvailable();
                    if(!isAvailable){
                        break;
                    }
                }

                if(!isAvailable){
                    try{
                        ewokArr.wait();
                    }
                    catch(InterruptedException e){}
                }
            }while(!isAvailable);

            for(int index: toAcquire){
                ewokArr[index - 1].acquire();
            }
        }
    }

    public void release(Collection<Integer> toRelease) {
        synchronized(ewokArr){
            for(int index: toRelease){
                ewokArr[index - 1].release();
            }

            ewokArr.notifyAll();
        }
    }
}
