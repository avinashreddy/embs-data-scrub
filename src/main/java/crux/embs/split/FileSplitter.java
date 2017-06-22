package crux.embs.split;

import com.google.common.collect.Lists;
import crux.embs.Configuration;
import crux.embs.transform.LineTransformer;
import crux.embs.transform.NoopLineTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileSplitter {

    Logger log = LoggerFactory.getLogger(FileSplitter.class);

    public List<String> split(String filePath, String targetDir, LineTransformer lineTransformer, String extension, int splitSize) {
        log.info("Processing file {}", filePath);
        final LineIterator it;
        try {
            it = FileUtils.lineIterator(new File(filePath), "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Error building LineIterator " + filePath);
        }

        List<String> ret = new ArrayList<>();
        try {
            List<String> lines = new ArrayList<>();
            int splitCount = 0;
            int colCount = -1;
            while (it.hasNext()) {
                String line = it.nextLine();
                int lineColCount = getColCount(line, "|");
                if(colCount == -1) {
                    colCount = lineColCount;
                } else {
                    Assert.isTrue(colCount == lineColCount, "Expected " + colCount + ". Found " + lineColCount);
                }
                lines.add(lineTransformer.transFormLine(line));
                if(lines.size() == splitSize) {
                    String splitName = getSplitName(filePath, targetDir, splitCount++, extension);
                    createFile(splitName, lines);
                    ret.add(splitName);
                    lines.clear();
                }
            }
            if(!lines.isEmpty()) {
                String splitName = getSplitName(filePath, targetDir, splitCount++, extension);
                createFile(splitName, lines);
                ret.add(splitName);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return ret;
    }

    private int getColCount(String line, String s) {
        return StringUtils.countOccurrencesOf(line, s);
    }

    private String getSplitName(String filePath, String targetDir, int splitCount, String extension) {
        File f = new File(filePath);
        return targetDir + "/" + f.getName() + "." + splitCount + extension;
    }

    private void createFile(String filePath, List<String> lines) {
        try {
            log.info("Creating file {}", filePath);
            FileUtils.writeLines(new File(filePath ), lines);
        } catch (IOException e) {
            throw new IllegalStateException("Error creating file " + filePath);
        }
    }
}
