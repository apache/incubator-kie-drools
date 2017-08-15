/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.workitem.rest;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestGeoCodeApiCallWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(org.jbpm.process.workitem.rest.RestGeoCodeApiCallWorkItemHandler.class);

    private java.util.List<ResultGeoCodeApi> results;
    private int httpResponseCode;

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            //APPID from yahoo TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-
            String URL = (String) workItem.getParameter("URL");
            workItem.getParameters().remove("URL");
            URL = URL + (String) workItem.getParameter("Service");
            workItem.getParameters().remove("Service");
            URL = URL + (String) workItem.getParameter("Method");
            workItem.getParameters().remove("Method");

            java.util.Set<String> keys = workItem.getParameters().keySet();
            for (String parameter : keys) {
                URL = URL + parameter + "=" + workItem.getParameter(parameter) + "&";
            }

            java.net.HttpURLConnection connection;

            java.net.URL getUrl = new java.net.URL(URL);
            connection = (java.net.HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            logger.info("Content-Type: {}",
                        connection.getContentType());

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();
            String response = "";
            while ((line = reader.readLine()) != null) {
                response += line;
            }

            setHttpResponseCode(connection.getResponseCode());

            this.results = parseResults(response);

            logger.info("{}" + response);
            connection.disconnect();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private java.util.List<ResultGeoCodeApi> parseResults(String xml) {
        java.util.List<ResultGeoCodeApi> results = new java.util.ArrayList<ResultGeoCodeApi>();
        try {

            javax.xml.parsers.DocumentBuilderFactory docBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            // normalize text representation
            doc.getDocumentElement().normalize();
            org.w3c.dom.NodeList listOfResults = doc.getElementsByTagName("Result");
            for (int i = 0; i < listOfResults.getLength(); i++) {
                ResultGeoCodeApi result = new ResultGeoCodeApi();
                org.w3c.dom.Node nodeResult = listOfResults.item(i);
                if (nodeResult.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element elementResult = (org.w3c.dom.Element) nodeResult;
                    result.setPrecision(elementResult.getAttribute("precision"));

                    org.w3c.dom.NodeList latitudes = elementResult.getElementsByTagName("Latitude");
                    org.w3c.dom.Element latitudeElement = (org.w3c.dom.Element) latitudes.item(0);
                    org.w3c.dom.NodeList latitudeNodes = latitudeElement.getChildNodes();
                    result.setLatitude(((org.w3c.dom.Node) latitudeNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList longitudes = elementResult.getElementsByTagName("Longitude");
                    org.w3c.dom.Element longitudeElement = (org.w3c.dom.Element) longitudes.item(0);
                    org.w3c.dom.NodeList longitudeNodes = longitudeElement.getChildNodes();
                    result.setLongitude(((org.w3c.dom.Node) longitudeNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList addresses = elementResult.getElementsByTagName("Address");
                    org.w3c.dom.Element addressElement = (org.w3c.dom.Element) addresses.item(0);
                    org.w3c.dom.NodeList addressNodes = addressElement.getChildNodes();
                    result.setAddress(((org.w3c.dom.Node) addressNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList cities = elementResult.getElementsByTagName("City");
                    org.w3c.dom.Element cityElement = (org.w3c.dom.Element) cities.item(0);
                    org.w3c.dom.NodeList cityNodes = cityElement.getChildNodes();
                    result.setCity(((org.w3c.dom.Node) cityNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList states = elementResult.getElementsByTagName("State");
                    org.w3c.dom.Element stateElement = (org.w3c.dom.Element) states.item(0);
                    org.w3c.dom.NodeList stateNodes = stateElement.getChildNodes();
                    result.setState(((org.w3c.dom.Node) stateNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList zips = elementResult.getElementsByTagName("Zip");
                    org.w3c.dom.Element zipElement = (org.w3c.dom.Element) zips.item(0);
                    org.w3c.dom.NodeList zipNodes = zipElement.getChildNodes();
                    result.setZip(((org.w3c.dom.Node) zipNodes.item(0)).getNodeValue().trim());

                    org.w3c.dom.NodeList countries = elementResult.getElementsByTagName("Country");
                    org.w3c.dom.Element countryElement = (org.w3c.dom.Element) countries.item(0);
                    org.w3c.dom.NodeList countryNodes = countryElement.getChildNodes();
                    result.setCountry(((org.w3c.dom.Node) countryNodes.item(0)).getNodeValue().trim());

                    results.add(result);
                }
            }
        } catch (org.xml.sax.SAXException ex) {
            logger.error("Error durring processing",
                         ex);
        } catch (java.io.IOException ex) {
            logger.error("Error durring processing",
                         ex);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            logger.error("Error durring processing",
                         ex);
        }

        return results;
    }

    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // Do nothing, this work item cannot be aborted
    }

    /**
     * @return the results
     */
    public java.util.List<ResultGeoCodeApi> getResults() {
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
