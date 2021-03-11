package com.terraboxstudios.backed.site.util;

import com.terraboxstudios.backed.site.mailing.MailingService;
import com.terraboxstudios.backed.site.mysql.MySQLService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QueueManager implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        MySQLService.getInstance().shutdown();
        MailingService.getInstance().shutdown();
    }

}
