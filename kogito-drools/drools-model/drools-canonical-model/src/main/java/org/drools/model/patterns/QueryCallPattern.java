/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.patterns;

import java.util.Arrays;

import org.drools.model.Argument;
import org.drools.model.Condition;
import org.drools.model.QueryDef;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.QueryCallViewItem;

public class QueryCallPattern implements Condition, ModelComponent {

    private final QueryDef query;
    private final boolean open;
    private final Argument<?>[] arguments;

    public QueryCallPattern( QueryCallViewItem queryCallView ) {
        this(queryCallView.getQuery(), queryCallView.isOpen(), queryCallView.getArguments());
    }

    public QueryCallPattern( QueryDef query, boolean open, Argument<?>... arguments ) {
        this.query = query;
        this.open = open;
        this.arguments = arguments;
    }

    public QueryDef getQuery() {
        return query;
    }

    public Argument<?>[] getArguments() {
        return arguments;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public Type getType() {
        return Type.QUERY;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        throw new UnsupportedOperationException( "org.drools.model.patterns.QueryCallPattern.getBoundVariables -> TODO" );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof QueryCallPattern) ) return false;

        QueryCallPattern that = ( QueryCallPattern ) o;

        if ( open != that.open ) return false;
        if ( query != null ? !query.equals( that.query ) : that.query != null ) return false;
        return Arrays.equals( arguments, that.arguments );
    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (open ? 1 : 0);
        result = 31 * result + Arrays.hashCode( arguments );
        return result;
    }

    @Override
    public boolean isEqualTo( ModelComponent other ) {
        if ( this == other ) return true;
        if ( !(other instanceof QueryCallPattern) ) return false;

        QueryCallPattern that = ( QueryCallPattern ) other;

        return open == that.open && ModelComponent.areEqualInModel( query, that.query ) && ModelComponent.areEqualInModel( arguments, that.arguments );
    }

    @Override
    public String toString() {
        return "QueryCallPattern (" +
                "arguments: " + Arrays.toString(arguments) + ", " +
                "open: " + open  + ", " +
                "query: " + query + ")";
    }
}
