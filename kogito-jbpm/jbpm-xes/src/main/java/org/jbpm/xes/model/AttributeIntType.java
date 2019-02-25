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
 * <p>Java class for AttributeIntType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AttributeIntType">
 *   &lt;complexContent>
 *     &lt;extension base="{}AttributeType">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeIntType")
public class AttributeIntType extends AttributeType {

    @XmlAttribute(name = "value", required = true)
    protected long value;

    public AttributeIntType() {
    }

    public AttributeIntType(String key,
                            long value) {
        super(key);
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     */
    public long getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeIntType{" +
                "value=" + value +
                ", key='" + key + '\'' +
                ", stringOrDateOrInt=" + stringOrDateOrInt +
                '}';
    }
}
