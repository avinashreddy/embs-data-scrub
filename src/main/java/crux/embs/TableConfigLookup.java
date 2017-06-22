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
public class TableConfigLookup {

    List<TableConfig> configList = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        configList = new ObjectMapper().readValue(
                new ClassPathResource("table_config.json").getFile(),
                new TypeReference<List<TableConfig>>() {});
    }

    public TableConfig get(String tableName) {
        List<TableConfig> ret = configList.stream()
                .filter( e -> e.getTableName().equalsIgnoreCase(tableName))
                .collect(Collectors.toList());
        Preconditions.checkState(
                ret.size() == 1,
                "Expecting 1 TableConfig with tableName = '%s'. Found %s", tableName, ret.size());
        return ret.get(0);
    }
}
