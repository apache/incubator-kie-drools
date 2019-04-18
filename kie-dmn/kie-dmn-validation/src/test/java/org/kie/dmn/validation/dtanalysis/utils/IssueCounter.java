/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

public class IssueCounter {

    public static List<DMNMessage> collectOverlaps(final DTAnalysis analysis) {
        final ArrayList<DMNMessage> result = new ArrayList<>();
        result.addAll(collect(DMNMessageType.DECISION_TABLE_OVERLAP, analysis));
        result.addAll(collect(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE, analysis));
        result.addAll(collect(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY, analysis));
        return result;
    }

    public static List<DMNMessage> collect(final DMNMessageType decisionTableOverlapHitpolicyUnique,
                                           final DTAnalysis analysis) {
        List<DMNMessage> dmnMessages = analysis.asDMNMessages();
        return dmnMessages.stream().filter(x -> x.getMessageType().equals(decisionTableOverlapHitpolicyUnique)).collect(Collectors.toList());
    }
}
