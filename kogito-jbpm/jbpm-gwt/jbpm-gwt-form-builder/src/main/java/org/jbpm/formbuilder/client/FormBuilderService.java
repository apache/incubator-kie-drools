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

import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.RepresentationFactory;
import org.jbpm.formbuilder.shared.task.TaskRef;

/**
 * Client's interface with the REST API server.
 */
public interface FormBuilderService {

    /**
     * Gets a map of groups indexed by group name. Each group contains a list of menu items available in each group.
     * @return a map of groups with its lists of menu items.
     * @throws FormBuilderException in case of error.
     */
    void getMenuItems() throws FormBuilderException;
    
    /**
     * Gets a list of menu options, generally for a menu bar. Each option has a name, and 
     * maybe a subMenu consisting of a list of menu options, or maybe a command, or maybe both.
     * @return a list of menu options.
     * @throws FormBuilderException in case of error.
     */
    void getMenuOptions() throws FormBuilderException;
    
    /**
     * Saves a form on the server
     * @param form The form to be saved
     * @throws FormBuilderException in case of error
     */
    void saveForm(FormRepresentation form) throws FormBuilderException;
    
    /**
     * Saves a UI component on the server
     * @param formItem the UI component to be saved
     * @param formItemName the UI component name
     * @throws FormBuilderException in case of error
     */
    void saveFormItem(final FormItemRepresentation formItem, String formItemName) throws FormBuilderException;
    
    /**
     * Deletes a form from the server
     * @param form The form to be deleted
     * @throws FormBuilderException in case of error
     */
    void deleteForm(FormRepresentation form) throws FormBuilderException;
    
    void deleteFile(String url) throws FormBuilderException;
    
    /**
     * Deletes a UI component from the server
     * @param formItemName The UI component name
     * @param formItem the UI component to be deleted
     * @throws FormBuilderException in case of error
     */
    void deleteFormItem(String formItemName, final FormItemRepresentation formItem) throws FormBuilderException;
    
    /**
     * Translates a form. An event exposes where to retrieve the form from.
     * 
     * @param form Form to be translated
     * @param language Language to translate the form
     * @throws FormBuilderException in case of error
     */
    void generateForm(FormRepresentation form, String language, Map<String, Object> inputs) throws FormBuilderException;
    
    /**
     * Saves a new (custom) menu item on the server
     * @param groupName Group name of the new menu item
     * @param item The new menu item to be saved
     * @throws FormBuilderException in case of error
     */
    void saveMenuItem(String groupName, FBMenuItem item) throws FormBuilderException;
    
    /**
     * Deletes a custom menu item from the server
     * @param groupName Group name of the custom menu item to be deleted
     * @param item The custom menu item to be deleted
     * @throws FormBuilderException in case of error
     */
    void deleteMenuItem(String groupName, FBMenuItem item) throws FormBuilderException;
    
    /**
     * Returns the IoAssociations as matching on a simple string filter
     * @param filter a filter for a google-like search textfield
     * @return all best fit task definition references to the filter
     * @throws FormBuilderException in case of error
     */
    void getExistingIoAssociations(String filter) throws FormBuilderException;

    /**
     * Fires a TaskSelectedEvent in case you can find the proper task in the server
     * @param pkgName the name of the package
     * @param processName the name of the process
     * @param taskName the name of the task
     * @throws FormBuilderException in case of error
     */
    void selectIoAssociation(String pkgName, String processName, String taskName) throws FormBuilderException;
    
    /**
     * Returns existing validations from the server
     * @return all existing validation types available on server side
     * @throws FormBuilderException in case of error
     */
    void getExistingValidations() throws FormBuilderException;

    /**
     * Returns a single form
     * @param formName the name of the form to be returned
     * @return the requested form
     * @throws FormBuilderException in case of error.
     */
    void getForm(String formName) throws FormBuilderException;
    
    /**
     * Returns all forms
     * @return all existing forms
     * @throws FormBuilderException in case of error.
     */
    void getForms() throws FormBuilderException;
    
    /**
     * Populates the {@link RepresentationFactory} with the form items and representations
     * that belong to them.
     * @param callback callback to tell the client what to do once it finishes
     * @throws FormBuilderException in case of error.
     */
    void populateRepresentationFactory() throws FormBuilderException;

    /**
     * Loads a file from the server that contains a given language's form template
     * @param form the form representation to create a template from
     * @param language the result template expected language
     * @throws FormBuilderException in case of error.
     */
    void loadFormTemplate(FormRepresentation form, String language) throws FormBuilderException;
    
    interface RolesResponseHandler {
        void onResponse(List<String> roles);
    }
    
    void getCurrentRoles(RolesResponseHandler handler) throws FormBuilderException;
    
    /**
     * Returns the form representation that basically describes a task
     * @param task a task to be described
     * @return a form representation for that task
     */
    FormRepresentation toBasicForm(TaskRef task);

    /**
     * @return URL for uploading files to guvnor
     */
    String getUploadFileURL();

    /**
     * @return URL for mocking uploading files for form instances
     */
    String getUploadActionURL();
    
    void setPackageName(String packageName);

    void logout();

    void getFiles(List<String> types, FilesLoadedHandler handler);
}
