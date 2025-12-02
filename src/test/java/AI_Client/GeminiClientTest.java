package AI_Client;

import model.EvaluationResult;
import model.enums.ProcessingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class GeminiClientTest {
    @Test
    void evaluatesTweetSuccessfully() {
        assumeTrue(System.getenv("GEMINI_API_KEY") != null, "API key not set - skipping");
        TweetEvaluator evaluator =
                new GeminiClient(EvaluationCriteria.defaults());
        String testTweet =
                "This is how men will bend their back till they reach Badagry? this is fucked up! Damn";

        EvaluationResult result = evaluator.evaluateTweet(testTweet);

        assertNotNull(result);
        assertNotNull(result.status());
        assertNotNull(result.reason());

        assertEquals(ProcessingStatus.DELETE, result.status());

        System.out.println("Status: " + result.status());
        System.out.println("Reason: " + result.reason());
    }

    @Test
    void evaluatesCleanTweet() {
        assumeTrue(System.getenv("GEMINI_API_KEY") != null, "Skipping - no API key");

        TweetEvaluator evaluator =
                new GeminiClient(EvaluationCriteria.defaults());

        String testTweet =
                "Just finished a great book on software architecture!";

        EvaluationResult result = evaluator.evaluateTweet(testTweet);

        assertEquals(ProcessingStatus.KEEP, result.status());
    }
}