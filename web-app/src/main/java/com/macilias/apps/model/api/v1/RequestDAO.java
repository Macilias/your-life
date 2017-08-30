package com.macilias.apps.model.api.v1;

import com.macilias.apps.controller.APIUtil;
import com.macilias.apps.model.EmbeddedDb;
import com.macilias.apps.model.sidekick.api.v1.*;
import org.apache.commons.lang3.Validate;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb.TDB;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
@Repository
public class RequestDAO {

    @Inject
    EmbeddedDb embeddedDb;

    public void save(CustomRequest request, String namespace) {
        // upload the resulting model
        Dataset ds = embeddedDb.getDs();
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();

        OntModel m = ModelFactory.createOntologyModel();
        OntClass ontClass = m.createClass(namespace + CustomRequest.class.getSimpleName());
        Individual individual = ontClass.createIndividual(namespace + request.hashCode());
        if (request.getIntent() != null) {
            individual.addProperty(m.getProperty(namespace + APIUtil.INTENT), request.getIntent());
        }
        for (Argument argument : request.getArguments()) {
            ArgumentName argumentName = argument.getArgumentName();
            String defaultValue = argument.getDefaultValue();
            individual.addProperty(m.getProperty(namespace + "default_" + argumentName.name()), defaultValue);
            for (String value : argument.getArgumentValues()) {
                individual.addProperty(m.getProperty(namespace + argumentName.name()), value);
            }
        }
        if (request.getDate() != null) {
            individual.addProperty(m.getProperty(namespace + LocalDateTime.class.getSimpleName()), request.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (request.getChannel() != null) {
            individual.addProperty(m.getProperty(namespace + "requested_over_channel"), request.getChannel());
        }
        if (request.getResponse() != null) {
            individual.addProperty(m.getProperty(namespace + CustomResponse.class.getSimpleName()), request.getResponse().toString());
        }

        DatasetAccessor accessor = DatasetAccessorFactory.create(ds);

        Txn.executeWrite(dsg, () -> {
            accessor.add(m);
            TDB.sync(dsg);
            dsg.commit();
        });
    }

    public List<CustomRequest> getMatchingRequest() {
//        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
//        // Query data while live
//        // Read transaction.
//        Txn.executeRead(dsg, () -> {
//            Dataset ds = DatasetFactory.wrap(dsg);
//            String queryForRequest3 = "SELECT ?x ?r \n" +
//                    "WHERE { " +
//                    "   ?x  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <" + SidekickRequest.class.getName() + "> . \n" +
//                    "   OPTIONAL { ?x  <" + Settings.namespace + SidekickResponse.class.getName() + ">  ?r } " +
//                    "}";
//            String query = getPrefix() + queryForRequest3;
//            System.out.println(query);
//            try (QueryExecution qExec = QueryExecutionFactory.create(query, ds)) {
//                ResultSet rs = qExec.execSelect();
//                ResultSetFormatter.out(rs);
//            }
//
////            DatasetAccessor accessor = DatasetAccessorFactory.create(ds);
////            accessor.getModel().query(new SimpleSelector(null, ));
//
//        });
        // TODO implement
        return null;
    }

    public void setEmbeddedDb(EmbeddedDb embeddedDb) {
        this.embeddedDb = embeddedDb;
    }
}
