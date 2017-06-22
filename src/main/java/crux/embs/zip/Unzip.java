package crux.embs.zip;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class Unzip {

    public List<String> unzip(String fileZip, String targetDir) {
        File dir = new File(targetDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> ret = new ArrayList<>();
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(targetDir + fileName);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
                ret.add(newFile.getAbsolutePath());
            }
            zis.closeEntry();
            zis.close();
            return ret;
        }catch(Exception e) {
            throw new IllegalStateException(String.format("Error unzipping file [%s] to [%s]", fileZip, targetDir), e);
        }
    }
}
