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

	private String variableName;
	private String fieldName;
    private ArgumentValueDescr argument;

    public FieldAccessDescr(String variableName, String fieldName, ArgumentValueDescr argument) {
        this.variableName = variableName;
        this.fieldName = fieldName;     
        this.argument = argument;
    }    
    
	public FieldAccessDescr(String variableName, String fieldName) {
		this.variableName = variableName;
		this.fieldName = fieldName;		
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

    public ArgumentValueDescr getArgument() {
        return this.argument;
    }

    public void setArgument(ArgumentValueDescr argument) {
        this.argument = argument;
    }   
	
	
	
}
