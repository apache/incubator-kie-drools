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
import java.util.Arrays;

public class ListObjectsCommandNamedQuery extends ListObjectsCommand implements VisitableCommand, Serializable {

    private String namedQuery;
    private String objectName;
    private Serializable[] params;

    public ListObjectsCommandNamedQuery(){}

    public ListObjectsCommandNamedQuery(String entryPoint, String namedQuery,
                                        String objectName,
                                        Serializable... params) {
        super(entryPoint);
        this.namedQuery = namedQuery;
        this.objectName = objectName;
        this.params = params;
    }

    public String getNamedQuery() { return namedQuery; }

    public String getObjectName() {
        return objectName;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public void accept(VisitorCommand visitor) { visitor.visit(this); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ListObjectsCommandNamedQuery{");
        sb.append("namedQuery='").append(namedQuery).append('\'');
        sb.append(", objectName='").append(objectName).append('\'');
        sb.append(", params=").append(Arrays.toString(params));
        sb.append('}');
        return sb.toString();
    }
}
