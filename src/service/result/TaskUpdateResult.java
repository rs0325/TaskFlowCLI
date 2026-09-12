package service.result;

import model.Task;

import repository.result.SaveError;

public sealed interface TaskUpdateResult
        permits TaskUpdateResult.Success, TaskUpdateResult.NotFound, TaskUpdateResult.Failure {

    record Success(Task task) implements TaskUpdateResult {}

    record NotFound() implements TaskUpdateResult {}

    record Failure(SaveError error) implements TaskUpdateResult {}
}
