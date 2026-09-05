package repository.result;

public sealed interface DeleteResult
        permits DeleteResult.Success, DeleteResult.NotExits, DeleteResult.Failure {

    record Success() implements DeleteResult {}

    record NotExits() implements DeleteResult {}

    record Failure(DeleteError error) implements DeleteResult {}
}
