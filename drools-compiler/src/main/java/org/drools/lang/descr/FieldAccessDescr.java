package org.drools.lang.descr;

/**
 * This represents direct field access.
 * Not a whole lot different from what you can do with the method access,
 * but in this case it is just a field by name (same as in a column).
 *
 *
 * eg: foo.bar
 */
public class FieldAccessDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 3262446325341307441L;
    
    private String fieldName;
    private String argument;

    public FieldAccessDescr(String fieldName) {
        this.fieldName = fieldName;     
    }

    public FieldAccessDescr(String fieldName, String argument) {
        this.fieldName = fieldName;     
        this.argument = argument;
    }    
    
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

    public String getArgument() {
        return this.argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }
    
    public String toString() {
        return fieldName + ( (this.argument!=null) ? this.argument: "" );
    }
	
}
