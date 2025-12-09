import ai_client.EvaluationCriteria;
import config.ConfigLoader;
import orchestrator.TweetAuditOrchestrator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println(
                    "Usage: tweet-audit <archivePath> <outputPath> " +
                            "<configPath>");
            System.exit(1);
        }

        if (args.length < 3) {
            System.err.println(
                    "Usage: tweet-audit <archivePath> <outputPath> <configPath>");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            System.out.println("Progress saved. you can resume later.");
        }));

        System.out.println("""
                ================================
                        TWEET AUDIT TOOL
                ================================
                """);

        String inputFilePath = args[0];
        String outputFilePath = args[1];
        String configPath = args[2];

        try {
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println(
                        "GEMINI_API_KEY environment variable not set");
                System.exit(1);
            }

            EvaluationCriteria criteria = ConfigLoader.load(configPath);
            new TweetAuditOrchestrator().run(inputFilePath,
                    outputFilePath, criteria);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
