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

package org.kie.internal.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter;

public class QueryFilter extends QueryContext {

    private static final long serialVersionUID = 2830463093685095275L;
    
    @XmlElement
    @XmlSchemaType(name="string")
    private String language ="";

    @XmlElement
    @XmlSchemaType(name="string")
    private String filterParams = "";

    @XmlElement
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, Object> params = new HashMap<String, Object>();

    public QueryFilter() {
        // default constructor for JAXB
    }

    public QueryFilter(QueryFilter orig) {
        super((QueryContext) orig);
        this.language = orig.language;
        this.filterParams = orig.filterParams;
        for( Entry<String, Object> entry : params.entrySet() ) {
           params.put(entry.getKey(), entry.getValue());
        }
    }

    public QueryFilter(int offset, int count) {
        super(offset, count);
    }

    public QueryFilter(int offset, int count, String orderBy, boolean ascending) {
        super(offset, count, orderBy, ascending);
    }

    public QueryFilter(int offset, int count, String filterParams, String language, String orderBy) {
        super(offset, count);
        this.filterParams = filterParams;
        this.language = language;
        this.orderBy = orderBy;
    }

    public QueryFilter( String filterParams, Map<String, Object> params, String orderBy, boolean isAscending) {
        this.filterParams = filterParams;
        this.params = params;
        this.orderBy = orderBy;
        this.ascending = isAscending;
    }

    public QueryFilter( String filterParams, Map<String, Object> params, String orderBy, int offset, int count ) {
        super(offset, count);
        this.filterParams = filterParams;
        this.params = params;
        this.orderBy = orderBy;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage( String language ) {
        this.language = language;
    }

    public String getFilterParams() {
        return filterParams;
    }

    public void setFilterParams( String filterParams ) {
        this.filterParams = filterParams;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams( Map<String, Object> params ) {
        this.params = params;
    }

}
