package ai_client;

import com.google.genai.Client;
import com.google.genai.types.*;
import ai_client.retry.RateLimiter;
import ai_client.retry.RetryHandler;
import model.EvaluationResult;
import model.enums.ProcessingStatus;

import java.util.Objects;

public class GeminiClient implements TweetEvaluator {
    private final EvaluationCriteria criteria;
    private final RateLimiter rateLimiter = new RateLimiter();
    private final RetryHandler retryHandler = new RetryHandler();

    public GeminiClient(EvaluationCriteria criteria) {
        this.criteria = criteria;
    }

    public EvaluationResult evaluateTweet(String tweetText) {
        rateLimiter.waitIfNeeded();

        return retryHandler.execute(
                () -> makeApiCall(tweetText),
                new EvaluationResult(ProcessingStatus.API_ERROR,
                        "Failed after retries")
        );

    }

    private EvaluationResult makeApiCall(String tweetText) {
        try (Client client = new Client()) {
            String prompt = buildPrompt(tweetText);
            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .thinkingConfig(ThinkingConfig.builder()
                                    .thinkingBudget(0))
                            .systemInstruction(
                                    Content.fromParts(Part.fromText("You" +
                                            " are evaluating tweets for " +
                                            "deletion. Be concise. " +
                                            "Always start responses with" +
                                            " DELETE or KEEP. Followed " +
                                            "by a brief reason not more " +
                                            "than 10/15 words."))
                            )
                            .build();

            GenerateContentResponse response = client
                    .models.generateContent(
                            "gemini-2.5-flash",
                            prompt,
                            config
                    );
            String responseText = response.text();

            return parseResponse(responseText);
        }
    }

    private String buildPrompt(String tweetText) {
        return """
                Evaluate the following tweet based on these criteria:
                %s
                Tweet: "%s"
                """.formatted(criteria.toPromptText(), tweetText);

    }

    private EvaluationResult parseResponse(String responseText) {

        ProcessingStatus status;
        if (Objects.requireNonNull(responseText)
                .startsWith("DELETE")) {
            status = ProcessingStatus.DELETE;
        } else if (responseText.startsWith("KEEP")) {
            status = ProcessingStatus.KEEP;
        } else {
            status = ProcessingStatus.NEEDS_MANUAL_REVIEW;
        }

        String reason =
                responseText.substring(responseText.indexOf('.') + 1)
                        .trim();

        return new EvaluationResult(status, reason);
    }
}
