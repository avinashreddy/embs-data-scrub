package crux.embs.schemagen;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchemaGenerator {

    @Autowired
    private DataSourceLookup dataSourceLookup;

    @Autowired
    private TableSchemaGenerator schemaGenerator;

    public void writeToFile(String file) throws IOException {
        FileUtils.writeLines(new File(file), sort(toLines(schemaGenerator.getSchemas(getJdbcTemplate()))));
    }

    private Collection<?> sort(List<String> strings) {
         Collections.sort(strings);
         return strings;
    }

    private List<String> toLines(Map<String, String> fileSchemas) {
        List<String> ret = new ArrayList<>();
        for(Map.Entry<String, String> entry : fileSchemas.entrySet()) {
            ret.add( "{ \"tableName\": " + "\"" + entry.getKey() + "\", \"schema\": " + "\"" + entry.getValue() +"\"},");
        }
        return ret;
    }

    private JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSourceLookup.getDataSource());
    }
}
