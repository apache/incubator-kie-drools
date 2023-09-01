package org.drools.drl.ast.descr;

/**
 * This represents a qualified identifier, like enums or subfield
 * access in variables like "$p.name". This is a constraint on a single 
 * field of a pattern. 
 * The "text" contains the content. 
 */
public class QualifiedIdentifierRestrictionDescr extends EvaluatorBasedRestrictionDescr {

    private static final long serialVersionUID = 510l;

    public QualifiedIdentifierRestrictionDescr(){
    }

    public QualifiedIdentifierRestrictionDescr(final String evaluator,
                                               final boolean isNegated,
                                               final String parameterText,
                                               final String text) {
        super( evaluator,
               isNegated,
               parameterText );
        this.setText( text );
    }

    public String toString() {
        return "[QualifiedIndentifierRestr: " + super.toString() + " " + this.getText() + " ]";
    }
}
