package org.drools.builder.help;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.io.Resource;

import com.sun.tools.xjc.Options;

public interface DroolsJaxbHelperProvider {

    public String[] addXsdModel(Resource resource,
                                KnowledgeBuilder kbuilder,
                                Options xjcOpts,
                                String systemId) throws IOException;

    public JAXBContext newJAXBContext(String[] classNames,
                                      Map<String, ? > properties,
                                      KnowledgeBase kbase) throws JAXBException ;
}
