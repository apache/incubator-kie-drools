/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.query;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="kieQueryContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryContext implements Serializable {

    private static final long serialVersionUID = -3174717972613778773L;

    @XmlElement
    @XmlSchemaType(name="int")
    protected Integer offset = 0;

    @XmlElement
    @XmlSchemaType(name="int")
    protected Integer count = 10;

    @XmlElement
    @XmlSchemaType(name="int")
    protected String orderBy;

    @XmlElement
    @XmlSchemaType(name="boolean")
    protected Boolean ascending;

    public QueryContext() {
        // default JAXB constructor
    }

    public QueryContext(QueryContext queryContext) {
        this.offset = queryContext.offset;
        this.count = queryContext.count;
        this.orderBy = queryContext.orderBy;
        this.ascending = queryContext.ascending;
    }

    public QueryContext(Integer offset, Integer count) {
        this.offset = offset;
        this.count = count;
    }

    public QueryContext(String orderBy, boolean asc) {
        this.orderBy = orderBy;
        this.ascending = asc;
    }

    public QueryContext(Integer offset, Integer count, String orderBy, boolean asc) {
        this.offset = offset;
        this.count = count;
        this.orderBy = orderBy;
        this.ascending = asc;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset( Integer offset ) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount( Integer count ) {
        this.count = count;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy( String orderBy ) {
        this.orderBy = orderBy;
    }

    public Boolean isAscending() {
        return ascending;
    }

    public void setAscending( Boolean ascending ) {
        this.ascending = ascending;
    }

    public void clear() {
       this.ascending = null;
       this.count = 10;
       this.offset = 0;
       this.orderBy = null;
    }
}
