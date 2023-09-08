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
package org.drools.ruleunits.impl.conf;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.event.rule.RuleEventListener;


public class RuleConfigImpl implements RuleConfig {

    private final List<AgendaEventListener> agendaEventListeners;
    private final List<RuleRuntimeEventListener> ruleRuntimeEventListeners;
    private final List<RuleEventListener> ruleEventListeners;

    public RuleConfigImpl() {
        agendaEventListeners = new ArrayList<>();
        ruleRuntimeEventListeners = new ArrayList<>();
        ruleEventListeners = new ArrayList<>();
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners() {
        return agendaEventListeners;
    }

    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeListeners() {
        return ruleRuntimeEventListeners;
    }

    @Override
    public List<RuleEventListener> getRuleEventListeners() {
        return ruleEventListeners;
    }

}
