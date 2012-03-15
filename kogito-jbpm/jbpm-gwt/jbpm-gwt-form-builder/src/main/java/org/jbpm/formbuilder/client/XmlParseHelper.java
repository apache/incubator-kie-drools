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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formapi.common.reflect.ReflectionHelper;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formbuilder.client.command.BaseCommand;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;
import org.jbpm.formbuilder.client.menu.items.ErrorMenuItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.options.MainMenuOption;
import org.jbpm.formbuilder.client.validation.OtherValidationsAware;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * This class is to help {@link FormBuilderModel} to parse response messages
 * and transform request bodies.
 */
public class XmlParseHelper {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    /**
     * Method to output xml from a form item and is name with the following format:
     * <code>
     * &lt;formItem name="${formItemName}"&gt;<br>
     * &nbsp;&nbsp;&lt;content&gt;${formItem.asJson()}&lt;/content&gt;<br>
     * &lt;/formItem&gt;<br>
     * </code>
     * 
     * @param formItemName name of the form item
     * @param formItem the form item to format
     * @return XML request body
     */
    public String asXml(String formItemName, FormItemRepresentation formItem) throws FormEncodingException {
        StringBuilder builder = new StringBuilder();
        String json = FormEncodingFactory.getEncoder().encode(formItem);
        builder.append("<formItem name=\"").append(formItemName).append("\">");
        builder.append("<content><![CDATA[").append(json).append("]]></content>");
        builder.append("</formItem>");
        return builder.toString();
    }
    
    /**
     * Method to output xml from a menu item and its group's name with the following format:
     * <code>
     * &lt;menuItem&gt;<br>
     * &nbsp;&nbsp;&lt;groupName&gt;${groupName}&lt;/groupName&gt;<br>
     * &nbsp;&nbsp;&lt;name&gt;${item.description.text}&lt;/name&gt;<br>
     * &nbsp;&nbsp;&lt;clone&gt;&lt;![CDATA[${item.asJson()}]]&gt;&lt;/clone&gt;<br>
     * &nbsp;&nbsp;&lt;effect&gt;${item.formEffects[0].class.name}&lt;/effect&gt;<br>
     * &nbsp;&nbsp;&lt;effect&gt;${item.formEffects[1].class.name}&lt;/effect&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;effect&gt;${item.formEffects[n].class.name}&lt;/effect&gt;<br>
     * &lt;/menuItem&gt;<br>
     * </code>
     * 
     * @param groupName the menu item group's name
     * @param item the menu item
     * @return XML request body
     */
    public String asXml(String groupName, FBMenuItem item) {
        StringBuilder builder = new StringBuilder();
        builder.append("<menuItem>");
        builder.append("<groupName>").append(groupName).append("</groupName>");
        builder.append("<name>").append(item.getDescription().getText()).append("</name>");
        try {
            String json = FormEncodingFactory.getEncoder().encode(item.buildWidget().getRepresentation());
            String jsonTag = new StringBuilder("<clone><![CDATA[").append(json).append("]]></clone>").toString();
            builder.append(jsonTag);
        } catch (FormEncodingException e) {
            builder.append("<clone error=\"true\">Exception:").append(e.getMessage()).append("</clone>");
        }
        for (String key : item.getAllowedEvents()) {
            builder.append("<allowedEvent>").append(key).append("</allowedEvent>");
        }
        for (FBFormEffect effect : item.getFormEffects()) {
            builder.append("<effect className=\"").append(effect.getClass().getName()).append("\" />");
        }
        builder.append("</menuItem>");
        return builder.toString();
    }
    
    /**
     * Ment to parse an XML response with the following format:
     * <code>
     * &lt;tasks&gt;<br>
     * &nbsp;&nbsp;&lt;task processId="${task.processId}" taskName="${task.taskId}"&gt;<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;input name="${task.inputs[0].name}" source="${task.inputs[0].sourceExpression}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;input name="${task.inputs[1].name}" source="${task.inputs[1].sourceExpression}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;input name="${task.inputs[n].name}" source="${task.inputs[n].sourceExpression}"/&gt;<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;output name="${task.outputs[0].name}" source="${task.outputs[0].sourceExpression}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;output name="${task.outputs[1].name}" source="${task.outputs[1].sourceExpression}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;output name="${task.outputs[n].name}" source="${task.outputs[n].sourceExpression}"/&gt;<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;metaData key="${task.metaData.entrySet[0].key}" value="${task.metaData.entrySet[0].value}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;metaData key="${task.metaData.entrySet[1].key}" value="${task.metaData.entrySet[1].value}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;metaData key="${task.metaData.entrySet[n].key}" value="${task.metaData.entrySet[n].value}"/&gt;<br>
     * <br>
     * &nbsp;&nbsp;&lt;/task&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &lt;/tasks&gt;<br>
     * </code>
     * 
     * @param responseText The XML response.
     * @return a list of task definition references.
     */
    public List<TaskRef> readTasks(String responseText) {
        Document xml = XMLParser.parse(responseText);
        List<TaskRef> retval = null;
        NodeList list = xml.getElementsByTagName("task");
        if (list != null) {
            retval = new ArrayList<TaskRef>(list.getLength());
            for (int index = 0; index < list.getLength(); index++) {
                Element elem = (Element) list.item(index);
                TaskRef ref = new TaskRef();
                ref.setProcessId(elem.getAttribute("processId"));
                ref.setTaskId(elem.getAttribute("taskName"));
                ref.setInputs(extractTaskIO(elem.getElementsByTagName("input")));
                ref.setOutputs(extractTaskIO(elem.getElementsByTagName("output")));
                NodeList mdList = elem.getElementsByTagName("metaData");
                if (mdList != null) {
                    Map<String, String> metaData = new HashMap<String, String>();
                    for (int i = 0; i < mdList.getLength(); i++) {
                        Element mdElem = (Element) mdList.item(i);
                        metaData.put(mdElem.getAttribute("key"), mdElem.getAttribute("value"));
                    }
                    ref.setMetaData(metaData);
                }
                retval.add(ref);
            }
        }
        return retval;
    }
    
    /**
     * Ment to parse an XML response with the following format:
     * <code>
     * &lt;menuOptions&gt;<br>
     * &nbsp;&nbsp;&lt;menuOption name="${option[0].html}" (commandClass="${option[0].command.class.name}")&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;menuOption name="${option[0].subMenu[0].html}" commandClass="${option[0].subMenu[0].command.class.name}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;menuOption name="${option[0].subMenu[n].html}" (commandClass="${option[0].subMenu[n].command.class.name}")&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;menuOption name="${option[0].subMenu[n].subMenu[0].html}" commandClass="${option[0].subMenu[n].subMenu[0].command.class.name}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/menuOption&gt;<br>
     * &nbsp;&nbsp;&lt;/menuOption&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;menuOption name="${option[m].html}" commandClass="${option[m].command.class.name}"/&gt;<br>
     * &lt;/menuOptions&gt;<br>
     * </code>
     * 
     * @param responseText The XML response.
     * @return a list of menuOptions.
     */
    public List<MainMenuOption> readMenuOptions(String responseText) {
        Document xml = XMLParser.parse(responseText);
        NodeList menuOptions = xml.getElementsByTagName("menuOptions").item(0).getChildNodes();
        return readMenuOptions(menuOptions);
    }
    
    /**
     * Ment to parse an XML response with the following format:
     * <code>
     * &lt;listForms&gt;<br>
     * &nbsp;&nbsp;&lt;form&gt;&lt;json&gt;${jsonFromFormRepresentation}&lt;/json&gt;&lt;/form&gt;<br>
     * &lt;/listForms&gt;<br>
     * </code>
     * 
     * @param responseText the XML response.
     * @return a list of FormRepresentation items.
     */
    public List<FormRepresentation> readForms(String responseText) {
        Document xml = XMLParser.parse(responseText);
        NodeList list = xml.getElementsByTagName("json");
        List<FormRepresentation> retval = new ArrayList<FormRepresentation>();
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        if (list != null) {
            for (int index = 0; index < list.getLength(); index++) {
                Node node = list.item(index);
                String json = getText(node);
                try {
                    FormRepresentation form = decoder.decode(json);
                    retval.add(form);
                } catch (FormEncodingException e) {
                    FormRepresentation error = new FormRepresentation();
                    error.setName(i18n.Error(e.getLocalizedMessage()));
                    retval.add(error);
                }
            }
        }
        return retval;
    }
    
    /**
     * Ment to parse an XML response with the following format:
     * <code>
     * &lt;menuGroups&gt;<br>
     * &nbsp;&nbsp;&lt;menuGroup name="???"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;menuItem className="???" optionName="???"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;itemJson&gt;???&lt;/itemJson&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;effect className="???"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/menuItem&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;menuItem ...&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/menuItem&gt;<br>
     * &nbsp;&nbsp;&lt;/menuGroup&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;menuGroup name="???"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;/menuGroup&gt;<br>
     * &lt;/menuGroups&gt;<br>
     * </code>
     * 
     * @param responseText the XML response.
     * @return a map of lists of FBMenuItem instances.
     */
    public Map<String, List<FBMenuItem>> readMenuMap(String responseText) {
        Document xml = XMLParser.parse(responseText);
        Map<String, List<FBMenuItem>> menuItems = new HashMap<String, List<FBMenuItem>>();
        NodeList groups = xml.getElementsByTagName("menuGroup");
        for (int index = 0; index < groups.getLength(); index++) {
            Node groupNode = groups.item(index);
            String groupName = ((Element) groupNode).getAttribute("name");
            NodeList items = ((Element) groupNode).getElementsByTagName("menuItem");
            menuItems.put(groupName, readMenuItems(items, groupName));
        }
        return menuItems;
    }
    
    /**
     * Parses and returns a formId from an XML response of the following format:
     * <code>&lt;formId&gt;${response}&lt;/formId&gt;</code>
     * 
     * @param responseText XML response to parse
     * @return a formId
     */
    public String getFormItemId(String responseText) {
        return textOfFirstNode(responseText, "formItemId");
    }
    
    /**
     * Parses and returns a formItemId from an XML response of the following format:
     * <code>&lt;formItemId&gt;${response}&lt;/formItemId&gt;</code>
     * 
     * @param responseText XML response to parse
     * @return a formItemId
     */
    public String getFormId(String responseText) {
        return textOfFirstNode(responseText, "formId");
    }
    
    /**
     * Parses and returns a file name from an XML response of the following format:
     * <code>&lt;fileName&gt;${response}&lt;/fileName&gt;</code>
     * 
     * @param responseText XML response to parse
     * @return a file name on the server
     */
    public String getFileName(String responseText) {
        return textOfFirstNode(responseText, "fileName");
    }

    /**
     * Parses and returns a map of strings with string keys from an XML response of the 
     * following format:
     * 
     * <code>
     * &lt;properties&gt;<br>
     * &nbsp;&nbsp;&lt;property key="${key[0]}" value="${value[0]}"/&gt;<br>
     * &nbsp;&nbsp;&lt;property key="${key[1]}" value="${value[1]}"/&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;property key="${key[n]}" value="${value[n]}"/&gt;<br>
     * &lt;/properties&gt;<br>
     * </code>
     * @param responseText XML response to parse
     * @return a map of the string values indexed by property name
     */
    public Map<String, String> readPropertyMap(String responseText) {
        Document xml = XMLParser.parse(responseText);
        Map<String, String> retval = new HashMap<String, String>();
        NodeList list = xml.getElementsByTagName("property");
        for (int index = 0; index < list.getLength(); index++) {
            Element propElement = (Element) list.item(index);
            String key = propElement.getAttribute("key");
            String value = propElement.getAttribute("value");
            retval.put(key, value);
        }
        return retval;
    }
    
    /**
     * Method to output xml from a form preview and its group of test input variables with the following format:
     * <code>
     * &lt;formPreview&gt;<br>
     * &nbsp;&nbsp;&lt;representation&gt;${form.toJson()}&lt;/representation&gt;<br>
     * &nbsp;&nbsp;&lt;input key="${inputs[0].key}" value="${inputs[0].value}"/&gt;<br>
     * &nbsp;&nbsp;&lt;input key="${inputs[1].key}" value="${inputs[1].value}"/&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;input key="${inputs[n].key}" value="${inputs[n].value}"/&gt;<br>
     * &lt;/formPreview&gt;<br>
     * </code>
     * 
     * @param form the form representation to transform on server side to a given language
     * @param inputs the data inputs of the form representation to test it
     * @return XML request body
     * @throws FormEncodingException in case of error parsing the form representation
     */
    public String asXml(FormRepresentation form, Map<String, Object> inputs) throws FormEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append("<formPreview>");
        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        String json = encoder.encode(form);
        builder.append("<representation><![CDATA[").append(json).append("]]></representation>");
        if (inputs != null) {
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                String key = entry.getKey();
                Object obj = entry.getValue();
                builder.append("<input key=\"").append(key).append("\" value=\"").append(obj).append("\"/>");
            }
        }
        builder.append("</formPreview>");
        return builder.toString();
    }
    
    /**
     * Parses and returns a validation dto list from an XML response of the 
     * following format:
     * 
     * <code>
     * &lt;validations&gt;<br>
     * &nbsp;&nbsp;&lt;validation className="${fbValidationItem[0].class.name}"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[0]}" value="${value[0]}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[1]}" value="${value[1]}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[n]}" value="${value[n]}"/&gt;<br>
     * &nbsp;&nbsp;&lt;/validation&gt;<br>
     * &nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&lt;validation className="${fbValidationItem[m].class.name}"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[0]}" value="${value[0]}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[1]}" value="${value[1]}"/&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;property key="${key[p]}" value="${value[p]}"/&gt;<br>
     * &nbsp;&nbsp;&lt;/validation&gt;<br>
     * &lt;/validations&gt;<br>
     * </code>
     * @param responseText XML response to parse
     * @return a list of validation items
     */
    public List<FBValidationItem> readValidations(String responseText) throws Exception {
        Document xml = XMLParser.parse(responseText);
        NodeList validationList = xml.getElementsByTagName("validation");
        List<FBValidationItem> retval = new ArrayList<FBValidationItem>();
        for (int index = 0; index < validationList.getLength(); index++) {
            Element valElement = (Element) validationList.item(index);
            String klass = valElement.getAttribute("className");
            Object obj = ReflectionHelper.newInstance(klass);
            if (obj instanceof FBValidationItem) {
                FBValidationItem validItem = (FBValidationItem) obj;
                validItem.populatePropertiesMap(readValidationMap(valElement.getElementsByTagName("property")));
                retval.add(validItem);
            }
        }
        for (FBValidationItem item : retval) {
            if (item instanceof OtherValidationsAware) {
                OtherValidationsAware aware = (OtherValidationsAware) item;
                aware.setExistingValidations(retval);
            }
        }
        return retval;
    }
    
    private Map<String, HasValue<String>> readValidationMap(NodeList properties) {
        Map<String, HasValue<String>> retval = new HashMap<String, HasValue<String>>();
        for (int index = 0; index < properties.getLength(); index++) {
            Element propElement = (Element) properties.item(index);
            String key = propElement.getAttribute("key");
            final String value = propElement.getAttribute("value");
            TextBox text = new TextBox();
            text.setValue(value);
            retval.put(key, text);
        }
        return retval;
    }
    
    private List<MainMenuOption> readMenuOptions(NodeList menuOptions) {
        List<MainMenuOption> options = new ArrayList<MainMenuOption>();
        for (int index = 0; index < menuOptions.getLength(); index++) {
            Node menuNode = menuOptions.item(index);
            Element menuElement = (Element) menuNode;
            String name = menuElement.getAttribute("name");
            MainMenuOption option = new MainMenuOption();
            option.setHtml(name);
            if (menuElement.hasAttribute("commandClass")) {
                String className = menuElement.getAttribute("commandClass");
                try {
                    Object obj = ReflectionHelper.newInstance(className);
                    if (obj instanceof BaseCommand) {
                        option.setCommand((BaseCommand) obj);
                    } else {
                        option.setHtml(option.getHtml()+ "(" + i18n.NotOfType(className, "BaseCommand") + ")");
                        option.setEnabled(false);
                    }
                } catch (Exception e) {
                    option.setHtml(option.getHtml() + i18n.Error(e.getLocalizedMessage()));
                    option.setEnabled(false);
                }
            } else {
                option.setSubMenu(readMenuOptions(menuElement.getChildNodes()));
            }
            options.add(option);
        }
        return options;
    }

    private String textOfFirstNode(String responseText, String tagName) {
        Document xml = XMLParser.parse(responseText);
        Node node = xml.getElementsByTagName(tagName).item(0);
        return getText(node);
    }

    private List<TaskPropertyRef> extractTaskIO(NodeList ioList) {
        List<TaskPropertyRef> retval = null;
        if (ioList != null) {
            retval = new ArrayList<TaskPropertyRef>(ioList.getLength());
            for (int i = 0; i < ioList.getLength(); i++) {
                Element inElem = (Element) ioList.item(i);
                TaskPropertyRef prop = new TaskPropertyRef();
                String name = inElem.getAttribute("name");
                prop.setName(name);
                String sourceExpression = inElem.getAttribute("source");
                prop.setSourceExpresion(sourceExpression);
                retval.add(prop);
            }
        }
        return retval;
    }
 
    private List<FBMenuItem> readMenuItems(NodeList items, String groupName) {
        List<FBMenuItem> menuItems = new ArrayList<FBMenuItem>();
        for (int index = 0; index < items.getLength(); index ++) {
            Node itemNode = items.item(index);
            String itemClassName = ((Element) itemNode).getAttribute("className");
            try {
                Object obj = ReflectionHelper.newInstance(itemClassName);
                FBMenuItem menuItem = null;
                if (obj instanceof CustomMenuItem) {
                    CustomMenuItem customItem = (CustomMenuItem) obj;
                    String optionName = ((Element) itemNode).getAttribute("optionName");
                    customItem.setRepresentation(makeRepresentation(itemNode));
                    customItem.setOptionName(optionName);
                    customItem.setGroupName(groupName);
                    menuItem = customItem;
                } else if (obj instanceof FBMenuItem) {
                    menuItem = (FBMenuItem) obj;
                } else {
                    throw new Exception(i18n.NotOfType(itemClassName, "FBMenuItem"));
                }
                NodeList effects = ((Element) itemNode).getElementsByTagName("effect");
                for (FBFormEffect effect : readItemEffects(effects)) {
                    menuItem.addEffect(effect);
                }
                NodeList allowedEvents = ((Element) itemNode).getElementsByTagName("allowedEvent");
                for (String allowedEventName : readAllowedEvents(allowedEvents)) {
                    menuItem.addAllowedEvent(allowedEventName);
                }
                menuItems.add(menuItem);
            } catch (Exception e) {
                menuItems.add(new ErrorMenuItem(e.getMessage()));
            }
        }
        return menuItems;
    }
    
    private List<String> readAllowedEvents(NodeList allowedEvents) {
        List<String> retval = new ArrayList<String>();
        for (int index = 0; index < allowedEvents.getLength(); index++) {
            Node node = allowedEvents.item(index);
            retval.add(getText(node));
        }
        return retval;
    }
    
    private FormItemRepresentation makeRepresentation(Node itemNode) throws FormEncodingException {
        NodeList list = ((Element) itemNode).getElementsByTagName("itemJson");
        FormItemRepresentation rep = null;
        if (list.getLength() > 0) {
            Node node = list.item(0);
            String json = getText(node);
            FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
            rep = (FormItemRepresentation) decoder.decodeItem(json);
        }
        return rep;
    }
    
    private String getText(Node node) {
        NodeList list = node.getChildNodes();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < list.getLength(); index++) {
            builder.append(list.item(index).getNodeValue());
        }
        return builder.toString();
    }

    private List<FBFormEffect> readItemEffects(NodeList effects) throws Exception {
        List<FBFormEffect> itemEffects = new ArrayList<FBFormEffect>();
        for (int i = 0; i < effects.getLength(); i++) {
            Node effectNode = effects.item(i);
            String effectClassName = ((Element) effectNode).getAttribute("className");
            Object efobj = ReflectionHelper.newInstance(effectClassName);
            if (efobj instanceof FBFormEffect) {
                itemEffects.add((FBFormEffect) efobj);
            } else {
                throw new Exception(i18n.NotOfType(effectClassName, "FBFormEffect"));
            }
        }
        return itemEffects;
    }

    public List<String> readRoles(String response) {
        String[] rolesArray = response.split(",");
        List<String> retval = new ArrayList<String>(rolesArray.length);
        for (String role : rolesArray) {
            retval.add(role);
        }
        return retval;
    }

    /**
     * Ment to parse an XML response with the following format:
     * <code>
     * &lt;files&gt;<br>
     * &nbsp;&nbsp;&lt;file&gt;${url}&lt;/file&gt;<br>
     * &lt;/files&gt;<br>
     * </code>
     * 
     * @param responseText the XML response.
     * @return a list of Strings representing names of files.
     */
    public List<String> readFiles(String responseText) {
        Document xml = XMLParser.parse(responseText);
        NodeList list = xml.getElementsByTagName("file");
        List<String> retval = new ArrayList<String>();
        if (list != null) {
            for (int index = 0; index < list.getLength(); index++) {
                Node node = list.item(index);
                String url = getText(node);
                retval.add(url);
            }
        }
        return retval;
    }

}
