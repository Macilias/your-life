package com.macilias.apps.view;

import com.macilias.apps.model.EmbeddedDb;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application object for your web application.
 */
public class WicketApplication extends WebApplication {

    @SpringBean
    EmbeddedDb embeddedDb;
    FusekiServer db;

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        //System.out.println(embeddedDb);
        if (db == null) {
            db = embeddedDb.getServer();
            db.start();
        }
        return ApiTestPage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.cfg.xml");
        final SpringComponentInjector injector = new SpringComponentInjector(this, ctx);
        injector.inject(this);
        getComponentInstantiationListeners().add(injector);

        // add your configuration here

        getResourceSettings().getResourceFinders().add(new WebApplicationPath(getServletContext(), ""));
        getResourceSettings().getResourceFinders().add(new WebApplicationPath(getServletContext(), "/"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db == null) {
            db = embeddedDb.getServer();
        }
        db.stop();
    }
}
