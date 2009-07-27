package org.drools.command.runtime;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


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

    private final static QName _BatchExecution_QNAME = new QName("http://drools.org/drools-5.0/knowledge-session", "batch-execution");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.drools.process.command
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetGlobalCommand }
     * 
     */
    public SetGlobalCommand createSetGlobalCommand() {
        return new SetGlobalCommand();
    }

    /**
     * Create an instance of {@link GetGlobalCommand }
     * 
     */
    public GetGlobalCommand createGetGlobalCommand() {
        return new GetGlobalCommand();
    }

    /**
     * Create an instance of {@link BatchExecutionCommand }
     * 
     */
    public BatchExecutionCommand createBatchExecutionCommand() {
        return new BatchExecutionCommand();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchExecutionCommand }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://drools.org/drools-5.0/knowledge-session", name = "batch-execution")
    public JAXBElement<BatchExecutionCommand> createBatchExecution(BatchExecutionCommand value) {
        return new JAXBElement<BatchExecutionCommand>(_BatchExecution_QNAME, BatchExecutionCommand.class, null, value);
    }

}
