package AI_Client;

import model.EvaluationResult;

public interface TweetEvaluator {
    EvaluationResult evaluateTweet(String tweetText);
}
