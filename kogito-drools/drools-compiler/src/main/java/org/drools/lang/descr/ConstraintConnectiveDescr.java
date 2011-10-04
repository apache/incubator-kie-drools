/*
 * Copyright 2011 JBoss Inc
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
import java.util.List;

/**
 * A descriptor to represent logical connectives in constraints, like
 * &&, || and ^. 
 */
public class ConstraintConnectiveDescr extends BaseDescr {
    private static final long serialVersionUID = 520l;
    
    private ConnectiveType     connective       = ConnectiveType.AND;
    private List<BaseDescr>    descrs           = new ArrayList<BaseDescr>();

    public ConstraintConnectiveDescr() {
    }
    
    public ConstraintConnectiveDescr( ConnectiveType connective ) {
        this.connective = connective;
    }
    
    public static ConstraintConnectiveDescr newAnd() {
        return new ConstraintConnectiveDescr( ConnectiveType.AND );
    }

    public static ConstraintConnectiveDescr newOr() {
        return new ConstraintConnectiveDescr( ConnectiveType.OR );
    }

    public static ConstraintConnectiveDescr newXor() {
        return new ConstraintConnectiveDescr( ConnectiveType.XOR );
    }

    public static ConstraintConnectiveDescr newIncAnd() {
        return new ConstraintConnectiveDescr( ConnectiveType.INC_AND );
    }

    public static ConstraintConnectiveDescr newIncOr() {
        return new ConstraintConnectiveDescr( ConnectiveType.INC_OR );
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }

    public List<BaseDescr> getDescrs() {
        return this.descrs;
    }
    
    public ConnectiveType getConnective() {
        return connective;
    }

    public void setConnective( ConnectiveType connective ) {
        this.connective = connective;
    }

    public void addOrMerge(final BaseDescr baseDescr) {
        if ( baseDescr instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr con = (ConstraintConnectiveDescr)baseDescr;
            if( con.getConnective().equals( this.connective ) ) {
                for( BaseDescr descr : con.getDescrs() ) {
                    addDescr( descr );
                }
            } else {
                addDescr( con );
            }
        } else {
            addDescr( baseDescr );
        }
    }
    
    @Override
    public String toString() {
        return "["+this.connective+" "+descrs+" ]";
    }

    @Override
    public void copyLocation(BaseDescr d) {
        super.copyLocation(d);
        if (descrs.size() == 1 && descrs.get(0) instanceof BindingDescr) {
            descrs.get(0).copyLocation(d);
        }
    }
}
