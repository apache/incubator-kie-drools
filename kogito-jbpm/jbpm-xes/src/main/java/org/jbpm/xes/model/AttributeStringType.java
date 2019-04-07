/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AttributeStringType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AttributeStringType">
 *   &lt;complexContent>
 *     &lt;extension base="{}AttributeType">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeStringType", propOrder = {
        "key",
        "value",
        "stringOrDateOrInt"
})
public class AttributeStringType extends AttributeType {

    @XmlAttribute(name = "value", required = true)
    protected String value;

    public AttributeStringType() {
    }

    public AttributeStringType(String key,
                               String value) {
        super(key);
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * @param value allowed object is
     * {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeStringType{" +
                "value='" + value + '\'' +
                ", key='" + key + '\'' +
                ", stringOrDateOrInt=" + stringOrDateOrInt +
                '}';
    }
}
