package repository;

import java.util.List;

import model.Task;

public sealed interface LoadAllResult permits LoadAllResult.Success, LoadAllResult.Failure, LoadAllResult.NotExits {

record Success(List<Task> tasks) implements LoadAllResult {}

record NotExits() implements LoadAllResult {}

record Failure(LoadError error) implements LoadAllResult {}
}
