package parser;

import io.ILogWriter;
import io.XEStoCSVWriter;

public class WriterFactory {

    public static final String OPENXES_TO_CSV_WRITER = "OpenXEStoCSV";

    public static ILogWriter parserFor(String value) {
        switch (value) {
            case OPENXES_TO_CSV_WRITER:
                return new XEStoCSVWriter();
            default:
                return null;
        }
    }
}
