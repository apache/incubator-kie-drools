// Copied under Apache License from https://github.com/dmn-tck/tck/blob/8c23dc13caa508a33d11b47cca318d7c3a3ca2fc/LICENSE-ASL-2.0.txt
package org.kie.dmn.validation.dtanalysis.mcdc.dmntck;

import org.kie.dmn.feel.util.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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
