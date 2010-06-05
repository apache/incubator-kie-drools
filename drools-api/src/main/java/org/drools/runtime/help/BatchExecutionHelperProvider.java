package org.drools.runtime.help;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface BatchExecutionHelperProvider {
    XStream newXStreamMarshaller();
    XStream newJSonMarshaller();
}
