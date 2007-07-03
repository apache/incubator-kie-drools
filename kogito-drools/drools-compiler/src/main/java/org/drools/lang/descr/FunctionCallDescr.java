package org.drools.lang.descr;

/** This represents a function call - as in calling a Drools function. 
 * eg: functionName(argument list)
 */
public class FunctionCallDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 400L;
    private String name;
    private String arguments;

    public FunctionCallDescr(final String name) {
        this.name = name;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(final String arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name + this.arguments;
    }

}
