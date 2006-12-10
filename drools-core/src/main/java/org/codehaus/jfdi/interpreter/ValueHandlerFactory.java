package org.codehaus.jfdi.interpreter;

public interface ValueHandlerFactory {

    /** the type parameter is one of the following constants */
    /** Obviously if it was in quotes, its a string literal (which could be anything) */
    public static final int STRING     = 1;
    /** Means true integer, not Javas interpretation of it */
    public static final int CHAR       = 2;
    public static final int INTEGER    = 4;
    public static final int LONG       = 8;
    public static final int FLOAT      = 16;
    public static final int DOUBLE     = 32;
    public static final int BIGINTEGER = 64;    
    public static final int BIGDECIMAL = 128;
    public static final int BOOLEAN    = 256;  
    public static final int NULL       = 512;    
    public static final int MAP        = 1024;    
    public static final int LIST       = 2048;

    /**
     * This is for creating a literal "value handler"
     */
    public ValueHandler createLiteral(Class cls,
                                               String val);

    /** 
     * A local variable requires a type of some form (class).
     */
    public ValueHandler createLocalVariable(String identifier,
                                                     String type,
                                                     boolean isFinal);

    /** 
     * To be implemented by the concrete factory.
     * External variables will be provided to the parser ahead of time.
     */
    public ValueHandler createExternalVariable(String identifier);
    
    /**
     * return true if the specified external identifier is a variable.
     */
    public boolean isValidVariable(String identifier);
    
    public String[] getRequiredVariables();
    

}