/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.stringtemplate.v4.ST;

@SupportedAnnotationTypes("org.jbpm.process.workitem.core.util.Wid")
public class WidProcessor extends AbstractProcessor {

    public WidProcessor() {
        super();
    }

    private Map<String, List<Wid>> processingResults;
    private boolean resetResults = true;

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
                                      new ArrayList<>());

                if (typeElement.getInterfaces() != null && typeElement.getInterfaces().size() > 0) {
                    for (TypeMirror mirror : typeElement.getInterfaces()) {
                        if (mirror.getAnnotation(Wid.class) != null) {
                            processingResults.get(typeElement.asType().toString()).add(mirror.getAnnotation(Wid.class));
                        }
                    }
                }

                processingResults.get(typeElement.asType().toString()).add(typeElement.getAnnotation(Wid.class));
            }
        }

        return postProcessWorkItemDefinition();
    }

    public boolean postProcessWorkItemDefinition() {
        if (processingResults == null || processingResults.size() < 1) {
            return false;
        }

        try {

            Map<String, WidInfo> wrappedResults = new HashMap<>();
            for (String key : processingResults.keySet()) {
                wrappedResults.put(key,
                                   new WidInfo(processingResults.get(key)));
            }

            String widName = "WorkDefinitions";
            if (processingEnv.getOptions().containsKey("widName")) {
                widName = processingEnv.getOptions().get("widName");
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Unable to find option \"widName\", using default (WorkDefinitions)");
            }

            boolean generateTemplates = false;
            if (processingEnv.getOptions().containsKey("generateTemplates")) {
                generateTemplates = Boolean.parseBoolean(processingEnv.getOptions().get("generateTemplates"));
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Unable to find option \"generateTemplates\", using default (false).");
            }

            List<String> templateResourceList = new ArrayList();
            if (processingEnv.getOptions().containsKey("templateResources")) {
                templateResourceList = Arrays.asList(processingEnv.getOptions().get("templateResources").split(","));
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Unable to find option \"templateResources\", using default (none).");
            }

            if (generateTemplates) {
                for (String templateResource : templateResourceList) {
                    String templateInfo[] = templateResource.split(":");
                    writeStream(getFileObject("",
                                              templateInfo[0]),
                                getTemplateData(templateInfo[1],
                                                wrappedResults));
                }
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Not generating templates.");
            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     MessageFormat.format("Error post-processing workitem annotations: {0}.",
                                                                          e.getMessage()));
        }
        return true;
    }

    public byte[] getTemplateData(String templateResource,
                                  Map<String, WidInfo> widInfoMap) throws IOException {

        ST stTemplate = new ST(getTemplateResourceFileAsString(templateResource),
                               '$',
                               '$');

        stTemplate.add("widInfo",
                       widInfoMap);
        stTemplate.add("openbracket",
                       "{");
        stTemplate.add("closebracket",
                       "}");

        return stTemplate.render().getBytes();
    }

    public void writeStream(FileObject fileObject,
                            byte[] data) throws IOException {
        OutputStream stream = fileObject.openOutputStream();
        stream.write(data);
        stream.close();
    }

    public FileObject getFileObject(String pkg,
                                    String name) throws IOException {
        return processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                                                       pkg,
                                                       name);
    }

    public void setResetResults(boolean resetResults) {
        this.resetResults = resetResults;
    }

    // for testing
    public Map<String, List<Wid>> getProcessingResults() {
        return processingResults;
    }

    // for testing
    public void setProcessingResults(Map<String, List<Wid>> processingResults) {
        this.processingResults = processingResults;
    }

    public String getTemplateResourceFileAsString(String resourceFileName) throws IOException {
        FileObject fileObject = processingEnv.getFiler().getResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                resourceFileName);

        return fileObject.getCharContent(true).toString();
    }

    // for testing
    public ProcessingEnvironment getProcessingEnvironment() {
        return this.processingEnv;
    }
}