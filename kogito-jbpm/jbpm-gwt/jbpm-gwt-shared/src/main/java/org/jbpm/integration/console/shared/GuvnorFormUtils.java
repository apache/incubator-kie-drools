package org.jbpm.integration.console.shared;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuvnorFormUtils {

    public static final String GUVNOR_FORM_LANGUAGE = "guvnor.form.language";
    
    private static final Logger logger = LoggerFactory.getLogger(GuvnorFormUtils.class);
    
    private final GuvnorConnectionUtils utils = new GuvnorConnectionUtils();
    
    public String getFormFromGuvnor(String name) {
        if (utils.guvnorExists()) {
             try {
                 String templateName;
                 if(utils.templateExistsInRepo(name)) {
                     templateName = name;
                 } else {
                     return null;
                 }
                 return getFormDefinitionFromGuvnor(templateName);
             } catch (Throwable t) {
                 logger.error("Could not load process template from Guvnor: " + t.getMessage());
                 return null;
             }
         } else {
             logger.warn("Could not connect to Guvnor.");
         }
        return null;
    }
    
    public String getFormDefinitionURLFromGuvnor(String templateName) {
        return utils.getFormTemplateURLFromGuvnor(templateName, "drl");
    }

    
    public String getFormDefinitionFromGuvnor(String name) {
        if (utils.guvnorExists()) {
            try {
                String templateName;
                if (utils.templateExistsInRepo(name)) {
                    templateName = name;
                } else {
                    return null;
                }
                String formTemplateURL = getFormDefinitionURLFromGuvnor(templateName);
                if (formTemplateURL != null) {
                    try {
                        return getStringForURL(formTemplateURL, "GET");
                    } catch (Exception e) {
                        logger.error("Exception getting input stream for form template url: " + formTemplateURL);
                        return null;
                    }
                } else {
                    logger.info("Could not get the form template from guvnor");
                    return null;
                }
            } catch (Throwable t) {
                logger.error("Could not load process template from Guvnor: "
                        + t.getMessage());
                return null;
            }
        } else {
            logger.warn("Could not connect to Guvnor.");
        }
        return null;
    }
    
    private String getStringForURL(String urlLocation, String requestMethod) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
        .setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection.setRequestProperty("Accept", "text/plain,text/html,application/xhtml+xml,application/xml");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setConnectTimeout(Integer.parseInt(utils.getGuvnorConnectTimeout()));
        connection.setReadTimeout(Integer.parseInt(utils.getGuvnorReadTimeout()));
        utils.applyAuth(connection);
        connection.connect();

        BufferedReader sreader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        
        return stringBuilder.toString();
    }

    
    public String getFormDefaultLanguage() {
        Properties props = utils.getGuvnorProperties();
        return utils.isEmpty(props.getProperty(GUVNOR_FORM_LANGUAGE)) ? "ftl" : props.getProperty(GUVNOR_FORM_LANGUAGE);
    }
}
