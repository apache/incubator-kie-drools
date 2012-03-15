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
package org.jbpm.formbuilder.client.effect.scripthandlers;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scriptviews.PopulateComboBoxScriptHelperView;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * 
 */
@Reflectable
public class PopulateComboBoxScriptHelper extends AbstractScriptHelper {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    private String url = "";
    private String method = "";
    private String resultStatus = "";
    private String responseLanguage = "";
    private String resultXPath = "";
    private String subPathForKeys = "";
    private String subPathForValues = "";
    private String checkBoxId = "";
    
    private Map<String, String> headers = new HashMap<String, String>();

    private PopulateComboBoxScriptHelperView view;

    public PopulateComboBoxScriptHelper() {
        super();
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        if (this.view != null) {
            this.view.writeDataTo(this);
        }
        String urlValue = this.url;
        String methodValue = this.method;
        String resultStatusValue = this.resultStatus;
        String resultPathValue = this.resultXPath;
        String subPathForKeysValue = this.subPathForKeys;
        String subPathForValuesValue = this.subPathForValues;
        String checkBoxIdValue = this.checkBoxId;
        String responseLanguageValue = this.responseLanguage;
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("@className", RestServiceScriptHelper.class.getName());
        map.put("urlValue", urlValue);
        map.put("methodValue", methodValue);
        map.put("resultStatusValue", resultStatusValue);
        map.put("resultPathValue", resultPathValue);
        map.put("subPathForKeysValue", subPathForKeysValue);
        map.put("subPathForValuesValue", subPathForValuesValue);
        map.put("checkBoxIdValue", checkBoxIdValue);
        map.put("responseLanguageValue", responseLanguageValue);
        Map<String, Object> headersMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersMap.put(entry.getKey(), entry.getValue());
        }
        map.put("headers", headersMap);
        return map;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        String urlValue = (String) dataMap.get("urlValue");
        if (urlValue == null) urlValue = "";
        String methodValue = (String) dataMap.get("methodValue");
        if (methodValue == null) methodValue = "";
        String resultStatusValue = (String) dataMap.get("resultStatusValue");
        if (resultStatusValue == null) resultStatusValue = "";
        String resultPathValue = (String) dataMap.get("resultPathValue");
        if (resultPathValue == null) resultPathValue = "";
        String subPathForKeysValue = (String) dataMap.get("subPathForKeysValue");
        if (subPathForKeysValue == null) subPathForKeysValue = "";
        String subPathForValuesValue = (String) dataMap.get("subPathForValuesValue");
        if (subPathForValuesValue == null) subPathForValuesValue = "";
        String checkBoxIdValue = (String) dataMap.get("checkBoxIdValue");
        if (checkBoxIdValue == null) checkBoxIdValue = "";
        String responseLanguageValue = (String) dataMap.get("responseLanguageValue");
        if (responseLanguageValue == null) responseLanguageValue = "";
        @SuppressWarnings("unchecked")
        Map<String, Object> headerMap = (Map<String, Object>) dataMap.get("headers"); 

        this.url = urlValue;
        this.method = methodValue;
        this.resultStatus = resultStatusValue;
        this.resultXPath = resultPathValue;
        this.responseLanguage = responseLanguageValue;
        this.headers.clear();
        if (headerMap != null) {
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                headers.put(entry.getKey(), (String) entry.getValue());
            }
        }
        this.subPathForKeys = subPathForKeysValue;
        this.subPathForValues = subPathForValuesValue;
        this.checkBoxId = checkBoxIdValue;
        if (view != null) {
            view.readDataFrom(this);
        }
    }

    @Override
    public String asScriptContent() {
        long id = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("var checkBoxRef" + id + " = document.getElementById('" + checkBoxId + "');");
        sb.append("var url" + id + " = \"" + url + "\";");
        sb.append("var method" + id + " = \"" + method + "\";");
        sb.append("var xmlhttp" + id + ";");
        sb.append("if (window.XMLHttpRequest) {/* code for IE7+, Firefox, Chrome, Opera, Safari*/");
        sb.append("   xmlhttp" + id + "=new XMLHttpRequest();");
        sb.append("} else {/* code for IE6, IE5*/");
        sb.append("   xmlhttp" + id + "=new ActiveXObject(\"Microsoft.XMLHTTP\");");
        sb.append("}");
        sb.append("xmlhttp" + id + ".onreadystatechange=function() {");
        sb.append("   if (xmlhttp" + id + ".readyState==4 && xmlhttp" + id + ".status==" + resultStatus + ") {");  
        sb.append("      var xmlDoc" + id + " = null;");
        sb.append("      if (window.ActiveXObject) { /* code for IE*/");
        sb.append("         xmlDoc" + id + "=new ActiveXObject(\"Microsoft.XMLDOM\");");
        sb.append("         xmlDoc" + id + ".write(xmlhttp" + id + ".responseText);");
        sb.append("      } else if (document.implementation && document.implementation.createDocument) { /* code for Mozilla, Firefox, Opera, etc.*/");
        sb.append("         xmlDoc" + id + "=document.implementation.createDocument(\"\",\"\",null);");
        sb.append("         xmlDoc" + id + ".write(xmlhttp" + id + ".responseText);");
        sb.append("      } else {");
        sb.append("         alert('Your browser cannot handle this script');");
        sb.append("      }");
        sb.append("      var xmlNodeList" + id + " = xmlDoc" + id + ".selectNodes(\"" + resultXPath + "\");");
        sb.append("      checkBoxRef" + id + ".options.length = 0; /*clears combobox*/");
        sb.append("      for (var idx = 0; idx < xmlNodeList" + id + ".length; idx++ ) {");
        sb.append("         var opt = document.createElement('option');");
        sb.append("         opt.value = xmlNodeList" + id + ".item(idx).getElementsByTagName('" + subPathForKeys + "')[0].nodeValue;");
        sb.append("         opt.innerText = xmlNodeList" + id + ".item(idx).getElementsByTagName('" + subPathForValues + "')[0].nodeValue;");
        sb.append("         checkBoxRef" + id + ".options.add(opt);");
        sb.append("      }");
        sb.append("   }");
        sb.append("}");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append("xmlhttp" + id + ".setRequestHeader(\"" + header.getKey() + "\",\"" + header.getValue() + "\");");
        }
        sb.append("xmlhttp" + id + ".open(method" + id + ", url" + id + ", true);");
        sb.append("xmlhttp" + id + ".send();");
        return sb.toString();
    }

    @Override
    public Widget draw() {
        if (view == null) {
            view = new PopulateComboBoxScriptHelperView(this);
        }
        return view;
    }
    
    @Override
    public String getName() {
        return i18n.PopulateComboBoxScriptHelperName();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResponseLanguage() {
        return responseLanguage;
    }

    public void setResponseLanguage(String responseLanguage) {
        this.responseLanguage = responseLanguage;
    }

    public String getResultXPath() {
        return resultXPath;
    }

    public void setResultXPath(String resultXPath) {
        this.resultXPath = resultXPath;
    }

    public String getSubPathForKeys() {
        return subPathForKeys;
    }

    public void setSubPathForKeys(String subPathForKeys) {
        this.subPathForKeys = subPathForKeys;
    }

    public String getSubPathForValues() {
        return subPathForValues;
    }

    public void setSubPathForValues(String subPathForValues) {
        this.subPathForValues = subPathForValues;
    }

    public String getCheckBoxId() {
        return checkBoxId;
    }

    public void setCheckBoxId(String checkBoxId) {
        this.checkBoxId = checkBoxId;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
