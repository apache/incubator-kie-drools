package org.optaplanner.core.impl.io.jaxb.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;

class JaxbCustomPropertiesAdapterTest {

    private final Unmarshaller unmarshaller;

    public JaxbCustomPropertiesAdapterTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TestBean.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Test
    void readCustomProperties() throws JAXBException {
        String xmlFragment = "<testBean>"
                + "  <customProperties>"
                + "    <property xmlns=\"https://www.optaplanner.org/xsd/solver\" name=\"firstKey\" value=\"firstValue\"/>"
                + "    <property xmlns=\"https://www.optaplanner.org/xsd/solver\" name=\"secondKey\" value=\"secondValue\"/>"
                + "  </customProperties>"
                + "</testBean>";
        Reader stringReader = new StringReader(xmlFragment);
        TestBean testBean = (TestBean) unmarshaller.unmarshal(stringReader);
        assertThat(testBean.customProperties)
                .hasSize(2)
                .containsEntry("firstKey", "firstValue")
                .containsEntry("secondKey", "secondValue");
    }

    @Test
    void nullValues() {
        JaxbCustomPropertiesAdapter jaxbCustomPropertiesAdapter = new JaxbCustomPropertiesAdapter();
        assertThat(jaxbCustomPropertiesAdapter.marshal(null)).isNull();
        assertThat(jaxbCustomPropertiesAdapter.unmarshal(null)).isNull();
    }

    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlRootElement
    private static class TestBean {

        @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
        private Map<String, String> customProperties = null;

        public TestBean() {
        }
    }
}
