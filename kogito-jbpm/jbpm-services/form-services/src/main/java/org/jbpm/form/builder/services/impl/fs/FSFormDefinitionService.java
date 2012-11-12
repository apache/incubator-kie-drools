/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.form.builder.services.impl.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jbpm.form.builder.services.api.FileException;
import org.jbpm.form.builder.services.api.FileService;
import org.jbpm.form.builder.services.api.FormDefinitionService;
import org.jbpm.form.builder.services.api.FormServiceException;
import org.jbpm.form.builder.services.impl.base.BaseFormDefinitionService;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;
import org.jbpm.form.builder.services.model.forms.FormRepresentationEncoder;

/**
 *

 */
public class FSFormDefinitionService extends BaseFormDefinitionService implements FormDefinitionService {

    private String baseUrl;
    private String fileSeparator = System.getProperty("file.separator");
    @Inject
    private FileService fileService;
    

    public List<FormRepresentation> getForms() throws FormServiceException {
        List<FormRepresentation> forms = new ArrayList<FormRepresentation>();
        List<String> loadFilesByType;
        try {
            loadFilesByType = fileService.loadFilesByType("formdef");
        } catch (FileException ex) {
            throw new FormServiceException(ex.getMessage(), ex);
        }
        for (String assetId : loadFilesByType) {
            if (isFormName(assetId)) {
                FormRepresentation form = getForm(assetId.replace(".formdef", "").replace(baseUrl, ""));
                forms.add(form);
            }
        }
        return forms;
    }

    public Map<String, FormItemRepresentation> getFormItems() throws FormServiceException {
        try {
            Map<String, FormItemRepresentation> items = new HashMap<String, FormItemRepresentation>();
            List<String> loadFilesByType = fileService.loadFilesByType("json");
            for (String assetId : loadFilesByType) {
                if (isItemName(assetId)) {
                    FormItemRepresentation item = getFormItem(assetId.replace(".json", ""));
                    items.put(assetId, item);
                }
            }
            return items;
        } catch (Exception ex) {
            throw new FormServiceException(ex.getMessage(), ex);
        }
    }

    public String saveForm(FormRepresentation form) throws FormServiceException {
        String finalUrl = baseUrl + fileSeparator + form.getName() + ".formdef";
        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        try {
            String encoded = encoder.encode(form);
            File file = new File(finalUrl);
            FileUtils.writeStringToFile(file, encoded);
        } catch (Exception ex) {
            throw new FormServiceException(ex.getMessage(), ex);
        }

        return form.getName();
    }

    public String saveFormItem(String formItemName, FormItemRepresentation formItem) throws FormServiceException {
        StringBuilder builder = new StringBuilder();
        updateItemName(formItemName, builder);
        String finalUrl = baseUrl + fileSeparator + builder.toString() + ".json";

        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        try {
            FileUtils.writeStringToFile(new File(finalUrl), encoder.encode(formItem));
        } catch (Exception ex) {
            throw new FormServiceException(ex.getMessage(), ex);
        }

        return formItemName;
    }

    public void deleteForm(String formId) throws FormServiceException {
        String deleteUrl = baseUrl + fileSeparator + formId + ".formdef";
        FileUtils.deleteQuietly(new File(deleteUrl));
    }

    public void deleteFormByURL(String formUrl) throws FormServiceException {
        
        FileUtils.deleteQuietly(new File(formUrl));
    }

    public void deleteFormItem(String formItemId) throws FormServiceException {
        if (formItemId != null && !"".equals(formItemId)) {
            String deleteUrl = baseUrl + fileSeparator + formItemId + ".json";
            FileUtils.deleteQuietly(new File(deleteUrl));
        }

    }
    public void deleteFormItemByURL(String itemUrl) throws FormServiceException {
            FileUtils.deleteQuietly(new File(itemUrl));

    }

    public FormRepresentation getForm(String formId) throws FormServiceException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        File file = new File(baseUrl + fileSeparator + formId + ".formdef");
        String json;
        try {
            json = FileUtils.readFileToString(file);
            return decoder.decode(json);
        } catch (Exception ex) {
            throw new FormServiceException(ex.getMessage(), ex);
        }
    }

    public FormRepresentation getFormByUUID(String uuid) throws FormServiceException {
        throw new UnsupportedOperationException("Not supported in FS implementation.");
    }

    public FormItemRepresentation getFormItem(String formItemId) throws FormServiceException {
        if (formItemId != null && !"".equals(formItemId)) {
            try {
                FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
                String getUrl = baseUrl + fileSeparator + formItemId + ".json";
                String json = FileUtils.readFileToString(new File(getUrl));
                return decoder.decodeItem(json);
            } catch (Exception ex) {
                throw new FormServiceException(ex.getMessage(), ex);
            }
        }
        return null;
    }

    public void saveTemplate(String templateName, String content) throws FormServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void setFileService(FSFileService fileService) {
        this.fileService = fileService;
    }
}
