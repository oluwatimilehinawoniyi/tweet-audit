package orchestrator;

import csv.CsvHandler;
import AI_Client.EvaluationCriteria;
import AI_Client.GeminiClient;
import AI_Client.TweetEvaluator;
import model.EvaluationResult;
import model.Tweet;
import parser.TwitterArchiveParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TweetAuditOrchestrator {

    public void run(String archivePath, String outputCsvPath,
                    EvaluationCriteria criteria)
            throws IOException {
        validateFile(archivePath);

        TweetEvaluator evaluator = new GeminiClient(criteria);
        CsvHandler csvHandler = new CsvHandler(outputCsvPath);
        TwitterArchiveParser parser = new TwitterArchiveParser();

        Set<String> processedIds = csvHandler.getProcessedTweetIds();
        List<Tweet> allTweets = parser.parseTweets(archivePath);

        int totalTweets = allTweets.size();
        int alreadyProcessed = processedIds.size();
        int remainingTweets = totalTweets - alreadyProcessed;

        System.out.println("Total Tweets: " + totalTweets);
        System.out.println("Processed Tweets: " + alreadyProcessed);
        System.out.println("Remaining Tweets: " + remainingTweets);

        if (remainingTweets == 0) {
            System.out.println("No tweets left in archive");
            return;
        }

        System.out.print("Continue? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("y")) {
            System.out.println("Aborted.");
            return;
        }

        int processed = 0;
        for (Tweet tweet : allTweets) {
            if (processedIds.contains(tweet.id())) {
                continue;
            }

            System.out.print(
                    "Processing " + (processed + 1) + "/" + remainingTweets + "... ");
            EvaluationResult result =
                    evaluator.evaluateTweet(tweet.text());

            System.out.println(result.status());
            csvHandler.writeResult(tweet.id(), tweet.text(),
                    result.status(), result.reason());
            processed++;
            if (processed % 10 == 0) {
                System.out.println(
                        "Processed " + processed + " of " + remainingTweets);
            }
        }

        System.out.println("DONE! Processed " + processed + " tweets.");
    }

    private void validateFile(String archivePath) {
        Path path = Paths.get(archivePath);
        if (Files.notExists(path)) {
            System.out.println("Archive does not exist");
            System.exit(1);
        }

        if (Files.isDirectory(path)) {
            System.out.println("Archive is expected to be a file, not a " +
                    "directory");
            System.exit(1);
        }
    }
}
