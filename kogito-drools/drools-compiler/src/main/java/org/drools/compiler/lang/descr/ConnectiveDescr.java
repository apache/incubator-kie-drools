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

import org.drools.core.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * age < 40 & > 30
 */
public class ConnectiveDescr extends RestrictionDescr {

    private static final long                     serialVersionUID = 510l;

    public final static RestrictionConnectiveType AND              = RestrictionConnectiveType.AND;
    public final static RestrictionConnectiveType OR               = RestrictionConnectiveType.OR;

    private RestrictionConnectiveType             connective;
    private List<Object>                          children;
    private String                                prefix;
    private boolean                               paren;

    public ConnectiveDescr() { }

    public ConnectiveDescr(final RestrictionConnectiveType connective) {
        super();
        this.connective = connective;
        this.children = Collections.emptyList();
        this.paren = true;
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        super.readExternal( in );
        this.connective = (RestrictionConnectiveType) in.readObject();
        this.children = (List<Object>) in.readObject();
        this.prefix = (String) in.readObject();
        this.paren = in.readBoolean();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject(connective);
        out.writeObject( children );
        out.writeObject( prefix );
        out.writeBoolean( paren );
    }

    public RestrictionConnectiveType getConnective() {
        return this.connective;
    }
    
    public boolean isParen() {
        return paren;
    }

    public void setParen(boolean paren) {
        this.paren = paren;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void add(ConnectiveDescr restriction) {
        if ( this.children == Collections.EMPTY_LIST ) {
            this.children = new ArrayList<Object>(2);
        }
        this.children.add( restriction );
    }    
    
    public void add(String restriction) {
        if ( this.children == Collections.EMPTY_LIST ) {
            this.children = new ArrayList<Object>(2);
        }        
        this.children.add( restriction );
    }      

    public List<Object> getRestrictions() {
        return this.children;
    }
    
    public void buildExpression(StringBuilder sb ) {
        if ( !StringUtils.isEmpty( prefix )) {
            sb.append( prefix );      
            sb.append( " " );            
        }

        for ( int i = 0; i < this.children.size(); i++ ) {
            if ( isParen() && children.get( i )  instanceof ConnectiveDescr ) {
                sb.append( "(" );
                ((ConnectiveDescr)children.get( i ) ).buildExpression( sb );
                sb.append( ")" );
            } else {
                sb.append( children.get( i ) );
            }
            
            if ( i < this.children.size() -1 ) {
                sb.append( " " );
                sb.append( connective );
                sb.append( " " );
            }
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buildExpression( buf );
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
