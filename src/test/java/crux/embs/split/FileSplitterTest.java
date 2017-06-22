package crux.embs.split;

import crux.embs.transform.CSVLineTransformer;
import crux.embs.transform.NoopLineTransformer;
import org.junit.Test;

public class FileSplitterTest {

    @Test
    public void test() {
        FileSplitter splitter = new FileSplitter();
        splitter.split("/Users/avinash.palicharla/src/EMBS/work/01/unzip/GNM_ISS.DAT",
                "/Users/avinash.palicharla/src/EMBS/work/01/csv/",
                new NoopLineTransformer(),
                ".pipe",
                500);
    }
}
