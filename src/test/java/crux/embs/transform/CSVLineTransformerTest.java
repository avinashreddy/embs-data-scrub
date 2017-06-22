package crux.embs.transform;

import org.junit.Assert;
import org.junit.Test;

public class CSVLineTransformerTest {

    CSVLineTransformer transformer = new CSVLineTransformer();

    @Test
    public void test() {
        String input = "FHL|2B6115|31326LYL3|1677479|20170501|CA|17.25|11|29|#|2017|100.0|95|\"H\"";
        String output = transformer.transFormLine(input);
        Assert.assertEquals(output, "\"FHL\",\"2B6115\",\"31326LYL3\",\"1677479\",\"20170501\",\"CA\",\"17.25\",\"11\",\"29\",\"#\",\"2017\",\"100.0\",\"95\",\"\\\"H\\\"\"");
    }
}
