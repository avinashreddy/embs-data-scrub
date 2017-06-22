package crux.embs.bq;

import com.google.api.core.ApiClock;
import com.google.cloud.WaitForOption;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobStatistics;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import crux.embs.FileConfig;
import crux.embs.FileConfigLookup;
import crux.embs.TableConfig;
import crux.embs.TableConfigLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class FileLoader {

    Logger log = LoggerFactory.getLogger(FileLoader.class);

    @Autowired
    private FileConfigLookup fileConfigLookup;

    @Autowired
    private TableConfigLookup tableConfigLookup;

    private BigQuery bigquery;

    private final String DATA_SET_NAME = "embs";

    @PostConstruct
    public void init() {
        bigquery = BigQueryOptions.getDefaultInstance().getService();
//       createTable("embs", "test", "col_1");
    }

    public long load(String filePath) {
        File file = new File(filePath);
        Preconditions.checkState(file.exists(), "filePath does not exist");
        FileConfig fileConfig = fileConfigLookup.getByFileName(file.getName());
        TableConfig tableConfig = tableConfigLookup.get(fileConfig.getTable());


        TableId tableId = TableId.of(DATA_SET_NAME, fileConfig.getTable());
        WriteChannelConfiguration writeChannelConfiguration = WriteChannelConfiguration.newBuilder(tableId)
                        .setFormatOptions(FormatOptions.csv())
                        .setAutodetect(false)
                        .setSchema(parse(tableConfig.getSchema()))
                        .build();
        TableDataWriteChannel writer = bigquery.writer(writeChannelConfiguration);
// Write data to writer
        try (OutputStream stream = Channels.newOutputStream(writer)) {
                Files.copy(file, stream);
        } catch (IOException e) {
            throw new IllegalStateException("Error copying file " + filePath, e);
        }
// Get load job
        log.info(String.format("Loading file %s to table %s", filePath, tableConfig.getTableName()));
        Job job = writer.getJob();
        try {
            job = waitFor(job, filePath, tableConfig.getTableName());
            job.waitFor();
            log.info(String.format("Loaded file %s to table %s", filePath, tableConfig.getTableName()));
        } catch (InterruptedException e) {
            throw new IllegalStateException("File load interrupted " + filePath, e);
        } catch (TimeoutException e) {
            throw new IllegalStateException("File load interrupted " + filePath, e);

        }
        JobStatistics stats = job.getStatistics();
        return 0L; //stats.getOutputRows();

    }

    public Job waitFor(Job job, String table, String file) throws InterruptedException, TimeoutException {
        WaitForOption.Timeout timeout = WaitForOption.Timeout.getOrDefault();
        WaitForOption.CheckingPeriod checkingPeriod = WaitForOption.CheckingPeriod.getOrDefault();
        long timeoutMillis = timeout.getTimeoutMillis();
        ApiClock clock = bigquery.getOptions().getClock();
        long startTime = clock.millisTime();
        while (!job.isDone()) {
            log.info(String.format("job running %s. Loading file %s to table %s", job.getJobId(), file, table));
            if (timeoutMillis  != -1 && (clock.millisTime() - startTime)  >= timeoutMillis) {
                throw new TimeoutException();
            }
            checkingPeriod.sleep();
        }
        return bigquery.getJob(job.getJobId());
    }

    private Schema parse(String schemaString) {
        List<Field> fields = new ArrayList<>();

        for(String fieldStr : schemaString.split(",")) {
            String[] filedAttr = fieldStr.split(":");
            fields.add(Field.of(filedAttr[0], StringToType.get(filedAttr[1])));
        }
        return Schema.of(fields);
    }

    public Table createTable(String datasetName, String tableName, String fieldName) {
        // [START createTable]
        TableId tableId = TableId.of(datasetName, tableName);
        // Table field definition
        Field field = Field.of(fieldName, Field.Type.string());
        // Table schema definition
        Schema schema = Schema.of(field);
        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
        Table table = bigquery.create(tableInfo);
        // [END createTable]
        return table;
    }

    private static class StringToType {

        public static Field.Type get(String type) {
            switch(type) {
                case "string" : return Field.Type.string();
                case "integer" : return Field.Type.integer();
                case "float" : return Field.Type.floatingPoint();
                case "timestamp" : return Field.Type.timestamp();
            }
            throw new IllegalStateException("Unknown type " + type);
        }
    }
}
