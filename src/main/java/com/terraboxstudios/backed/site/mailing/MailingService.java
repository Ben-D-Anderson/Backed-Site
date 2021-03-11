package com.terraboxstudios.backed.site.mailing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailingService {

    private final ExecutorService executorService;
    private static MailingService instance;

    public static MailingService getInstance() {
        if (instance == null) instance = new MailingService();
        return instance;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void addToQueue(Runnable runnable) {
        executorService.submit(runnable);
    }

    public MailingService() {
        executorService = Executors.newSingleThreadExecutor();
    }

}
