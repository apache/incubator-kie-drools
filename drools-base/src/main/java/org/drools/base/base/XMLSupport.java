/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.base;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.drools.base.common.MissingDependencyException;
import org.drools.base.util.Drools;
import org.kie.api.internal.utils.KieService;

public interface XMLSupport extends KieService {

    String NO_XML_SUPPORT = "You're trying to perform a xml related operation without the necessary xml support for drools. Please add the module org.drools:drools-xml-support to your classpath.";

    static <T> T throwExceptionForMissingXmlSupport() {
        if (Drools.isNativeImage()) {
            return null;
        }
        throw new MissingDependencyException(NO_XML_SUPPORT);
    }

    class Holder {
        private static final XMLSupport xmlSupport = KieService.load(XMLSupport.class);
    }

    static XMLSupport get() {
        return XMLSupport.Holder.xmlSupport != null ? XMLSupport.Holder.xmlSupport : throwExceptionForMissingXmlSupport();
    }

    static boolean present() {
        return XMLSupport.Holder.xmlSupport != null;
    }

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