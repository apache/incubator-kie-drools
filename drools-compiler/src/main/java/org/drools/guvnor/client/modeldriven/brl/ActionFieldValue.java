package org.drools.guvnor.client.modeldriven.brl;


/**
 * Holds field and value for "action" parts of the rule.
 *
 * @author Michael Neale
 */
public class ActionFieldValue
    implements
    PortableObject {

    public String field;
    public String value;
    public long nature;
    /**
     * This is used only when action is first created.
     * This means that there is no value yet for the constraint.
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * This may be string, or number, anything really.
     */
    public static final int TYPE_LITERAL   = 1;

    /**
     * This is when it is set to a valid previously bound variable.
     */
    public static final int TYPE_VARIABLE  = 2;

    /**
     * This is for a "formula" that calculates a value.
     */
    public static final int TYPE_FORMULA = 3;

    /**
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int TYPE_ENUM      = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;

    /**
     * This is for a field to be a placeholder for a template
     */
    public static final int TYPE_TEMPLATE = 7;
    
    /**
     * This is the datatype archectype (eg String, Numeric etc).
     */
    public String type;

    public ActionFieldValue(final String field,
                            final String value,
                            final String type) {
        this.field = field;
        this.value = value;
        this.type = type;
    }

    public ActionFieldValue() {
    }

    /**
     * This will return true if the value is really a "formula" - in
     * the sense of like an excel spreadsheet.
     *
     *  If it IS a formula, then the value should never be turned into a
     *  string, always left as-is.
     *
     */
    public boolean isFormula() {
        return this.value != null && this.value.trim().startsWith( "=" );
    }

}
