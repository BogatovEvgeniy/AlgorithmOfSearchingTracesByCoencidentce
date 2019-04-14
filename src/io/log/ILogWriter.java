package io.log;

import io.FileUtils;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public interface ILogWriter {

    String DATE_TIME_PATH_TEMPLATE = "dd_MM_yyyy_HH-mm";
    String DESTINATION_DIR =
            FileUtils.getCurrentDirectoryPath()
                    + "Results"
                    + File.separator
                    + new SimpleDateFormat(DATE_TIME_PATH_TEMPLATE).format(System.currentTimeMillis())
                    + File.separator;

    File write(XLog log);

    File write(XLog log, String fileName);

    File write(XLog log, String destDirectory, String fileName);
}
