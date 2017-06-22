package crux.embs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileConfigLookup {

    List<FileConfig> fileConfigList = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        fileConfigList = new ObjectMapper().readValue(
                new ClassPathResource("file_config.json").getFile(),
                new TypeReference<List<FileConfig>>() {});
    }

    public FileConfig getByFileName(String fileName) {
        String type = fileName.substring(0, fileName.indexOf('.'));
        if("FNM_MODD".equals(type)
                || "FNM_MODM".equals(type)
                || fileName.indexOf("_") < 0) {
            return getByFileType(type);
        } else {
            type = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
            return getByFileType(type);
        }
    }

    public FileConfig getByFileType(String fileType) {
        List<FileConfig> ret = fileConfigList.stream()
                .filter( e -> e.getFileType().equals(fileType))
                .collect(Collectors.toList());
        Preconditions.checkState(
                ret.size() == 1,
                "Expecting 1 FileConfig with fileType = '%s'. Found %s", fileType, ret.size());
        return ret.get(0);
    }
}
