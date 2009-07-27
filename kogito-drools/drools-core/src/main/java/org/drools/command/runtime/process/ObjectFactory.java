package org.drools.command.runtime.process;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.drools.process.command package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.drools.process.command
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AbortWorkItemCommand }
     * 
     */
    public AbortWorkItemCommand createAbortWorkItemCommand() {
        return new AbortWorkItemCommand();
    }

    /**
     * Create an instance of {@link CompleteWorkItemCommand }
     * 
     */
    public CompleteWorkItemCommand createCompleteWorkItemCommand() {
        return new CompleteWorkItemCommand();
    }

    /**
     * Create an instance of {@link StartProcessCommand }
     * 
     */
    public StartProcessCommand createStartProcessCommand() {
        return new StartProcessCommand();
    }

    /**
     * Create an instance of {@link SignalEventCommand }
     * 
     */
    public SignalEventCommand createSignalEventCommand() {
        return new SignalEventCommand();
    }
}
