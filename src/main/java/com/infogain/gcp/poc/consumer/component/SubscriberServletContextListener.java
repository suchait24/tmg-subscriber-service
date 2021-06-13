package com.infogain.gcp.poc.consumer.component;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/*
USAGE of this class -
to perform the operations like persist batch id, incase of jvm shutdown.
 */

@Slf4j
public class SubscriberServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("context is ready!!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Context is being destroyed.");
    }
}
