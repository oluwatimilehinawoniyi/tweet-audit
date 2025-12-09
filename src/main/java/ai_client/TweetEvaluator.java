package ai_client;

import model.EvaluationResult;

public interface TweetEvaluator {
    EvaluationResult evaluateTweet(String tweetText);
}
