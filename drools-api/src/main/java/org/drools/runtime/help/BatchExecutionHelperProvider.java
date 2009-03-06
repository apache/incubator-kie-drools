package org.drools.runtime.help;

import com.thoughtworks.xstream.XStream;

public interface BatchExecutionHelperProvider {
    XStream newXStreamMarshaller();
}
