/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
// Copied under Apache License from https://github.com/dmn-tck/tck/blob/8c23dc13caa508a33d11b47cca318d7c3a3ca2fc/LICENSE-ASL-2.0.txt
package org.kie.dmn.validation.dtanalysis.mcdc.dmntck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.feel.util.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="labels" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="testCase" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="inputNode" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *                           &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="resultNode" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="computed" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
 *                             &lt;element name="expected" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                           &lt;attribute name="errorResult" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="cast" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="extensionElements" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="type" type="{http://www.omg.org/spec/DMN/20160719/testcase}testCaseType" default="decision" /&gt;
 *                 &lt;attribute name="invocableName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@Generated("com.sun.tools.xjc.Driver")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "modelName",
    "labels",
    "testCase"
})
@XmlRootElement(name = "testCases")
public class TestCases {

    protected String modelName;
    protected TestCases.Labels labels;
    @XmlElement(required = true)
    protected java.util.List<TestCases.TestCase> testCase;

    /**
     * Gets the value of the modelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * Gets the value of the labels property.
     * 
     * @return
     *     possible object is
     *     {@link TestCases.Labels }
     *     
     */
    public TestCases.Labels getLabels() {
        return labels;
    }

    /**
     * Sets the value of the labels property.
     * 
     * @param value
     *     allowed object is
     *     {@link TestCases.Labels }
     *     
     */
    public void setLabels(TestCases.Labels value) {
        this.labels = value;
    }

    /**
     * Gets the value of the testCase property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testCase property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestCase().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TestCases.TestCase }
     * 
     * 
     */
    public java.util.List<TestCases.TestCase> getTestCase() {
        if (testCase == null) {
            testCase = new ArrayList<>();
        }
        return this.testCase;
    }

    public TestCases withModelName(String value) {
        setModelName(value);
        return this;
    }

    public TestCases withLabels(TestCases.Labels value) {
        setLabels(value);
        return this;
    }

    public TestCases withTestCase(TestCases.TestCase... values) {
        if (values!= null) {
            for (TestCases.TestCase value: values) {
                getTestCase().add(value);
            }
        }
        return this;
    }

    public TestCases withTestCase(Collection<TestCases.TestCase> values) {
        if (values!= null) {
            getTestCase().addAll(values);
        }
        return this;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "label"
    })
    public static class Labels {

        protected java.util.List<String> label;

        /**
         * Gets the value of the label property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the label property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLabel().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public java.util.List<String> getLabel() {
            if (label == null) {
                label = new ArrayList<>();
            }
            return this.label;
        }

        public TestCases.Labels withLabel(String... values) {
            if (values!= null) {
                for (String value: values) {
                    getLabel().add(value);
                }
            }
            return this;
        }

        public TestCases.Labels withLabel(Collection<String> values) {
            if (values!= null) {
                getLabel().addAll(values);
            }
            return this;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="inputNode" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *                 &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
     *               &lt;/extension&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="resultNode" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="computed" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
     *                   &lt;element name="expected" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *                 &lt;attribute name="errorResult" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="cast" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="extensionElements" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="type" type="{http://www.omg.org/spec/DMN/20160719/testcase}testCaseType" default="decision" /&gt;
     *       &lt;attribute name="invocableName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description",
        "inputNode",
        "resultNode",
        "extensionElements"
    })
    public static class TestCase {

        protected String description;
        protected java.util.List<TestCases.TestCase.InputNode> inputNode;
        protected java.util.List<TestCases.TestCase.ResultNode> resultNode;
        protected TestCases.TestCase.ExtensionElements extensionElements;
        @XmlAttribute(name = "id")
        protected String id;
        @XmlAttribute(name = "type")
        protected TestCaseType type;
        @XmlAttribute(name = "invocableName")
        protected String invocableName;
        @XmlAttribute(name = "name")
        protected String name;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the inputNode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the inputNode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInputNode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TestCases.TestCase.InputNode }
         * 
         * 
         */
        public java.util.List<TestCases.TestCase.InputNode> getInputNode() {
            if (inputNode == null) {
                inputNode = new ArrayList<>();
            }
            return this.inputNode;
        }

        /**
         * Gets the value of the resultNode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resultNode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResultNode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TestCases.TestCase.ResultNode }
         * 
         * 
         */
        public java.util.List<TestCases.TestCase.ResultNode> getResultNode() {
            if (resultNode == null) {
                resultNode = new ArrayList<>();
            }
            return this.resultNode;
        }

        /**
         * Gets the value of the extensionElements property.
         * 
         * @return
         *     possible object is
         *     {@link TestCases.TestCase.ExtensionElements }
         *     
         */
        public TestCases.TestCase.ExtensionElements getExtensionElements() {
            return extensionElements;
        }

        /**
         * Sets the value of the extensionElements property.
         * 
         * @param value
         *     allowed object is
         *     {@link TestCases.TestCase.ExtensionElements }
         *     
         */
        public void setExtensionElements(TestCases.TestCase.ExtensionElements value) {
            this.extensionElements = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link TestCaseType }
         *     
         */
        public TestCaseType getType() {
            if (type == null) {
                return TestCaseType.DECISION;
            } else {
                return type;
            }
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link TestCaseType }
         *     
         */
        public void setType(TestCaseType value) {
            this.type = value;
        }

        /**
         * Gets the value of the invocableName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInvocableName() {
            return invocableName;
        }

        /**
         * Sets the value of the invocableName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInvocableName(String value) {
            this.invocableName = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets a map that contains attributes that aren't bound to any typed property on this class.
         * 
         * <p>
         * the map is keyed by the name of the attribute and 
         * the value is the string value of the attribute.
         * 
         * the map returned by this method is live, and you can add new attribute
         * by updating the map directly. Because of this design, there's no setter.
         * 
         * 
         * @return
         *     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

        public TestCases.TestCase withDescription(String value) {
            setDescription(value);
            return this;
        }

        public TestCases.TestCase withInputNode(TestCases.TestCase.InputNode... values) {
            if (values!= null) {
                for (TestCases.TestCase.InputNode value: values) {
                    getInputNode().add(value);
                }
            }
            return this;
        }

        public TestCases.TestCase withInputNode(Collection<TestCases.TestCase.InputNode> values) {
            if (values!= null) {
                getInputNode().addAll(values);
            }
            return this;
        }

        public TestCases.TestCase withResultNode(TestCases.TestCase.ResultNode... values) {
            if (values!= null) {
                for (TestCases.TestCase.ResultNode value: values) {
                    getResultNode().add(value);
                }
            }
            return this;
        }

        public TestCases.TestCase withResultNode(Collection<TestCases.TestCase.ResultNode> values) {
            if (values!= null) {
                getResultNode().addAll(values);
            }
            return this;
        }

        public TestCases.TestCase withExtensionElements(TestCases.TestCase.ExtensionElements value) {
            setExtensionElements(value);
            return this;
        }

        public TestCases.TestCase withId(String value) {
            setId(value);
            return this;
        }

        public TestCases.TestCase withType(TestCaseType value) {
            setType(value);
            return this;
        }

        public TestCases.TestCase withInvocableName(String value) {
            setInvocableName(value);
            return this;
        }

        public TestCases.TestCase withName(String value) {
            setName(value);
            return this;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "any"
        })
        public static class ExtensionElements {

            @XmlAnyElement(lax = true)
            protected java.util.List<Object> any;

            /**
             * Gets the value of the any property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the any property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAny().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             * {@link Element }
             * 
             * 
             */
            public java.util.List<Object> getAny() {
                if (any == null) {
                    any = new ArrayList<>();
                }
                return this.any;
            }

            public TestCases.TestCase.ExtensionElements withAny(Object... values) {
                if (values!= null) {
                    for (Object value: values) {
                        getAny().add(value);
                    }
                }
                return this;
            }

            public TestCases.TestCase.ExtensionElements withAny(Collection<Object> values) {
                if (values!= null) {
                    getAny().addAll(values);
                }
                return this;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
         *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
         *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
         *     &lt;/extension&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class InputNode
            extends ValueType
        {

            @XmlAttribute(name = "name", required = true)
            @XmlSchemaType(name = "anySimpleType")
            protected String name;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            public TestCases.TestCase.InputNode withName(String value) {
                setName(value);
                return this;
            }

            @Override
            public TestCases.TestCase.InputNode withValue(JAXBElement<Object> value) {
                setValue(value);
                return this;
            }

            @Override
            public TestCases.TestCase.InputNode withComponent(ValueType.Component... values) {
                if (values!= null) {
                    for (ValueType.Component value: values) {
                        getComponent().add(value);
                    }
                }
                return this;
            }

            @Override
            public TestCases.TestCase.InputNode withComponent(Collection<ValueType.Component> values) {
                if (values!= null) {
                    getComponent().addAll(values);
                }
                return this;
            }

            @Override
            public TestCases.TestCase.InputNode withList(JAXBElement<ValueType.List> value) {
                setList(value);
                return this;
            }

            @Override
            public TestCases.TestCase.InputNode withExtensionElements(ValueType.ExtensionElements value) {
                setExtensionElements(value);
                return this;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="computed" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
         *         &lt;element name="expected" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *       &lt;attribute name="errorResult" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
         *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
         *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="cast" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "computed",
            "expected"
        })
        public static class ResultNode {

            protected ValueType computed;
            protected ValueType expected;
            @XmlAttribute(name = "errorResult")
            protected Boolean errorResult;
            @XmlAttribute(name = "name", required = true)
            @XmlSchemaType(name = "anySimpleType")
            protected String name;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "cast")
            protected String cast;

            /**
             * Gets the value of the computed property.
             * 
             * @return
             *     possible object is
             *     {@link ValueType }
             *     
             */
            public ValueType getComputed() {
                return computed;
            }

            /**
             * Sets the value of the computed property.
             * 
             * @param value
             *     allowed object is
             *     {@link ValueType }
             *     
             */
            public void setComputed(ValueType value) {
                this.computed = value;
            }

            /**
             * Gets the value of the expected property.
             * 
             * @return
             *     possible object is
             *     {@link ValueType }
             *     
             */
            public ValueType getExpected() {
                return expected;
            }

            /**
             * Sets the value of the expected property.
             * 
             * @param value
             *     allowed object is
             *     {@link ValueType }
             *     
             */
            public void setExpected(ValueType value) {
                this.expected = value;
            }

            /**
             * Gets the value of the errorResult property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isErrorResult() {
                if (errorResult == null) {
                    return false;
                } else {
                    return errorResult;
                }
            }

            /**
             * Sets the value of the errorResult property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setErrorResult(Boolean value) {
                this.errorResult = value;
            }

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the cast property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCast() {
                return cast;
            }

            /**
             * Sets the value of the cast property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCast(String value) {
                this.cast = value;
            }

            public TestCases.TestCase.ResultNode withComputed(ValueType value) {
                setComputed(value);
                return this;
            }

            public TestCases.TestCase.ResultNode withExpected(ValueType value) {
                setExpected(value);
                return this;
            }

            public TestCases.TestCase.ResultNode withErrorResult(Boolean value) {
                setErrorResult(value);
                return this;
            }

            public TestCases.TestCase.ResultNode withName(String value) {
                setName(value);
                return this;
            }

            public TestCases.TestCase.ResultNode withType(String value) {
                setType(value);
                return this;
            }

            public TestCases.TestCase.ResultNode withCast(String value) {
                setCast(value);
                return this;
            }

        }

    }

}
