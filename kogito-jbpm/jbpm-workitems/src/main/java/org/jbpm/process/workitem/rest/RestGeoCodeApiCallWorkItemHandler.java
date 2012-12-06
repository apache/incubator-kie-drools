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

package org.jbpm.process.workitem.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/*@author: salaboy*/
public class RestGeoCodeApiCallWorkItemHandler implements WorkItemHandler {

    private List<ResultGeoCodeApi> results;
    private int httpResponseCode;
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            //APPID from yahoo TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-
            String URL = (String) workItem.getParameter("URL");
            workItem.getParameters().remove("URL");
            URL = URL + (String) workItem.getParameter("Service");
            workItem.getParameters().remove("Service");
            URL = URL + (String) workItem.getParameter("Method");
            workItem.getParameters().remove("Method");

            Set<String> keys = workItem.getParameters().keySet();
            for (String parameter : keys) {
                URL = URL + parameter + "=" + workItem.getParameter(parameter) + "&";
            }


            HttpURLConnection connection;
            
            URL getUrl = new URL(URL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("Content-Type: " + connection.getContentType());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();
            String response = "";
            while ((line = reader.readLine()) != null) { 
                response +=line;
            } 

            setHttpResponseCode(connection.getResponseCode());

            
            this.results = parseResults(response);
            
            System.out.println("" + response);
            connection.disconnect();

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }





    }

    private List<ResultGeoCodeApi> parseResults(String xml){
        List<ResultGeoCodeApi> results = new ArrayList<ResultGeoCodeApi>();
        try {
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
              // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList listOfResults = doc.getElementsByTagName("Result");
            for(int i= 0; i< listOfResults.getLength(); i++){
                   ResultGeoCodeApi result = new ResultGeoCodeApi();
                   Node nodeResult = listOfResults.item(i);
                   if(nodeResult.getNodeType() == Node.ELEMENT_NODE){
                    Element elementResult = (Element)nodeResult;
                    result.setPrecision(elementResult.getAttribute("precision"));

                    
                    NodeList latitudes = elementResult.getElementsByTagName("Latitude");
                    Element latitudeElement = (Element)latitudes.item(0);
                    NodeList latitudeNodes = latitudeElement.getChildNodes();
                    result.setLatitude(((Node)latitudeNodes.item(0)).getNodeValue().trim());

                    NodeList longitudes = elementResult.getElementsByTagName("Longitude");
                    Element longitudeElement = (Element)longitudes.item(0);
                    NodeList longitudeNodes = longitudeElement.getChildNodes();
                    result.setLongitude(((Node)longitudeNodes.item(0)).getNodeValue().trim());

                    NodeList addresses = elementResult.getElementsByTagName("Address");
                    Element addressElement = (Element)addresses.item(0);
                    NodeList addressNodes = addressElement.getChildNodes();
                    result.setAddress(((Node)addressNodes.item(0)).getNodeValue().trim());


                    
                    NodeList cities = elementResult.getElementsByTagName("City");
                    Element cityElement = (Element)cities.item(0);
                    NodeList cityNodes = cityElement.getChildNodes();
                    result.setCity(((Node)cityNodes.item(0)).getNodeValue().trim());

                    NodeList states = elementResult.getElementsByTagName("State");
                    Element stateElement = (Element)states.item(0);
                    NodeList stateNodes = stateElement.getChildNodes();
                    result.setState(((Node)stateNodes.item(0)).getNodeValue().trim());

                    NodeList zips = elementResult.getElementsByTagName("Zip");
                    Element zipElement = (Element)zips.item(0);
                    NodeList zipNodes = zipElement.getChildNodes();
                    result.setZip(((Node)zipNodes.item(0)).getNodeValue().trim());

                    NodeList countries = elementResult.getElementsByTagName("Country");
                    Element countryElement = (Element)countries.item(0);
                    NodeList countryNodes = countryElement.getChildNodes();
                    result.setCountry(((Node)countryNodes.item(0)).getNodeValue().trim());

                    results.add(result);
                   }

            }

        } catch (SAXException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return results;

    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, this work item cannot be aborted
    }

    /**
     * @return the results
     */
    public List<ResultGeoCodeApi> getResults() {
        return results;
    }

    /**
     * @return the httpResponseCode
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * @param httpResponseCode the httpResponseCode to set
     */
    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }
}
