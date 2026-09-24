package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

public record Task(
		long id,
		String title,
		String description,
		TaskStatus status,
		Priority priority,
		@Nullable
		LocalDate dueDate,
		List<String> tags,
		LocalDateTime createdAt,
		@Nullable
		LocalDateTime completedAt
		) {

	public Task {
		// 受け取ったリストを後から変更されても影響しないよう、不変リストとして保持する
		tags = List.copyOf(tags);
	}

	public Task withStatus(TaskStatus newStatus) {
		LocalDateTime newCompletedAt = newStatus == TaskStatus.DONE ? LocalDateTime.now() : null;

		return new Task(id, title, description, newStatus, priority, dueDate, tags, createdAt, newCompletedAt);
	}

	public Task withContent(
			String newTitle,
			String newDescription,
			Priority newPriority,
			@Nullable
			LocalDate newDueDate,
			List<String> newTags
			) {
		return new Task(id, newTitle, newDescription, status, newPriority, newDueDate, newTags, createdAt, completedAt);
	}
}
