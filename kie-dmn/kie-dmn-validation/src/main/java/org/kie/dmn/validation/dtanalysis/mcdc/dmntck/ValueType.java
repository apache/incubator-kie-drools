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
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


/**
 * <p>Java class for valueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="valueType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/&gt;
 *           &lt;element name="component" maxOccurs="unbounded" minOccurs="0"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
 *                   &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *                   &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *                 &lt;/extension&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="list"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;sequence&gt;
 *                     &lt;element name="item" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                   &lt;/sequence&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
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
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@Generated("com.sun.tools.xjc.Driver")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueType", propOrder = {
    "value",
    "component",
    "list",
    "extensionElements"
})
@XmlSeeAlso({
             TestCases.TestCase.InputNode.class,
    ValueType.Component.class
})
public class ValueType {

    @XmlElementRef(name = "value", namespace = "http://www.omg.org/spec/DMN/20160719/testcase", type = JAXBElement.class, required = false)
    protected JAXBElement<Object> value;
    @XmlElement(nillable = true)
    protected java.util.List<ValueType.Component> component;
    @XmlElementRef(name = "list", namespace = "http://www.omg.org/spec/DMN/20160719/testcase", type = JAXBElement.class, required = false)
    protected JAXBElement<ValueType.List> list;
    protected ValueType.ExtensionElements extensionElements;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setValue(JAXBElement<Object> value) {
        this.value = value;
    }

    /**
     * Gets the value of the component property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the component property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueType.Component }
     * 
     * 
     */
    public java.util.List<ValueType.Component> getComponent() {
        if (component == null) {
            component = new ArrayList<>();
        }
        return this.component;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ValueType.List }{@code >}
     *     
     */
    public JAXBElement<ValueType.List> getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ValueType.List }{@code >}
     *     
     */
    public void setList(JAXBElement<ValueType.List> value) {
        this.list = value;
    }

    /**
     * Gets the value of the extensionElements property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType.ExtensionElements }
     *     
     */
    public ValueType.ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    /**
     * Sets the value of the extensionElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType.ExtensionElements }
     *     
     */
    public void setExtensionElements(ValueType.ExtensionElements value) {
        this.extensionElements = value;
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

    public ValueType withValue(JAXBElement<Object> value) {
        setValue(value);
        return this;
    }

    public ValueType withComponent(ValueType.Component... values) {
        if (values!= null) {
            for (ValueType.Component value: values) {
                getComponent().add(value);
            }
        }
        return this;
    }

    public ValueType withComponent(Collection<ValueType.Component> values) {
        if (values!= null) {
            getComponent().addAll(values);
        }
        return this;
    }

    public ValueType withList(JAXBElement<ValueType.List> value) {
        setList(value);
        return this;
    }

    public ValueType withExtensionElements(ValueType.ExtensionElements value) {
        setExtensionElements(value);
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
     *     &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
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
    public static class Component
        extends ValueType
    {

        @XmlAttribute(name = "name")
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

        public ValueType.Component withName(String value) {
            setName(value);
            return this;
        }

        @Override
        public ValueType.Component withValue(JAXBElement<Object> value) {
            setValue(value);
            return this;
        }

        @Override
        public ValueType.Component withComponent(ValueType.Component... values) {
            if (values!= null) {
                for (ValueType.Component value: values) {
                    getComponent().add(value);
                }
            }
            return this;
        }

        @Override
        public ValueType.Component withComponent(Collection<ValueType.Component> values) {
            if (values!= null) {
                getComponent().addAll(values);
            }
            return this;
        }

        @Override
        public ValueType.Component withList(JAXBElement<ValueType.List> value) {
            setList(value);
            return this;
        }

        @Override
        public ValueType.Component withExtensionElements(ValueType.ExtensionElements value) {
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

        public ValueType.ExtensionElements withAny(Object... values) {
            if (values!= null) {
                for (Object value: values) {
                    getAny().add(value);
                }
            }
            return this;
        }

        public ValueType.ExtensionElements withAny(Collection<Object> values) {
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
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="item" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "item"
    })
    public static class List {

        protected java.util.List<ValueType> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ValueType }
         * 
         * 
         */
        public java.util.List<ValueType> getItem() {
            if (item == null) {
                item = new ArrayList<>();
            }
            return this.item;
        }

        public ValueType.List withItem(ValueType... values) {
            if (values!= null) {
                for (ValueType value: values) {
                    getItem().add(value);
                }
            }
            return this;
        }

        public ValueType.List withItem(Collection<ValueType> values) {
            if (values!= null) {
                getItem().addAll(values);
            }
            return this;
        }

    }

}
