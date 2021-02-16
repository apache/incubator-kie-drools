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

public class Link {

    private Node source;
    private Node target;
    private ReactivityType reactivityType;

    public Link(Node source, Node target, ReactivityType reactivityType ) {
        super();
        this.source = source;
        this.target = target;
        this.reactivityType = reactivityType;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public ReactivityType getReactivityType() {
        return reactivityType;
    }

    public void setReactivityType( ReactivityType reactivityType ) {
        this.reactivityType = reactivityType;
    }

    @Override
    public String toString() {
        return "Link [source=" + source.getRuleName() + ", target=" + target.getRuleName() + ", type=" + reactivityType + "]";
    }
}
