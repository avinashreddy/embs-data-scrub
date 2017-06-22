package crux.embs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileAggregator {

    @Autowired
    FileConfigLookup fileConfigLookup;

    Map<String, List<File>> aggregate(Collection<File> files) {
        Map<String, List<File>> ret = new HashMap<>();
        for(File file : files) {
            String tableName = fileConfigLookup.getByFileName(file.getName()).getTable();
            List<File> tableFiles = ret.get(tableName);
            if(tableFiles == null) {
                tableFiles = new ArrayList<>();
                ret.put(tableName, tableFiles);
            }
            tableFiles.add(file);
        }

        return ret;
    }
}
