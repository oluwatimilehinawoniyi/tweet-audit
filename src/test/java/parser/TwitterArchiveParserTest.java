package parser;

import model.Tweet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitterArchiveParserTest {

    private TwitterArchiveParser parser;

    @BeforeEach
    void setUp() {
        parser = new TwitterArchiveParser();
    }

    @Test
    void parsesTweetsFromFile() throws IOException {
        List<Tweet> tweets = parser.parseTweets("data/tweets.js");

        assertFalse(tweets.isEmpty());
        assertNotNull(tweets.getFirst().id());
        assertNotNull(tweets.getFirst().text());
    }
}