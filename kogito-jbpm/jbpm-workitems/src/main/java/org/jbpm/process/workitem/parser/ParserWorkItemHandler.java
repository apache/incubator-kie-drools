/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.process.workitem.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WorkItemHandler that is capable of parsing String to objects and vice-versa. Currently supports only two formats
 * <ul>
 *  <li>XML</li>
 *  <li>JSON</li>
 * </ul>
 * Here is the list of supported parameters:
 * <ul>
 *  <li>Input - The input data. If you provide a string, the parser will try to parse the string to an object of type Type and format Format (see below), if not, it will try to parse the object to a String of format Format. It is a required parameter;</li>
 *  <li>Format - It is a required parameter that can have the values JSON or XML;</li>
 *  <li>Result - The result is an output parameter that will be a string if you provide an Input object that is not of String type; and an object of type Type if you provide an Input object of type String.</li>
 *  <li>Type - The FQN of the object type (for example com.acme.Customer)</li>
 * </ul>
 * 
 * Providing these parameters correctly is enough to use the parser task. Please bear in mind that it uses JAXB to parse the object, so it must have the @XmlRootElement annotation.
 * 
 *
 */
public class ParserWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	public static final String JSON = "JSON";
	public static final String XML = "XML";
	/**
	 * Only supports JSON or XML.
	 */
	public static final String FORMAT = "Format";
	/**
	 * The target object type full qualified name (com.acme.Customer)
	 * 
	 */
	public static final String TYPE = "Type";
	/**
	 * The input object of type TYPE or String (if you set toObject)
	 */
	public static final String INPUT = "Input";
	/**
	 * The resulting object or String (if toObject is false it will be a
	 * String).
	 */
	public static final String RESULT = "Result";

	private ClassLoader cl;

	public ParserWorkItemHandler() {
		this.cl = this.getClass().getClassLoader();
	}

	public ParserWorkItemHandler(ClassLoader cl) {
		this.cl = cl;
	}

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		// no-op
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		Object input;
		Object result = null;
		boolean toObject;
		Map<String, Object> results = new HashMap<String, Object>();
		try {
    		String format = wi.getParameter(FORMAT).toString();
    		Class<?> type = null;
    		input = wi.getParameter(INPUT);
    		toObject = input instanceof String;
    		if (toObject) {
    			try {
    				String typeStr = wi.getParameter(TYPE).toString();
    				type = cl.loadClass(typeStr);
    			} catch (Exception e) {
    				throw new RuntimeException("Could not load the provided type. The parameter " + TYPE + 
    				        " is required when parsing from String to Object. Please provide the full qualified name of the target object class.", e);
    			}
    		}
    		if (JSON.equals(format.toUpperCase())) {
    			try {
    				result = toObject ? convertJSONToObject(input.toString(), type) : convertToJSON(input);
    			} catch (Exception e) {
    				throw new RuntimeException("Error parsing to JSON. Check the input format or the output object",e);
    			}
    		} else if (XML.equals(format.toUpperCase())) {
    			try {
    				result = toObject ? convertXMLToObject(input.toString(), type)
    						: convertToXML(input);
    			} catch (JAXBException e) {
    				throw new RuntimeException("Error parsing to XML. Check the input format or the output object", e);
    			}
    		}
    		results.put(RESULT, result);
    		wim.completeWorkItem(wi.getId(), results);
		} catch (Exception e) {
		    handleException(e);
		}
	}
	

	protected Object convertXMLToObject(String input, Class<?> type) {
		return JAXB.unmarshal(new StringReader(input), type);
	}

	protected String convertToXML(Object input) throws JAXBException {
		StringWriter result = new StringWriter();	
		JAXBContext jaxbContext = JAXBContext.newInstance(input.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		jaxbMarshaller.marshal(input, result);
		return result.toString();
	}
	
	protected Object convertJSONToObject(String input, Class<?> type) throws JsonMappingException, IOException {		
		return new ObjectMapper().readValue(input, type);
	}

	protected Object convertToJSON(Object input) throws JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(input);
	}
}