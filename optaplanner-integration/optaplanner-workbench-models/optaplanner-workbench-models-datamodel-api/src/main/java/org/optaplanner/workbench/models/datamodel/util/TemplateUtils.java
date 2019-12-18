/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.workbench.models.datamodel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.kie.soup.project.datamodel.oracle.DataType;

public final class TemplateUtils {

    private TemplateUtils() {
    }

    public static Collection<InterpolationVariable> extractInterpolationVariables(final String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }
        List<InterpolationVariable> interpolationVariableList = new ArrayList<>();
        int pos = 0;
        while ((pos = text.indexOf("@{",
                                   pos)) != -1) {
            int end = text.indexOf('}',
                                   pos + 2);
            if (end != -1) {
                String varName = text.substring(pos + 2,
                                                end);
                pos = end + 1;
                InterpolationVariable var = new InterpolationVariable(varName,
                                                                      DataType.TYPE_OBJECT);
                interpolationVariableList.add(var);
            } else {
                break;
            }
        }
        return interpolationVariableList;
    }

    public static String substituteTemplateVariable(final String text,
                                                    final Function<String, String> keyToValueFunction) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder resultBuilder = new StringBuilder(text);

        int pos;
        while ((pos = resultBuilder.indexOf("@{")) != -1) {
            int end = resultBuilder.indexOf("}",
                                            pos + 2);
            if (end != -1) {
                final String varName = resultBuilder.substring(pos + 2,
                                                               end);
                final String value = keyToValueFunction.apply(varName);
                if (value == null) {
                    return text;
                }
                resultBuilder.replace(pos,
                                      end + 1,
                                      value);
            } else {
                break;
            }
        }

        return resultBuilder.toString();
    }
}
