package csv;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import model.enums.ProcessingStatus;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;

import java.util.Set;


@Slf4j
public class CsvHandler {

    private static final String[] CSV_HEADERS =
            {"id", "text", "status", "reason"};
    private final File file;
    private final File checkpointFile;

    public CsvHandler(String fileName) {
        this.file = new File(fileName);
        this.checkpointFile = new File(fileName.replace(".csv",
                "_checkpoint.txt"));
    }

    public Set<String> getProcessedTweetIds() {

        if (!checkpointFile.exists()) {
            return new HashSet<>();
        }

        try {
            return new HashSet<>(
                    Files.readAllLines(checkpointFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeResult(String tweetId, String tweetText,
                            ProcessingStatus status, String reason) {
        appendToCheckpoint(tweetId);
        if (status == ProcessingStatus.DELETE) {
            writeToResultCsv(tweetId, tweetText, reason);
        }
    }

    private void appendToCheckpoint(String tweetId) {
        try (FileWriter fw = new FileWriter(checkpointFile, true)) {
            fw.write(tweetId + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToResultCsv(String tweetId, String tweetText,
                                  String reason) {
        boolean fileExists = file.exists();
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(file, true))) {

            if (!fileExists) {
                writer.writeNext(new String[]{"id", "text", "reason"});
            }
            writer.writeNext(new String[]{tweetId, tweetText, reason});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
