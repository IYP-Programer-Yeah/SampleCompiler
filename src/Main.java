import parser.Parser;
import parser.ParserInitializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by HoseinGhahremanzadeh on 4/17/2019.
 */
public class Main {
    public static void main(String[] args) {
        String logDir = "log.txt";
        String binDir = "out.bin";
        String sourceDir = "lexer-sample-input.clike";
        for (int i=0;i<args.length;i++) {
            if (args[i].toLowerCase().equals("-log") && i < args.length - 1)
                logDir = args[i + 1];
            if (args[i].toLowerCase().equals("-src") && i < args.length - 1)
                sourceDir = args[i + 1];
            if (args[i].toLowerCase().equals("-bin") && i < args.length - 1)
                binDir = args[i + 1];
        }

        File sourceFile = new File(sourceDir);
        File logFile = new File(logDir);
        File binFile = new File(binDir);
        FileInputStream source = null;
        try {
            source = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileOutputStream log = null;
        try {
            log = new FileOutputStream(logFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileOutputStream bin = null;
        try {
            bin = new FileOutputStream(binFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Parser parser = ParserInitializer.createParser("test-parser-table.npt", source, log, bin, sourceDir);
        parser.parse();
        parser.writeOutput();
    }
}
