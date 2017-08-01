/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.query.jpa.builder.impl;

import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.internal.query.data.QueryData;

public abstract class AbstractQueryBuilderImpl<T> {

    protected QueryWhere queryWhere = new QueryWhere();

    public QueryWhere getQueryWhere() {
        return queryWhere;
    }

    protected <P> void addRangeParameter( String listId, String name, P parameter, boolean start) {
        if( parameter == null ) {
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        this.queryWhere.addRangeParameter(listId, parameter, start);
    }

    protected <P> void addRangeParameters( String listId, String name, P paramMin, P paramMax) {
        if( paramMin == null && paramMax == null ) {
            throw new IllegalArgumentException("At least one range parameter for " + name + " criteria is required." );
        }
        this.queryWhere.addRangeParameters(listId, paramMin, paramMax);
    }

    private <P> void addParameter( String listId, P... parameter ) {
        this.queryWhere.addParameter(listId, parameter);
    }

    protected void addLongParameter( String listId, String name, long [] parameter) {
        if( parameter == null ) {
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        Long [] wrapArr = new Long[parameter.length];
        for( int i = 0; i < parameter.length; ++i ) {
            wrapArr[i] = parameter[i];
        }
        addParameter(listId, wrapArr);
    }

    protected void addIntParameter( String listId, String name, int [] parameter) {
        if( parameter == null ) {
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        Integer [] wrapArr = new Integer[parameter.length];
        for( int i = 0; i < parameter.length; ++i ) {
            wrapArr[i] = parameter[i];
        }
        addParameter(listId, wrapArr);
    }

    protected <P> void addObjectParameter(String listId, String name, P... parameter) {
        if( parameter == null ) {
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        for( int i = 0; i < parameter.length; ++i ) {
           if( parameter[i] == null ) {
               throw new IllegalArgumentException("A null " + name + " criteria (argument " + i + ") is invalid." );
           }
        }
        addParameter(listId, parameter);
    }

    public final T union() {
        this.queryWhere.setToUnion();
        return (T) this;
    }

    public final T or() {
        this.queryWhere.setToUnion();
        return (T) this;
    }

    public final T intersect() {
        this.queryWhere.setToIntersection();
        return (T) this;
    }

    public final T and() {
        this.queryWhere.setToIntersection();
        return (T) this;
    }

    public T newGroup() {
        this.queryWhere.newGroup();
        return (T) this;
    }

    public T endGroup() {
        this.queryWhere.endGroup();
        return (T) this;
    }

    public final T regex() {
        this.queryWhere.setToLike();
        return (T) this;
    }

    public final T like() {
        this.queryWhere.setToLike();
        return (T) this;
    }

    public final T equals() {
        this.queryWhere.setToNormal();
        return (T) this;
    }

    public T clear() {
        this.queryWhere.clear();
        return (T) this;
    }

    public final T maxResults( int maxResults ) {
        if( maxResults < 0 ) {
            throw new IllegalArgumentException( "A max results criteria of less than 0 is invalid." );
        }
        this.queryWhere.setCount(maxResults);
        return (T) this;
    }

    public final T offset( int offset ) {
        if( offset < 0 ) {
            throw new IllegalArgumentException( "An offset criteria of less than 0 is invalid." );
        }
        this.queryWhere.setOffset(offset);
        return (T) this;
    }
}
