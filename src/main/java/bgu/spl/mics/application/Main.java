package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;

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
            new Thread(new C3POMicroservice()).start();
            new Thread(new HanSoloMicroservice()).start();
            new Thread(new LandoMicroservice(config.R2D2)).start();
            new Thread(new R2D2Microservice(config.R2D2)).start();
            try{
                Thread.sleep(100);
            }catch(Exception e){}
            new Thread(new LeiaMicroservice(config.attacks)).start();
        }

        try {
            FileWriter fw = new FileWriter(args[1]);
            gson.toJson(Diary.getInstance(), fw);
            fw.flush();
            fw.close();
        } catch (Exception e) {

        }

	}
}
