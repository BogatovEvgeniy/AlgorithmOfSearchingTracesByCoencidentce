package io;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static String getCurrentDirectoryPath() {
        return System.getProperty("user.dir") + File.separator;
    }

    public static void createFileIfNeed(File file) throws IOException {
        if (file.exists()) {
            return;
        }

        file.getParentFile().mkdirs();
        file.createNewFile();

    }
}
