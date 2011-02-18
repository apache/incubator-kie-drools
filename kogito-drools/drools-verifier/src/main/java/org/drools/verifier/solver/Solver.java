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

package org.drools.verifier.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.data.VerifierComponent;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 */
class Solver {

    private List<Set<VerifierComponent>> possibilityLists = new ArrayList<Set<VerifierComponent>>();
    private Solver                       subSolver        = null;
    private boolean                      isChildExists    = false;
    private boolean                      isChildForall    = false;
    private boolean                      isChildNot       = false;

    private OperatorDescrType            type;

    protected Solver(OperatorDescrType type) {
        this.type = type;
    }

    public void addOperator(OperatorDescrType type) {
        if ( subSolver != null ) {
            subSolver.addOperator( type );
        } else {
            subSolver = new Solver( type );
        }
    }

    /**
     * Add new descr.
     * 
     * @param descr
     */
    public void add(VerifierComponent descr) {

        if ( subSolver != null ) {
            subSolver.add( descr );
        } else {
            if ( type == OperatorDescrType.AND ) {
                if ( possibilityLists.isEmpty() ) {
                    possibilityLists.add( new HashSet<VerifierComponent>() );
                }
                for ( Set<VerifierComponent> set : possibilityLists ) {
                    set.add( descr );
                }
            } else if ( type == OperatorDescrType.OR ) {
                Set<VerifierComponent> set = new HashSet<VerifierComponent>();
                set.add( descr );
                possibilityLists.add( set );
            }
        }
    }

    /**
     * Ends subSolvers data collection.
     * 
     */
    protected void end() {
        if ( subSolver != null && subSolver.subSolver == null ) {
            if ( type == OperatorDescrType.AND ) {
                if ( possibilityLists.isEmpty() ) {
                    possibilityLists.add( new HashSet<VerifierComponent>() );
                }

                List<Set<VerifierComponent>> newPossibilities = new ArrayList<Set<VerifierComponent>>();

                List<Set<VerifierComponent>> sets = subSolver.getPossibilityLists();
                for ( Set<VerifierComponent> possibilityList : possibilityLists ) {

                    for ( Set<VerifierComponent> set : sets ) {
                        Set<VerifierComponent> newSet = new HashSet<VerifierComponent>();
                        newSet.addAll( possibilityList );
                        newSet.addAll( set );
                        newPossibilities.add( newSet );
                    }
                }
                possibilityLists = newPossibilities;

            } else if ( type == OperatorDescrType.OR ) {

                possibilityLists.addAll( subSolver.getPossibilityLists() );

            }

            subSolver = null;

        } else if ( subSolver != null && subSolver.subSolver != null ) {

            subSolver.end();
        }

    }

    public void setChildForall(boolean b) {
        if ( subSolver != null ) {
            subSolver.setChildForall( b );
        } else {
            isChildForall = b;
        }
    }

    public void setChildExists(boolean b) {
        if ( subSolver != null ) {
            subSolver.setChildExists( b );
        } else {
            isChildExists = b;
        }
    }

    public void setChildNot(boolean b) {
        if ( subSolver != null ) {
            subSolver.setChildNot( b );
        } else {
            isChildNot = b;
        }
    }

    public boolean isForall() {
        if ( subSolver != null ) {
            return subSolver.isForall();
        } else {
            return isChildForall;
        }
    }

    public boolean isExists() {
        if ( subSolver != null ) {
            return subSolver.isExists();
        } else {
            return isChildExists;
        }
    }

    public boolean isChildNot() {
        if ( subSolver != null ) {
            return subSolver.isChildNot();
        } else {
            return isChildNot;
        }
    }

    public List<Set<VerifierComponent>> getPossibilityLists() {
        return possibilityLists;
    }
}
