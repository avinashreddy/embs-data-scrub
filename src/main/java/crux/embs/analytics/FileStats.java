package crux.embs.analytics;

import java.io.File;

public class FileStats {

    private String zipFile;

    private long zipBytes;

    private String datFile;

    private long datBytes;

    private String gzipFile;

    private long gzipBytes;

    private long lineCount;


    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    public String getGzipFile() {
        return gzipFile;
    }

    public void setGzipFile(String gzipFile) {
        this.gzipFile = gzipFile;
    }

    public long getZipBytes() {
        return zipBytes;
    }

    public void setZipBytes(long zipBytes) {
        this.zipBytes = zipBytes;
    }

    public String getDatFile() {
        return datFile;
    }

    public void setDatFile(String datFile) {
        this.datFile = datFile;
    }

    public long getDatBytes() {
        return datBytes;
    }

    public void setDatBytes(long datBytes) {
        this.datBytes = datBytes;
    }

    public long getGzipBytes() {
        return gzipBytes;
    }

    public void setGzipBytes(long gzipBytes) {
        this.gzipBytes = gzipBytes;
    }

    public long getLineCount() {
        return lineCount;
    }

    public void setLineCount(long lineCount) {
        this.lineCount = lineCount;
    }

    public String toCsv() {
        return  name(zipFile) + "\t"  + zipBytes + "\t" +
                name(gzipFile) + "\t" + gzipBytes + "\t" +
                name(datFile) + "\t" + datBytes + "\t" +
                lineCount + "\t" + buildCommand(name(gzipFile));
    }

    private String name(String file) {
        return new File(file).getName();
    }

    private String getResourceName(String file) {
        String name = new File(file).getName();
        return name.substring(0, name.indexOf("."));
    }

    private String buildCommand(String name) {
        String command = "%s %s tab_%s";
        return String.format(command, "ttt", name, getResourceName(name));
    }

    private String getDir(String name) {
         if(name.indexOf("_") < 0) return name;
         return name.substring(name.indexOf("_") + 1);
    }
}
