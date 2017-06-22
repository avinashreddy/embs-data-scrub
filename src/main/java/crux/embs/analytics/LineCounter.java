package crux.embs.analytics;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;


public class LineCounter {
    public int count(String file) {
        try(InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
