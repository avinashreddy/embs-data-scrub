package crux.embs.schemagen;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableSchemaGenerator {

    String sqlTables = "SELECT table_name FROM information_schema.tables WHERE TABLE_schema = 'embs_owner' AND TABLE_type = 'BASE TABLE'";

    String sqlSchema = "SELECT string_agg(y.X, ',') FROM \n" +
            "(SELECT column_name || ':' || \n" +
            "\n" +
            "CASE WHEN data_type = 'character' THEN 'string' \n" +
            "WHEN data_type = 'double precision' THEN 'float' \n" +
            "WHEN data_type = 'smallint' THEN 'integer' \n" +
            "WHEN data_type = 'timestamp without time zone' THEN 'string'\n" +
            "WHEN data_type = 'date' THEN 'string'\n" +
            "WHEN data_type = 'character varying' THEN 'string'\n" +
            "WHEN data_type = 'real' THEN 'float'\n" +
            "WHEN data_type = 'text' THEN 'string'\n" +
            "WHEN data_type = 'integer' THEN 'integer'\n" +
            "ELSE 'ERROR:UNKNOWN DATA TYPE'  \n" +
            "END AS X\n" +
            "FROM information_schema.columns \n" +
                "WHERE table_schema = 'embs_owner'\n" +
            "AND table_name = ?\n" +
            "ORDER BY table_name, ordinal_position) y";

    public Map<String, String> getSchemas(JdbcTemplate jdbcTemplate) {
        List<String> tables = jdbcTemplate.queryForList(sqlTables, String.class);
        Map<String, String> ret = new HashMap<>();
        for(String table : tables) {
            ret.put(table, getSchema(table, jdbcTemplate));
        }
        return ret;
    }

    public String getSchema(String table, JdbcTemplate jdbcTemplate) {
        String ret = jdbcTemplate.queryForObject(sqlSchema, String.class, table.toLowerCase());
        Assert.isTrue(StringUtils.hasText(ret), "Cannot find schema for " + table.toLowerCase());
        Assert.isTrue(!ret.contains("ERROR:UNKNOWN DATA TYPE"), "Unknown datatype found: " + table.toLowerCase() + " " + ret);
        return ret;
    }
}
