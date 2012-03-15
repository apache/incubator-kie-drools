/**
 * Copyright 2010 JBoss Inc
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
package org.jbpm.integration.console.shared;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Base64;

public class GuvnorConnectionUtils {
    public static final String GUVNOR_PROTOCOL_KEY = "guvnor.protocol";
    public static final String GUVNOR_HOST_KEY = "guvnor.host";
    public static final String GUVNOR_USR_KEY = "guvnor.usr";
    public static final String GUVNOR_PWD_KEY = "guvnor.pwd";
    public static final String GUVNOR_PACKAGES_KEY = "guvnor.packages";
    public static final String GUVNOR_SUBDOMAIN_KEY = "guvnor.subdomain";
    public static final String GUVNOR_CONNECTTIMEOUT_KEY = "guvnor.connect.timeout";
    public static final String GUVNOR_READTIMEOUT_KEY = "guvnor.read.timeout";
    public static final String EXT_BPMN = "bpmn";
    public static final String EXT_BPMN2 = "bpmn2";
    
    private static final Logger logger = LoggerFactory.getLogger(GuvnorConnectionUtils.class);
    private static Properties properties = new Properties();
    
    static {
        try {
            properties.load(GuvnorConnectionUtils.class.getResourceAsStream("/jbpm.console.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load jbpm.console.properties", e);
        }
    }
    
    public String getProcessImageURLFromGuvnor(String processId) {
        List<String> allPackages = getPackageNames();
        for(String pkg : allPackages) {
            // query the package to get a list of all processes in this package
            List<String> allProcessesInPackage = getAllProcessesInPackage(pkg);
            // check each process to see if it has the matching id set
            for(String process : allProcessesInPackage) {
                String processContent = getProcessSourceContent(pkg, process);
                Pattern p = Pattern.compile("<\\S*process[\\s\\S]*id=\"" + processId + "\"", Pattern.MULTILINE);
                Matcher m = p.matcher(processContent);
                if(m.find()) {
                    try {
                        String imageBinaryURL = getGuvnorProtocol()
                        + "://"
                        + getGuvnorHost()
                        + "/"
                        + getGuvnorSubdomain()
                        + "/org.drools.guvnor.Guvnor/package/"
                        + pkg
                        + "/LATEST/"
                        + URLEncoder.encode(processId, "UTF-8")
                        + "-image.png";
                        
                        return imageBinaryURL;
                    } catch (Exception e) {
                       logger.error("Could not read process image: " + e.getMessage());
                       throw new RuntimeException("Could not read process image: " + e.getMessage());
                    }
                }
            }
        }
        logger.info("Did not find process image for: " + processId);
        return null;
    }
    
    public String getFormTemplateURLFromGuvnor(String templateName) {
        return getFormTemplateURLFromGuvnor(templateName, "drl");
    }
    
    public String getFormTemplateURLFromGuvnor(String templateName, String format) {
        List<String> allPackages = getPackageNames();
        try {
            for(String pkg : allPackages) {
                String templateURL = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/"
                + pkg
                + "/assets/"
                + URLEncoder.encode(templateName, "UTF-8");
                
                URL checkURL = new URL(templateURL);
                HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
                checkConnection.setRequestMethod("GET");
                checkConnection.setRequestProperty("Accept", "application/atom+xml");
                checkConnection.setConnectTimeout(Integer.parseInt(getGuvnorConnectTimeout()));
                checkConnection.setReadTimeout(Integer.parseInt(getGuvnorReadTimeout()));
                applyAuth(checkConnection);
                checkConnection.connect();
                if(checkConnection.getResponseCode() == 200) {
                    
                    String toReturnURL = getGuvnorProtocol()
                    + "://"
                    + getGuvnorHost()
                    + "/"
                    + getGuvnorSubdomain()
                    + "/org.drools.guvnor.Guvnor/package/"
                    + pkg
                    + "/LATEST/"
                    + URLEncoder.encode(templateName, "UTF-8")
                    + "." + format;
                    
                    return toReturnURL;
                }
            }
        } catch (Exception e) {
           logger.error("Exception returning template url : " + e.getMessage());
           return null;
        }
        logger.info("Could not find process template url for: " + templateName);
        return null;
    }
    
    public InputStream getFormTemplateFromGuvnor(String templateName) {
        String formTemplateURL = getFormTemplateURLFromGuvnor(templateName);
        if(formTemplateURL != null) {
            try {
                return getInputStreamForURL(formTemplateURL, "GET");
            } catch (Exception e) {
                logger.error("Exception getting input stream for form template url: " + formTemplateURL);
                return null;
            }
        } else {
            logger.info("Could not get the form template from guvnor");
            return null;
        }
    }
    
    public byte[] getProcessImageFromGuvnor(String processId) {
        String processImageURL = getProcessImageURLFromGuvnor(processId);
        if(processImageURL != null) {
            try {
                InputStream is = getInputStreamForImageURL(processImageURL, "GET");
                if (is != null) {
                    return IOUtils.toByteArray(is);
                } else {
                    return null;
                }
            } catch (Exception e) {
               logger.error("Exception reading process image: " + e.getMessage());
               throw new RuntimeException("Could not read process image: " + e.getMessage());
            }
        } else {
            logger.info("Invalid process image for: " + processId);
            return null;
        }
    }
    
    private String getProcessSourceContent(String packageName, String assetName) {
        String assetSourceURL = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/" + packageName + "/assets/" + assetName
                + "/source/";

        try {
            InputStream in = getInputStreamForURL(assetSourceURL, "GET");
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("Error retrieving asset content: " + e.getMessage());
            return "";
        }
    }
    
    public List<String> getAllProcessesInPackage(String pkgName) {
        List<String> processes = new ArrayList<String>();
        String assetsURL = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/"
                + pkgName
                + "/assets/";
        
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(getInputStreamForURL(assetsURL, "GET"));

            String format = "";
            String title = ""; 
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("format".equals(reader.getLocalName())) {
                        format = reader.getElementText();
                    } 
                    if ("title".equals(reader.getLocalName())) {
                        title = reader.getElementText();
                    }
                    if ("asset".equals(reader.getLocalName())) {
                        if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                            processes.add(title);
                            title = "";
                            format = "";
                        }
                    }
                }
            }
            // last one
            if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                processes.add(title);
            }
        } catch (Exception e) {
            logger.error("Error finding processes in package: " + e.getMessage());
        } 
        return processes;
    }
    
    public List<String> getPackageNames() {
        List<String> allPackages = getPackageNamesFromGuvnor();
        if(!isEmpty(properties.getProperty(GUVNOR_PACKAGES_KEY))) {
            // make sure that all user defined package names are in the list of provided packages
            String[] providedPackages = properties.getProperty(GUVNOR_PACKAGES_KEY).trim().split( ",\\s*" );
            List<String> retList = new ArrayList<String>();
            for(String pkg : providedPackages) {
                if(allPackages.contains(pkg)) {
                    retList.add(pkg);
                }
            }
            return retList;
        } else {
            return allPackages;
        }
    }
    
    public StringReader createChangeSet() {
        try {
            StringTemplate changeSetTemplate = new StringTemplate(
                    readFile(GuvnorConnectionUtils.class.getResourceAsStream("/ChangeSet.st")));
            TemplateInfo info = new TemplateInfo(getGuvnorProtocol(), getGuvnorHost(), 
                    getGuvnorUsr(), getGuvnorPwd(), getGuvnorSubdomain(), getPackageNames());
            changeSetTemplate.setAttribute("data",  info.getData());
            return new StringReader(changeSetTemplate.toString());
        } catch (IOException e) {
            logger.error("Exception creating changeset: " + e.getMessage());
            return new StringReader("");
        }
    }
    

    public String getGuvnorProtocol() {
        return isEmpty(properties.getProperty(GUVNOR_PROTOCOL_KEY)) ? "" : properties.getProperty(GUVNOR_PROTOCOL_KEY).trim();
    }
    
    public String getGuvnorHost() {
        if(!isEmpty(properties.getProperty(GUVNOR_HOST_KEY))) {
            String retStr = properties.getProperty(GUVNOR_HOST_KEY).trim();
            if(retStr.startsWith("/")){
                retStr = retStr.substring(1);
            }
            if(retStr.endsWith("/")) {
                retStr = retStr.substring(0,retStr.length() - 1);
            }
            return retStr;
        } else {
            return "";
        }
    }
    
    public String getGuvnorSubdomain() {
        return isEmpty(properties.getProperty(GUVNOR_SUBDOMAIN_KEY)) ? "" : properties.getProperty(GUVNOR_SUBDOMAIN_KEY).trim();
    }
    
    public String getGuvnorUsr() {
        return isEmpty(properties.getProperty(GUVNOR_USR_KEY)) ? "" : properties.getProperty(GUVNOR_USR_KEY).trim();
    }
    
    public String getGuvnorPwd() {
        return isEmpty(properties.getProperty(GUVNOR_PWD_KEY)) ? "" : properties.getProperty(GUVNOR_PWD_KEY).trim();
    }
    
    public String getGuvnorPackages() {
        return isEmpty(properties.getProperty(GUVNOR_PACKAGES_KEY)) ? "" : properties.getProperty(GUVNOR_PACKAGES_KEY).trim();
    }
    
    public String getGuvnorConnectTimeout() {
        return isEmpty(properties.getProperty(GUVNOR_CONNECTTIMEOUT_KEY)) ? "10000" : properties.getProperty(GUVNOR_CONNECTTIMEOUT_KEY).trim();
    }
    
    public String getGuvnorReadTimeout() {
        return isEmpty(properties.getProperty(GUVNOR_READTIMEOUT_KEY)) ? "10000" : properties.getProperty(GUVNOR_READTIMEOUT_KEY).trim();
    }
    
    protected Properties getGuvnorProperties() {
        return properties;
    }
    
    private List<String> getPackageNamesFromGuvnor() {
        List<String> packages = new ArrayList<String>();
        String packagesURL = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(getInputStreamForURL(packagesURL, "GET"));
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        String pname = reader.getElementText();
                        if(!pname.equalsIgnoreCase("Packages")) {
                             packages.add(pname);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error retriving packages from guvnor: " + e.getMessage());
        }
        return packages;
    }
    
    
    public boolean templateExistsInRepo(String templateName) throws Exception {
        List<String> allPackages = getPackageNames();
        try {
            for(String pkg : allPackages) {
                String templateURL = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/"
                + pkg
                + "/assets/"
                + URLEncoder.encode(templateName, "UTF-8");
                
                URL checkURL = new URL(templateURL);
                HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
                checkConnection.setRequestMethod("GET");
                checkConnection.setRequestProperty("Accept", "application/atom+xml");
                checkConnection.setConnectTimeout(Integer.parseInt(getGuvnorConnectTimeout()));
                checkConnection.setReadTimeout(Integer.parseInt(getGuvnorReadTimeout()));
                applyAuth(checkConnection);
                checkConnection.connect();
                if(checkConnection.getResponseCode() == 200) {
                    return true;
                }
            }
        } catch (Exception e) {
           logger.error("Exception checking template url : " + e.getMessage());
           return false;
        }
        logger.info("Could not find process template for: " + templateName);
        return false;
    }
    
    protected void applyAuth(HttpURLConnection connection) {
        String auth = getGuvnorUsr() + ":" + getGuvnorPwd();
        connection.setRequestProperty("Authorization", "Basic "
                + Base64.encodeBase64String(auth.getBytes()));
    }

    private InputStream getInputStreamForImageURL(String urlLocation, String requestMethod) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
        .setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection.setRequestProperty("Accept", "text/plain,text/html,application/xhtml+xml,application/xml");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setConnectTimeout(Integer.parseInt(getGuvnorConnectTimeout()));
        connection.setReadTimeout(Integer.parseInt(getGuvnorReadTimeout()));
        applyAuth(connection);

        connection.connect();
        return connection.getInputStream();
    }
    
    private InputStream getInputStreamForURL(String urlLocation,
            String requestMethod) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
        .setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection.setRequestProperty("Accept", "text/plain,text/html,application/xhtml+xml,application/xml");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setConnectTimeout(Integer.parseInt(getGuvnorConnectTimeout()));
        connection.setReadTimeout(Integer.parseInt(getGuvnorReadTimeout()));
        applyAuth(connection);
        connection.connect();

        BufferedReader sreader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        
        return new ByteArrayInputStream(stringBuilder.toString().getBytes(
                "UTF-8"));
    }
    
    protected boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( str.charAt( i ) != ' ' ) {
                return false;
            }
        }
        return true;
    }
    
    private String readFile(InputStream inStream) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(inStream);
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
    
    public boolean guvnorExists() {
        String checkURLStr = getGuvnorProtocol()
                + "://"
                + getGuvnorHost()
                + "/"
                + getGuvnorSubdomain()
                + "/rest/packages/";
        
        try {
            URL checkURL = new URL(checkURLStr);
            HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
            checkConnection.setRequestMethod("GET");
            checkConnection.setRequestProperty("Accept", "application/atom+xml");
            checkConnection.setConnectTimeout(4000);
            applyAuth(checkConnection);
            checkConnection.connect();
            return (checkConnection.getResponseCode() == 200);
        } catch (Exception e) {
            logger.error("Error checking guvnor existence: " + e.getMessage());
            return false;
        } 
    }
    
    private class TemplateInfo {
        List<String> data = new ArrayList<String>();
        
        public TemplateInfo(String protocol, String host, String usr, String pwd,
                String subdomain, List<String> packages) {
            for(String pkg : packages) {
                StringBuffer sb = new StringBuffer();
                sb.append("<resource source=\"");
                sb.append(protocol).append("://");
                sb.append(host).append("/");
                sb.append(subdomain).append("/").append("org.drools.guvnor.Guvnor/package/");
                sb.append(pkg).append("/LATEST\"");
                sb.append(" type=\"PKG\"");
                if(!isEmpty(usr) && !isEmpty(pwd)) {
                    sb.append(" basicAuthentication=\"enabled\"");
                    sb.append(" username=\"").append(usr).append("\"");
                    sb.append(" password=\"").append(pwd).append("\"");
                }
                sb.append(" />");
                data.add(sb.toString());
            }
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }
}
