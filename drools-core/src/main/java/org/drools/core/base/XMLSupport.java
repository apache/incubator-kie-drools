package org.drools.core.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;

import org.drools.core.util.Drools;
import org.kie.api.internal.utils.ServiceRegistry;

public interface XMLSupport {

    String NO_XML_SUPPORT = "You're trying to perform a xml related operation without the necessary xml support for drools. Please add the module org.drools:drools-xml-support to your classpath.";

    static <T> T throwExceptionForMissingXmlSupport() {
        if (Drools.isNativeImage()) {
            return null;
        }
        throw new RuntimeException(NO_XML_SUPPORT);
    }

    class Holder {
        private static final XMLSupport xmlSupport = ServiceRegistry.getService(XMLSupport.class);
    }

    static XMLSupport get() {
        return XMLSupport.Holder.xmlSupport != null ? XMLSupport.Holder.xmlSupport : throwExceptionForMissingXmlSupport();
    }

    static boolean present() {
        return XMLSupport.Holder.xmlSupport != null;
    }

    void writeToXml(Writer writer, Object obj) throws IOException;

    default String toXml(Object obj) {
        return toXml(Options.DEFAULT_OPTIONS, obj);
    }
    default <T> T fromXml(String s) {
        return fromXml(Options.DEFAULT_OPTIONS, s);
    }

    static Options options() {
        return new Options();
    }

    String toXml(Options options, Object obj);
    <T> T fromXml(Options options, String s);

    XmlMarshaller kieModuleMarshaller();

    interface XmlMarshaller<T> {

        String toXML(T obj);

        T fromXML(String string);
        T fromXML(File file);
        T fromXML(URL url);
        T fromXML(InputStream stream);
    }

    class Options {

        static final Options DEFAULT_OPTIONS = new Options();

        private boolean dom = false;
        private boolean trusted = false;
        private ClassLoader classLoader;

        public Options useDom(boolean dom) {
            this.dom = dom;
            return this;
        }

        public boolean isDom() {
            return dom;
        }

        public Options trusted(boolean trusted) {
            this.trusted = trusted;
            return this;
        }

        public boolean isTrusted() {
            return trusted;
        }

        public Options withClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }
    }
}