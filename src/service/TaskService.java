package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import model.Priority;
import model.Task;
import model.TaskStatus;

import repository.TaskRepository;
import repository.result.SaveResult;

public class TaskService {
    private final TaskRepository repository;
    private final List<Task> tasks;

    public TaskService(TaskRepository repository, List<Task> tasks) {
        this.repository = repository;
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

    private long nextId() {
        long maxId = 0;

        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        return maxId + 1;
    }
}
