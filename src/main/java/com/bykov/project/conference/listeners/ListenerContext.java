package com.bykov.project.conference.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.services.ServiceMailSend;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ListenerContext implements ServletContextListener {
    private ScheduledExecutorService executorService;
    private final Logger LOG = LogManager.getLogger(ServiceMailSend.class);
    @Override
    public void contextInitialized(ServletContextEvent event) {
        executorService = Executors.newSingleThreadScheduledExecutor();

        LOG.info("Sending emails...");
       // executorService.scheduleAtFixedRate(new MailSendService(),0,1, TimeUnit.DAYS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        executorService.shutdownNow();
    }
}
