package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.model.Bif;
import org.drools.beliefs.bayes.model.Definition;
import org.drools.beliefs.bayes.model.Network;
import org.drools.beliefs.bayes.model.Variable;
import org.drools.beliefs.bayes.model.XmlBifParser;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class ParserTest {

    @Test
    public void testSprinklerLoadBif() {

        Bif bif = (Bif) XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"));
        Network network = bif.getNetwork();
        assertEquals( "Garden", network.getName() );
        assertEquals( "package = org.drools.beliefs.bayes.integration", network.getProperties().get(0) );

        Map<String, Variable> varMap = varToMap( network.getVariables() );
        assertEquals(4, varMap.size());

        Variable var = varMap.get("WetGrass");
        assertEquals("WetGrass", var.getName());
        assertEquals(2, var.getOutComes().size());
        assertEquals(var.getOutComes(), Arrays.asList(new String[]{"false", "true"}));
        assertEquals("position = (0,10)", var.getProperties().get(0));

        var = varMap.get("Cloudy");
        assertEquals( "Cloudy", var.getName());
        assertEquals(2, var.getOutComes().size());
        assertEquals(var.getOutComes(), Arrays.asList(new String[]{"false", "true"}));
        assertEquals( "position = (0,-10)", var.getProperties().get(0) );

        var = varMap.get("Sprinkler");
        assertEquals( "Sprinkler", var.getName());
        assertEquals( 2, var.getOutComes().size() );
        assertEquals(var.getOutComes(), Arrays.asList(new String[]{"false", "true"}));
        assertEquals("position = (13,0)", var.getProperties().get(0) );

        var = varMap.get("Rain");
        assertEquals( "Rain", var.getName());
        assertEquals( 2, var.getOutComes().size() );
        assertEquals(var.getOutComes(), Arrays.asList(new String[]{"false", "true"}));
        assertEquals("position = (-12,0)", var.getProperties().get(0) );

        Map<String, Definition> defMap = defToMap( network.getDefinitions() );
        assertEquals( 4, defMap.size() );

        Definition def = defMap.get( "WetGrass");
        assertEquals( "WetGrass", def.getName());
        assertEquals( 2, def.getGiven().size());
        assertEquals(def.getGiven(), Arrays.asList(new String[]{"Sprinkler", "Rain"}));
        assertEquals("1.0 0.0 0.1 0.9 0.1 0.9 0.01 0.99", def.getProbabilities());

        def = defMap.get( "Cloudy");
        assertEquals( "Cloudy", def.getName());
        assertNull(def.getGiven());
        assertEquals("0.5 0.5", def.getProbabilities().trim());

        def = defMap.get( "Sprinkler");
        assertEquals( "Sprinkler", def.getName());
        assertEquals( 1, def.getGiven().size());
        assertEquals("Cloudy", def.getGiven().get(0));
        assertEquals("0.5 0.5 0.9 0.1", def.getProbabilities().trim());

        def = defMap.get( "Rain");
        assertEquals( "Rain", def.getName() );
        assertNull( def.getGiven());
        assertEquals("0.5 0.5", def.getProbabilities().trim());
    }

    @Test
    public void testSprinklerBuildBayesNework() {
        Bif bif = (Bif) XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"));

        BayesNetwork network = XmlBifParser.buildBayesNetwork( bif );
        Map<String, GraphNode<BayesVariable>> map = nodeToMap(network);

        GraphNode<BayesVariable> node = map.get( "WetGrass" );
        BayesVariable wetGrass = node.getContent();
        assertEquals(Arrays.asList(new String[]{"false", "true"}), Arrays.asList(wetGrass.getOutcomes()));
        assertEquals( 2, wetGrass.getGiven().length );
        assertEquals( Arrays.asList( wetGrass.getGiven() ), Arrays.asList( new String[] { "Sprinkler", "Rain" }) );
        assertTrue( Arrays.deepEquals( new double[][] { { 1.0, 0.0 }, { 0.1, 0.9 }, { 0.1, 0.9 }, { 0.01, 0.99 } }, wetGrass.getProbabilityTable() ) );

        node = map.get( "Sprinkler" );
        BayesVariable sprinkler = node.getContent();
        assertEquals(Arrays.asList(new String[]{"false", "true"}), Arrays.asList(sprinkler.getOutcomes()));
        assertEquals( 1, sprinkler.getGiven().length );
        assertEquals( "Cloudy", sprinkler.getGiven()[0]);
        assertTrue( Arrays.deepEquals( new double[][] { {0.5, 0.5}, { 0.9, 0.1} }, sprinkler.getProbabilityTable() ) );

        node = map.get( "Cloudy" );
        BayesVariable cloudy = node.getContent();
        assertEquals(Arrays.asList(new String[]{"false", "true"}), Arrays.asList(cloudy.getOutcomes()));
        assertEquals(0, cloudy.getGiven().length);
        assertTrue( Arrays.deepEquals( new double[][] { {0.5, 0.5} }, cloudy.getProbabilityTable() ) );

        node = map.get( "Rain" );
        BayesVariable rain = node.getContent();
        assertEquals(Arrays.asList(new String[]{"false", "true"}), Arrays.asList(rain.getOutcomes()));
        assertEquals( 0, rain.getGiven().length );
        assertTrue( Arrays.deepEquals( new double[][] { {0.5, 0.5} }, rain.getProbabilityTable() ) );
    }

    Map<String, GraphNode<BayesVariable>> nodeToMap(BayesNetwork network) {
        Map<String, GraphNode<BayesVariable>> map = new HashMap<String, GraphNode<BayesVariable>>();
        for ( GraphNode<BayesVariable> node : network ) {
            map.put( node.getContent().getName(), node );
        }
        return map;
    }


    public Map<String, Variable> varToMap(List<Variable> list) {
        Map<String, Variable> map = new HashMap<String, Variable>();
        for ( Variable var : list ) {
            map.put( var.getName(), var );
        }
        return map;
    }

    public Map<String, Definition> defToMap(List<Definition> list) {
        Map<String, Definition> map = new HashMap<String, Definition>();
        for ( Definition def : list ) {
            map.put( def.getName(), def );
        }
        return map;
    }
}
