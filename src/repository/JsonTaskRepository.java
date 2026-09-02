package repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import model.Task;

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
}	

