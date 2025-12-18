package model;

import model.enums.ProcessingStatus;

public record EvaluationResult(
        ProcessingStatus status,
        String reason
) {
}
