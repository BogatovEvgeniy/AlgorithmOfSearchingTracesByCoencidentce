import io.log.XesLogReader;
import org.deckfour.xes.model.XLog;
import parser.WriterFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CommandParser {
    private static final int COMMAND_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int LOG_PATH_INDEX = 2;

    public static void parse(String[] args, LogWriter logWriter) {
        switch (args[COMMAND_INDEX]) {
            case "-saveAs":
                saveAs(args, logWriter);
            default:
                break;
        }
    }

    private static Boolean saveAs(String[] args, LogWriter logWriter) {
        try {
            String logFilePath = args[LOG_PATH_INDEX];
            File file = new File(logFilePath);
            List<XLog> parsedLog = new XesLogReader().parse(file);
            if (parsedLog != null && parsedLog.size() > 0) {
                String currentDirPath = System.getProperty("user.dir") + File.separator;
                WriterFactory.parserFor(args[VALUE_INDEX]).write(parsedLog.get(0), currentDirPath, file.getName());
                return true;
            } else {
                logWriter.write("Unable to parse log");
                return false;
            }
        } catch (IndexOutOfBoundsException e) {
            logWriter.write("Some of arguments missed. Arguments received:" + Arrays.toString(args));
            logWriter.write("Input format should be the next: <command>[-saveAs] <Save as type>[\"OpenXEStoCSV\"] <Path to log file>. Arguments received:" + Arrays.toString(args));
        } catch (Exception e) {
            logWriter.write("Something goes wrong. Arguments received:" + Arrays.toString(args));
            logWriter.write(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}
