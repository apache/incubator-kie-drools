package org.drools.beliefs.bayes.assembler;

import java.util.ArrayList;

import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.model.Bif;
import org.drools.beliefs.bayes.model.XmlBifParser;
import org.drools.compiler.builder.AbstractResourceProcessor;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderError;

public class JunctionTreeProcessor extends AbstractResourceProcessor<JunctionTree> {

    public JunctionTreeProcessor(Resource resource) {
        super(resource);
    }

    @Override
    public void process() {
        BayesNetwork network;
        JunctionTreeBuilder builder;
        ArrayList<KnowledgeBuilderError> errors = new ArrayList<>();

        Bif bif = XmlBifParser.loadBif(getResource(), errors);
        if (bif == null) {
            errors.forEach(this::appendError);
            return;
        }

        try {
            network = XmlBifParser.buildBayesNetwork(bif);
        } catch (Exception e) {
            appendError(new BayesNetworkAssemblerError(getResource(), "Unable to parse opening Stream:\n" + e.toString()));
            return;
        }

        try {
            builder = new JunctionTreeBuilder(network);
        } catch (Exception e) {
            appendError(new BayesNetworkAssemblerError(getResource(), "Unable to build Junction Tree:\n" + e.toString()));
            return;
        }

        setProcessedResource(
                builder.build(
                        getResource(),
                        network.getPackageName(),
                        network.getName()));
    }
}
