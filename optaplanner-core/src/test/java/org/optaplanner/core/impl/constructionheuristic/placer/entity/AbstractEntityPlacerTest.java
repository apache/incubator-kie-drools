/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.util.Iterator;

import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;

public abstract class AbstractEntityPlacerTest {

    public static void assertEntityPlacement(Placement placement, String entityCode, String... valueCodes) {
        Iterator<Move> iterator = placement.iterator();
        assertThat(iterator).isNotNull();
        for (String valueCode : valueCodes) {
            assertThat(iterator.hasNext()).isTrue();
            ChangeMove<?> move = (ChangeMove) iterator.next();
            assertCode(entityCode, move.getEntity());
            assertCode(valueCode, move.getToPlanningValue());
        }
        assertThat(iterator.hasNext()).isFalse();
    }

    public static void assertValuePlacement(Placement placement, String valueCode, String... entityCodes) {
        Iterator<Move> iterator = placement.iterator();
        assertThat(iterator).isNotNull();
        for (String entityCode : entityCodes) {
            assertThat(iterator.hasNext()).isTrue();
            ChangeMove<?> move = (ChangeMove) iterator.next();
            assertCode(entityCode, move.getEntity());
            assertCode(valueCode, move.getToPlanningValue());
        }
        assertThat(iterator.hasNext()).isFalse();
    }

}
