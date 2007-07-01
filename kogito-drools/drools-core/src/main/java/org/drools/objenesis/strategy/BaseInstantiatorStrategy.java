package org.drools.objenesis.strategy;

/**
 * Base {@link InstantiatorStrategy} class basically containing helpful constant to sort out JVMs.
 * 
 * @author Henri Tremblay
 */
public abstract class BaseInstantiatorStrategy
    implements
    InstantiatorStrategy {

    /** JVM_NAME prefix for JRockit */
    protected static final String JROCKIT        = "BEA";

    /** JVM_NAME prefix for GCJ */
    protected static final String GNU            = "GNU libgcj";

    /** JVM_NAME prefix for Sun Java HotSpot */
    protected static final String SUN            = "Java HotSpot";

    /** JVM version */
    protected static final String VM_VERSION     = System.getProperty( "java.runtime.version" );

    /** JVM version */
    protected static final String VM_INFO        = System.getProperty( "java.vm.info" );

    /** Vendor version */
    protected static final String VENDOR_VERSION = System.getProperty( "java.vm.version" );

    /** Vendor name */
    protected static final String VENDOR         = System.getProperty( "java.vm.vendor" );

    /** JVM name */
    protected static final String JVM_NAME       = System.getProperty( "java.vm.name" );
}
