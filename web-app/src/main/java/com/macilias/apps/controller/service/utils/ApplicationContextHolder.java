package com.macilias.apps.controller.service.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class ApplicationContextHolder implements ServletContextListener {

    private static final AtomicReference<ApplicationContext> appContext = new AtomicReference<ApplicationContext>();
    private static final AtomicReference<ServletContext> servletContext = new AtomicReference<ServletContext>();

    /**
     * UNIT-TESTING ONLY.
     *
     * @param ctx
     * @return
     */
    public static void setApplicationContext(ApplicationContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("ApplicationContext cannot be NULL");
        }
        appContext.set(ctx);
    }

    /**
     * Returns the application's <code>WebApplicationContext</code> .
     *
     * @return
     * @throws IllegalStateException If retrieving the Spring context failed
     */
    public static ApplicationContext getSpringApplicationContext() {
        final ApplicationContext result = appContext.get();
        if (result != null) {
            return result;
        }

        if (servletContext.get() == null) {
            throw new IllegalStateException("Unable to retrieve ApplicationContext, not servlet context ? Make sure this listener is added to the web.xml");
        }

        final ApplicationContext ctx = (ApplicationContext) servletContext.get().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if (ctx == null) {
            throw new IllegalStateException("Unable to retrieve ApplicationContext , Spring failed to initialize ? Make sure this listener is added to the web.xml");
        }

        appContext.set(ctx);
        return ctx;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        appContext.set(null);
        servletContext.set(null);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext.set(event.getServletContext());
    }

}

