/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

public class FieldTemplateDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;
    private String            name;
    private String            classType;

    public FieldTemplateDescr() {
        this( null,
              null );
    }

    public FieldTemplateDescr(final String name,
                              final String type) {
        this.name = name;
        this.classType = type;
    }

    public String getName() {
        return this.name;
    }

    public String getClassType() {
        return this.classType;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(final String classType) {
        this.classType = classType;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
