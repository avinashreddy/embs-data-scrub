package crux.embs;

import crux.embs.analytics.FileStats;
import crux.embs.utils.FileConcat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Component
public class DirectoryProcessor {

    Logger log = LoggerFactory.getLogger(DirectoryProcessor.class);

    private final Configuration configuration;

    private final FileProcessor fileProcessor;

    private final ExecutorService executorService;

    @Autowired
    private FileAggregator fileAggregator;

    @Autowired
    private FileConcat fileConcat;

    @Autowired
    public DirectoryProcessor(Configuration configuration, FileProcessor fileProcessor, ExecutorService executorService) {
        this.configuration = configuration;
        this.fileProcessor = fileProcessor;
        this.executorService = executorService;
    }

    public void process() throws IOException, ExecutionException, InterruptedException {
        configuration.initDirs();
        Collection<File> zipFiles = FileUtils.listFiles(new File(configuration.getZipDir()),
                HiddenFileFilter.VISIBLE, FalseFileFilter.FALSE);
        log.info("Found {} files", zipFiles.size());

        List<FutureTask<FileStats>> tasks = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(zipFiles.size());
        for(File zipFile : zipFiles) {
            FutureTask<FileStats> task = new FutureTask<>(new Callable<FileStats>() {
                @Override
                public FileStats call() throws Exception {
                    FileStats ret = fileProcessor.process(zipFile);
                    latch.countDown();
                    return ret;
                }
            });
            tasks.add(task);
            executorService.execute(task);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        for(FutureTask<FileStats> task : tasks) {
            System.out.println(task.get().toCsv());
        }

//        Map<String, List<File>> tableFilesMap = fileAggregator.aggregate(FileUtils.listFiles(new File(configuration.getCsvDir()),
//                HiddenFileFilter.VISIBLE, FalseFileFilter.FALSE));
//
//        for(final Map.Entry<String, List<File>> tableFiles: tableFilesMap.entrySet()) {
//            FutureTask<Boolean> task = new FutureTask<>(() -> fileConcat.concat(configuration.getTableFilesDir() + tableFiles.getKey() + ".csv", tableFiles.getValue()));
//            executorService.execute(task);
//        }


        try {
            executorService.shutdown();
            log.info("waiting for tasks to complete......");
            executorService.awaitTermination(15, TimeUnit.MINUTES);
            log.info("All tasks completed......");
        } catch (Exception e) {
            log.error("Executor service interrupted", e);
        }

    }

}
