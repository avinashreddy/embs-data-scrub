package crux.embs;

import com.google.cloud.bigquery.Field;

public class TableConfig {

    private String tableName;
    private String schema;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
