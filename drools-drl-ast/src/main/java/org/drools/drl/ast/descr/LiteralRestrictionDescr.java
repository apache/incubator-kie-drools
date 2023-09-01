package org.drools.drl.ast.descr;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;


/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a pattern. 
 * The "text" contains the content. 
 */
public class LiteralRestrictionDescr extends EvaluatorBasedRestrictionDescr {
    public static final int   TYPE_NULL        = 1;
    public static final int   TYPE_NUMBER      = 2;
    public static final int   TYPE_STRING      = 3;
    public static final int   TYPE_BOOLEAN     = 4;

    private static final long serialVersionUID = 510l;
    private int               type;

    public LiteralRestrictionDescr(){
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final String text) {
        this( evaluator,
              false,
              (List<String>) null,
              text,
              TYPE_STRING );// default type is string if not specified
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final String text) {
        this( evaluator,
              isNegated,
              (List<String>) null,
              text,
              TYPE_STRING );// default type is string if not specified
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final String parameterText,
                                   final String text,
                                   final int type) {
        super( evaluator,
               isNegated,
               parameterText );
        this.setText( text );
        this.type = type;
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final List<String> parameters,
                                   final String text,
                                   final int type) {
        super( evaluator,
               isNegated,
               parameters );
        this.setText( text );
        this.type = type;
    }

    public String toString() {
        return super.toString() + " " + this.getText();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        switch ( this.type ) {
            case TYPE_NUMBER :
                try {
                    // in the DRL, we always use US number formatting 
                    return DecimalFormat.getInstance(Locale.US).parse( this.getText() );
                } catch ( ParseException e ) {
                    // return String anyway
                    return this.getText();
                }
            case TYPE_BOOLEAN :
                return Boolean.valueOf( this.getText() );
            default :
                return this.getText();
        }
    }
}
