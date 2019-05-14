package scanner;

import java.io.InputStream;
import java.util.ArrayList;

public class ScannerWrapper {
    public String getScanner() {
        return analyzer;
    }

    String analyzer;

    ArrayList<String> tokens;
    int tokenIndex;

    public ScannerWrapper(InputStream is) throws Exception {
        analyzer = new String();
        tokens = new ArrayList<>();
        tokenIndex = 0;
        tokens.add("Test4");
        tokens.add("Test3");
        tokens.add("$");
    }

    public String nextToken() throws Exception {
        return tokens.get(tokenIndex++);
    }
}
