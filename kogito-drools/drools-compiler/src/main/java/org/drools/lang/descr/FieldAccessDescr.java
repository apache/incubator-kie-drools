package org.drools.lang.descr;

/**
 * This represents direct field access.
 * Not a whole lot different from what you can do with the method access,
 * but in this case it is just a field by name (same as in a pattern).
 *
 *
 * eg: foo.bar
 */
public class FieldAccessDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 3262446325341307441L;

    private String            fieldName;
    private String            argument;

    public FieldAccessDescr(final String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldAccessDescr(final String fieldName,
                            final String argument) {
        this.fieldName = fieldName;
        this.argument = argument;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getArgument() {
        return this.argument;
    }

    public void setArgument(final String argument) {
        this.argument = argument;
    }

    public String toString() {
        return this.fieldName + ((this.argument != null) ? this.argument : "");
    }

}
