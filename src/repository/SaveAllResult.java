package repository;

public sealed interface SaveAllResult permits SaveAllResult.Success, SaveAllResult.Failure {

	record Success() implements SaveAllResult {}

	record Failure(SaveError error) implements SaveAllResult {}

}