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

import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scriptviews.RestServiceScriptHelperView;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * 
 */
@Reflectable
public class RestServiceScriptHelper extends AbstractScriptHelper {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
	private RestServiceScriptHelperView view;

	private String method = "";
	private String url = "";
	private String resultStatus = "";
	private String resultXPath = "";
	private String exportVariableName = "";
	private String responseLanguage = "";
	private Map<String, String> headers = new HashMap<String, String>();
	
    public RestServiceScriptHelper() {
        super();
    }
    
    @Override
    public Map<String, Object> getDataMap() {
    	if (view != null) {
    		view.writeDataTo(this);
    	}
        String urlValue = this.url;
        String methodValue = this.method;
        String resultStatusValue = this.resultStatus;
        String resultPathValue = this.resultXPath;
        String exportVariableNameValue = this.exportVariableName;
        String responseLanguageValue = this.responseLanguage;
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("@className", RestServiceScriptHelper.class.getName());
        map.put("urlValue", urlValue);
        map.put("methodValue", methodValue);
        map.put("resultStatusValue", resultStatusValue);
        map.put("resultPathValue", resultPathValue);
        map.put("exportVariableNameValue", exportVariableNameValue);
        map.put("responseLanguageValue", responseLanguageValue);
        Map<String, Object> headersMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersMap.put(entry.getKey(), entry.getValue());
        }
        map.put("headers", headersMap);
        return map;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) throws FormEncodingException {
        String urlValue = (String) dataMap.get("urlValue");
        if (urlValue == null) urlValue = "";
        String methodValue = (String) dataMap.get("methodValue");
        if (methodValue == null) methodValue = "";
        String resultStatusValue = (String) dataMap.get("resultStatusValue");
        if (resultStatusValue == null) resultStatusValue = "";
        String resultPathValue = (String) dataMap.get("resultPathValue");
        if (resultPathValue == null) resultPathValue = "";
        String exportVariableNameValue = (String) dataMap.get("exportVariableNameValue");
        if (exportVariableNameValue == null) exportVariableNameValue = "";
        String responseLanguageValue = (String) dataMap.get("responseLanguageValue");
        if (responseLanguageValue == null) responseLanguageValue = "";
        @SuppressWarnings("unchecked")
        Map<String, Object> headerMap = (Map<String, Object>) dataMap.get("headers"); 

        this.url = urlValue;
        this.method = methodValue;
        this.resultStatus = resultStatusValue;
        this.responseLanguage = responseLanguageValue;
        this.resultXPath = resultPathValue;
        this.exportVariableName = exportVariableNameValue;
        headers.clear();
        if (headerMap != null) {
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                headers.put(entry.getKey(), (String) entry.getValue());
            }
        }
        if (view != null) {
        	view.readDataFrom(this);
        }
    }

    @Override
    public String asScriptContent() {
        long id = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        if (view != null) {
        	view.writeDataTo(this);
        }
        sb.append("var " + exportVariableName + " = null;");
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
        sb.append("      " + exportVariableName + " = new Array();");
        sb.append("      for (var idx = 0; idx < xmlNodeList" + id + ".length; idx++ ) {");
        sb.append("         " + exportVariableName + "[idx] = xmlNodeList" + id + ".item(idx).text;");
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
    		view = new RestServiceScriptHelperView(this);
    	}
        return view;
    }

    @Override
    public String getName() {
        return i18n.RestServiceScriptHelperName();
    }

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getResultXPath() {
		return resultXPath;
	}

	public void setResultXPath(String resultXPath) {
		this.resultXPath = resultXPath;
	}

	public String getExportVariableName() {
		return exportVariableName;
	}

	public void setExportVariableName(String exportVariableName) {
		this.exportVariableName = exportVariableName;
	}

	public String getResponseLanguage() {
		return responseLanguage;
	}

	public void setResponseLanguage(String responseLanguage) {
		this.responseLanguage = responseLanguage;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
