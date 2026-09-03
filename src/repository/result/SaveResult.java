package repository.result;

public sealed interface SaveResult permits SaveResult.Success, SaveResult.Failure {

	record Success() implements SaveResult {}

	record Failure(SaveError error) implements SaveResult {}

}
