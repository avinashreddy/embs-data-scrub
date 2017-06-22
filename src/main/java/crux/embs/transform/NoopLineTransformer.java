package crux.embs.transform;


public class NoopLineTransformer implements LineTransformer {

    public String transFormLine(String line) {
        return line;
    }
}
