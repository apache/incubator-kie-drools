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
package org.jbpm.formbuilder.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;
import org.jbpm.formbuilder.server.xml.AssetDTO;
import org.jbpm.formbuilder.server.xml.PackageDTO;
import org.jbpm.formbuilder.server.xml.PackageListDTO;

public class GuvnorHelper {

	public static final String ENCODING = "UTF-8"; 
	
    private final String baseUrl;
    private final String user;
    private final String password;
    private final String domainName;
    private final int portNumber;
    
    public GuvnorHelper(String baseUrl, String user, String password) {
        this.baseUrl = baseUrl;
        this.user = user;
        this.password = password;
        int beginIndex = this.baseUrl.indexOf("://") + 3;
        int endIndex = this.baseUrl.indexOf("/", beginIndex);
        if (endIndex < 0) {
        	endIndex = this.baseUrl.length();
        }
		String aux = this.baseUrl.substring(beginIndex, endIndex);
		if (aux.contains(":")) {
			String[] parts = aux.split(":");
			this.domainName = parts[0];
			this.portNumber = Integer.valueOf(parts[1]);
		} else {
			this.domainName = aux;
			this.portNumber = 80;
		}
        
    }

    private HttpClient client = null;
    
    public void setClient(HttpClient client) {
        this.client = client;
    }
    
    public HttpClient getHttpClient() {
        if (client == null) {
            return new HttpClient();
        }
        return client;
    }
    
    public GetMethod createGetMethod(String url) {
        return new GetMethod(url);
    }
    
    public DeleteMethod createDeleteMethod(String url) {
        return new DeleteMethod(url);
    }
    
    public PutMethod createPutMethod(String url) {
        return new PutMethod(url);
    }
    
    public PostMethod createPostMethod(String url) {
        return new PostMethod(url);
    }

    public String getPackageNameByContentUUID(String uuid) throws JAXBException, IOException {
        HttpClient client = getHttpClient();
        GetMethod call = createGetMethod(getRestBaseUrl());
        try {
            setAuth(client, call);
            call.addRequestHeader("Accept", "application/xml");
            client.executeMethod(call);
            PackageListDTO dto = jaxbTransformation(PackageListDTO.class, call.getResponseBodyAsStream(), PackageListDTO.RELATED_CLASSES);
            for (PackageDTO pkg : dto.getPackage()) {
                if (uuid.equals(pkg.getMetadata().getUuid())) {
                    return pkg.getTitle();
                } 
            }
            for (PackageDTO pkg : dto.getPackage()) {
                for (String url : pkg.getAssets()) {
                    GetMethod subCall = createGetMethod(url);
                    try {
                        subCall.setRequestHeader("Accept", "application/xml");
                        client.executeMethod(subCall);
                        AssetDTO subDto = jaxbTransformation(AssetDTO.class, subCall.getResponseBodyAsStream(), AssetDTO.RELATED_CLASSES);
                        if (subDto.getMetadata().getUuid().equals(uuid)) {
                            return pkg.getTitle();
                        }
                    } finally {
                        subCall.releaseConnection();
                    }
                }
            }
        } finally {
            call.releaseConnection();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T jaxbTransformation(Class<T> retType, InputStream input, Class<?>... boundTypes) throws JAXBException, IOException {
        String content = IOUtils.toString(input);
        StringReader reader = new StringReader(content);
        JAXBContext ctx = JAXBContext.newInstance(boundTypes);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(reader);
        return (T) obj;
    }
    
    public String jaxbSerializing(Object obj, Class<?>... boundTypes) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(boundTypes);
        Marshaller marshaller = ctx.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }
    
    public void setAuth(HttpClient client, HttpMethod method) {
    	if (notEmpty(this.user) && notEmpty(this.password)) {
    		client.getParams().setAuthenticationPreemptive(true);
    		UsernamePasswordCredentials defaultcreds = 
    				new UsernamePasswordCredentials(this.user, this.password);
    		AuthScope authScope = new AuthScope(this.domainName, this.portNumber, AuthScope.ANY_REALM);
			client.getState().setCredentials(authScope, defaultcreds);
    	}
    }
    
    private boolean notEmpty(String s) {
    	return s != null && !"".equals(s);
    }
    
    public String getApiSearchUrl(String pkgName) throws UnsupportedEncodingException {
        return new StringBuilder(this.baseUrl).
            append("/org.drools.guvnor.Guvnor/api/packages/").
            append(URLEncoder.encode(pkgName, ENCODING)).append("/").toString();
    }

    public String getRestBaseUrl() {
        return new StringBuilder(this.baseUrl).append("/rest/packages/").toString();
    }

    public String getUser() {
        return this.user;
    }
}
