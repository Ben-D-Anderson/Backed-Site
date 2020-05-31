package backed.site.mysql;

import backed.site.util.QueueManager;

import java.util.ArrayList;

public class MySQLQueue {

    private volatile ArrayList<Runnable> queue = new ArrayList<>();
    private static MySQLQueue instance;

    public static MySQLQueue getInstance() {
        if (instance == null) instance = new MySQLQueue();
        return instance;
    }

    public void addToQueue(Runnable runnable) {
        queue.add(runnable);
    }

    public MySQLQueue() {
        Thread thread = new Thread(() -> {
            while (QueueManager.running) {
                if (queue.size() > 0) {
                    queue.get(0).run();
                    queue.remove(0);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "MySQL Queue Thread");
        thread.start();
    }

}
