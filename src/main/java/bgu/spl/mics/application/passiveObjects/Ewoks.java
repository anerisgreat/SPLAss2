package bgu.spl.mics.application.passiveObjects;


import java.util.Collection;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks ewoks;
    private static int numEwoks;
    private Ewok[] ewokArr;//need to make sure list is ordered by serialNumber

    public static void setNumEwoks(int n){
        numEwoks = n;
    }

    public synchronized static Ewoks getInstance() {
        if (ewoks == null) {
            ewoks = new Ewoks();
        }
        return ewoks;
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
                    ewokArr.wait();
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
