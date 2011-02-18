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

package org.drools.template.model;

/**
 * This represents a RHS fragement. A rule may have many of these, or just one.
 * They are all mushed together.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 */
public class Consequence extends DRLElement
    implements
    DRLJavaEmitter {

    private String _snippet;

    /**
     * @param _snippet
     *            The _snippet to set.
     */
    public void setSnippet(final String snippet) {
        this._snippet = snippet;
    }

    public String getSnippet() {
        return this._snippet;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine( "\t\t" + this._snippet );
    }

}
