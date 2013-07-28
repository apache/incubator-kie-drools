package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.StringUtils;

import java.util.Arrays;

public class DefeasibleLogicalDependency extends SimpleLogicalDependency {
    private static final String[] EMPTY_DEFEATS = new String [0];
    public static final String    DEFEATS = Defeats.class.getSimpleName();
    public static final String    DEFEATER = Defeater.class.getSimpleName();

    private DefeasibilityStatus         status;
    private String[]                    defeats;
    private LinkedListEntry<DefeasibleLogicalDependency> rootDefeated;
    private LinkedListEntry<DefeasibleLogicalDependency> tailDefeated;

    //    private int                         attacks;

    private DefeasibleLogicalDependency defeatedBy;

    private boolean isDefeater;


    public DefeasibleLogicalDependency(Activation justifier, Object justified) {
        super(justifier, justified);
        initDefeats();
    }

    public DefeasibleLogicalDependency(Activation justifier, Object justified, Object object, Object value) {
        super(justifier, justified, object, value);
        initDefeats();
    }

    private void initDefeats() {
        Object o = getJustifier().getRule().getMetaData().get(DEFEATER);



        if ( o != null && ((Boolean)o).booleanValue() ) {
            isDefeater = true;
        }

        o = getJustifier().getRule().getMetaData().get(DEFEATS);
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

    public void addDefeated(DefeasibleLogicalDependency defeated) {
        defeated.setDefeatedBy( this );
        LinkedListEntry def = defeated.getJustifierEntry();
        if (rootDefeated == null) {
            rootDefeated = def;
        } else {
            tailDefeated.setNext( def );
            def.setPrevious( rootDefeated );
        }
        tailDefeated = def;
    }

    public void removeDefeated(DefeasibleLogicalDependency defeated) {
        defeated.setDefeatedBy( null );
        if (this.rootDefeated.getObject() == defeated) {
            removeFirst();
        } else if (this.tailDefeated.getObject() == defeated) {
            removeLast();
        } else {
            LinkedListEntry<DefeasibleLogicalDependency> entry = this.rootDefeated;
            while ( entry.getObject() != defeated ) {
                entry = entry.getNext();
            }
            entry.getPrevious().setNext(entry.getNext());
            (entry.getNext()).setPrevious(entry.getPrevious());
            entry.nullPrevNext();

        }
    }

    public DefeasibleLogicalDependency removeFirst() {
        if (this.rootDefeated == null) {
            return null;
        }
        final LinkedListEntry<DefeasibleLogicalDependency> node = this.rootDefeated;
        this.rootDefeated = node.getNext();
        node.setNext(null);
        if (this.rootDefeated != null) {
            this.rootDefeated.setPrevious(null);
        } else {
            this.tailDefeated = null;
        }
        return node.getObject();
    }

    public DefeasibleLogicalDependency removeLast() {
        if (this.tailDefeated == null) {
            return null;
        }
        final LinkedListEntry<DefeasibleLogicalDependency> node = this.tailDefeated;
        this.tailDefeated = node.getPrevious();
        node.setPrevious(null);
        if (this.tailDefeated != null) {
            this.tailDefeated.setNext(null);
        } else {
            this.rootDefeated = this.tailDefeated;
        }
        return node.getObject();
    }

    public LinkedListEntry<DefeasibleLogicalDependency> getRootDefeated() {
        return this.rootDefeated;
    }

    public LinkedListEntry<DefeasibleLogicalDependency> getTailDefeated() {
        return this.tailDefeated;
    }

    public String[] getDefeats() {
        return this.defeats;
    }

    public DefeasibleLogicalDependency getDefeatedBy() {
        return defeatedBy;
    }

    public void setDefeatedBy(DefeasibleLogicalDependency defeatedBy) {
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
