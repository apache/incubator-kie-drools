package org.drools.drl.ast.descr;

import java.util.List;

public class VariableRestrictionDescr extends EvaluatorBasedRestrictionDescr {

    private static final long serialVersionUID = 510l;

    public VariableRestrictionDescr(){
    }

    public VariableRestrictionDescr(final String evaluator,
                                    final String identifier ) {
        super( evaluator,
               false,
               (List<String>)null );
        this.setText( identifier );
    }

    public VariableRestrictionDescr(final String evaluator,
                                    final boolean isNegated,
                                    final String parameterText,
                                    final String identifier ) {
        super( evaluator,
               isNegated,
               parameterText );
        this.setText( identifier );
    }

    public String getIdentifier() {
        return this.getText();
    }
    
    public String toString() {
        return "[VariableRestriction: " + super.toString() + " " + this.getText() + " ]";
    }

}
