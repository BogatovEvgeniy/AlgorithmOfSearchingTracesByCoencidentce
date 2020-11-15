import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private static final String LOG_NAME = "\\AlgorithmAppLogger.txt";
    public static final String NEW_PARAGHRAPH = "\n";
    private final File destinationFile;

    public LogWriter() {
        destinationFile = new File(System.getProperty("user.dir") + LOG_NAME);
    }

    private void initiateDestinationFileIfNeed() throws IOException {
        if (!destinationFile.exists()){
            destinationFile.createNewFile();
        }
    }


    public void write(String s) {
        try {
            initiateDestinationFileIfNeed();
            FileWriter fileWriter = new FileWriter(destinationFile);
            fileWriter.write(getCurrentTime());
            fileWriter.write(s);
            fileWriter.write(NEW_PARAGHRAPH);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.FULL)
                .format(new Date(System.currentTimeMillis())) + ": ";
    }
}
