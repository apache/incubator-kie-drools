/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryContext extends org.kie.api.runtime.query.QueryContext {

    private static final long serialVersionUID = -3174717972613778773L;

    public QueryContext() {
        super();
    }

    public QueryContext(Integer offset, Integer count, String orderBy, boolean asc) {
        super(offset, count, orderBy, asc);
    }

    public QueryContext(Integer offset, Integer count) {
        super(offset, count);
    }

    public QueryContext(org.kie.api.runtime.query.QueryContext queryContext) {
        super(queryContext);
    }

    public QueryContext(String orderBy, boolean asc) {
        super(orderBy, asc);
    }


}
