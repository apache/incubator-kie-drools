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

import org.kie.dmn.feel.util.Generated;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for testCaseType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="testCaseType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="decision"/&gt;
 *     &lt;enumeration value="bkm"/&gt;
 *     &lt;enumeration value="decisionService"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@Generated("com.sun.tools.xjc.Driver")
@XmlType(name = "testCaseType")
@XmlEnum
public enum TestCaseType {

    @XmlEnumValue("decision")
    DECISION("decision"),
    @XmlEnumValue("bkm")
    BKM("bkm"),
    @XmlEnumValue("decisionService")
    DECISION_SERVICE("decisionService");
    private final String value;

    TestCaseType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TestCaseType fromValue(String v) {
        for (TestCaseType c: TestCaseType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
