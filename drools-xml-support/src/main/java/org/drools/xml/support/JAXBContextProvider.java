package org.drools.xml.support;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextProvider {

    static {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
    }

    public static JAXBContext newInstance(Class<?>... classesToBeBound) throws JAXBException {
        return JAXBContext.newInstance(classesToBeBound);
    }
}