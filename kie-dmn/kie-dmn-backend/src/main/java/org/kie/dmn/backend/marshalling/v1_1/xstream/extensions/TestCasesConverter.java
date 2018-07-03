package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.TestCase;
import org.kie.dmn.model.v1_1.extensions.TestCases;

import java.util.Map;

public class TestCasesConverter extends DMNModelInstrumentedBaseConverter {
    public TestCasesConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        TestCases o = (TestCases) parent;
        CustomStaxReader underlyingReader = (CustomStaxReader) reader.underlyingReader();
        String modelName = underlyingReader.getAttribute("modelName");
        if (modelName != null) {
            o.setModelName(modelName);
        }
        Map<String, String> currentNSCtx = underlyingReader.getNsContext();
        o.getNsContext().putAll(currentNSCtx);
        o.setLocation( underlyingReader.getLocation() );
        o.setAdditionalAttributes( underlyingReader.getAdditionalAttributes() );
    }

    @Override
    protected void assignChildElement(Object o, String nodeName, Object child) {
        TestCases parent = (TestCases) o;
        switch (nodeName) {
            case "testCase": {
                parent.getTestCase().add((TestCase) child);
                break;
            }
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TestCases();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TestCases.class);
    }
}
