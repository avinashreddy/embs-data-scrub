package crux.embs.zip;

import crux.embs.FileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@Component
public class Gzip {

    Logger log = LoggerFactory.getLogger(Gzip.class);

    public String gzip(String file, String gzipDir ) {

        String gzipFile = gzipDir + new File(file).getName() + ".gz";

        byte[] buffer = new byte[1024];

        try{
            GZIPOutputStream gzos =
                    new GZIPOutputStream(new FileOutputStream(gzipFile));

            FileInputStream in = new FileInputStream(new File(file));

            int len;
            while ((len = in.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }

            in.close();

            gzos.finish();
            gzos.close();

            log.info("gzipped file {} to {}", file, gzipFile);

            return gzipFile;

        }catch(IOException ex){
            throw new IllegalStateException(ex);
        }
    }

}
