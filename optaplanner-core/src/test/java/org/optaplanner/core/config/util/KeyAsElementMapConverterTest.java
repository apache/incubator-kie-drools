/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.util;

import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;

import static org.junit.Assert.*;

public class KeyAsElementMapConverterTest {

    @Test
    public void read() {
        String xml = "<keyAsElementMapConverterTestBean>\n"
                + "  <customProperties>\n"
                + "    <alpha>value1</alpha>\n"
                + "    <beta>7</beta>\n"
                + "  </customProperties>\n"
                + "</keyAsElementMapConverterTestBean>";
        XStream xStream = XStreamConfigReader.buildXStream();
        xStream.processAnnotations(KeyAsElementMapConverterTestBean.class);
        xStream.allowTypes(new Class[]{KeyAsElementMapConverterTestBean.class});
        KeyAsElementMapConverterTestBean bean = (KeyAsElementMapConverterTestBean) xStream.fromXML(xml);
        assertEquals("value1", bean.customProperties.get("alpha"));
        assertEquals("7", bean.customProperties.get("beta"));
    }

    @XStreamAlias("keyAsElementMapConverterTestBean")
    public static class KeyAsElementMapConverterTestBean {

        @XStreamConverter(KeyAsElementMapConverter.class)
        protected Map<String, String> customProperties = null;

        public Map<String, String> getCustomProperties() {
            return customProperties;
        }

        public void setCustomProperties(Map<String, String> customProperties) {
            this.customProperties = customProperties;
        }

    }

}
