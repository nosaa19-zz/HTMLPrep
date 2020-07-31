import Service.InputPrep;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {

        String Folder = "F:/dataset/ETL/Test5/";
        String SelectedFiles = null;

        if(args.length > 0) {

            if(args.length == 1){
                Folder = args[0];
                SelectedFiles = null;
            }

            if(args.length == 2){
                Folder = args[0];
                SelectedFiles = args[1];
            }
        }

        System.out.println(Folder);
        File inputFile = new File(Folder + "Input");
        if (!inputFile.exists()) {
            System.out.println("Folder not found!");
        } else if (inputFile.listFiles().length > 0) {
            InputPrep In = new InputPrep();
            try {
                In.Run(Folder, inputFile.listFiles(), SelectedFiles);
            } catch (Exception ex) {
                Logger.getLogger(InputPrep.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.exit(0);

    }
}
