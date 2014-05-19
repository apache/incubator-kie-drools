package org.drools.beliefs.bayes.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.assembler.BayesNetworkAssemblerError;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.beliefs.graph.impl.EdgeImpl;
import org.drools.compiler.compiler.ParserError;
import org.drools.core.io.internal.InternalResource;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderErrors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XmlBifParser {

    public static Bif loadBif(Resource resource, KnowledgeBuilderErrors errors) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
        } catch (IOException e) {
            errors.add( new ParserError(resource, "Exception opening Stream:\n" + e.toString(), 0, 0) );
            return null;
        }

        try {
            String encoding = resource instanceof InternalResource ? ((InternalResource) resource).getEncoding() : null;
            XStream xstream;
            if (encoding != null) {
                xstream = new XStream(new DomDriver(encoding));
            } else {
                xstream = new XStream();
            }

            initXStream(xstream);

            Bif bif = (Bif) xstream.fromXML(is);
            return bif;
        } catch (Exception e) {
            errors.add( new BayesNetworkAssemblerError(resource, "Unable to parse opening Stream:\n" + e.toString()) );
            return null;
        }
    }

    public static Bif loadBif(URL url) {
        XStream xstream = new XStream();
        initXStream( xstream );

        Bif bif = (Bif) xstream.fromXML(url);
        return bif;
    }

    private static void initXStream(XStream xstream) {
        xstream.processAnnotations(Bif.class);
        xstream.processAnnotations(Network.class);
        xstream.processAnnotations(Probability.class);
        xstream.processAnnotations(Definition.class);
    }

    public static BayesNetwork buildBayesNetwork(Bif bif) {
        String name =  bif.getNetwork().getName();
        String packageName = "default";
        List<String> props = bif.getNetwork().getProperties();
        if (props != null ) {
            for ( String prop : props ) {
                prop = prop.trim();
                if (prop.startsWith("package") ) {
                    packageName = prop.substring( prop.indexOf('=') + 1).trim();
                }
            }
        }

        BayesNetwork graph = new BayesNetwork(name, packageName);

        Map<String, GraphNode<BayesVariable>> map = new HashMap<String, GraphNode<BayesVariable>>();
        for (Definition def : bif.getNetwork().getDefinitions()) {
            GraphNode<BayesVariable> node = graph.addNode();
            BayesVariable var = buildVariable(def, bif.getNetwork(), node.getId());
            node.setContent( var );
            map.put( var.getName(), node );
        }

        for(Entry<String, GraphNode<BayesVariable>>  entry : map.entrySet()) {
            GraphNode<BayesVariable> node = entry.getValue();
            BayesVariable var = node.getContent();
            if ( var.getGiven() != null && var.getGiven().length > 0 )  {
                for ( String given : var.getGiven() ) {
                    GraphNode<BayesVariable> givenNode = map.get( given );
                    EdgeImpl e = new EdgeImpl();
                    e.setOutGraphNode(givenNode);
                    e.setInGraphNode(node);
                }
            }
        }

        return graph;
    }

    private static BayesVariable buildVariable(Definition def, Network network, int id) {
        List<String> outcomes = new ArrayList();
        getOutcomesByVariable(network, def.getName(), outcomes);
        List<String> given = (def.getGiven() == null) ? Collections.<String>emptyList() : def.getGiven();

        return new BayesVariable<String>(def.getName(), id, outcomes.toArray( new String[ outcomes.size()] ),
                                         getProbabilities(def.getProbabilities(), outcomes), given.toArray(new String[given.size()]) );
    }

    private static void getOutcomesByVariable(Network network, String nameDefinition, List<String> outcomes) {
        for (Variable var : network.getVariables()) {
            if (var.getName().equals(nameDefinition)) {
                for (String outcome : var.getOutComes()) {
                    outcomes.add(outcome);
                }
            }
        }
    }

    private static double[][] getProbabilities(String table,List<String> outcomes) {
        table = table.trim();

        String[] values = table.split(" ");
        double probabilities[][] = new double[values.length/2][outcomes.size()];


        int k = 0;
        for(int i = 0, length = values.length/2; i < length; i++){
            for(int j = 0; j < outcomes.size(); j++){
                probabilities[i][j] = Double.valueOf(values[k++]);
            }
        }
        return probabilities;
    }

    private static double[][] getPosition(String stringPosition, double[][] position) {
        if (stringPosition != null) {
            stringPosition = clearStringPostion(stringPosition);
            int i = 0;
            int j = 0;
            for (String pos : stringPosition.split(",")) {
                position[i][j] = Double.parseDouble(pos);
                if (i < j) {
                    i += 1;
                }
                j += 1;
            }
        }
        return null;
    }

    private static String clearStringPostion(String stringPosition){
        stringPosition = stringPosition.replace("position", "");
        stringPosition = stringPosition.replace("=", "");
        stringPosition = stringPosition.replace("(", "");
        stringPosition = stringPosition.replace(")", "");
        stringPosition = stringPosition.trim();
        return stringPosition;
    }

//    private void setIncomingNodes(BayesNetwork bayesNetwork){
//        for(BayesVariable node : bayesNetwork.getNodos()){
//            if(node.getGiven()!=null && !node.getGiven().isEmpty()){
//                node.setIncomingNodes(this.getNodesByGiven(node.getGiven(), bayesNetwork.getNodos()));
//            }
//        }
//    }
//
//    private List<BayesVariable> getNodesByGiven(List<String> given, List<BayesVariable> nodes){
//        List<BayesVariable> listIncoming = new ArrayList();
//        for(String giv : given){
//            for(BayesVariable node : nodes){
//                if(node.getName().equals(giv)){
//                    listIncoming.add(node);
//                    break;
//                }
//            }
//        }
//        return listIncoming;
//    }


}
