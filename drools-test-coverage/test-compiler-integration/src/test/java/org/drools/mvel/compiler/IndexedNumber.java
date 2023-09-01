package org.drools.mvel.compiler;

import java.io.Serializable;

/**
 * IndexedNumber
 *
 * Created: 22/06/2006
 *
 * @version $Id$
 */

public class IndexedNumber implements Serializable {

    private int     number = 0;

    private int     index  = 0;

    private boolean printed;

    public IndexedNumber() {
    }

    /**
     * @param pNb
     * @param pIndex
     */
    public IndexedNumber(final int pNb,
                         final int pIndex) {
        this.number = pNb;
        this.index = pIndex;
    }

    /**
     * @return le/la/les index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @param pIndex le/la/les index sauvegarder.
     */
    public void setIndex(final int pIndex) {
        this.index = pIndex;
    }

    /**
     * @return le/la/les nb.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * @param pNb le/la/les nb sauvegarder.
     */
    public void setNumber(final int pNb) {
        this.number = pNb;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IndexedNumber[ " + this.number + ", " + this.index + " ]";
    }

    public boolean isPrinted() {
        return this.printed;
    }

    public void setPrinted(final boolean printed) {
        this.printed = printed;
    }

}
