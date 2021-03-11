package backed.site.util;

import backed.site.mailing.MailingService;
import backed.site.mysql.MySQLService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QueueManager implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        MySQLService.getInstance().shutdown();
        MailingService.getInstance().shutdown();
    }

}
