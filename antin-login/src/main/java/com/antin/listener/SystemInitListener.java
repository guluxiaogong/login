package com.antin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SystemInitListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(SystemInitListener.class);

    public void contextDestroyed(ServletContextEvent event) {
        // Do nothing
    }

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute("appctx", servletContext.getContextPath());
        servletContext.setAttribute("sysName", servletContext.getInitParameter("sysName"));
        logger.info("Listener start...{}", servletContext.getAttribute("sysName"));
    }

}
