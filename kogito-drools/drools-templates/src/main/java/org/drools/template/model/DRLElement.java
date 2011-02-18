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
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * The LayerSupertype for this model/parse tree.
 */
public abstract class DRLElement {

    private String _comment;

    public void setComment(final String comment) {
        this._comment = comment;
    }

    String getComment() {
        return this._comment;
    }

    boolean isCommented() {
        return (this._comment != null && !("".equals( this._comment )));
    }

}
