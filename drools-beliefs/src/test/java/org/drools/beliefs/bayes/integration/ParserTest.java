/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.model.Bif;
import org.drools.beliefs.bayes.model.Definition;
import org.drools.beliefs.bayes.model.Network;
import org.drools.beliefs.bayes.model.Variable;
import org.drools.beliefs.bayes.model.XmlBifParser;
import org.drools.beliefs.graph.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {

    @Test
    public void testSprinklerLoadBif() {

        Bif bif = XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"));
        Network network = bif.getNetwork();
        assertThat(network.getName()).isEqualTo("Garden");
        assertThat(network.getProperties().get(0)).isEqualTo("package = org.drools.beliefs.bayes.integration");

        Map<String, Variable> varMap = varToMap( network.getVariables() );
        assertThat(varMap).hasSize(4);

        Variable var = varMap.get("WetGrass");
        assertThat(var.getName()).isEqualTo("WetGrass");
        assertThat(var.getOutComes()).hasSize(2).containsExactly("false", "true");
        assertThat(var.getProperties().get(0)).isEqualTo("position = (0,10)");

        var = varMap.get("Cloudy");
        assertThat(var.getName()).isEqualTo("Cloudy");
        assertThat(var.getOutComes()).hasSize(2).containsExactly("false", "true");
        assertThat(var.getProperties().get(0)).isEqualTo("position = (0,-10)");

        var = varMap.get("Sprinkler");
        assertThat(var.getName()).isEqualTo("Sprinkler");
        assertThat(var.getOutComes()).hasSize(2).containsExactly("false", "true");
        assertThat(var.getProperties().get(0)).isEqualTo("position = (13,0)");

        var = varMap.get("Rain");
        assertThat(var.getName()).isEqualTo("Rain");
        assertThat(var.getOutComes()).hasSize(2).containsExactly("false", "true");
        assertThat(var.getProperties().get(0)).isEqualTo("position = (-12,0)");

        Map<String, Definition> defMap = defToMap( network.getDefinitions() );
        assertThat(defMap).hasSize(4);

        Definition def = defMap.get( "WetGrass");
        assertThat(def.getName()).isEqualTo("WetGrass");
        assertThat(def.getGiven()).hasSize(2).containsExactly("Sprinkler", "Rain");
        assertThat(def.getProbabilities()).isEqualTo("1.0 0.0 0.1 0.9 0.1 0.9 0.01 0.99");

        def = defMap.get( "Cloudy");
        assertThat(def.getName()).isEqualTo("Cloudy");
        assertThat(def.getGiven()).isNull();
        assertThat(def.getProbabilities().trim()).isEqualTo("0.5 0.5");

        def = defMap.get( "Sprinkler");
        assertThat(def.getName()).isEqualTo("Sprinkler");
        assertThat(def.getGiven()).hasSize(1).containsExactly("Cloudy");
        assertThat(def.getProbabilities().trim()).isEqualTo("0.5 0.5 0.9 0.1");

        def = defMap.get( "Rain");
        assertThat(def.getName()).isEqualTo("Rain");
        assertThat(def.getGiven()).isNull();
        assertThat(def.getProbabilities().trim()).isEqualTo("0.5 0.5");
    }

    @Test
    public void testSprinklerBuildBayesNework() {
        Bif bif = XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"));

        BayesNetwork network = XmlBifParser.buildBayesNetwork( bif );
        Map<String, GraphNode<BayesVariable>> map = nodeToMap(network);

        GraphNode<BayesVariable> node = map.get( "WetGrass" );
        BayesVariable wetGrass = node.getContent();
        assertThat(wetGrass.getOutcomes()).containsExactly("false", "true");
        assertThat(wetGrass.getGiven()).hasSize(2).containsExactly("Sprinkler", "Rain");
        assertThat(wetGrass.getProbabilityTable()).isDeepEqualTo(new double[][]{{1.0, 0.0}, {0.1, 0.9}, {0.1, 0.9}, {0.01, 0.99}});

        node = map.get( "Sprinkler" );
        BayesVariable sprinkler = node.getContent();
        assertThat(wetGrass.getOutcomes()).containsExactly("false", "true");
        assertThat(sprinkler.getGiven()).hasSize(1);
        assertThat(sprinkler.getGiven()[0]).isEqualTo("Cloudy");
        assertThat(sprinkler.getProbabilityTable()).isDeepEqualTo(new double[][]{{0.5, 0.5}, {0.9, 0.1}});

        node = map.get( "Cloudy" );
        BayesVariable cloudy = node.getContent();
        assertThat(wetGrass.getOutcomes()).containsExactly("false", "true");
        assertThat(cloudy.getGiven()).isEmpty();
        assertThat(cloudy.getProbabilityTable()).isDeepEqualTo(new double[][]{{0.5, 0.5}});

        node = map.get( "Rain" );
        BayesVariable rain = node.getContent();
        assertThat(rain.getOutcomes()).containsExactly("false", "true");
        assertThat(rain.getGiven()).isEmpty();
        assertThat(rain.getProbabilityTable()).isDeepEqualTo(new double[][]{{0.5, 0.5}});
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
