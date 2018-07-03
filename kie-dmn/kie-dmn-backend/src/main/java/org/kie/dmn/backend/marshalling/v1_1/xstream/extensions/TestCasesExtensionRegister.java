package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.model.v1_1.extensions.Component;
import org.kie.dmn.model.v1_1.extensions.InputNode;
import org.kie.dmn.model.v1_1.extensions.ResultNode;
import org.kie.dmn.model.v1_1.extensions.TestCase;
import org.kie.dmn.model.v1_1.extensions.TestCases;
import org.kie.dmn.model.v1_1.extensions.Value;
import org.kie.dmn.model.v1_1.extensions.ValueType;

import javax.xml.namespace.QName;

public class TestCasesExtensionRegister implements DMNExtensionRegister {
    private static final String URI_TC = "http://www.omg.org/spec/DMN/20160719/testcase";

    @Override
    public void registerExtensionConverters(XStream xstream) {
        xstream.alias( "testCases", TestCases.class );
        xstream.alias( "testCase", TestCase.class );
        xstream.alias( "inputNode", InputNode.class );
        xstream.alias( "value", Value.class );
        xstream.alias( "resultNode", ResultNode.class );
        xstream.alias( "expected", ValueType.class );
        xstream.alias( "component", Component.class );
        xstream.registerConverter( new TestCasesConverter( xstream ) );
        xstream.registerConverter( new TestCaseConverter( xstream ) );
        xstream.registerConverter( new ResultNodeConverter( xstream ) );
        xstream.registerConverter( new InputNodeConverter( xstream ) );
        xstream.registerConverter( new ValueConverter( xstream ) );
        xstream.registerConverter( new ComponentConverter( xstream ) );
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        qmap.registerMapping( new QName(URI_TC, "testCases", "tc"), "testCases" );
    }
}
