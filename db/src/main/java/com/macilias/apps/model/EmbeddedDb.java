package com.macilias.apps.model;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.Serializable;

/**
 * This class is responsible for handling access and lifecycle to the Fuseki Server
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class EmbeddedDb implements ServletContextListener, Serializable {

    private static final Logger LOG = Logger.getLogger(EmbeddedDb.class);

    FusekiServer server;
    Dataset ds;

    public FusekiServer getServer() {
        if (server == null) {
            server = FusekiServer.create()
                    .setPort(Settings.FUSEKI_PORT)
                    .add("/ds", getDs())
                    .build();
        }
        return server;
    }

    public Dataset getDs() {
        if (ds == null) {
            ds = DatasetFactory.createTxnMem();
        }
        return ds;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOG.info("contextInitialized()");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LOG.info("contextDestroyed() stopping the db");
        if (server != null) {
            server.stop();
        }
    }

    public static void silance() {
        LogCtl.setLevel(Fuseki.serverLogName, "WARN");
        LogCtl.setLevel(Fuseki.actionLogName, "WARN");
        LogCtl.setLevel(Fuseki.requestLogName, "WARN");
        LogCtl.setLevel(Fuseki.adminLogName, "WARN");
        LogCtl.setLevel("org.eclipse.jetty", "WARN");
    }
}
