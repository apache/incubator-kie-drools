package org.drools.verifier.visitor;

import org.drools.lang.descr.BaseDescr;

/**
 * This exception is thrown when verifier tries to handle a descr that it is not
 * familiar with.
 * 
 * @author trikkola
 */
public class UnknownDescriptionException extends Exception {
    private static final long serialVersionUID = 6636873223159735829L;

    final BaseDescr           descr;

    public UnknownDescriptionException(BaseDescr descr) {
        super( "Descr ( " + descr.getClass() + " ) is unknown to drools verifier." );
        this.descr = descr;
    }

}
