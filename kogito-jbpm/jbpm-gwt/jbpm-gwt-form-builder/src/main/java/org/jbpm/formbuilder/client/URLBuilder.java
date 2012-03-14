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
package org.jbpm.formbuilder.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class URLBuilder {

    public static String getMenuItemsURL(String contextPath) {
        return getBaseUrl() + contextPath + "/menu/items/";
    }

    public static String getMenuOptionsURL(String contextPath) {
        return getBaseUrl() + contextPath + "/menu/options/";
    }
    
    public static String saveFormURL(String contextPath, String packageName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/form/definitions/package/").append(packageName).toString();
    }

    public static String saveFormItemURL(String contextPath, String packageName, String formItemName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/form/items/package/").
            append(packageName).append("/name/").append(encode(formItemName)).toString();
    }
    
    public static String deleteFormURL(String contextPath, String packageName, String formName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/form/definitions/package/").
            append(packageName).append("/id/").append(formName).toString();
    }

    public static String deleteFormItemURL(String contextPath,
            String packageName, String formItemName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/formItems/package/").
            append(packageName).append("/formItemName/").append(encode(formItemName)).toString();
    }

    public static String generateFormURL(String contextPath, String language) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/form/preview/lang/").append(language).toString();
    }

    public static String getIoAssociationsURL(String contextPath, String packageName) {
        return getBaseUrl() + contextPath + "/io/package/" + packageName + "/";
    }

    public static String getIoAssociationURL(String contextPath, String pkgName, String processName, String taskName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/io/package/").append(pkgName).
            append("/process/").append(encode(processName)).append("/task/").append(encode(taskName)).toString();
    }

    public static String getValidationsURL(String contextPath) {
        return getBaseUrl() + contextPath + "/menu/validations/";
    }
    
    public static String getFormURL(String contextPath, String packageName, String formName) {
        return new StringBuilder(getFormsURL(contextPath, packageName)).append("/id/").append(encode(formName)).toString();
    }
    
    public static String getFormsURL(String contextPath, String packageName) {
        return new StringBuilder(getBaseUrl()).append(contextPath).
            append("/form/definitions/package/").append(packageName).toString();
    }

    public static String getRepresentationMappingsURL(String contextPath) {
        return getBaseUrl() + contextPath + "/menu/mappings";
    }

    public static String loadFormTemplateURL(String contextPath, String language) {
        return new StringBuilder(getBaseUrl()).append(contextPath).
            append("/form/template/lang/").append(encode(language)).toString();
    }

    public static String uploadFileURL(String contextPath, String packageName) {
        return new StringBuilder(getBaseUrl()).
            append("uploadFile?packageName=").
            append(packageName).toString();
    }

    public static String uploadActionURL() {
        return new StringBuilder(getBaseUrl()).append("uploadAction").toString();
    }

    private static String encode(String string) { 
        return URL.encodePathSegment(string);
    }

    private static String getBaseUrl() {
        return GWT.getModuleBaseURL().replace("/" + GWT.getModuleName(), "");
    }

    public static String getCurrentRolesURL(String contextPath) {
        return new StringBuilder(getBaseUrl()).append(contextPath).
            append("/user/current/roles").toString();
    }

    public static String getLogoutURL(String contextPath) {
        return new StringBuilder(getBaseUrl()).append(contextPath).
            append("/user/current/logout").toString();
    }

    public static String deleteFileURL(String contextPath, String packageName, String url) {
        return new StringBuilder(getBaseUrl()).append(contextPath).append("/files/package/").
            append(encode(packageName)).append(url).toString();
    }

    public static String getFilesURL(String contextPath, String packageName, List<String> types) {
        StringBuilder params = new StringBuilder();
        if (types != null) {
            for (Iterator<String> iter = types.iterator(); iter.hasNext();) {
                params.append("type=").append(iter.next());
                if (iter.hasNext()) {
                    params.append("&");
                }
            }
        }
        return new StringBuilder(getBaseUrl()).append(contextPath).
            append("/files/package/").append(encode(packageName)).append("?").
            append(params.toString()).toString();
    }
}
