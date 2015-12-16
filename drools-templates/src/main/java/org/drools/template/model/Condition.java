/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.template.model;

/**
 * This class represents a single LHS item (which will be the same as a line in
 * traditional DRL).
 */
public class Condition extends DRLElement
        implements
        DRLJavaEmitter {

    public String _snippet;

    /**
     * @param snippet The snippet to set.
     */
    public void setSnippet(final String snippet) {
        this._snippet = snippet;
    }

    public String getSnippet() {
        return this._snippet;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine("\t\t" + this._snippet);
    }
}
