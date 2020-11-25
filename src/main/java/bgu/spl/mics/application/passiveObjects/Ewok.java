package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    static int totalSerial = 0;

	int serialNumber;
	boolean available;

    public Ewok(int serialNumber){
        this.serialNumber = totalSerial;
        totalSerial++;

        this.available = true;
    }
	
  
    /**
     * Acquires an Ewok
     */
    public void acquire() {
        this.available = false;
    }

    /**
     * release an Ewok
     */
    public void release() {
        this.available = true;
    }

    /**
     * checks if Ewok is available
     */
    public boolean getAvailable(){
        return this.available;
    }
}
