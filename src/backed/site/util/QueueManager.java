package backed.site.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QueueManager implements ServletContextListener {

    public static volatile boolean running = true;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        running = false;
    }

}
