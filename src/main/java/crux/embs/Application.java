package crux.embs;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import crux.embs.bq.FileLoader;
import crux.embs.schemagen.SchemaGenerator;
import crux.embs.schemagen.TableSchemaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    DirectoryProcessor directoryProcessor;

    @Autowired
    SchemaGenerator schemaGenerator;

    @Autowired
    Configuration configuration;

    @Autowired
    FileLoader fileLoader;

    @Override
    public void run(String... args) throws Exception {
//     schemaGenerator.writeToFile(configuration.getSchemaFile());
//       directoryProcessor.process();
        fileLoader.load("/Users/avinash.palicharla/src/EMBS/work/03/csvgzip/GNM_CS2.DAT.0.csv.gz");

    }

    @Bean
    public ExecutorService executorService() {
        return
                Executors.newFixedThreadPool(20,
                        new ThreadFactoryBuilder()
                                .setNameFormat("alertservice-pool-%d")
                                .setUncaughtExceptionHandler((t, e) -> logger.error("Error sending alert." + t, e)).build()
                );
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}