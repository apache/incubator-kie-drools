/*
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
package org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.FormsStorage;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.Form;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormConfiguration;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormContent;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormFilter;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormInfo;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormsStorageImpl implements FormsStorage {

    public static final String PROJECT_FORM_STORAGE_PROP = "quarkus.kogito-runtime-tools.forms.folder";

    private static final String CONFIG_EXT = ".config";

    private static final String FORMS_STORAGE_PATH = "/forms";

    private static final String JAR_FORMS_STORAGE_PATH = "/target/classes" + FORMS_STORAGE_PATH;
    private static final String FS_FORMS_STORAGE_PATH = "/src/main/resources" + FORMS_STORAGE_PATH;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsStorageImpl.class);

    private final Map<String, FormInfo> formInfoMap = new HashMap<>();
    private final Map<String, Form> modifiedForms = new HashMap<>();

    private URL classLoaderFormsUrl;
    private URL formsStorageUrl;

    public FormsStorageImpl() {
        start(Thread.currentThread().getContextClassLoader().getResource(FORMS_STORAGE_PATH));
    }

    FormsStorageImpl(final URL classLoaderFormsUrl) {
        start(classLoaderFormsUrl);
    }

    private void start(final URL classLoaderFormsUrl) {
        start(classLoaderFormsUrl, getFormStorageUrl(classLoaderFormsUrl));
    }

    private void start(final URL classLoaderFormsUrl, final URL formsStorageUrl) {
        try {
            this.classLoaderFormsUrl = classLoaderFormsUrl;
            this.formsStorageUrl = formsStorageUrl;
        } catch (Exception ex) {
            LOGGER.warn("Couldn't properly initialize FormsStorageImpl");
        } finally {
            init();
        }
    }

    private URL getFormStorageUrl(URL classLoaderFormsUrl) {
        if (classLoaderFormsUrl == null) {
            return null;
        }

        String storageUrl = ConfigProvider.getConfig()
                .getOptionalValue(PROJECT_FORM_STORAGE_PROP, String.class)
                .orElseGet(() -> classLoaderFormsUrl.getFile().replace(JAR_FORMS_STORAGE_PATH, FS_FORMS_STORAGE_PATH));

        File formsStorageFolder = new File(storageUrl);

        if (!formsStorageFolder.exists() || !formsStorageFolder.isDirectory()) {
            LOGGER.warn("Cannot initialize form storage folder in path '" + formsStorageFolder.getPath() + "'");
        }

        try {
            return formsStorageFolder.toURI().toURL();
        } catch (MalformedURLException ex) {
            LOGGER.warn("Cannot initialize form storage folder in path '" + formsStorageFolder.getPath() + "'", ex);
        }
        return null;
    }

    @Override
    public int getFormsCount() {
        return formInfoMap.keySet().size();
    }

    @Override
    public Collection<FormInfo> getFormInfoList(FormFilter filter) {
        if (filter != null && !filter.getNames().isEmpty()) {
            return formInfoMap.entrySet().stream()
                    .filter(entry -> StringUtils.containsAnyIgnoreCase(entry.getKey(), filter.getNames().toArray(new String[0])))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        } else {
            return formInfoMap.values();
        }
    }

    private FormInfo.FormType getFormType(String type) {
        switch (type) {
            case "html":
                return FormInfo.FormType.HTML;
            case "tsx":
                return FormInfo.FormType.TSX;
        }
        return null;
    }

    @Override
    public Form getFormContent(String formName) throws IOException {
        FormInfo formInfo = formInfoMap.get(formName);

        if (formInfo == null) {
            throw new RuntimeException("Cannot find form '" + formName + "'");
        }

        return modifiedForms.getOrDefault(formName, loadForm(formInfo));
    }

    private Form loadForm(FormInfo formInfo) throws IOException {
        File formFile = getFormFile(formInfo.getName(), formInfo);
        File formConfig = getFormConfigFile(formInfo.getName());
        String formConfiguration = "";
        if (formConfig != null && formConfig.exists()) {
            formConfiguration = IOUtils.toString(new FileInputStream(formConfig), StandardCharsets.UTF_8);
        }
        Form form;
        if (formFile != null && formFile.exists()) {
            form = new Form(formInfo, IOUtils.toString(new FileInputStream(formFile), StandardCharsets.UTF_8), readFormConfiguration(formConfiguration));
        } else {
            throw new FileNotFoundException(formInfo.getName() + "'s config file was not found");
        }
        return form;
    }

    private FormConfiguration readFormConfiguration(String configStr) {
        if (StringUtils.isEmpty(configStr)) {
            return new FormConfiguration("", new FormResources());
        }

        JsonObject configJSON = new JsonObject(configStr);

        FormResources resources = new FormResources();

        JsonObject resourcesJSON = configJSON.getJsonObject("resources");

        resourcesJSON.getJsonObject("scripts").stream().forEach(entry -> resources.getScripts().put(entry.getKey(), entry.getValue().toString()));

        resourcesJSON.getJsonObject("styles").stream().forEach(entry -> resources.getStyles().put(entry.getKey(), entry.getValue().toString()));

        return new FormConfiguration(configJSON.getString("schema"), resources);
    }

    private File getFormFile(String formName, FormInfo formInfo) throws MalformedURLException {
        URL formUrl = new URL(classLoaderFormsUrl.toString() + File.separator + formName + "." + formInfo.getType().getValue());
        return FileUtils.toFile(formUrl);
    }

    private File getFormConfigFile(String formName) throws MalformedURLException {
        URL configUri = new URL(classLoaderFormsUrl.toString() + File.separator + formName + CONFIG_EXT);
        return FileUtils.toFile(configUri);
    }

    @Override
    public void updateFormContent(String formName, FormContent formContent) throws IOException {
        if (this.formsStorageUrl == null) {
            throw new RuntimeException("Cannot store form'" + formName + "'. Form storage couldnt be properly initialized.");
        }

        FormInfo formInfo = formInfoMap.get(formName);

        if (formInfo == null) {
            throw new RuntimeException("Cannot find form '" + formName + "'");
        }

        if (formContent == null) {
            throw new RuntimeException("Invalid form content");
        }

        File formFile = getPersistableFormFile(formInfo);
        File configFile = getPersistableConfigFile(formInfo);

        if (!formFile.exists() || !configFile.exists()) {
            throw new RuntimeException("Cannot store form '" + formName + "'. Unable to find form");
        }

        FileUtils.write(formFile, formContent.getSource(), StandardCharsets.UTF_8);

        FileUtils.write(configFile, JsonObject.mapFrom(formContent.getConfiguration()).encodePrettily(), StandardCharsets.UTF_8);

        LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), TimeZone.getDefault().toZoneId());
        FormInfo newInfo = new FormInfo(formName, formInfo.getType(), lastModified);

        formInfoMap.put(formName, newInfo);

        modifiedForms.put(formName, new Form(newInfo, formContent.getSource(), formContent.getConfiguration()));
    }

    private File getPersistableFormFile(FormInfo formInfo) {
        return FileUtils.getFile(this.formsStorageUrl.getFile() + "/" + formInfo.getName() + "." + formInfo.getType().getValue());
    }

    private File getPersistableConfigFile(FormInfo formInfo) {
        return FileUtils.getFile(this.formsStorageUrl.getFile() + "/" + formInfo.getName() + CONFIG_EXT);
    }

    private void init() {
        readFormResources().stream()
                .filter(file -> hasConfigFile(FilenameUtils.removeExtension(file.getName())))
                .forEach(file -> {
                    LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), TimeZone.getDefault().toZoneId());
                    formInfoMap.put(FilenameUtils.removeExtension(file.getName()),
                            new FormInfo(FilenameUtils.removeExtension(file.getName()), getFormType(FilenameUtils.getExtension(file.getName())), lastModified));
                });
    }

    private Collection<File> readFormResources() {
        if (classLoaderFormsUrl != null) {
            LOGGER.info("form's files path is {}", classLoaderFormsUrl.toString());
            File rootFolder = FileUtils.toFile(classLoaderFormsUrl);
            return FileUtils.listFiles(rootFolder, new String[] { "html", "tsx" }, false);
        }
        return Collections.emptyList();
    }

    private boolean hasConfigFile(String formName) {
        try {
            return getFormConfigFile(formName).exists();
        } catch (MalformedURLException e) {
            LOGGER.info(e.getMessage());
            return false;
        }
    }
}
