package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.Nullable;

import model.Priority;
import model.Task;
import model.TaskStatus;
import repository.result.SaveResult;
import service.TaskFilter;
import service.TaskService;
import service.result.TaskDeleteResult;
import service.result.TaskUpdateResult;

public class TaskCli {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskService service;
    private final CommandParser parser;
    private final BufferedReader in;
    private final PrintStream out;

    public TaskCli(TaskService service, BufferedReader in, PrintStream out) {
        this.service = service;
        this.parser = new CommandParser();
        this.in = in;
        this.out = out;
    }

    public @Nullable String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean handle(String input) {
        ParseResult result = parser.parse(input);

        if (result instanceof ParseResult.Failure failure) {
            out.println(failure.error().getMessage());
            return true;
        }

        Command command = ((ParseResult.Success) result).command();

        if (command instanceof Command.Add) {
            add();
        } else if (command instanceof Command.List list) {
            list(list.tag());
        } else if (command instanceof Command.Show show) {
            show(show.id());
        } else if (command instanceof Command.Edit edit) {
            edit(edit.id());
        } else if (command instanceof Command.Done done) {
            done(done.id());
        } else if (command instanceof Command.Delete delete) {
            delete(delete.id());
        } else if (command instanceof Command.Exit) {
            return false;
        }

        return true;
    }

    private void add() {
        TaskInput taskInput = readTaskInput();

        if (taskInput == null) {
            out.println("入力が中断されたため、登録を取り消しました。");
            return;
        }

        SaveResult result = service.add(
                taskInput.title(),
                taskInput.description(),
                taskInput.priority(),
                taskInput.dueDate(),
                taskInput.tags()
        );

        if (result instanceof SaveResult.Failure failure) {
            out.println(failure.error().getMessage());
            return;
        }

        out.println("タスクを登録しました。");
    }

    private void list(@Nullable String tag) {
        List<Task> tasks = service.find(new TaskFilter(tag, null, null, null));

        if (tasks.isEmpty()) {
            out.println("タスクはありません。");
            return;
        }

        // 桁をそろえるとフォントによって崩れるため、項目名を添えて1行で表示する
        for (Task task : tasks) {
            out.println(String.join(" / ",
                    "[" + task.getId() + "] " + task.getStatus().display,
                    "優先度: " + task.getPriority().display,
                    task.getTitle(),
                    "期限: " + formatDate(task.getDueDate())
            ));
        }
    }

    private void show(long id) {
        Optional<Task> found = service.findById(id);

        if (found.isEmpty()) {
            printNotFound(id);
            return;
        }

        Task task = found.get();

        out.println("ID: " + task.getId());
        out.println("タイトル: " + task.getTitle());
        out.println("説明: " + task.getDescription());
        out.println("状態: " + task.getStatus().display);
        out.println("優先度: " + task.getPriority().display);
        out.println("期限: " + formatDate(task.getDueDate()));
        out.println("タグ: " + String.join(", ", task.getTags()));
        out.println("作成日時: " + formatDateTime(task.getCreatedAt()));
        out.println("完了日時: " + formatDateTime(task.getCompletedAt()));
    }

    private void edit(long id) {
        if (service.findById(id).isEmpty()) {
            printNotFound(id);
            return;
        }

        TaskInput taskInput = readTaskInput();

        if (taskInput == null) {
            out.println("入力が中断されたため、編集を取り消しました。");
            return;
        }

        TaskUpdateResult result = service.edit(
                id,
                taskInput.title(),
                taskInput.description(),
                taskInput.priority(),
                taskInput.dueDate(),
                taskInput.tags()
        );

        printUpdateResult(id, result, "タスクを更新しました。");
    }

    private void done(long id) {
        TaskUpdateResult result = service.changeStatus(id, TaskStatus.DONE);

        printUpdateResult(id, result, "タスクを完了にしました。");
    }

    private void delete(long id) {
        TaskDeleteResult result = service.delete(id);

        if (result instanceof TaskDeleteResult.Success) {
            out.println("タスクを削除しました。");
        } else if (result instanceof TaskDeleteResult.NotFound) {
            printNotFound(id);
        } else if (result instanceof TaskDeleteResult.Failure failure) {
            out.println(failure.error().getMessage());
        }
    }

    private void printUpdateResult(long id, TaskUpdateResult result, String successMessage) {
        if (result instanceof TaskUpdateResult.Success) {
            out.println(successMessage);
        } else if (result instanceof TaskUpdateResult.NotFound) {
            printNotFound(id);
        } else if (result instanceof TaskUpdateResult.Failure failure) {
            out.println(failure.error().getMessage());
        }
    }

    private void printNotFound(long id) {
        out.println("ID " + id + " のタスクは見つかりません。");
    }

    private record TaskInput(
            String title,
            String description,
            Priority priority,
            @Nullable LocalDate dueDate,
            List<String> tags) {}

    private @Nullable TaskInput readTaskInput() {
        String title = prompt("タイトル: ");
        if (title == null) {
            return null;
        }

        String description = prompt("説明: ");
        if (description == null) {
            return null;
        }

        LocalDate dueDate = null;
        while (true) {
            String line = prompt("期限 (YYYY-MM-DD、未入力で期限なし): ");
            if (line == null) {
                return null;
            }
            if (line.isEmpty()) {
                break;
            }
            try {
                dueDate = LocalDate.parse(line);
                break;
            } catch (DateTimeParseException e) {
                out.println("期限はYYYY-MM-DD形式で入力してください。");
            }
        }

        Priority priority;
        while (true) {
            String line = prompt("優先度 (LOW/MEDIUM/HIGH): ");
            if (line == null) {
                return null;
            }
            Priority parsed = parsePriority(line);
            if (parsed != null) {
                priority = parsed;
                break;
            }
            out.println("優先度はLOW、MEDIUM、HIGHのいずれかで入力してください。");
        }

        String tagLine = prompt("タグ (カンマ区切りで複数指定可): ");
        if (tagLine == null) {
            return null;
        }

        return new TaskInput(title, description, priority, dueDate, parseTags(tagLine));
    }

    private @Nullable String prompt(String label) {
        out.print(label);
        out.flush();

        String line = readLine();

        return line == null ? null : line.trim();
    }

    private static @Nullable Priority parsePriority(String input) {
        for (Priority priority : Priority.values()) {
            if (priority.name().equalsIgnoreCase(input) || priority.display.equals(input)) {
                return priority;
            }
        }

        return null;
    }

    private static List<String> parseTags(String input) {
        List<String> tags = new ArrayList<>();

        for (String tag : input.split(",")) {
            String trimmed = tag.trim();

            if (!trimmed.isEmpty() && !tags.contains(trimmed)) {
                tags.add(trimmed);
            }
        }

        return tags;
    }

    private static String formatDate(@Nullable LocalDate date) {
        return date == null ? "なし" : date.toString();
    }

    private static String formatDateTime(@Nullable LocalDateTime dateTime) {
        return dateTime == null ? "なし" : dateTime.format(DATE_TIME_FORMAT);
    }
}
