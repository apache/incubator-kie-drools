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

public class Node {

    private String packageName;
    private String ruleName;
    private Rule rule;

    private Set<Link> incomingLinks;
    private Set<Link> outgoingLinks;

    public Node(Rule rule) {
        this.packageName = rule.getPkg();
        this.ruleName = rule.getName();
        this.rule = rule;
        incomingLinks = new HashSet<>();
        outgoingLinks = new HashSet<>();
    }

    public String getFqdn() {
        return packageName + "." + ruleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Rule getRule() {
        return rule;
    }

    public Set<Link> getIncomingLinks() {
        return incomingLinks;
    }

    public Set<Link> getOutgoingLinks() {
        return outgoingLinks;
    }

    public static void linkNodes(Node source, Node target, Link.Type type) {
        Link link = new Link(source, target, type);
        source.getOutgoingLinks().add(link);
        target.getIncomingLinks().add(link);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (packageName == null) {
            if (other.packageName != null)
                return false;
        } else if (!packageName.equals(other.packageName))
            return false;
        if (ruleName == null) {
            if (other.ruleName != null)
                return false;
        } else if (!ruleName.equals(other.ruleName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Node [packageName=" + packageName + ", ruleName=" + ruleName + ", outgoingLinks=" + outgoingLinks + "]";
    }
}
