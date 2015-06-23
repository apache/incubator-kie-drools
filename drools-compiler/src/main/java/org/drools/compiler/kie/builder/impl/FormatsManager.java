/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class FormatsManager {

    private FormatsManager() { }

    private static final FormatsManager INSTANCE = new FormatsManager();

    static FormatsManager get() {
        return INSTANCE;
    }

    private Map<String, String> registry = new HashMap<String, String>() {{
        put("gdst", "org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableConverter");
        put("scgd", "org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardConverter");
        put("template", "org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateConverter");
    }};

    private Map<String, FormatConverter> converters = new HashMap<String, FormatConverter>();

    public FormatConverter getConverterFor(String fileName) {
        return isKieExtension(fileName) ? FormatConverter.DummyConverter.INSTANCE : getExternalConverter(fileName);
    }

    private FormatConverter getExternalConverter(String fileName) {
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos < 0) {
            return null;
        }
        String extension = fileName.substring(dotPos+1);
        FormatConverter converter = converters.get(extension);
        if (converter == null) {
            String converterClassName = registry.get(extension);
            if (converterClassName != null) {
                try {
                    converter = ((Class<? extends FormatConverter>)Class.forName(converterClassName)).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return converter;
    }

    static boolean isKieExtension(String fileName) {
        return !fileName.endsWith( ".java" ) && ResourceType.determineResourceType(fileName) != null;
    }
}
