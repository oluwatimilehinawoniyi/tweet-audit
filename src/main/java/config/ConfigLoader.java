package config;

import ai_client.EvaluationCriteria;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ConfigLoader {
    public static EvaluationCriteria load(String path) throws IOException {
        Gson gson = new Gson();
        CriteriaConfig config = gson.fromJson(new FileReader(path),
                CriteriaConfig.class);

        return new EvaluationCriteria(
                config.forbiddenWords,
                config.professionalCheck,
                config.tone,
                config.excludePolitics
        );
    }

    private static class CriteriaConfig {
        List<String> forbiddenWords;
        boolean professionalCheck;
        String tone;
        boolean excludePolitics;
    }
}
