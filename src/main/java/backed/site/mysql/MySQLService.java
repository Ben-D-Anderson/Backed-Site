package backed.site.mysql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQLService {

    private final ExecutorService executorService;
    private static MySQLService instance;

    public static MySQLService getInstance() {
        if (instance == null) instance = new MySQLService();
        return instance;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void addToQueue(Runnable runnable) {
        executorService.submit(runnable);
    }

    public MySQLService() {
        executorService = Executors.newSingleThreadExecutor();
    }

}
