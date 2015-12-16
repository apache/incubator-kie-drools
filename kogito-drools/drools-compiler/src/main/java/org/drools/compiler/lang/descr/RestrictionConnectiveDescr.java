/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * age < 40 & > 30
 */
public class RestrictionConnectiveDescr extends RestrictionDescr {

    private static final long                     serialVersionUID = 510l;

    public final static RestrictionConnectiveType AND              = RestrictionConnectiveType.AND;
    public final static RestrictionConnectiveType OR               = RestrictionConnectiveType.OR;

    private RestrictionConnectiveType             connective;
    private List<RestrictionDescr>                restrictions;

    public RestrictionConnectiveDescr() { }

    public RestrictionConnectiveDescr(final RestrictionConnectiveType connective) {
        super();
        this.connective = connective;
        this.restrictions = Collections.emptyList();
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        super.readExternal( in );
        this.connective = (RestrictionConnectiveType) in.readObject();
        this.restrictions = (List<RestrictionDescr>) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( connective );
        out.writeObject( restrictions );
    }

    public RestrictionConnectiveType getConnective() {
        return this.connective;
    }

    public void addRestriction(RestrictionDescr restriction) {
        if ( this.restrictions == Collections.EMPTY_LIST ) {
            this.restrictions = new ArrayList<RestrictionDescr>();
        }
        this.restrictions.add( restriction );
    }

    public void addOrMerge(RestrictionDescr restriction) {
        if ( (restriction instanceof RestrictionConnectiveDescr) && ((RestrictionConnectiveDescr) restriction).connective == this.connective ) {
            if ( this.restrictions == Collections.EMPTY_LIST ) {
                this.restrictions = new ArrayList<RestrictionDescr>();
            }
            this.restrictions.addAll( ((RestrictionConnectiveDescr) restriction).getRestrictions() );
        } else {
            this.addRestriction( restriction );
        }
    }

    public List<RestrictionDescr> getRestrictions() {
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
