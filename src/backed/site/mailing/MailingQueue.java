package backed.site.mailing;

import backed.site.util.QueueManager;

import java.util.ArrayList;

public class MailingQueue {

    private volatile ArrayList<Runnable> queue = new ArrayList<>();
    private static MailingQueue instance;

    public static MailingQueue getInstance() {
        if (instance == null) instance = new MailingQueue();
        return instance;
    }

    public void addToQueue(Runnable runnable) {
        queue.add(runnable);
    }

    public MailingQueue() {
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
            System.out.println("Mailing Queue Thread Stopped");
        }, "Mailing Queue Thread");
        thread.start();
    }

}
