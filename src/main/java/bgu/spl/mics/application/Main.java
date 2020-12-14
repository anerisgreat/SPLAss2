package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.Gson;

import bgu.spl.mics.application.AppConfig;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;



/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
        Gson gson = new Gson();
        AppConfig config = null;
        try{
            FileReader fReader = new FileReader(args[0]);
            config = gson.fromJson(fReader, AppConfig.class);
        }
        catch(FileNotFoundException e){
            System.out.println("Bad input file!");
        }
        if(config != null){
            //Setting up proper number of Ewoks
            Ewoks.setNumEwoks(config.Ewoks);
            Thread[] threads = new Thread[5];
            threads[0] = new Thread(new C3POMicroservice());
            threads[1] = new Thread(new HanSoloMicroservice());
            threads[2] = new Thread(new LandoMicroservice(config.R2D2));
            threads[3] = new Thread(new R2D2Microservice(config.R2D2));
            threads[4] = new Thread(new LeiaMicroservice(config.attacks));
            for(Thread t: threads)
                t.start();
            for(Thread t: threads){
                try{t.join();}
                catch(InterruptedException e){}
            }
            try {
                FileWriter fw = new FileWriter(args[1]);
                gson.toJson(Diary.getInstance(), fw);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                System.out.println("Error while writing to diary!");
            }
        }

	}
}
