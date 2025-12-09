package parser;

import com.google.gson.Gson;
import model.Tweet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TwitterArchiveParser {
    public List<Tweet> parseTweets(String filePath) throws IOException {
        String tweetJson = Files.readString(Path.of(filePath));
        Gson gson = new Gson();
        TweetWrapper[] wrappers =
                gson.fromJson(tweetJson, TweetWrapper[].class);

        List<Tweet> tweets = new ArrayList<>();
        for (TweetWrapper wrapper : wrappers) {
            tweets.add(new Tweet(
                    wrapper.tweet().id(),
                    wrapper.tweet().text(),
                    null,
                    null
            ));
        }

        return tweets;
    }
}
