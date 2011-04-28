/*
 * Copyright 2010 JBoss Inc
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

package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * age < 40 & > 30
 */
public class StringConnectiveDescr extends RestrictionDescr {

    private static final long                     serialVersionUID = 510l;

    public final static RestrictionConnectiveType AND              = RestrictionConnectiveType.AND;
    public final static RestrictionConnectiveType OR               = RestrictionConnectiveType.OR;

    private RestrictionConnectiveType             connective;
    private List<Object>                restrictions;

    public StringConnectiveDescr(final RestrictionConnectiveType connective) {
        super();
        this.connective = connective;
        this.restrictions = Collections.emptyList();
    }

    public RestrictionConnectiveType getConnective() {
        return this.connective;
    }

    public void add(Object object) {
        if ( (object instanceof StringConnectiveDescr) && ((StringConnectiveDescr) object).connective == this.connective ) {
            if ( this.restrictions == Collections.EMPTY_LIST ) {
                this.restrictions = new ArrayList<Object>();
            }
            this.restrictions.addAll( ((StringConnectiveDescr) object).getRestrictions() );
        } else {
            this.restrictions.add( object );
        }
    }

    public List<Object> getRestrictions() {
        return this.restrictions;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append( "( " );
        for ( Iterator it = this.restrictions.iterator(); it.hasNext(); ) {
            buf.append( it.next().toString() );
            if ( it.hasNext() ) {
                buf.append( this.connective.toString() );
            }
        }
        buf.append( "  )" );
        return buf.toString();
    }

    /**
     * The connective types that can be used for a restriction
     */
    public static enum RestrictionConnectiveType {
        AND {
            public String toString() {
                return "&&";
            }
        },
        OR {
            public String toString() {
                return "||";
            }
        };
    }
}
