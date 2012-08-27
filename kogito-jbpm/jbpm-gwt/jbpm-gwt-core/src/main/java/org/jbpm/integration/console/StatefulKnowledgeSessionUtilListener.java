package org.jbpm.integration.console;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatefulKnowledgeSessionUtilListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(StatefulKnowledgeSessionUtilListener.class);
    
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // do nothing
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Dispose statefull knowledge session...");
        StatefulKnowledgeSessionUtil.dispose();
    }


}
