/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.integration.console.forms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.commons.io.IOUtils;
import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;
import org.jbpm.integration.console.shared.GuvnorConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Kris Verlaenen
 */
public abstract class AbstractFormDispatcher implements FormDispatcherPlugin {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFormDispatcher.class);
    
    public URL getDispatchUrl(FormAuthorityRef ref) {
        StringBuffer sb = new StringBuffer();
        Properties properties = new Properties();
        try {
            properties.load(AbstractFormDispatcher.class.getResourceAsStream("/jbpm.console.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load jbpm.console.properties", e);
        }
        sb.append("http://");
        sb.append(properties.getProperty("jbpm.console.server.host").trim());
        sb.append(":").append(new Integer(properties.getProperty("jbpm.console.server.port").trim()));
        sb.append("/" + properties.getProperty("jbpm.console.server.context", "gwt-console-server") + "/rs/form/" + getType(ref) + "/");
        sb.append(ref.getReferenceId());
        sb.append("/render");

        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to resolve form dispatch url", e);
        }
    }
    
    protected String getType(FormAuthorityRef ref) {
        FormAuthorityRef.Type type = ref.getType();
        if (type.equals(FormAuthorityRef.Type.TASK)) {
            return "task";
        }
        if (type.equals(FormAuthorityRef.Type.PROCESS)) {
            return "process";
        }
        throw new IllegalArgumentException(
                "Unknown form authority type: " + ref.getType());
    }
    
    public InputStream getTemplate(String name) {
        // try to find on classpath
        InputStream nameTaskformResult = AbstractFormDispatcher.class.getResourceAsStream("/" + name + "-taskform.ftl");
        if (nameTaskformResult != null) {
            return nameTaskformResult;
        } else {
            InputStream nameResult = AbstractFormDispatcher.class.getResourceAsStream("/" + name + ".ftl");
            if (nameResult != null) {
                return nameResult;
            }
        }
        // try to find in guvnor repository
        GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
        if(guvnorUtils.guvnorExists()) {
            try {
                String templateName;
                if(guvnorUtils.templateExistsInRepo(name + "-taskform")) {
                    templateName = name + "-taskform";
                } else if(guvnorUtils.templateExistsInRepo(name)) {
                    templateName = name;
                } else {
                    return null;
                }
                return guvnorUtils.getFormTemplateFromGuvnor(templateName);
            } catch (Throwable t) {
                logger.error("Could not load process template from Guvnor: " + t.getMessage());
                return null;
            }
        } else {
            logger.warn("Could not connect to Guvnor.");
            return null;
        }
    }

    protected DataHandler processTemplate(final String name, InputStream src, Map<String, Object> renderContext) {
        DataHandler merged = null;
        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(name, new InputStreamReader(src), cfg);
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(bout);
            temp.process(renderContext, out);
            out.flush();
            merged = new DataHandler(new DataSource() {
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(bout.toByteArray());
                }
                public OutputStream getOutputStream() throws IOException {
                    return bout;
                }
                public String getContentType() {
                    return "*/*";
                }
                public String getName() {
                    return name + "_DataSource";
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to process form template", e);
        }
        return merged;
    }
}
