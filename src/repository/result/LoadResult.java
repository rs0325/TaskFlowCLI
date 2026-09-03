package repository.result;

import model.Task;

public sealed interface LoadResult
permits LoadResult.Success, LoadResult.Failure, LoadResult.NotExits {

record Success(Task task) implements LoadResult {}

record NotExits() implements LoadResult {}

record Failure(LoadError error) implements LoadResult {}
}