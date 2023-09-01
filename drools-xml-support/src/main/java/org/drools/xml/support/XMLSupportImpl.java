package org.drools.xml.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.base.base.XMLSupport;
import org.drools.commands.runtime.BatchExecutionCommandImpl;
import org.drools.xml.support.converters.KieModuleMarshaller;

import static org.kie.utll.xml.XStreamUtils.createNonTrustingXStream;
import static org.kie.utll.xml.XStreamUtils.createTrustingXStream;

public class XMLSupportImpl implements XMLSupport {

    @Override
    public String toXml(Options options, Object obj) {
        return createXStream(options).toXML(obj);
    }

    @Override
    public <T> T fromXml(Options options, String s) {
        return (T) createXStream(options).fromXML(s);
    }

    private XStream createXStream(Options options) {
        XStream xStream;
        if (options.isTrusted()) {
            if (options.isDom()) {
                xStream = createTrustingXStream();
            } else {
                xStream = createTrustingXStream(new DomDriver());
            }
        } else {
            if (options.isDom()) {
                xStream = createNonTrustingXStream();
            } else {
                xStream = createNonTrustingXStream(new DomDriver());
            }
        }

        if (options.getClassLoader() != null) {
            xStream.setClassLoader(options.getClassLoader());
        }

        return xStream;
    }

    public KieModuleMarshaller kieModuleMarshaller() {
        return KieModuleMarshaller.MARSHALLER;
    }

    public BatchExecutionCommandImpl createBatchExecutionCommand() {
        return new BatchExecutionCommandImpl();
    }
}
