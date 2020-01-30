/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.api.model.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.model.tree.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.model.tree.enums.OPERATOR;
import org.kie.pmml.api.model.tree.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;
import org.kie.pmml.api.model.tree.predicates.KiePMMLSimplePredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class KiePMMLNodeTest {

    private final static List<KiePMMLNode> NODES = new ArrayList<>();
    private final String SCORE = "SCORE";
    private final String WILL_PLAY = "will play";
    private final String NO_PLAY = "no play";
    private final String MAY_PLAY = "may play";
    private final String HUMIDITY = "humidity";
    private final String TEMPERATURE = "temperature";
    private final String OUTLOOK = "outlook";
    private final String SUNNY = "sunny";
    private final String WINDY = "windy";
    private final String OVERCAST = "overcast";
    private final String RAIN = "rain";


    private KiePMMLNode WILL_PLAY_NODE;

    /*
    http://dmg.org/pmml/v4-4/TreeModel.html
    <Node score="will play">
      <True/>
      <Node score="will play">
        <SimplePredicate field="outlook" operator="equal" value="sunny"/>
        <Node score="will play">
          <CompoundPredicate booleanOperator="and">
            <SimplePredicate field="temperature" operator="lessThan" value="90"/>
            <SimplePredicate field="temperature" operator="greaterThan" value="50"/>
          </CompoundPredicate>
          <Node score="will play">
            <SimplePredicate field="humidity" operator="lessThan" value="80"/>
          </Node>
          <Node score="no play">
            <SimplePredicate field="humidity" operator="greaterOrEqual" value="80"/>
          </Node>
        </Node>
        <Node score="no play">
          <CompoundPredicate booleanOperator="or">
            <SimplePredicate field="temperature" operator="greaterOrEqual" value="90"/>
            <SimplePredicate field="temperature" operator="lessOrEqual" value="50"/>
          </CompoundPredicate>
        </Node>
      </Node>
      <Node score="may play">
        <CompoundPredicate booleanOperator="or">
          <SimplePredicate field="outlook" operator="equal" value="overcast"/>
          <SimplePredicate field="outlook" operator="equal" value="rain"/>
        </CompoundPredicate>
        <Node score="may play">
          <CompoundPredicate booleanOperator="and">
            <SimplePredicate field="temperature" operator="greaterThan" value="60"/>
            <SimplePredicate field="temperature" operator="lessThan" value="100"/>
            <SimplePredicate field="outlook" operator="equal" value="overcast"/>
            <SimplePredicate field="humidity" operator="lessThan" value="70"/>
            <SimplePredicate field="windy" operator="equal" value="false"/>
          </CompoundPredicate>
        </Node>
        <Node score="no play">
          <CompoundPredicate booleanOperator="and">
            <SimplePredicate field="outlook" operator="equal" value="rain"/>
            <SimplePredicate field="humidity" operator="lessThan" value="70"/>
          </CompoundPredicate>
        </Node>
      </Node>
    </Node>
     */

    @Before
    public void setup() {
        KiePMMLCompoundPredicate compoundPredicate1 = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                                                 Arrays.asList(getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.LESS_THAN, 90),
                                                                                               getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.GREATER_THAN, 50)));
        KiePMMLNode willPlayLevel3 = KiePMMLNode.builder()
                .withScore(WILL_PLAY)
                .withKiePMMLPredicate(getKiePMMLSimplePredicate(HUMIDITY, OPERATOR.LESS_THAN, 80)).build();
        KiePMMLNode noPlayLevel3 = KiePMMLNode.builder()
                .withScore(NO_PLAY)
                .withKiePMMLPredicate(getKiePMMLSimplePredicate(HUMIDITY, OPERATOR.GREATER_OR_EQUAL, 80)).build();

        KiePMMLNode willPlayLevel2 = KiePMMLNode.builder()
                .withScore(WILL_PLAY)
                .withKiePMMLPredicate(compoundPredicate1)
                .withKiePMMLNodes(Arrays.asList(willPlayLevel3, noPlayLevel3))
                .build();

        KiePMMLCompoundPredicate compoundPredicate2 = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.OR,
                                                                                  Arrays.asList(getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.GREATER_OR_EQUAL, 90),
                                                                                                getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.LESS_OR_EQUAL, 50)));
        KiePMMLNode noPlayLevel2WillPlay = KiePMMLNode.builder()
                .withScore(NO_PLAY)
                .withKiePMMLPredicate(compoundPredicate2)
                .build();

        KiePMMLSimplePredicate simplePredicate1 = getKiePMMLSimplePredicate(OUTLOOK, OPERATOR.EQUAL, SUNNY);
        KiePMMLNode willPlayLevel1 = KiePMMLNode.builder()
                .withScore(WILL_PLAY)
                .withKiePMMLPredicate(simplePredicate1)
                .withKiePMMLNodes(Arrays.asList(willPlayLevel2, noPlayLevel2WillPlay))
                .build();


        KiePMMLCompoundPredicate compoundPredicate3 = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                                                  Arrays.asList(getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.GREATER_THAN, 60),
                                                                                                getKiePMMLSimplePredicate(TEMPERATURE, OPERATOR.LESS_THAN, 100),
                                                                                                getKiePMMLSimplePredicate(OUTLOOK, OPERATOR.EQUAL, OVERCAST),
                                                                                                getKiePMMLSimplePredicate(HUMIDITY, OPERATOR.LESS_THAN, 70),
                                                                                                getKiePMMLSimplePredicate(WINDY, OPERATOR.EQUAL, false)));
        KiePMMLNode mayPlayLevel2 = KiePMMLNode.builder()
                .withScore(MAY_PLAY)
                .withKiePMMLPredicate(compoundPredicate3)
                .build();
        KiePMMLCompoundPredicate compoundPredicate4 = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                                                  Arrays.asList(getKiePMMLSimplePredicate(OUTLOOK, OPERATOR.EQUAL, RAIN),
                                                                                                getKiePMMLSimplePredicate(HUMIDITY, OPERATOR.LESS_THAN, 70)));
        KiePMMLNode noPlayLevel2MayPlay = KiePMMLNode.builder()
                .withScore(NO_PLAY)
                .withKiePMMLPredicate(compoundPredicate4)
                .build();


        KiePMMLCompoundPredicate compoundPredicate5 = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.OR,
                                                                                  Arrays.asList(getKiePMMLSimplePredicate(OUTLOOK, OPERATOR.EQUAL, OVERCAST),
                                                                                                getKiePMMLSimplePredicate(OUTLOOK, OPERATOR.EQUAL, RAIN)));
        KiePMMLNode mayPlayLevel1 = KiePMMLNode.builder()
                .withScore(MAY_PLAY)
                .withKiePMMLPredicate(compoundPredicate5)
                .withKiePMMLNodes(Arrays.asList(mayPlayLevel2, noPlayLevel2MayPlay))
                .build();
        WILL_PLAY_NODE = KiePMMLNode.builder()
                .withScore(WILL_PLAY)
                .withKiePMMLNodes(Arrays.asList(willPlayLevel1, mayPlayLevel1))
                .build();
    }




    @Test
    public void builder() {
        KiePMMLNode retrieved = KiePMMLNode.builder()
                .build();
        assertNotNull(retrieved);
        assertNotNull(retrieved.getId());
        assertNull(retrieved.getKiePMMLNodes());
        assertNull(retrieved.getKiePMMLPredicate());
        assertNull(retrieved.getScore());
        retrieved = KiePMMLNode.builder()
                .withKiePMMLNodes(NODES)
                .withScore(SCORE)
                .build();
        assertNotNull(retrieved);
        assertEquals(NODES, retrieved.getKiePMMLNodes());
        assertEquals(SCORE, retrieved.getScore());
    }

    // TODO {gcardosi} re-implement with native drools rules
    /*@Test
    public void evaluateNoField() {
        Optional<String> retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap("NOT_EXISTING", SUNNY));
        commonEvaluate(retrieved, true, null);
    }*/

    // TODO {gcardosi} re-implement with native drools rules
    /*@Test
    public void evaluate() {
        Optional<String> retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(OUTLOOK, SUNNY));
        commonEvaluate(retrieved, false, WILL_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(TEMPERATURE, 55));
        commonEvaluate(retrieved, false, WILL_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(TEMPERATURE, 60));
        commonEvaluate(retrieved, false, WILL_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(HUMIDITY, 75));
        commonEvaluate(retrieved, false, WILL_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(HUMIDITY, 82));
        commonEvaluate(retrieved, false, NO_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(TEMPERATURE, 95));
        commonEvaluate(retrieved, false, NO_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(TEMPERATURE, 50));
        commonEvaluate(retrieved, false, NO_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(OUTLOOK, OVERCAST));
        commonEvaluate(retrieved, false, MAY_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(Collections.singletonMap(OUTLOOK, RAIN));
        commonEvaluate(retrieved, false, MAY_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(mapOf(TEMPERATURE, 95,
                                                  OUTLOOK, OVERCAST,
                                                  HUMIDITY, 65,
                                                  WINDY, false));
        commonEvaluate(retrieved, false, MAY_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(mapOf(TEMPERATURE, 65,
                                                  OUTLOOK, OVERCAST,
                                                  HUMIDITY, 65,
                                                  WINDY, true));
        commonEvaluate(retrieved, false, NO_PLAY);
        retrieved = WILL_PLAY_NODE.evaluate(mapOf(OUTLOOK, RAIN,
                                                  HUMIDITY, 65));
        commonEvaluate(retrieved, false, NO_PLAY);
    }*/

    private KiePMMLSimplePredicate getKiePMMLSimplePredicate(String name, OPERATOR operator, Object value) {
        return KiePMMLSimplePredicate.builder(name, Collections.emptyList(), operator)
                .withValue(value)
                .build();
    }

    private KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR booleanOperator, List<KiePMMLPredicate> predicates) {
        return KiePMMLCompoundPredicate.builder(Collections.emptyList(), booleanOperator)
                .withKiePMMLPredicates(predicates)
                .build();
    }
}