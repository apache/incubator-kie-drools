package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSMode;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Arrays;

public class DefeasibleMode extends JTMSMode { //extends LinkedListEntry<Activation> implements Mode {
    private static final String[] EMPTY_DEFEATS = new String[0];
    public static final  String   DEFEATS       = Defeats.class.getSimpleName();
    public static final  String   DEFEATER      = Defeater.class.getSimpleName();

    private DefeasibilityStatus status;
    private String[]            defeats;
    private DefeasibleMode      rootDefeated;
    private DefeasibleMode      tailDefeated;

    //    private int                         attacks;

    private DefeasibleMode defeatedBy;

    private boolean isDefeater;

    private BeliefSystem beliefSystem;

    @Override
    public Object getBeliefSystem() {
        return beliefSystem;
    }

    public DefeasibleMode(String value, BeliefSystem beliefSystem) {
        super(value, beliefSystem);
        this.beliefSystem = beliefSystem;
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

    public void addDefeated(DefeasibleMode defeated) {
        defeated.setDefeatedBy( this );
        if (rootDefeated == null) {
            rootDefeated = defeated;
        } else {
            tailDefeated.setNext( defeated );
            defeated.setPrevious( rootDefeated );
        }
        tailDefeated = defeated;
    }

    public void removeDefeated(DefeasibleMode defeated) {
        defeated.setDefeatedBy( null );
        if (this.rootDefeated == defeated) {
            removeFirst();
        } else if (this.tailDefeated == defeated) {
            removeLast();
        } else {
            DefeasibleMode entry = this.rootDefeated;
            while ( entry != defeated ) {
                entry = ( DefeasibleMode) entry.getNext();
            }
            entry.getPrevious().setNext(entry.getNext());
            (entry.getNext()).setPrevious(entry.getPrevious());
            entry.nullPrevNext();

        }
    }

    public DefeasibleMode removeFirst() {
        if (this.rootDefeated == null) {
            return null;
        }
        final DefeasibleMode node = this.rootDefeated;
        this.rootDefeated = (DefeasibleMode) node.getNext();
        node.setNext(null);
        if (this.rootDefeated != null) {
            this.rootDefeated.setPrevious(null);
        } else {
            this.tailDefeated = null;
        }
        return node;
    }

    public DefeasibleMode removeLast() {
        if (this.tailDefeated == null) {
            return null;
        }
        final DefeasibleMode node = this.tailDefeated;
        this.tailDefeated = (DefeasibleMode) node.getPrevious();
        node.setPrevious(null);
        if (this.tailDefeated != null) {
            this.tailDefeated.setNext(null);
        } else {
            this.rootDefeated = this.tailDefeated;
        }
        return node;
    }

    public DefeasibleMode getRootDefeated() {
        return this.rootDefeated;
    }

    public DefeasibleMode getTailDefeated() {
        return this.tailDefeated;
    }

    public String[] getDefeats() {
        return this.defeats;
    }

    public DefeasibleMode getDefeatedBy() {
        return defeatedBy;
    }

    public void setDefeatedBy(DefeasibleMode defeatedBy) {
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
}
