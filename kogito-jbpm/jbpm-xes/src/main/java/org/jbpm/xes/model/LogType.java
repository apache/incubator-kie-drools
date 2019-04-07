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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for LogType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LogType">
 *   &lt;complexContent>
 *     &lt;extension base="{}ElementType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{}ExtensionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="global" type="{}GlobalsType" maxOccurs="2" minOccurs="0"/>
 *         &lt;element name="classifier" type="{}ClassifierType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="trace" type="{}TraceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="xes.version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="xes.features" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="openxes.version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogType", propOrder = {
        "extension",
        "global",
        "classifier",
        "stringOrDateOrInt",
        "trace",
})
public class LogType extends ElementType {

    protected List<ExtensionType> extension;
    protected List<GlobalsType> global;
    protected List<ClassifierType> classifier;
    protected List<TraceType> trace;

    @XmlAttribute(name = "xes.version", required = true)
    protected String xesVersion;

    @XmlAttribute(name = "xes.features")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String xesFeatures;

    @XmlAttribute(name = "openxes.version")
    protected String openxesVersion;

    /**
     * Gets the value of the extension property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extension property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtension().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionType }
     */
    public List<ExtensionType> getExtension() {
        if (extension == null) {
            extension = new ArrayList<ExtensionType>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the global property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the global property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalsType }
     */
    public List<GlobalsType> getGlobal() {
        if (global == null) {
            global = new ArrayList<GlobalsType>();
        }
        return this.global;
    }

    /**
     * Gets the value of the classifier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classifier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassifier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassifierType }
     */
    public List<ClassifierType> getClassifier() {
        if (classifier == null) {
            classifier = new ArrayList<ClassifierType>();
        }
        return this.classifier;
    }

    /**
     * Gets the value of the trace property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trace property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrace().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TraceType }
     */
    public List<TraceType> getTrace() {
        if (trace == null) {
            trace = new ArrayList<TraceType>();
        }
        return this.trace;
    }

    /**
     * Gets the value of the xesVersion property.
     * @return possible object is
     * {@link String }
     */
    public String getXesVersion() {
        return xesVersion;
    }

    /**
     * Sets the value of the xesVersion property.
     * @param value allowed object is
     * {@link String }
     */
    public void setXesVersion(String value) {
        this.xesVersion = value;
    }

    /**
     * Gets the value of the xesFeatures property.
     * @return possible object is
     * {@link String }
     */
    public String getXesFeatures() {
        return xesFeatures;
    }

    /**
     * Sets the value of the xesFeatures property.
     * @param value allowed object is
     * {@link String }
     */
    public void setXesFeatures(String value) {
        this.xesFeatures = value;
    }

    /**
     * Gets the value of the openxesVersion property.
     * @return possible object is
     * {@link String }
     */
    public String getOpenxesVersion() {
        return openxesVersion;
    }

    /**
     * Sets the value of the openxesVersion property.
     * @param value allowed object is
     * {@link String }
     */
    public void setOpenxesVersion(String value) {
        this.openxesVersion = value;
    }
}
