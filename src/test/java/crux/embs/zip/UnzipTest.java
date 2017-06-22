package crux.embs.zip;


import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class UnzipTest {

    @Test
    public void test() throws IOException {
        String zipFile = new ClassPathResource("/crux/embs/zip/FHL_CS2.ZIP.201705042033").getFile().getAbsolutePath();
        Unzip unzip = new Unzip();
        unzip.unzip(zipFile, "./zip/");
    }

}
