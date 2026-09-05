package repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import model.Task;

import repository.result.DeleteError;
import repository.result.DeleteResult;
import repository.result.LoadAllResult;
import repository.result.LoadError;
import repository.result.LoadResult;
import repository.result.SaveAllResult;
import repository.result.SaveError;
import repository.result.SaveResult;

public class JsonTaskRepository {
	private static final String FILE_DIR = "data";
	
	private static final Gson gson = new Gson();
	
    public LoadResult load(String fileName) {
        File file = new File(FILE_DIR + "/" + fileName);

        if (!file.exists()) {
            return new LoadResult.NotExits();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Task>() {}.getType();
            Task task = gson.fromJson(reader, type);
            return new LoadResult.Success(task);
        } catch (JsonSyntaxException e) {
            return new LoadResult.Failure(
                    LoadError.INVALID_JSON
            );
        } catch (IOException e) {
            return new LoadResult.Failure(
                    LoadError.IO_ERROR
            );
        } catch (SecurityException e) {
            return new LoadResult.Failure(
                    LoadError.ACCESS_DENIED
            );
        }
    }
    
    public LoadAllResult loadAll() {
    	File directory = new File(FILE_DIR);
    	
    	if (!directory.exists()) {
    		return new LoadAllResult.NotExits();
    	}
    	
        if (!directory.isDirectory()) {
            return new LoadAllResult.Failure(
            		LoadError.IO_ERROR,
            		null
            );
        }

        File[] files = directory.listFiles();

        if (files == null) {
            return new LoadAllResult.Failure(
                    LoadError.IO_ERROR,
                    null
            );
        }
        
        List<Task> tasks = new ArrayList<>();

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            LoadResult result = load(file.getName());

            if (result instanceof LoadResult.Success success) {
                tasks.add(success.task());

            } else if (result instanceof LoadResult.Failure failure) {
                return new LoadAllResult.Failure(
                		failure.error(),
                        file.getName()
                );
            }
        }

        return new LoadAllResult.Success(tasks);
    }

    public SaveResult save(Task task) {
        File directory = new File(FILE_DIR);

        try {
            if (!directory.exists() && !directory.mkdirs()) {
                return new SaveResult.Failure(
                        SaveError.DIRECTORY_CREATE_FAILED
                );
            }

            File file = new File(directory, fileName(task.getId()));

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(task, writer);
                return new SaveResult.Success();
            }
        } catch (IOException e) {
            return new SaveResult.Failure(
                    SaveError.IO_ERROR
            );
        } catch (SecurityException e) {
            return new SaveResult.Failure(
                    SaveError.ACCESS_DENIED
            );
        }
    }

    public SaveAllResult saveAll(List<Task> tasks) {
        for (Task task : tasks) {
            SaveResult result = save(task);

            if (result instanceof SaveResult.Failure failure) {
                if (failure.error() == SaveError.DIRECTORY_CREATE_FAILED) {
                    return new SaveAllResult.Failure(
                            failure.error(),
                            null
                    );
                }

                return new SaveAllResult.Failure(
                        failure.error(),
                        fileName(task.getId())
                );
            }
        }

        return new SaveAllResult.Success();
    }

    public DeleteResult delete(long id) {
        try {
            File file = new File(FILE_DIR + "/" + fileName(id));

            if (!file.exists()) {
                return new DeleteResult.NotExits();
            }

            if (!file.delete()) {
                return new DeleteResult.Failure(
                        DeleteError.IO_ERROR
                );
            }

            return new DeleteResult.Success();
        } catch (SecurityException e) {
            return new DeleteResult.Failure(
                    DeleteError.ACCESS_DENIED
            );
        }
    }

    private static String fileName(long id) {
        return id + ".json";
    }
}	

