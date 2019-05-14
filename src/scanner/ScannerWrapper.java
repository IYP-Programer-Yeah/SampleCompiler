package scanner;

import java.io.InputStream;

public class ScannerWrapper {
    public String getScanner() {
        return analyzer;
    }

    String analyzer;

    boolean isEof = false;

    public ScannerWrapper(InputStream is) throws Exception {
        analyzer = new String();
    }

    public String nextToken() throws Exception {
        if (!isEof) {
            isEof = true;
            return "Test3";
        }
        else
            return "$";
    }
}
