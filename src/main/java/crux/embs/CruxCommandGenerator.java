package crux.embs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CruxCommandGenerator {

    Logger log = LoggerFactory.getLogger(CruxCommandGenerator.class);

    public void generate(String sourceDir) {
        FileUtils.deleteQuietly(new File(sourceDir + "/commands.txt"));
        Collection<File> dirs = FileUtils.listFilesAndDirs(new File(sourceDir), FalseFileFilter.FALSE, HiddenFileFilter.VISIBLE);
        List<String> commands = new ArrayList<>();
        for(File dir : dirs) {
            if(sourceDir.equals(dir.getAbsolutePath() + "/")) {
                continue;
            }
            Assert.isTrue(dir.isDirectory(), dir.getAbsolutePath() + " is not a directory");
            commands.addAll(buildCommands(dir));
//            commands.add(""); //A empty line
        }
        try {
            FileUtils.writeLines(new File(sourceDir + "/commands.txt"), commands);
        } catch (IOException e) {
            throw new IllegalStateException("Error creating command file", e);
        }
    }

    private List<String> buildCommands(File dir) {
        String command = "%s %s/%s tab_%s";
        String dirName = dir.getName();
        log.info("Reading dir " + dir.getAbsolutePath());
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, HiddenFileFilter.VISIBLE, FalseFileFilter.FALSE));
        List<String> commands = new ArrayList<>();
        Assert.isTrue(files.size() > 0, dir.getAbsolutePath() + " is empty");
        for(int i = 0; i < files.size(); i++) {
            String fileName = files.get(i).getName();
            String cruxResourceName =  fileName.substring(0, fileName.indexOf("."));

            if(i == 0) {
                commands.add(String.format(command, "ttt ", dirName, cruxResourceName, cruxResourceName));
            } else {
                commands.add(String.format(command, "ttt ", dirName, cruxResourceName, cruxResourceName));
            }
        }
        return commands;
    }
}
