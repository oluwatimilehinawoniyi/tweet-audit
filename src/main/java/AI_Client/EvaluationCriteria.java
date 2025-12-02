package AI_Client;

import java.util.List;

public record EvaluationCriteria(
        List<String> forbiddenWords,
        boolean professionalCheck,
        String tone,
        boolean excludePolitics
) {
    public static EvaluationCriteria defaults() {
        return new EvaluationCriteria(
                List.of(),
                true,
                "respectful and thoughtful",
                false
        );
    }

    public String toPromptText() {
        StringBuilder criteria = new StringBuilder();

        criteria.append("Evaluate with a ").append(tone).append(" tone.\n\n");

        criteria.append("Flag tweets that:\n");
        criteria.append("- Contains offensive language\n");
        criteria.append("- Reflects opinions I might regret publicly " +
                "expressing\n");

        if (professionalCheck) {
            criteria.append("- Could be misunderstood out of context\n");
            criteria.append("- Fail professional standards\n");
        }

        if (!forbiddenWords.isEmpty()) {
            criteria.append("- Contain these words\n")
                    .append(String.join(", ", forbiddenWords))
                    .append("\n");
        }

        if (excludePolitics) {
            criteria.append("- Contains political content\n");
        }
        return criteria.toString();
    }
}
