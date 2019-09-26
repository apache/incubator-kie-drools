/*
 * Copyright 2005 JBoss Inc
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

package org.kie.dmn.core.compiler.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionTable;

public class GeneratorsUtil {

    public static String getDecisionTableName(String dtName, DecisionTable dt) {
        String decisionName;
        if (dt.getParent() instanceof DRGElement ) {
            decisionName = dtName;
        } else {
            if (dt.getId() != null) {
                decisionName = dt.getId();
            } else {
                DMNModelInstrumentedBase cursor = dt;
                List<String> path = new ArrayList<>();
                while (!(cursor instanceof DRGElement)) {
                    int indexOf = cursor.getParent().getChildren().indexOf(cursor);
                    path.add(String.valueOf(indexOf));
                    cursor = cursor.getParent();
                }
                path.add(((DRGElement) cursor).getName());
                decisionName = path.stream().sorted( Collections.reverseOrder()).collect( Collectors.joining("/"));
            }
        }
        return decisionName;
    }
}
