package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.eclipse.jdt.annotation.Nullable;

import model.Priority;
import model.Task;
import model.TaskStatus;

import repository.TaskRepository;
import repository.result.DeleteResult;
import repository.result.SaveResult;

import service.result.TaskDeleteResult;
import service.result.TaskUpdateResult;

public class TaskService {
    private final TaskRepository repository;
    private final List<Task> tasks;

    public TaskService(TaskRepository repository, List<Task> tasks) {
        this.repository = repository;
        // 登録・更新・削除で一覧を変更するため、変更可能なリストとして保持する
        this.tasks = new ArrayList<>(tasks);
    }

    public SaveResult add(
            String title,
            String description,
            Priority priority,
            @Nullable LocalDate dueDate,
            List<String> tags) {
        Task task = new Task(
                nextId(),
                title,
                description,
                TaskStatus.TODO,
                priority,
                dueDate,
                tags,
                LocalDateTime.now(),
                null
        );

        SaveResult result = repository.save(task);

        if (result instanceof SaveResult.Failure) {
            return result;
        }

        tasks.add(task);

        return result;
    }

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    public List<Task> find(TaskFilter filter) {
        return tasks.stream()
                .filter(filter::matches)
                .toList();
    }

    public Optional<Task> findById(long id) {
        int index = indexOf(id);

        if (index < 0) {
            return Optional.empty();
        }

        return Optional.of(tasks.get(index));
    }

    public TaskUpdateResult changeStatus(long id, TaskStatus status) {
        return update(id, task -> task.withStatus(status));
    }

    public TaskUpdateResult edit(
            long id,
            String title,
            String description,
            Priority priority,
            @Nullable LocalDate dueDate,
            List<String> tags) {
        return update(id, task -> task.withContent(title, description, priority, dueDate, tags));
    }

    public TaskDeleteResult delete(long id) {
        int index = indexOf(id);

        if (index < 0) {
            return new TaskDeleteResult.NotFound();
        }

        DeleteResult result = repository.delete(id);

        if (result instanceof DeleteResult.Failure failure) {
            return new TaskDeleteResult.Failure(failure.error());
        }

        tasks.remove(index);

        return new TaskDeleteResult.Success();
    }

    private TaskUpdateResult update(long id, UnaryOperator<Task> updater) {
        int index = indexOf(id);

        if (index < 0) {
            return new TaskUpdateResult.NotFound();
        }

        Task updated = updater.apply(tasks.get(index));

        SaveResult result = repository.save(updated);

        if (result instanceof SaveResult.Failure failure) {
            return new TaskUpdateResult.Failure(failure.error());
        }

        tasks.set(index, updated);

        return new TaskUpdateResult.Success(updated);
    }

    private int indexOf(long id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                return i;
            }
        }

        return -1;
    }

    private long nextId() {
        long maxId = 0;

        for (Task task : tasks) {
            if (task.id() > maxId) {
                maxId = task.id();
            }
        }

        return maxId + 1;
    }
}
