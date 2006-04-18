package org.drools.leaps;
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





import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Leaps Tuple implementation
 * 
 * @author Alexander Bagerman
 */
class LeapsTuple
    implements
    Tuple,
    Serializable {
    private static final long        serialVersionUID       = 1L;

    private final PropagationContext context;

    private boolean                  readyForActivation;

    private final FactHandleImpl[]   factHandles;

    private FactHandleImpl[]         blockingNotFactHandles = null;

    private FactHandleImpl[]         existsFactHandles      = null;

    private Set                      logicalDependencies;

    private Activation               activation;

    private final LeapsRule          leapsRule;

    /**
     * agendaItem parts
     */
    LeapsTuple(FactHandleImpl factHandles[],
               LeapsRule leapsRule,
               PropagationContext context) {
        this.factHandles = factHandles;
        this.leapsRule = leapsRule;
        this.context = context;

        if ( this.leapsRule != null ) {
            if ( this.leapsRule.containsNotColumns() ) {
                this.blockingNotFactHandles = new FactHandleImpl[this.leapsRule.getNotColumnConstraints().length];
                for ( int i = 0; i < this.blockingNotFactHandles.length; i++ ) {
                    this.blockingNotFactHandles[i] = null;
                }
            }
            if ( this.leapsRule.containsExistsColumns() ) {
                this.existsFactHandles = new FactHandleImpl[this.leapsRule.getExistsColumnConstraints().length];
                for ( int i = 0; i < this.existsFactHandles.length; i++ ) {
                    this.existsFactHandles[i] = null;
                }
            }
        }
        this.readyForActivation = (this.leapsRule == null || !this.leapsRule.containsExistsColumns());
    }

    /**
     * get rule that caused this tuple to be generated
     * 
     * @return rule
     */
    LeapsRule getLeapsRule() {
        return this.leapsRule;
    }

    /**
     * Determine if this tuple depends upon a specified object.
     * 
     * @param handle
     *            The object handle to test.
     * 
     * @return <code>true</code> if this tuple depends upon the specified
     *         object, otherwise <code>false</code>.
     * 
     * @see org.drools.spi.Tuple
     */
    public boolean dependsOn(FactHandle handle) {
        for ( int i = 0, length = this.factHandles.length; i < length; i++ ) {
            if ( handle.equals( this.factHandles[i] ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public FactHandle get(int col) {
        return this.factHandles[col];
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public FactHandle get(Declaration declaration) {
        return this.get( declaration.getColumn() );
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public FactHandle[] getFactHandles() {
        return this.factHandles;
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    /**
     * to determine if "active" agendaItem needs to be valid from the queue on
     * fact retraction
     * 
     * @return indicator if agendaItem was null'ed
     */
    boolean isActivationNull() {
        return this.activation == null;
    }

    Activation getActivation() {
        return this.activation;
    }

    /**
     * @see java.lang.Object
     */
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof LeapsTuple) ) {
            return false;
        }

        FactHandle[] thatFactHandles = ((LeapsTuple) object).getFactHandles();
        if ( thatFactHandles.length != this.factHandles.length ) {
            return false;
        }

        for ( int i = 0, length = this.factHandles.length; i < length; i++ ) {
            if ( !this.factHandles[i].equals( thatFactHandles[i] ) ) {
                return false;
            }

        }
        return true;
    }

    /**
     * indicates if exists conditions complete and there is no blocking facts
     * 
     * @return
     */
    boolean isReadyForActivation() {
        return this.readyForActivation;
    }

    /**
     * @see java.lang.Object
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer( "LeapsTuple [" + this.leapsRule.getRule().getName() + "] " );

        for ( int i = 0, length = this.factHandles.length; i < length; i++ ) {
            buffer.append( ((i == 0) ? "" : ", ") + this.factHandles[i] );
        }

        if ( this.existsFactHandles != null ) {
            buffer.append( "\nExists fact handles by position" );
            for ( int i = 0, length = this.existsFactHandles.length; i < length; i++ ) {
                buffer.append( "\nposition " + i ).append( this.existsFactHandles[i] );
            }
        }
        if ( this.blockingNotFactHandles != null ) {
            buffer.append( "\nblockingNot fact handles by position" );
            for ( int i = 0, length = this.blockingNotFactHandles.length; i < length; i++ ) {
                buffer.append( "\nposition " + i ).append( this.blockingNotFactHandles[i] );
            }
        }

        return buffer.toString();
    }

    void setBlockingNotFactHandle(FactHandleImpl factHandle,
                                  int index) {
        this.readyForActivation = false;
        this.blockingNotFactHandles[index] = factHandle;
    }

    void removeBlockingNotFactHandle(int index) {
        this.blockingNotFactHandles[index] = null;
        this.setReadyForActivation();
    }

    void setExistsFactHandle(FactHandleImpl factHandle,
                             int index) {
        this.existsFactHandles[index] = factHandle;
        this.setReadyForActivation();
    }

    void removeExistsFactHandle(int index) {
        this.existsFactHandles[index] = null;
        this.setReadyForActivation();
    }

    private void setReadyForActivation() {
        this.readyForActivation = true;

        if ( this.blockingNotFactHandles != null ) {
            for ( int i = 0, length = this.blockingNotFactHandles.length; i < length; i++ ) {
                if ( this.blockingNotFactHandles[i] != null ) {
                    this.readyForActivation = false;
                    return;
                }
            }
        }

        if ( this.existsFactHandles != null ) {
            for ( int i = 0, length = this.existsFactHandles.length; i < length; i++ ) {
                if ( this.existsFactHandles[i] == null ) {
                    this.readyForActivation = false;
                    return;
                }
            }
        }
    }

    PropagationContext getContext() {
        return this.context;
    }

    void addLogicalDependency(FactHandle handle) {
        if ( this.logicalDependencies == null ) {
            this.logicalDependencies = new HashSet();
        }
        this.logicalDependencies.add( handle );
    }

    Iterator getLogicalDependencies() {
        if ( this.logicalDependencies != null ) {
            return this.logicalDependencies.iterator();
        }
        return null;
    }
}