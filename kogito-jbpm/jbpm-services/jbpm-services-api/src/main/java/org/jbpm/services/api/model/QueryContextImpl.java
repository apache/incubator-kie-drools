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

package org.jbpm.services.api.model;

import java.io.Serializable;

import org.kie.internal.query.QueryContext;

public class QueryContextImpl implements QueryContext, Serializable {

	private static final long serialVersionUID = -5456354468631560912L;

	private Integer offset = 0;
	private Integer count = 10;
	private String orderBy;
	private Boolean ascending;
	
	public QueryContextImpl() {
		
	}
	
	public QueryContextImpl(Integer offset, Integer count) {
		this.offset = offset;
		this.count = count;
	}
	
	public QueryContextImpl(String orderBy, boolean asc) {
		this.orderBy = orderBy;
		this.ascending = asc;
	}
	
	public QueryContextImpl(Integer offset, Integer count, String orderBy, boolean asc) {
		this.offset = offset;
		this.count = count;
		this.orderBy = orderBy;
		this.ascending = asc;
	}

	@Override
	public Integer getOffset() {
		return offset;
	}

	@Override
	public Integer getCount() {
		return count;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Boolean isAscending() {
		return ascending;
	}

}
