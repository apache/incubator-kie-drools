package org.drools.verifier.visitor;

import org.drools.drl.ast.descr.BaseDescr;

/**
 * This exception is thrown when verifier tries to handle a descr that it is not
 * familiar with.
 */
public class UnknownDescriptionException extends Exception {
    private static final long serialVersionUID = 510l;

    final BaseDescr           descr;

    public UnknownDescriptionException(BaseDescr descr) {
        super( "Descr ( " + descr.getClass() + " ) is unknown to drools verifier." );
        this.descr = descr;
    }

}
