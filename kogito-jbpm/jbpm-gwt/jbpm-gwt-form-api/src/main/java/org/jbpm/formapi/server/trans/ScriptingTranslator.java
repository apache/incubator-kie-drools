/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formapi.server.trans;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;

public class ScriptingTranslator implements Translator {

    private final VelocityEngine engine = new VelocityEngine();
    private final Map<URL, Template> templates = new HashMap<URL, Template>();

    private final String language;
    private final String folderLocation;
    
    public ScriptingTranslator(String language, String folderClassPathLocation) {
        this.language = language;
        this.folderLocation = folderClassPathLocation;
        String url = getClass().getResource(folderClassPathLocation + "form.vm").toExternalForm();
        String folderFileLocation = url.replace("form.vm", "");
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
        engine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".class", URLResourceLoader.class.getName());
        engine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".root", folderFileLocation);
        engine.init();
    }

    @Override
    public String getLanguage() {
        return language;
    }
    
    @Override
    public String translateItem(FormItemRepresentation item) throws TranslatorException {
        return runVelocityScript(item, item.getTypeId());
    }

    @Override
    public URL translateForm(FormRepresentation form) throws TranslatorException {
        return saveToURL(runVelocityScript(form, "form"));
    }
    
    /*
     * utilitary methods
     */
    private String runVelocityScript(Object item, String scriptName) throws TranslatorException {
        URL velocityTemplate = getClass().getResource(folderLocation + scriptName + ".vm");
        if (velocityTemplate == null) {
            throw new TranslatorException("Unknown typeId: " + scriptName);
        }
        Template template = null;
        synchronized (this) {
            if (!templates.containsKey(velocityTemplate)) {
                Template temp = engine.getTemplate(scriptName + ".vm");
                templates.put(velocityTemplate, temp);
            }
            template = templates.get(velocityTemplate);
        }
        VelocityContext context = new VelocityContext();
        context.put("item", item);
        context.put("language", this);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
    
    private URL saveToURL(String fileContent) throws TranslatorException {
        try {
            File tmpFile = File.createTempFile("formBuilderTrans", ".ftl");
            FileUtils.writeStringToFile(tmpFile, fileContent);
            return new URL(URLDecoder.decode(tmpFile.toURI().toString(), "UTF-8"));
        } catch (IOException e) {
            throw new TranslatorException("Problem saving URL file", e);
        }
    }

    public String getParam(String paramName, String paramValue) {
        StringBuilder builder = new StringBuilder("");
        if (paramValue != null && !"".equals(paramValue)) {
            builder.append(paramName).append("=\"").append(paramValue).append("\" ");
        }
        return builder.toString();
    }
    
    public String getParam(String paramName, Integer paramValue) {
        StringBuilder builder = new StringBuilder("");
        if (paramValue != null) {
            builder.append(paramName).append("=\"").append(paramValue).append("\" ");
        }
        return builder.toString();
    }
    
    public String getStyleParam(String paramName, String paramValue) {
        StringBuilder builder = new StringBuilder("");
        if (paramValue != null && !"".equals(paramValue)) {
            builder.append(paramName).append(": ").append(paramValue).append("; ");
        }
        return builder.toString();
    }

    public String getOnEventParams(FormItemRepresentation item) {
        Map<String, FBScript> actions = item.getEventActions();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, FBScript> entry : actions.entrySet()) {
            String attrName = entry.getKey();
            FBScript script = entry.getValue();
            if (script != null) {
                String attrContent = script.getContent().replaceAll("\n", " ");
                builder.append(getParam(attrName, attrContent)).append(' ');
            }
        }
        return builder.toString();
    }
}
