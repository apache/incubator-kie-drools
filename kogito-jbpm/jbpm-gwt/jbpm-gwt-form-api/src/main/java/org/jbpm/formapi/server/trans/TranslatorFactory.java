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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TranslatorFactory {

    private static final Log log = LogFactory.getLog(TranslatorFactory.class);
    private static final String DEFAULT_FILE = "/FormBuilder.properties";
    private static final String LANGUAGES_PROPERTY_NAME = "form.builder.languages";
    private static final TranslatorFactory INSTANCE = new TranslatorFactory();
    
    private final Map<String, Translator> cache;

    private TranslatorFactory() {
        cache = new HashMap<String, Translator>();
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream(DEFAULT_FILE));
            String property = props.getProperty(LANGUAGES_PROPERTY_NAME);
            String[] languages = property == null ? new String[0] : property.split(",");
            for (String lang : languages) {
                getTranslator(lang);
            }
        } catch (IOException e) {
            log.error("Couldn't read file " + DEFAULT_FILE, e);
        } catch (TranslatorException e) {
            log.error("Couldn't initiate LanguageFactory", e);
        }
    }
    
    public static TranslatorFactory getInstance() {
        return INSTANCE;
    }
    
    public Translator getTranslator(String language) throws TranslatorException {
        synchronized(this) {
            if (!cache.containsKey(language)) {
                String kclass = new StringBuilder("org.jbpm.formbuilder.server.trans.").
                        append(language).append(".Translator").toString();
                Object obj = null;
                try {
                    Class<?> klass = Class.forName(kclass);
                    obj = klass.newInstance();
                } catch (Exception e) {
                    throw new TranslatorException("Couldn't find class " + kclass, e);
                }
                cache.put(language, (Translator) obj);
            }
        }
        return cache.get(language);
    }

    public boolean isClientSide(String type) {
        return "text/javascript".equals(type) || "text/vbscript".equals(type);
    }

    public Set<String> getLanguages() {
        return cache.keySet();
    }
}
