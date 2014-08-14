/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.task.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.jbpm.services.task.impl.model.xml.adapter.JaxbStringObjectMap;
import org.jbpm.services.task.impl.model.xml.adapter.StringObjectMapXmlAdapter;
import org.kie.internal.query.QueryFilter;


@XmlRootElement(name = "query-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryFilterImpl implements QueryFilter, Serializable {

	private static final long serialVersionUID = -6228032000092881493L;

    @XmlElement 
	private int offset = 0;
    
    @XmlElement 
    private int count = 0;
    
    @XmlElement 
    private boolean singleResult = false;
    
    @XmlElement 
    @XmlSchemaType(name="string")
    private String language ="";
    
    @XmlElement 
    @XmlSchemaType(name="string")
    private String orderBy = "";
    
    @XmlElement 
    @XmlSchemaType(name="string")
    private String filterParams = "";
    
    @XmlElement 
    @XmlSchemaType(name="boolean")
    private Boolean ascending;

    @XmlElement 
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    private Map<String, Object> params = new HashMap<String, Object>();

    public QueryFilterImpl() {
        // default constructor for JAXB
    }

    public QueryFilterImpl(int offset, int count) {
        this.offset = offset;
        this.count = count;
    }

    public QueryFilterImpl(int offset, int count, boolean singleResult) {
        this.offset = offset;
        this.count = count;
        this.singleResult = singleResult;
    }

    public QueryFilterImpl(int offset, int count, String orderBy, boolean ascending) {
      this.offset = offset;
      this.count = count;
      this.orderBy = orderBy;
      this.ascending = ascending;
    }

    public QueryFilterImpl(int offset, int count, boolean singleResult, String filterParams, String language, String orderBy) {
        this.offset = offset;
        this.count = count;
        this.singleResult = singleResult;
        this.filterParams = filterParams;
        this.language = language;
        this.orderBy = orderBy;
    }
    
    public QueryFilterImpl( String filterParams, Map<String, Object> params, String orderBy, boolean isAscending) {
        this.filterParams = filterParams;
        this.params = params;
        this.orderBy = orderBy;
        this.ascending = isAscending;
    }
    
    public QueryFilterImpl( String filterParams, Map<String, Object> params, String orderBy,int offset, int count ) {
        this.filterParams = filterParams;
        this.params = params;
        this.orderBy = orderBy;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public boolean isSingleResult() {
        return singleResult;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    public String getFilterParams() {
        return filterParams;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Boolean isAscending() {
      return ascending;
    }

}
