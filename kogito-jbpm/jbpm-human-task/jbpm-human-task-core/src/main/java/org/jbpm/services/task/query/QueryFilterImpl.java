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

import java.util.HashMap;
import java.util.Map;
import org.kie.internal.task.api.QueryFilter;

/**
 *
 * @author salaboy
 */
public class QueryFilterImpl implements QueryFilter{

    private int offset = 0;
    private int count = 0;
    private boolean singleResult = false;
    private String language ="";
    private String orderBy = "";
    private String filterParams = "";
    private boolean ascending;
    private Map<String, Object> params = new HashMap<String, Object>();

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
    public int getOffset() {
        return offset;
    }

    @Override
    public int getCount() {
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

    public boolean isAscending() {
      return ascending;
    }

}
