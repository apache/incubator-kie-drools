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
package org.kie.dmn.signavio;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class MultiInstanceDecisionLogicTest {
	
	private static final String ITERATOR = "I1";
	private static final String TOP_LEVEL = "A";
	
	@Mock
	private MultiInstanceDecisionLogic mid;
	
	@Mock
	private DMNModel model;


    @BeforeEach
    void setup() {
		MockitoAnnotations.openMocks(this);
		doReturn(TOP_LEVEL).when(mid).getTopLevelDecisionId();
		doReturn(ITERATOR).when(mid).getIteratorShapeId();
		
		DMNBaseNode a = decision("A");
		DMNBaseNode b = decision("B");
		DMNBaseNode c = decision("C");
		DMNBaseNode d = decision("D");
		
		DMNBaseNode i1 = input("I1");
		DMNBaseNode i2 = input("I2");
		
		connect(a, asList(b, c));
		connect(b, Collections.singletonList(i1));
		connect(c, asList(i1, d));
		connect(d, Collections.singletonList(i2));
	}
	
	
	private void connect(DMNBaseNode node, List<DMNNode> dependencies) {
		Map<String, DMNNode> deps = dependencies.stream().collect(toMap(DMNNode::getId, identity()));
		doReturn(deps).when(node).getDependencies();
	}
	
	
	private DMNBaseNode decision(String id) {
		DecisionNodeImpl decision = mock(DecisionNodeImpl.class);
		doReturn(id).when(decision).getId();
		doReturn(decision).when(model).getDecisionById(id);
		return decision;
	}
	
	
	private DMNBaseNode input(String id) {
		InputDataNodeImpl input = mock(InputDataNodeImpl.class);
		doReturn(id).when(input).getId();
		doReturn(input).when(model).getInputById(id);
		return input;
	}


    @Test
    void thatFindAllChildElements_withMid_collectsCorrectChildsAndSkipsExternals() {
		// Arrange
		MultiInstanceDecisionLogic.MIDDependenciesProcessor processor =
				new MultiInstanceDecisionLogic.MIDDependenciesProcessor(mid, model);
		
		// Act
		Collection<DMNNode> innerNodes = processor.findAllChildElements();
		
		// Assert
		Collection<String> ids = innerNodes.stream().map(DMNNode::getId).collect(toList());
		assertThat(ids).contains("A", "B", "C", "I1");
		assertThat(ids).hasSize(4);
	}
	
}