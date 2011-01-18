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
 * @author <a href="mailto:ricardo.rojas@bluesoft.cl"> Ricardo Rojas </a>
 * 
 * Represents an application-data tag (nominally at the rule-set level). The idea of this can
 * be extended to other ruleset level settings.
 */
public class Global extends DRLElement
    implements
    DRLJavaEmitter {

    private String identifier;
    private String className;

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @return Returns the varName.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param className
     *            The className to set.
     */
    public void setClassName(final String clazz) {
        this.className = clazz;
    }

    /**
     * @param varName
     *            The varName to set.
     */
    public void setIdentifier(final String namez) {
        this.identifier = namez;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine( "global " + this.className + " " + this.identifier + ";" );
    }
}
