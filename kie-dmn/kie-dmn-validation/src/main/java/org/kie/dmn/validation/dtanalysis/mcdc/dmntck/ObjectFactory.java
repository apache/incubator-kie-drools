// Copied under Apache License from https://github.com/dmn-tck/tck/blob/8c23dc13caa508a33d11b47cca318d7c3a3ca2fc/LICENSE-ASL-2.0.txt
package org.kie.dmn.validation.dtanalysis.mcdc.dmntck;

import org.kie.dmn.feel.util.Generated;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.omg.dmn.tck.marshaller._20160719 package. 
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
@Generated("com.sun.tools.xjc.Driver")
@XmlRegistry
public class ObjectFactory {

    private final static QName _ValueTypeValue_QNAME = new QName("http://www.omg.org/spec/DMN/20160719/testcase", "value");
    private final static QName _ValueTypeList_QNAME = new QName("http://www.omg.org/spec/DMN/20160719/testcase", "list");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.omg.dmn.tck.marshaller._20160719
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TestCases }
     * 
     */
    public TestCases createTestCases() {
        return new TestCases();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link TestCases.TestCase }
     * 
     */
    public TestCases.TestCase createTestCasesTestCase() {
        return new TestCases.TestCase();
    }

    /**
     * Create an instance of {@link TestCases.Labels }
     * 
     */
    public TestCases.Labels createTestCasesLabels() {
        return new TestCases.Labels();
    }

    /**
     * Create an instance of {@link ValueType.Component }
     * 
     */
    public ValueType.Component createValueTypeComponent() {
        return new ValueType.Component();
    }

    /**
     * Create an instance of {@link ValueType.List }
     * 
     */
    public ValueType.List createValueTypeList() {
        return new ValueType.List();
    }

    /**
     * Create an instance of {@link ValueType.ExtensionElements }
     * 
     */
    public ValueType.ExtensionElements createValueTypeExtensionElements() {
        return new ValueType.ExtensionElements();
    }

    /**
     * Create an instance of {@link TestCases.TestCase.InputNode }
     * 
     */
    public TestCases.TestCase.InputNode createTestCasesTestCaseInputNode() {
        return new TestCases.TestCase.InputNode();
    }

    /**
     * Create an instance of {@link TestCases.TestCase.ResultNode }
     * 
     */
    public TestCases.TestCase.ResultNode createTestCasesTestCaseResultNode() {
        return new TestCases.TestCase.ResultNode();
    }

    /**
     * Create an instance of {@link TestCases.TestCase.ExtensionElements }
     * 
     */
    public TestCases.TestCase.ExtensionElements createTestCasesTestCaseExtensionElements() {
        return new TestCases.TestCase.ExtensionElements();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/DMN/20160719/testcase", name = "value", scope = ValueType.class)
    public JAXBElement<Object> createValueTypeValue(Object value) {
        return new JAXBElement<Object>(_ValueTypeValue_QNAME, Object.class, ValueType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType.List }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/DMN/20160719/testcase", name = "list", scope = ValueType.class)
    public JAXBElement<ValueType.List> createValueTypeList(ValueType.List value) {
        return new JAXBElement<ValueType.List>(_ValueTypeList_QNAME, ValueType.List.class, ValueType.class, value);
    }

}
