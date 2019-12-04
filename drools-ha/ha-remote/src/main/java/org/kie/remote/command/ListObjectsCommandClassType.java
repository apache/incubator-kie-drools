/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.remote.command;

import java.io.Serializable;

public class ListObjectsCommandClassType extends ListObjectsCommand implements VisitableCommand,
                                                                               Serializable {

    private Class clazzType;

    public ListObjectsCommandClassType(){}

    public ListObjectsCommandClassType(String entryPoint, Class clazzType) {
        super(entryPoint);
        this.clazzType = clazzType;
    }

    public Class getClazzType() { return clazzType; }

    @Override
    public void accept(VisitorCommand visitor) { visitor.visit(this); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ListObjectCommandClassType{");
        sb.append("clazzType=").append(clazzType);
        sb.append('}');
        return sb.toString();
    }
}
