package org.drools.brms.client.modeldriven.brxml;


/**
 * Holds field and value for "action" parts of the rule.
 * 
 * @author Michael Neale
 */
public class ActionFieldValue
    implements
    PortableObject {

    public ActionFieldValue(String field, String value) {
        this.field = field;
        this.value = value;
    }
    
    public ActionFieldValue() {}
    
    public String field;
    public String value;
    
    /**
     * This will return true if the value is really a "formula" - in 
     * the sense of like an excel spreadsheet.
     * 
     *  If it IS a formula, then the value should never be turned into a 
     *  string, always left as-is.
     * 
     */
    public boolean isFormula() {
        if (value == null) return false;
        if (value.trim().startsWith( "=" )) {
            return true;
        } else {
            return false;
        }
    }
    
}
