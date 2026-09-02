package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

public class Task {
	private long id;
	private String title;
	private String description;
	private TaskStatus status;
	private Priority priority;
	@Nullable
	private LocalDate dueDate;
	private List<String> tags;
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;

	public long getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public Priority getPriority() {
		return priority;
	}
	public LocalDate getDueDate() {
		return dueDate;
	}
	public List<String> getTags() {
		return tags;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public Task(
			long id,
			String title,
			String description,
			TaskStatus status,
			Priority priority,
			@Nullable
			LocalDate dueDate,
			List<String> tags,
			LocalDateTime createdAt,
			LocalDateTime completedAt
			) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.dueDate = dueDate;
		this.tags = tags;
		this.createdAt = createdAt;
		this.completedAt = completedAt;
	}

}
