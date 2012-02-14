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
package org.jbpm.formapi.server.render;

import java.io.IOException;
import java.util.HashMap; 
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RendererFactory {

    private static final Log log = LogFactory.getLog(RendererFactory.class);
    private static final RendererFactory INSTANCE = new RendererFactory();
    private static final String DEFAULT_FILE = "/FormBuilder.properties";
    private static final String LANGUAGES_PROPERTY_NAME = "form.builder.languages";
    
    public static RendererFactory getInstance() {
        return INSTANCE;
    }

    private final Map<String, Renderer> cache;
    
    private RendererFactory() {
        cache = new HashMap<String, Renderer>();
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream(DEFAULT_FILE));
            String property = props.getProperty(LANGUAGES_PROPERTY_NAME);
            String[] languages = property == null ? new String[0] : property.split(",");
            for (String lang : languages) {
                getRenderer(lang);
            }
        } catch (IOException e) {
            log.error("Couldn't read file " + DEFAULT_FILE, e);
        } catch (RendererException e) {
            log.error("Couldn't initiate RendererFactory", e);
        }
    }
    
    public Renderer getRenderer(String language) throws RendererException {
        synchronized(this) {
            if (!cache.containsKey(language)) {
                String kclass = new StringBuilder("org.jbpm.formbuilder.server.render.").
                        append(language).append(".Renderer").toString();
                Object obj = null;
                try {
                    Class<?> klass = Class.forName(kclass);
                    obj = klass.newInstance();
                } catch (Exception e) {
                    throw new RendererException("Couldn't find class " + kclass, e);
                }
                cache.put(language, (Renderer) obj);
            }
        }
        return cache.get(language);
    }
}
