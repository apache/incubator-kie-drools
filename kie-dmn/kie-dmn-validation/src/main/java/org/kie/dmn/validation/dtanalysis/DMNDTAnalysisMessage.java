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
package org.kie.dmn.validation.dtanalysis;

import java.util.Collection;

import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

public class DMNDTAnalysisMessage extends DMNMessageImpl {

    private final DTAnalysis analysis;
    private final Collection<Integer> rules;

    public DMNDTAnalysisMessage(DTAnalysis analysis, Severity severity, String message, DMNMessageType messageType) {
        this(analysis, severity, message, messageType, null);
    }

    public DMNDTAnalysisMessage(DTAnalysis analysis, Severity severity, String message, DMNMessageType messageType, Collection<Integer> rules) {
        super(severity, message, messageType, analysis.getSource());
        this.analysis = analysis;
        this.rules = rules;
    }


    public DTAnalysis getAnalysis() {
        return analysis;
    }

    public Collection<Integer> getRules() {
       return rules;
    }

}
