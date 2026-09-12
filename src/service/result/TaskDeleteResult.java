package service.result;

import repository.result.DeleteError;

public sealed interface TaskDeleteResult
        permits TaskDeleteResult.Success, TaskDeleteResult.NotFound, TaskDeleteResult.Failure {

    record Success() implements TaskDeleteResult {}

    record NotFound() implements TaskDeleteResult {}

    record Failure(DeleteError error) implements TaskDeleteResult {}
}
