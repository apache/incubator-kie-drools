/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.workitem.core.util;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.stringtemplate.v4.ST;

@SupportedAnnotationTypes("org.jbpm.process.workitem.core.util.Wid")
public class WidProcessor extends AbstractProcessor {

    public WidProcessor() {
        super();
    }

    private Map<String, Wid> processingResults;
    private boolean resetResults = true;

    private static final String WID_ST_TEMPLATE = "[\n" +
            "$widInfo:{k|\n" +
            "[\n" +
            "    \"name\" : \"$widInfo.(k).name$\",\n" +
            "    \"displayName\" : \"$widInfo.(k).displayName$\",\n" +
            "    \"category\" : \"$widInfo.(k).category$\",\n" +
            "    \"description\" : \"$widInfo.(k).description$\",\n" +
            "    \"defaultHandler\" : \"$widInfo.(k).defaultHandler$\",\n" +
            "\n" +
            "    $if(widInfo.(k).parameters)$\n" +
            "    \"parameters\" : [\n" +
            "        $widInfo.(k).parameters:{k1|\n" +
            "            \"$k1$\" : new $widInfo.(k).parameters.(k1).type$()\n" +
            "        }; separator=\",\"$\n" +
            "    ],\n" +
            "    $endif$\n" +
            "    $if(widInfo.(k).results)$\n" +
            "    \"results\" : [\n" +
            "        $widInfo.(k).results:{k1|\n" +
            "            \"$k1$\" : new $widInfo.(k).results.(k1).type$()\n" +
            "        }; separator=\",\"$\n" +
            "    ],\n" +
            "    $endif$\n" +
            "    $if(widInfo.(k).mavenDepends)$\n" +
            "    \"mavenDependencies\" : [\n" +
            "        $widInfo.(k).mavenDepends:{k1|\n" +
            "             \"$widInfo.(k).mavenDepends.(k1).group$:$widInfo.(k).mavenDepends.(k1).artifact$:$widInfo.(k).mavenDepends.(k1).version$\"\n" +
            "        }; separator=\",\"$\n" +
            "    ],\n" +
            "    $endif$\n" +
            "    \"icon\" : \"$widInfo.(k).icon$\"\n" +
            "\n" +
            "]\n" +
            "}; separator=\",\"$\n" +
            "]";


    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        if (resetResults) {
            processingResults = new HashMap<>();
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Wid.class)) {

            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         MessageFormat.format("Wid Processor : processing class {0}.",
                                                                              typeElement.asType().toString()));

                processingResults.put(typeElement.asType().toString(),
                                      typeElement.getAnnotation(Wid.class));
            }
        }

        return postProcessWorkItemDefinition();
    }

    public boolean postProcessWorkItemDefinition() {
        if (processingResults == null || processingResults.size() < 1) {
            return false;
        }

        try {
            ST widTemplate = new ST(WID_ST_TEMPLATE,
                                    '$',
                                    '$');

            Map<String, WidInfo> wrappedResults = new HashMap<>();
            for (String key : processingResults.keySet()) {
                wrappedResults.put(key,
                                   new WidInfo(processingResults.get(key)));
            }
            widTemplate.add("widInfo",
                            wrappedResults);

            FileObject widFile = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                                                                         "",
                                                                         "WorkDefinitions.wid");
            OutputStream stream = widFile.openOutputStream();
            stream.write(widTemplate.render().getBytes());
            stream.close();
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     MessageFormat.format("Error post-processing workitem annotations: {0}.",
                                                                          e.getMessage()));
        }
        return true;
    }

    public void setResetResults(boolean resetResults) {
        this.resetResults = resetResults;
    }

    // for testing
    public Map<String, Wid> getProcessingResults() {
        return processingResults;
    }

    // for testing
    public void setProcessingResults(Map<String, Wid> processingResults) {
        this.processingResults = processingResults;
    }
}
