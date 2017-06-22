package crux.embs.utils;

import crux.embs.Configuration;
import crux.embs.zip.Gzip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

@Component
public class FileConcat {

    @Autowired
    Configuration configuration;

    @Autowired
    Gzip gzip;

    public boolean concat(String targetFileName, List<File> fileList) {
        try {
            Path outFile = Paths.get(targetFileName);
            try (FileChannel out = FileChannel.open(outFile, CREATE, WRITE)) {
                for (File file : fileList) {
                    Path inFile = Paths.get(file.getAbsolutePath());
                    System.out.println(inFile + "...");
                    try (FileChannel in = FileChannel.open(inFile, READ)) {
                        for (long p = 0, l = in.size(); p < l; )
                            p += in.transferTo(p, l - p, out);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        gzip.gzip(targetFileName, configuration.getTableFilesGzipDir());
        return true;
    }
}
