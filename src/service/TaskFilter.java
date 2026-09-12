package service;

import java.time.LocalDate;

import org.eclipse.jdt.annotation.Nullable;

import model.Priority;
import model.Task;
import model.TaskStatus;

public record TaskFilter(
        @Nullable String tag,
        @Nullable TaskStatus status,
        @Nullable Priority priority,
        @Nullable LocalDate dueBy) {

    public static TaskFilter none() {
        return new TaskFilter(null, null, null, null);
    }

    public boolean matches(Task task) {
        if (tag != null && !task.getTags().contains(tag)) {
            return false;
        }

        if (status != null && task.getStatus() != status) {
            return false;
        }

        if (priority != null && task.getPriority() != priority) {
            return false;
        }

        if (dueBy != null) {
            LocalDate dueDate = task.getDueDate();

            if (dueDate == null || dueDate.isAfter(dueBy)) {
                return false;
            }
        }

        return true;
    }
}
