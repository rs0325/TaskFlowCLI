package repository;

import model.Task;

public sealed interface LoadAllResult permits LoadAllResult.Success, LoadAllResult.Failure, LoadAllResult.NotExits {

record Success(Task task) implements LoadAllResult {}

record NotExits() implements LoadAllResult {}

record Failure(LoadError error) implements LoadAllResult {}
}
