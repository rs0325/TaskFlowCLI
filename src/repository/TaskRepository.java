package repository;

import java.util.List;

import model.Task;

import repository.result.DeleteResult;
import repository.result.LoadAllResult;
import repository.result.LoadResult;
import repository.result.SaveAllResult;
import repository.result.SaveResult;

public interface TaskRepository {

    LoadResult load(String fileName);

    LoadAllResult loadAll();

    SaveResult save(Task task);

    SaveAllResult saveAll(List<Task> tasks);

    DeleteResult delete(long id);
}
