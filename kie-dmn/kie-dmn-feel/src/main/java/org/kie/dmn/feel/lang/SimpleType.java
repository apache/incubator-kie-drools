package org.kie.dmn.feel.lang;

/**
 * A simple type definition interface, i.e., a type that does not contain fields
 */
public interface SimpleType extends Type {

    public static final String LIST = "list";
    public static final String CONTEXT = "context";
    public static final String FUNCTION = "function";
    public static final String BOOLEAN = "boolean";
    public static final String YEARS_AND_MONTHS_DURATION = "years and months duration";
    public static final String DAYS_AND_TIME_DURATION = "days and time duration";
    public static final String DATE_AND_TIME = "date and time";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String ANY = "Any";
}
