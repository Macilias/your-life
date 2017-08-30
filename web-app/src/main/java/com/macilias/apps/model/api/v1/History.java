package com.macilias.apps.model.api.v1;

import com.macilias.apps.model.EmbeddedDb;
import com.macilias.apps.model.Settings;
import com.macilias.apps.model.anna.api.v1.AnnaRequest;
import com.macilias.apps.model.sidekick.api.v1.*;
import org.apache.jena.query.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.system.Txn;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.macilias.apps.model.Settings.ANNA;
import static com.macilias.apps.model.Settings.SIDEKICK;
import static com.macilias.apps.model.Settings.getPrefix;

/**
 * This class manages past queries and provides a method to get related
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
@Repository
public class History {

    private static final Logger LOG = Logger.getLogger(History.class);

    private List<CustomRequest> previousRequests = new ArrayList<>();

    @Inject
    EmbeddedDb embeddedDb;

    @Inject
    RequestDAO requestDAO;

    /**
     * A query is matching when it served the same intent and had same filter as arguments like WHERE with same values.
     * Some arguments might be also checked by name, like WHAT in last post or
     *
     * @param intent        the Intent of the query, if empty arguments and argumentNames are ignored too and all query's after since date are returned
     * @param arguments     optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param argumentNames optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param exact         defines if all of the argument values must be identical
     *                      if exact == true:
     *                      a argument WHERE query for facebook & twitter does not match a previous query for facebook only
     *                      a argument WHERE query for facebook only does not match a previous query for facebook and twitter
     *                      a argumentName query for WHERE does not match queries with additional filters like SINCE
     *                      if exact == false:
     *                      the presented queries returns true, because the previous query is not exact but RELATED
     * @param since         if present, consider only Request younger than this LocalDateTime
     * @return
     */
    public List<CustomRequest> getMatchingRequests(String intent, List<Argument> arguments, List<ArgumentName> argumentNames, boolean exact, LocalDateTime since) {
        return new ArrayList<>();
    }

    /**
     * A query is matching when it served the same intent and had same filter as arguments like WHERE with same values.
     * Some arguments might be also checked by name, like WHAT in last post or
     *
     * @param intent        the Intent of the query, if empty arguments and argumentNames are ignored too and last query is returned
     * @param arguments     optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param argumentNames optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param exact         defines if all of the argument values must be identical
     *                      if exact == true:
     *                      a argument WHERE query for facebook & twitter does not match a previous query for facebook only
     *                      a argument WHERE query for facebook only does not match a previous query for facebook and twitter
     *                      a argumentName query for WHERE does not match queries with additional filters like SINCE
     *                      if exact == false:
     *                      the presented queries returns true, because the previous query is not exact but RELATED
     * @return
     */
    public Optional<CustomRequest> getLastMatchingRequest(String intent, List<Argument> arguments, List<ArgumentName> argumentNames, boolean exact) {
//        LOG.info("getLastRequestByIntent(): history size: " +previousRequests.size()+ ", searching history for " + intent.name() + " with  " + arguments.size()+ " arguments and " + argumentNames.size() + " argument names" );
//        if (previousRequests.size() == 0) {
//            LOG.info("I don`t know about any previous requests yet.");
//            return Optional.empty();
//        }
//        for (int i = previousRequests.size() - 1; i >= 0 ; i--) {
//            SidekickRequest previousRequest = previousRequests.get(i);
//            if (previousRequest.getIntent() != null && previousRequest.getIntent().equals(intent)){
//                LOG.info("getLastMatchingRequest(): a request with same intent " + intent.name() + " has been found");
//
//                if (current.getOptionalArgument(ArgumentName.WHERE).isPresent()) {
//                    Optional<Argument> previousRequestOptionalWhere = previousRequest.getOptionalArgument(ArgumentName.WHERE);
//                    if (previousRequestOptionalWhere.isPresent()) {
//                        Argument previousArgument = previousRequestOptionalWhere.get();
//                        if (previousArgument.containsAllValues(current.getOptionalArgument(ArgumentName.WHERE).get().getArgumentValues())) {
//                            // if argumentName has been specified it also needs to be part of the last request in case the value matches
//                            LOG.info("getLastMatchingRequest() found this one: " + previousRequest);
//                            return Optional.of(previousRequest);
//                        } else {
//                            LOG.info("it's not " + previousRequest + " on position: " + i + ", the values does not match.");
//                        }
//                    }
//                } else {
//                    // if no WHERE argumentName has been specified this is the last request
//                    LOG.info("getLastMatchingRequest(): found previous matching request fo intent");
//                    return Optional.of(previousRequest);
//                }
//            } else {
//                LOG.info("its not " + previousRequest + " on position: " + i);
//            }
//
//        }
//        LOG.info("Nothing matched your query.");
        return Optional.empty();
    }

//    public Optional<SidekickRequest> getLastRequest() {
//        if (previousRequests.size() > 0) {
//            return Optional.of(previousRequests.get(previousRequests.size() - 1));
//        }
//        return Optional.empty();
//    }

    public void addRequest(CustomRequest request) {
        LOG.info("addRequest(): " + request.toString());
        previousRequests.add(request);
        String namespace = Settings.APPLICATION;
        if (request instanceof SidekickRequest) {
            namespace = SIDEKICK;
        }
        if (request instanceof AnnaRequest) {
            namespace = ANNA;
        }
        requestDAO.save(request, namespace);
    }

    public void performWrite() {
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
        // Add some data while live.
        // Write transaction.
        Txn.executeWrite(dsg, () -> {
            Quad q = SSE.parseQuad("(_ :s :p _:b)");
            dsg.add(q);
        });
    }

    public void performReadAll(String namespace) {
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
        Txn.executeRead(dsg, () -> {
            Dataset ds = DatasetFactory.wrap(dsg);
            String selectAll = "SELECT * { ?s  ?p  ?o}";
            String selectAllNs = "PREFIX ns: <"+namespace+">\n" +
                    "SELECT * {\n" +
                    " ?s  ?p  ?o\n" +
                    " FILTER (isURI(?s) && STRSTARTS(str(?s), str(ns:) ) )\n" +
                    "}";
            try (QueryExecution qExec = QueryExecutionFactory.create(namespace != null ? selectAllNs : selectAll, ds)) {
                ResultSet rs = qExec.execSelect();
                ResultSetFormatter.out(rs);
            }
        });
    }

    public void performRead(String namespace) {
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
        // Query data while live
        // Read transaction.
        Txn.executeRead(dsg, () -> {
            Dataset ds = DatasetFactory.wrap(dsg);
            String queryForRequest3 = "SELECT ?x ?r \n" +
                    "WHERE { " +
                    "   ?x  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <" + namespace + CustomResponse.class.getName() + "> . \n" +
                    "   OPTIONAL { ?x  <" + namespace + CustomResponse.class.getName() + ">  ?r } " +
                    "}";
            String query = getPrefix() + queryForRequest3;
            System.out.println(query);
            try (QueryExecution qExec = QueryExecutionFactory.create(query, ds)) {
                ResultSet rs = qExec.execSelect();
                ResultSetFormatter.out(rs);
            }

//            DatasetAccessor accessor = DatasetAccessorFactory.create(ds);
//            accessor.getModel().query(new SimpleSelector(null, ));

        });
    }

    public void setEmbeddedDb(EmbeddedDb embeddedDb) {
        this.embeddedDb = embeddedDb;
    }

    public void setRequestDAO(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }
}
