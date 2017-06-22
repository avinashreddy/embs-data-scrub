package crux.embs;

import crux.embs.schemagen.FileToTableMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Deprecated
public class FileCategorize {

    Logger log = LoggerFactory.getLogger(FileCategorize.class);

    private final FileToTableMap fileToTableMap;

    @Autowired
    public FileCategorize(FileToTableMap fileToTableMap) {
        this.fileToTableMap = fileToTableMap;
    }

    public String byTargetTable(File file, String targetDir) throws IOException {

        final String fileType = getType(file.getName());
        final String dir = targetDir + fileType + "/";
        FileUtils.forceMkdir(new File(dir));
        String link = dir + file.getName();
        log.info("Copying {} to {}", file.getAbsoluteFile(), link);
        try {
            Files.createSymbolicLink(Paths.get(link), Paths.get(file.getAbsolutePath()));
//            FileUtils.copyFile(file, new File(newFile));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error copying %s to %s", file.getAbsoluteFile(), link), e);
        }
        return link;
    }

    private String getType(String name) {
        return fileToTableMap.getTable(name);
//        if (name.indexOf("_") < 0) {
//            if (name.indexOf(".") > 0) {
//                return name.substring(0, name.indexOf("."));
//            }
//            return name;
//        }
//        return name.substring(name.indexOf("_") + 1, name.indexOf("."));
    }

    public static void main(String[] a) throws IOException {
        Files.createSymbolicLink(Paths.get("/Users/avinash.palicharla/src/EMBS/01/ARMINDEX.DAT"), Paths.get("/Users/avinash.palicharla/src/EMBS/01/unzip/ARMINDEX.DAT"));


    }
}
