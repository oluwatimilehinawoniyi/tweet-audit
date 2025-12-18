package parser;

import com.google.gson.annotations.SerializedName;

public record TweetWrapper(TweetData tweet) {
    public record TweetData(
            @SerializedName("id_str") String id,
            @SerializedName("full_text") String text
    ) {
    }
}
