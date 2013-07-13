package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.StringUtils;

import java.util.Arrays;

public class DefeasibleLogicalDependency extends SimpleLogicalDependency {
    public static final String DEFEATS = Defeats.class.getSimpleName();
    public static final String DEFEATER = Defeater.class.getSimpleName();

    private DefeasibilityStatus         status;
    private String[]                    defeats;
    private DefeasibleLogicalDependency rootDefeated;
    private DefeasibleLogicalDependency tailDefeated;

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


        if ( o != null && !"FALSE".equals(((String) o).toUpperCase()) ) {
            isDefeater = true;
        }

        o = getJustifier().getRule().getMetaData().get(DEFEATS);
        if ( o != null ) {
            // this must be sorted, so superiority is a quick search.
            if (o instanceof String) {
                defeats = new String[]{(String) o};
                Arrays.sort(defeats);
            } else if (o instanceof Object[]) {
                defeats = (String[]) o;
                Arrays.sort(defeats);
            }
        }
    }

    public void addDefeated(DefeasibleLogicalDependency defeated) {
        defeated.setDefeatedBy( this );
        if (rootDefeated == null) {
            rootDefeated = defeated;
        } else {
            tailDefeated.setNext(defeated);
            defeated.setPrevious(rootDefeated);
        }
        tailDefeated = defeated;
    }

    public void removeDefeated(DefeasibleLogicalDependency defeated) {
        defeated.setDefeatedBy( null );
        if (this.rootDefeated == defeated) {
            removeFirst();
        } else if (this.tailDefeated == defeated) {
            removeLast();
        } else {
            defeated.getPrevious().setNext(defeated.getNext());
            (defeated.getNext()).setPrevious(defeated.getPrevious());
            defeated.nullPrevNext();
        }
    }

    public DefeasibleLogicalDependency removeFirst() {
        if (this.rootDefeated == null) {
            return null;
        }
        final DefeasibleLogicalDependency node = this.rootDefeated;
        this.rootDefeated = (DefeasibleLogicalDependency) node.getNext();
        node.setNext(null);
        if (this.rootDefeated != null) {
            this.rootDefeated.setPrevious(null);
        } else {
            this.tailDefeated = null;
        }
        return node;
    }

    public DefeasibleLogicalDependency removeLast() {
        if (this.tailDefeated == null) {
            return null;
        }
        final DefeasibleLogicalDependency node = this.tailDefeated;
        this.tailDefeated = (DefeasibleLogicalDependency) node.getPrevious();
        node.setPrevious(null);
        if (this.tailDefeated != null) {
            this.tailDefeated.setNext(null);
        } else {
            this.rootDefeated = this.tailDefeated;
        }
        return node;
    }

    public DefeasibleLogicalDependency getRootDefeated() {
        return this.rootDefeated;
    }

    public DefeasibleLogicalDependency getTailDefeated() {
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
}
