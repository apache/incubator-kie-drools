package org.drools.command.runtime.rule;

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
     * Create an instance of {@link InsertElementsCommand }
     * 
     */
    public InsertElementsCommand createInsertElementsCommand() {
        return new InsertElementsCommand();
    }

    /**
     * Create an instance of {@link FireAllRulesCommand }
     * 
     */
    public FireAllRulesCommand createFireAllRulesCommand() {
        return new FireAllRulesCommand();
    }

    /**
     * Create an instance of {@link QueryCommand }
     * 
     */
    public QueryCommand createQueryCommand() {
        return new QueryCommand();
    }

    /**
     * Create an instance of {@link InsertObjectCommand }
     * 
     */
    public InsertObjectCommand createInsertObjectCommand() {
        return new InsertObjectCommand();
    }
}
