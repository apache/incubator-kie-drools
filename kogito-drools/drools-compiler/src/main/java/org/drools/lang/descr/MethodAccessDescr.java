package org.drools.lang.descr;

/**
 * 
 * This represents a method call.
 * As in:
 * 
 * variableName.methodName(argument list)
 *
 */
public class MethodAccessDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 400L;

    private String            methodName;
    private String            arguments;

    public MethodAccessDescr(final String methodName) {
        this.methodName = methodName;
    }

    public MethodAccessDescr(final String methodName,
                             final String arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(final String arguments) {
        this.arguments = arguments;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public String toString() {
        return this.methodName + this.arguments;
    }

}
