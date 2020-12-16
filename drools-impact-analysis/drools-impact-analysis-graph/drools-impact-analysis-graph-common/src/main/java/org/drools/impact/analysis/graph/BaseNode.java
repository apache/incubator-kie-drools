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

package org.drools.impact.analysis.graph;

import java.util.HashSet;
import java.util.Set;

import org.drools.impact.analysis.model.Rule;

public abstract class BaseNode implements Node {

    protected Status status;

    protected String packageName;
    protected String ruleName;
    protected Rule rule;

    protected Set<Link> incomingLinks;
    protected Set<Link> outgoingLinks;

    public BaseNode(Rule rule) {
        this.packageName = rule.getPkg();
        this.ruleName = rule.getName();
        this.rule = rule;
        incomingLinks = new HashSet<>();
        outgoingLinks = new HashSet<>();
        status = Status.NONE;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getFqdn() {
        return packageName + "." + ruleName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public Set<Link> getIncomingLinks() {
        return incomingLinks;
    }

    @Override
    public Set<Link> getOutgoingLinks() {
        return outgoingLinks;
    }
}
