package org.drools.runtime.help.impl;

import org.drools.runtime.help.BatchExecutionHelperProvider;

import com.thoughtworks.xstream.XStream;

public class BatchExecutionHelperProviderImpl
    implements
    BatchExecutionHelperProvider {

    public XStream newXStreamMarshaller() {
        return newXStreamMarshaller( new XStream() );
    }

    public XStream newJSonMarshaller() {
        return XStreamJSon.newJSonMarshaller();
    }

    public XStream newXStreamMarshaller(XStream xstream) {
        return XStreamXML.newXStreamMarshaller(xstream);
    }
    
}
