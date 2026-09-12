package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import cli.TaskCli;

import model.Task;

import repository.JsonTaskRepository;
import repository.TaskRepository;
import repository.result.LoadAllResult;

import service.TaskService;

public class Main {
    private static final String PROMPT = "> ";

    public static void main(String[] args) {
        TaskRepository repository = new JsonTaskRepository();
        List<Task> tasks = loadTasks(repository);

        if (tasks == null) {
            System.exit(1);
            return;
        }

        TaskService service = new TaskService(repository, tasks);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        TaskCli cli = new TaskCli(service, reader, System.out);

        try {
            runLoop(cli);
        } finally {
            closeQuietly(reader);
        }
    }

    private static @Nullable List<Task> loadTasks(TaskRepository repository) {
        LoadAllResult result = repository.loadAll();

        return switch (result) {
            case LoadAllResult.Success success -> success.tasks();
            case LoadAllResult.NotExits notExits -> List.of();
            case LoadAllResult.Failure failure -> {
                System.out.println(failure.error().getMessage());

                if (failure.fileName() != null) {
                    System.out.println("対象ファイル: " + failure.fileName());
                }

                yield null;
            }
        };
    }

    private static void runLoop(TaskCli cli) {
        while (true) {
            System.out.print(PROMPT);
            System.out.flush();

            String line = cli.readLine();

            if (line == null || !cli.handle(line)) {
                return;
            }
        }
    }

    private static void closeQuietly(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            // 終了時のクローズ失敗は無視する
        }
    }
}
