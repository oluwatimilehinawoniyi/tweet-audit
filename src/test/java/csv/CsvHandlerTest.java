package csv;

import model.enums.ProcessingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CsvHandlerTest {
    private CsvHandler csvHandler;
    private final String testFile = "test-tweets.csv";

    @BeforeEach
    void setUp() throws IOException {
        csvHandler = new CsvHandler(testFile);
        deleteIfExists(testFile);
        deleteIfExists(
                testFile.replace(".csv", "_checkpoint.txt"));
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteIfExists(testFile);
        deleteIfExists(
                testFile.replace(".csv", "_checkpoint.txt"));
    }

    @Test
    void returnsEmptySetWhenFileDoesNotExist() {
        Set<String> ids = csvHandler.getProcessedTweetIds();
        assertTrue(ids.isEmpty());
    }

    @Test
    void onlyWritesDeletedTweetsToResultsCsv() throws IOException {
        csvHandler.writeResult("123", "Bad tweet", ProcessingStatus.DELETE, "Offensive");
        csvHandler.writeResult("456", "Good tweet", ProcessingStatus.KEEP, "Fine");

        Set<String> ids = csvHandler.getProcessedTweetIds();
        assertEquals(2, ids.size());

        List<String> csvLines = Files.readAllLines(Path.of(testFile));
        assertEquals(2, csvLines.size());
        assertTrue(csvLines.get(1).contains("123"));
        assertFalse(csvLines.get(1).contains("456"));
    }

    @Test
    void writesAndReadsBackTweetIds() {
        csvHandler.writeResult("419", "First model.Tweet",
                ProcessingStatus.DELETE, "Bad");
        csvHandler.writeResult("911", "Second model.Tweet",
                ProcessingStatus.KEEP, "Good");

        Set<String> ids = csvHandler.getProcessedTweetIds();

        assertEquals(2, ids.size());
        assertTrue(ids.containsAll(Set.of("419", "911")));
    }

    @Test
    void appendsWithoutOverwriting() {
        csvHandler.writeResult("419", "First model.Tweet",
                ProcessingStatus.DELETE, "Bad");
        csvHandler.writeResult("911", "Second model.Tweet",
                ProcessingStatus.KEEP, "Good");

        Set<String> ids = csvHandler.getProcessedTweetIds();

        assertEquals(2, ids.size());
    }

    private void deleteIfExists(String filename) {
        File file = new File(filename);
        if (file.exists() && !file.delete()) {
            System.err.println("Failed to delete: " + filename);
        }
    }
}