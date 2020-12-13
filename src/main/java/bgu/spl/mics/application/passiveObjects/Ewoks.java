package bgu.spl.mics.application.passiveObjects;


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
    private List<Ewok> ewokList;//need to make sure list is ordered by serialNumber

    public static Ewoks getInstance() {
        if (ewoks == null) {
            ewoks = new Ewoks();
        }
        return ewoks;
    }

    public void acquire(int i) {
        ewokList.get(i).acquire();
    }

    public void release(int i) {
        ewokList.get(i).release();
    }

    public boolean getAvailable(int i) {
        return ewokList.get(i).getAvailable();
    }
}
