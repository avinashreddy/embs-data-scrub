package crux.embs;

import crux.embs.zip.Gzip;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestFileGen {

    public static void main(String[] args) throws IOException {
        File f = new File("/Users/avinash.palicharla/src/EMBS/work/temp/test.csv");
//        for(int i = 0; i < 1000000; i++) {
//            List<String> recs = new ArrayList<>();
//            for(int j = 0; j < 10; j++) {
//                recs.add( i + "," + j + ", " + j * 5);
//            }
//            FileUtils.writeLines(f, recs, true);
//        }

        Gzip gzip = new Gzip();
        gzip.gzip(f.getAbsolutePath(), f.getParent() + "/");
    }
}
