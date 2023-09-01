package org.kie.efesto.common.api.constants;

public class Constants {

    public static final String MISSING_BODY_TEMPLATE = "Missing body in %s";
    public static final String MISSING_VARIABLE_IN_BODY = "Missing expected variable '%s' in body %s";
    public static final String MISSING_BODY_IN_METHOD = "Missing expected body in method %s";
    public static final String MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION = "Missing expected parameter %s in constructor invocation %s";
    public static final String MISSING_CONSTRUCTOR_IN_BODY = "Missing constructor invocation in body %s";
    public static final String MISSING_STATIC_INITIALIZER = "Missing expected static initializer in class %s";

    public static final String MISSING_VARIABLE_INITIALIZER_TEMPLATE = "Missing '%s' initializer in %s";
    public static final String MISSING_CHAINED_METHOD_DECLARATION_TEMPLATE = "Missing '%s' MethodDeclaration in %s";

    public static final String PACKAGE_CLASS_TEMPLATE = "%s.%s";

    //
    public static final String OUTPUTFIELDS_MAP_IDENTIFIER = "$outputFieldsMap";

    //
    public static final String INDEXFILE_DIRECTORY_PROPERTY = "indexfile.directory";

    private Constants() {
        // Avoid instantiation
    }
}
