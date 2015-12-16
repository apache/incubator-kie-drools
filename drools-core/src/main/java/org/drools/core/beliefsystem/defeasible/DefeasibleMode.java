/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSMode;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Arrays;

public class DefeasibleMode<M extends DefeasibleMode<M>> extends JTMSMode<M> { //extends LinkedListEntry<Activation> implements Mode {
    private static final String[] EMPTY_DEFEATS = new String[0];
    public static final  String   DEFEATS       = Defeats.class.getSimpleName();
    public static final  String   DEFEATER      = Defeater.class.getSimpleName();

    private DefeasibilityStatus status;
    private String[]            defeats;
    private M                   rootDefeated;
    private M                   tailDefeated;

    //    private int                         attacks;

    private DefeasibleMode<M>   defeatedBy;

    private boolean isDefeater;

    private BeliefSystem<M>     beliefSystem;

    private Mode nextMode;

    @Override
    public Object getBeliefSystem() {
        return beliefSystem;
    }

    public DefeasibleMode(String value, BeliefSystem beliefSystem) {
        super(value, beliefSystem);
        this.beliefSystem = beliefSystem;
    }

    public DefeasibleMode(String value, BeliefSystem beliefSystem, Mode nextMode) {
        super(value, beliefSystem);
        this.beliefSystem = beliefSystem;
        this.nextMode = nextMode;
    }

    public void initDefeats() {

        Object o = getLogicalDependency().getJustifier().getRule().getMetaData().get(DEFEATER);



        if ( o != null && ((Boolean)o).booleanValue() ) {
            isDefeater = true;
        }

        o = getLogicalDependency().getJustifier().getRule().getMetaData().get(DEFEATS);
        if ( o != null ) {
            // this must be sorted, so superiority is a quick search.
            if (o instanceof String) {
                defeats = new String[]{(String) o};
                Arrays.sort(defeats);
            } else if (o instanceof Object[]) {
                defeats = Arrays.copyOf( (Object[]) o, ( (Object[]) o ).length, String[].class );
                Arrays.sort(defeats);
            }
        } else {
            defeats = EMPTY_DEFEATS;
        }
    }

    public void addDefeated( M defeated ) {
        defeated.setDefeatedBy( this );
        if (rootDefeated == null) {
            rootDefeated = defeated;
        } else {
            tailDefeated.setNext( defeated );
            defeated.setPrevious( tailDefeated );
        }
        tailDefeated = defeated;
    }

    public void removeDefeated( DefeasibleMode<M> defeated ) {
        defeated.setDefeatedBy( null );
        if (this.rootDefeated == defeated) {
            removeFirst();
        } else if (this.tailDefeated == defeated) {
            removeLast();
        } else {
            DefeasibleMode<M> entry = this.rootDefeated;
            while ( entry != defeated ) {
                entry = entry.getNext();
            }
            entry.getPrevious().setNext( entry.getNext() );
            entry.getNext().setPrevious( entry.getPrevious() );
            entry.nullPrevNext();

        }
    }

    public DefeasibleMode<M> removeFirst() {
        if (this.rootDefeated == null) {
            return null;
        }
        final DefeasibleMode<M> node = this.rootDefeated;
        this.rootDefeated = node.getNext();
        node.setNext(null);
        if (this.rootDefeated != null) {
            this.rootDefeated.setPrevious(null);
        } else {
            this.tailDefeated = null;
        }
        return node;
    }

    public DefeasibleMode<M> removeLast() {
        if (this.tailDefeated == null) {
            return null;
        }
        final DefeasibleMode<M> node = this.tailDefeated;
        this.tailDefeated = node.getPrevious();
        node.setPrevious(null);
        if (this.tailDefeated != null) {
            this.tailDefeated.setNext(null);
        } else {
            this.rootDefeated = this.tailDefeated;
        }
        return node;
    }

    public M getRootDefeated() {
        return this.rootDefeated;
    }

    public M getTailDefeated() {
        return this.tailDefeated;
    }

    public String[] getDefeats() {
        return this.defeats;
    }

    public DefeasibleMode<M> getDefeatedBy() {
        return defeatedBy;
    }

    public void setDefeatedBy(DefeasibleMode<M> defeatedBy) {
        this.defeatedBy = defeatedBy;
    }

//    public int getAttacks() {
//        return attacks;
//    }
//
//    public void incAttacks() {
//        attacks++;
//    }
//
//    public void decAttacks() {
//        attacks--;
//    }

    public DefeasibilityStatus getStatus() {
        return status;
    }

    public void setStatus(DefeasibilityStatus status) {
        this.status = status;
    }

    public boolean isDefeater() {
        return isDefeater;
    }

    public void setDefeater(boolean defeater) {
        isDefeater = defeater;
    }

    public void clearDefeated() {
        this.rootDefeated = null;
        this.tailDefeated = null;
    }

    @Override
    public Mode getNextMode() {
        return nextMode;
    }

    @Override
    public String toString() {
        return "DefeasibleMode{" +
               "status=" + status +
               " Object=" + getLogicalDependency().getObject() +
               "} ";
    }
}
