package com.macilias.apps.view;

import com.macilias.apps.controller.Anna;
import com.macilias.apps.controller.AnnaImpl;
import com.macilias.apps.controller.service.utils.ApplicationContextHolder;
import com.macilias.apps.model.Sentence;
import com.macilias.apps.model.Settings;
import com.macilias.apps.model.anna.api.v1.AnnaRequest;
import com.macilias.apps.model.anna.api.v1.AnnaResponse;
import com.macilias.apps.model.api.v1.History;
import com.macilias.apps.model.sidekick.api.v1.ArgumentName;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;


import com.googlecode.wicket.kendo.ui.console.Console;
import com.googlecode.wicket.kendo.ui.form.TextField;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;

import java.util.Optional;
import java.util.UUID;

public class ApiTestPage extends WebPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ApiTestPage.class);

    private final FeedbackMessagesModel errorFeedbackMessagesModel;

    /**
     * A template for creating a nice SPARQL query
     */
    private static final String UPDATE_TEMPLATE =
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                    + "INSERT DATA"
                    + "{ <http://example/%s>    dc:title    \"A new book\" ;"
                    + "                         dc:creator  \"A.N.Other\" ." + "}   ";

    public ApiTestPage(final PageParameters parameters) {
        super(parameters);
        this.errorFeedbackMessagesModel = newErrorFeedbackMessagesModel(this);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add a form with an onSubmit implementation that sets a message
        Form<Void> form = new Form<>("form");
        add(form);

        // Add a form with an onSubmit implementation that sets a message
        Form<Void> anna = new Form<>("anna");
        add(anna);

        // Console //
        final Console console = new Console("console");
        add(console);

        // TextField //
        final TextField<String> textField = new TextField<>("message", Model.of(""));
        textField.setOutputMarkupId(true);
        anna.add(textField.setRequired(true));

        // Buttons //
        anna.add(new AjaxButton("button") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String sentence = textField.getModelObject();
                AnnaRequest annaRequest = new AnnaRequest(ApiTestPage.class.getSimpleName());
                annaRequest.addArgument(ArgumentName.WHAT, sentence);

                console.info(target, sentence);
                Anna anna = (AnnaImpl) ApplicationContextHolder.getSpringApplicationContext().getBean("anna");
                textField.setModelObject(null);
                target.add(textField);

                Optional<String> answer = anna.consume(new Sentence(sentence));
                if (answer.isPresent()) {
                    console.error(target, answer.get());
                    AnnaResponse annaResponse = answer.map(AnnaResponse::new).orElseGet(() -> new AnnaResponse(""));
                    annaRequest.setResponse(annaResponse);
                }
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.addRequest(annaRequest);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                for (FeedbackMessage message : errorFeedbackMessagesModel.getObject())
                {
                    console.error(target, message.getMessage());
                    message.markRendered();
                }
            }
        });

        Button button1 = new Button("create1") {
            @Override
            public void onSubmit() {
                info("button1.onSubmit executed");
                addToHttpFuseki();
            }
        };
        form.add(button1);

        Button button2 = new Button("query1") {
            @Override
            public void onSubmit() {
                info("button2.onSubmit executed");
                queryHttpsFuseki();
            }
        };
        button2.setDefaultFormProcessing(false);
        form.add(button2);

        Button button3 = new Button("create2") {
            @Override
            public void onSubmit() {
                info("button3.onSubmit executed");
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.performWrite();
            }
        };
        form.add(button3);

        Button button4 = new Button("query2") {
            @Override
            public void onSubmit() {
                info("button4.onSubmit executed");
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.performRead(Settings.SIDEKICK);
            }
        };
        button4.setDefaultFormProcessing(false);
        form.add(button4);

        Button button5 = new Button("query3") {
            @Override
            public void onSubmit() {
                info("button5.onSubmit executed");
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.performReadAll(Settings.ANNA);
            }
        };
        button5.setDefaultFormProcessing(false);
        form.add(button5);

        Button button6 = new Button("query4") {
            @Override
            public void onSubmit() {
                info("button6.onSubmit executed");
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.performReadAll(Settings.SIDEKICK);
            }
        };
        button6.setDefaultFormProcessing(false);
        form.add(button6);

        Button button7 = new Button("query5") {
            @Override
            public void onSubmit() {
                info("button5.onSubmit executed");
                History history = (History) ApplicationContextHolder.getSpringApplicationContext().getBean("history");
                history.performReadAll(null);
            }
        };
        button7.setDefaultFormProcessing(false);
        form.add(button7);

    }

    protected static FeedbackMessagesModel newErrorFeedbackMessagesModel(WebPage page) {
        return new FeedbackMessagesModel(page, new ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR));
    }

    private void addToHttpFuseki() {
        LOG.info("addToHttpFuseki()");
        //Add a new book to the collection
        String id = UUID.randomUUID().toString();
        LOG.info(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(String.format(UPDATE_TEMPLATE, id)),
                "http://localhost:" + Settings.FUSEKI_PORT + "/ds/update");
        upp.execute();
    }

    private void queryHttpsFuseki() {
        LOG.info("queryHttpsFuseki()");
        //Query the collection, dump output
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:" + Settings.FUSEKI_PORT + "/ds/query", "SELECT * WHERE {?x ?r ?y}");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();
    }

}
