package crux.embs;

import com.google.common.base.Preconditions;
import crux.embs.analytics.FileStats;
import crux.embs.analytics.LineCounter;
import crux.embs.bq.FileLoader;
import crux.embs.split.FileSplitter2;
import crux.embs.transform.CSVLineTransformer;
import crux.embs.transform.LineTransformer;
import crux.embs.transform.NoopLineTransformer;
import crux.embs.zip.Gzip;
import crux.embs.zip.Unzip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class FileProcessor {

    Logger log = LoggerFactory.getLogger(FileProcessor.class);

    private final Configuration configuration;

    private final Unzip unzip;

    private final FileSplitter2 fileSplitter;

    private final Gzip gzip;

    @Autowired
    private RunConfig runConfig;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    public FileProcessor(Configuration configuration, Unzip unzip,
                         FileSplitter2 fileSplitter, Gzip gzip) {
        this.configuration = configuration;
        this.unzip = unzip;
        this.fileSplitter = fileSplitter;
        this.gzip = gzip;
    }

    public FileStats process(final File zipFile)  {
        FileStats fileStats = new FileStats();
        fileStats.setZipFile(zipFile.getAbsolutePath());
        fileStats.setZipBytes(zipFile.length());
        try {
            final List<String> files = unzip.unzip(zipFile.getAbsolutePath(), configuration.getUnzipDir());
            Assert.isTrue(files.size() == 1,
                    String.format("Expected 1 file in %s. Found %s", zipFile.getAbsolutePath(), files.size()));

            final String datFile = files.get(0);
            log.info("Unzipped file {} to {}", zipFile.getAbsoluteFile(), datFile);
            fileStats.setDatFile(datFile);
            fileStats.setDatBytes(new File(datFile).length());
            fileStats.setLineCount(new LineCounter().count(datFile));

//            final String gzipFile = gzip.gzip(datFile, configuration.getGzipDir());
//            fileStats.setGzipFile(gzipFile);
//            fileStats.setGzipBytes(new File(gzipFile).length());


                String csv = transform(datFile, zipFile.getName());
                log.info("Created CSV {} for file {}", csv, datFile);
                final String csvgzipFile = gzip.gzip(csv, configuration.getCsvGzipDir());
                fileLoader.load(csvgzipFile);

            return fileStats;
        }catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String transform(String split, String sourceFileName) throws IOException {
        LineTransformer lt = new MetadataAddingLineTransformer(sourceFileName);
        List<String> ret = fileSplitter.split(
                split, configuration.getCsvDir(),
                lt, ".csv",
                Integer.MAX_VALUE);
        return ret.get(0);
    }

    private class MetadataAddingLineTransformer implements LineTransformer {
        private final String originalFile;

        private final LineTransformer lineTransformer = new CSVLineTransformer();

        private MetadataAddingLineTransformer(String originalFile) {
            Preconditions.checkArgument(StringUtils.hasText(originalFile), "originalFile is null/empty");
            this.originalFile = originalFile;
        }

        @Override
        public String transFormLine(String line) {
            return runConfig.getRuntTimeUTC() + "," + originalFile + "," + lineTransformer.transFormLine(line);
        }
    }

}
