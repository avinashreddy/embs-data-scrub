package crux.embs;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@ConfigurationProperties
public class Configuration {

    @Value("${HOME}")
    private String homeDir;

    @Value("${zipDir:zip/}")
    private String zipDir;

    @Value("${unzipDir:unzip/}")
    private String unzipDir;

    @Value("${gzipDir:gzip/}")
    private String gzipDir;

    @Value("${csvDir:csv/}")
    private String csvDir;

    @Value("${csvSplitDir:csvgzip/}")
    private String csvGzipDir;

    @Value("${schemaFile:schemaFile.csv/}")
    private String schemaFile;

    @Value("${schemaFile:tablefiles/}")
    private String tableFilesDir;

    @Value("${schemaFile:tablefilesgzip/}")
    private String tableFilesGzipDir;

    @Value("${splitSize:100000000}")
    private int splitSize;


    public String getZipDir() {
        return homeDir + zipDir;
    }

    public String getUnzipDir() {
        return homeDir + unzipDir;
    }

    public String getCsvDir() {
        return homeDir + csvDir;
    }

    public String getGzipDir() {
        return homeDir + gzipDir;
    }

    public String getCsvGzipDir() {
        return homeDir + csvGzipDir;
    }

    public String getSchemaFile() {
        return homeDir + schemaFile;
    }

    public String getTableFilesDir() {
        return  homeDir + tableFilesDir;
    }

    public String getTableFilesGzipDir() {
        return homeDir + tableFilesGzipDir;
    }

    public void initDirs() throws IOException {
        FileUtils.deleteQuietly(new File(getUnzipDir()));
        FileUtils.deleteQuietly(new File(getCsvDir()));
        FileUtils.deleteQuietly(new File(getGzipDir()));
        FileUtils.deleteQuietly(new File(getTableFilesDir()));
        FileUtils.deleteQuietly(new File(getTableFilesGzipDir()));

        FileUtils.forceMkdir(new File(getUnzipDir()));
        FileUtils.forceMkdir(new File(getCsvDir()));
        FileUtils.forceMkdir(new File(getGzipDir()));
        FileUtils.forceMkdir(new File(getCsvGzipDir()));
        FileUtils.forceMkdir(new File(getTableFilesDir()));
        FileUtils.forceMkdir(new File(getTableFilesGzipDir()));

    }
}
