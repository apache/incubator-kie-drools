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
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.TextCallback;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.RepresentationFactory;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseEvent;
import org.jbpm.formbuilder.client.bus.ExistingValidationsResponseEvent;
import org.jbpm.formbuilder.client.bus.LoadServerFormResponseEvent;
import org.jbpm.formbuilder.client.bus.MenuItemAddedEvent;
import org.jbpm.formbuilder.client.bus.MenuItemAddedHandler;
import org.jbpm.formbuilder.client.bus.MenuItemFromServerEvent;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveEvent;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveHandler;
import org.jbpm.formbuilder.client.bus.MenuOptionAddedEvent;
import org.jbpm.formbuilder.client.bus.PreviewFormResponseEvent;
import org.jbpm.formbuilder.client.bus.ui.FormSavedEvent;
import org.jbpm.formbuilder.client.bus.ui.RepresentationFactoryPopulatedEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.bus.ui.UserIsLoggedOutEvent;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.options.MainMenuOption;
import org.jbpm.formbuilder.shared.form.MockFormDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.XMLParser;

public class RestyFormBuilderModel implements FormBuilderService {

    private static final String DEFAULT_PACKAGE = "defaultPackage";
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final XmlParseHelper helper = new XmlParseHelper();
    private final MockFormDefinitionService mockFormService = new MockFormDefinitionService();
    private final String contextPath;
    private String packageName = DEFAULT_PACKAGE;
    
    public RestyFormBuilderModel(String contextPath) {
        this.contextPath = contextPath;
        //registered to save the menu items
        bus.addHandler(MenuItemAddedEvent.TYPE, new MenuItemAddedHandler() {
            @Override
            public void onEvent(MenuItemAddedEvent event) {
                FBMenuItem item = event.getMenuItem();
                saveMenuItem(event.getGroupName(), item);
                if (item instanceof CustomMenuItem) {
                    CustomMenuItem customItem = (CustomMenuItem) item;
                    String formItemName = customItem.getOptionName();
                    FormItemRepresentation formItem = customItem.getRepresentation();
                    saveFormItem(formItem, formItemName);
                }
            }
        });
        //registered to delete the menu items
        bus.addHandler(MenuItemRemoveEvent.TYPE, new MenuItemRemoveHandler() {
            @Override
            public void onEvent(MenuItemRemoveEvent event) {
                FBMenuItem item = event.getMenuItem();
                deleteMenuItem(event.getGroupName(), item);
                if (item instanceof CustomMenuItem) {
                    CustomMenuItem customItem = (CustomMenuItem) item;
                    String formItemName = customItem.getOptionName();
                    FormItemRepresentation formItem = customItem.getRepresentation();
                    deleteFormItem(formItemName, formItem);
                }
            }
        });
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    @Override
    public void getMenuItems() {
        Resource resource = new Resource(URLBuilder.getMenuItemsURL(this.contextPath));
        resource.get().send(new SimpleTextCallback(i18n.CouldntFindMenuItems()) {
            @Override
            public void onSuccess(Method method, String response) {
                if (method.getResponse().getStatusCode() == Response.SC_OK) {
                    Map<String, List<FBMenuItem>> menuItems = helper.readMenuMap(response);
                    for (String groupName : menuItems.keySet()) {
                        for (FBMenuItem menuItem : menuItems.get(groupName)) {
                            populateMockFormService(menuItem);
                            bus.fireEvent(new MenuItemFromServerEvent(menuItem, groupName));
                        }
                    }
                } else {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntFindMenuItems404()));
                }
            }
        });
    }

    public void getCurrentRoles(final FormBuilderService.RolesResponseHandler handler) {
    	Resource resource = new Resource(URLBuilder.getCurrentRolesURL(this.contextPath));
    	resource.get().send(new TextCallback() {
			@Override
			public void onSuccess(Method method, String response) {
				if (method.getResponse().getStatusCode() == Response.SC_OK) {
					List<String> roles = helper.readRoles(response);
					handler.onResponse(roles);
				} else {
					bus.fireEvent(new UserIsLoggedOutEvent());
				}
				
			}
			
			@Override
			public void onFailure(Method method, Throwable exception) {
				bus.fireEvent(new UserIsLoggedOutEvent());
			}
		});
    }
    
    public void logout() {
    	Resource resource = new Resource(URLBuilder.getLogoutURL(this.contextPath));
    	resource.post().send(new TextCallback() {
			@Override
			public void onSuccess(Method method, String response) {
				Window.Location.reload();
			}
			@Override
			public void onFailure(Method method, Throwable exception) {
				Window.Location.reload();
			}
		});
    }
    
    @Override
    public void getMenuOptions() {
        Resource resource = new Resource(URLBuilder.getMenuOptionsURL(this.contextPath));
        resource.get().send(new SimpleTextCallback(i18n.CouldntFindMenuOptions()) {
            @Override
            public void onSuccess(Method method, String response) {
                List<MainMenuOption> currentOptions = helper.readMenuOptions(response);
                for (MainMenuOption option : currentOptions) {
                    bus.fireEvent(new MenuOptionAddedEvent(option));
                }
            }
        });
    }

    @Override
    public void saveForm(final FormRepresentation form) {
        Resource resource = new Resource(URLBuilder.saveFormURL(this.contextPath, this.packageName));
        try {
            String json = FormEncodingFactory.getEncoder().encode(form);
            resource.post().text(json).send(new SimpleTextCallback(i18n.CouldntSaveForm()) {
                @Override
                public void onSuccess(Method method, String response) {
                    int code = method.getResponse().getStatusCode();
                    if (code == Response.SC_CONFLICT) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.FormAlreadyUpdated()));
                    } else if (code != Response.SC_CREATED) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.SaveFormUnkwnownStatus(String.valueOf(code))));
                    } else {
                        String name = helper.getFormId(response);
                        form.setLastModified(System.currentTimeMillis());
                        form.setSaved(true);
                        form.setName(name);
                        bus.fireEvent(new FormSavedEvent(form));
                    }
                }
            });
        } catch (FormEncodingException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntDecodeForm(), e));
        }
    }

    @Override
    public void saveFormItem(FormItemRepresentation formItem, String formItemName) {
        Resource resource = new Resource(URLBuilder.saveFormItemURL(this.contextPath, this.packageName, formItemName));
        try {
            String xml = helper.asXml(formItemName, formItem);
            resource.post().xml(XMLParser.parse(xml)).send(new SimpleTextCallback(i18n.CouldntSaveFormItem()) {
                @Override
                public void onSuccess(Method method, String response) {
                    int code = method.getResponse().getStatusCode();
                    if (code == Response.SC_CONFLICT) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.FormItemAlreadyUpdated()));
                    } else if (code != Response.SC_CREATED) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.SaveFormItemUnknownStatus(String.valueOf(code))));
                    } else {
                        String name = helper.getFormItemId(response);
                        bus.fireEvent(new NotificationEvent(Level.INFO, i18n.FormItemSaved(name)));
                    }
                }
            });
        } catch (FormEncodingException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntDecodeFormItem(formItemName), e));
        }
    }

    @Override
    public void deleteForm(FormRepresentation form)  {
        Resource resource = new Resource(URLBuilder.deleteFormURL(this.contextPath, this.packageName, form.getName()));
        try {
            resource.delete().send(new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingForm(""), exception));
                }
                @Override
                public void onResponseReceived(Request request, Response response) {
                    int code = response.getStatusCode();
                    if (code != Response.SC_ACCEPTED && code != Response.SC_NO_CONTENT && code != Response.SC_OK) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.ErrorDeletingForm(String.valueOf(code))));
                    } else {
                        bus.fireEvent(new NotificationEvent(Level.INFO, i18n.FormDeleted()));
                    }
                }
            });
        } catch (RequestException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingForm(""), e));
        }
    }

    @Override
    public void deleteFile(String url) throws FormBuilderException {
        Resource resource = new Resource(URLBuilder.deleteFileURL(this.contextPath, this.packageName, url));
        try {
            resource.delete().send(new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingFile(""), exception));
                }
                @Override
                public void onResponseReceived(Request request, Response response) {
                    int code = response.getStatusCode();
                    if (code != Response.SC_ACCEPTED && code != Response.SC_NO_CONTENT && code != Response.SC_OK) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.ErrorDeletingFile(String.valueOf(code))));
                    } else {
                        bus.fireEvent(new NotificationEvent(Level.INFO, i18n.FileDeleted()));
                    }
                }
            });
        } catch (RequestException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingForm(""), e));
        }
    }
    
	@Override
	public void getFiles(List<String> types, final FilesLoadedHandler handler) {
        String url = URLBuilder.getFilesURL(this.contextPath, this.packageName, types);
        Resource resource = new Resource(url);
        resource.get().send(new SimpleTextCallback(i18n.CouldntFindFiles("")) {
            @Override
            public void onSuccess(Method method, String response) {
                if (method.getResponse().getStatusCode() == Response.SC_OK) {
                    List<String> list = helper.readFiles(response);
                    handler.onFilesLoaded(list);
                } else {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntFindFiles(
                    		String.valueOf(method.getResponse().getStatusCode()))));
                }
            }
        });
	}


    @Override
    public void deleteFormItem(String formItemName, FormItemRepresentation formItem) {
        Resource resource = new Resource(URLBuilder.deleteFormItemURL(this.contextPath, this.packageName, formItemName));
        try {
            resource.delete().send(new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingFormItem(""), exception));
                }
                @Override
                public void onResponseReceived(Request request, Response response) {
                    int code = response.getStatusCode();
                    if (code != Response.SC_ACCEPTED && code != Response.SC_NO_CONTENT && code != Response.SC_OK) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.ErrorDeletingFormItem(String.valueOf(code))));
                    } else {
                        bus.fireEvent(new NotificationEvent(Level.INFO, i18n.FormItemDeleted()));
                    }
                }
            });
        } catch (RequestException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingFormItem(""), e));
        }
    }

    @Override
    public void generateForm(FormRepresentation form, final String language,
            Map<String, Object> inputs) {
        Resource resource = new Resource(URLBuilder.generateFormURL(this.contextPath, language));
        try {
            String xml = helper.asXml(form, inputs);
            resource.post().header(Resource.HEADER_ACCEPT, Resource.CONTENT_TYPE_TEXT).
                xml(XMLParser.parse(xml)).send(new SimpleTextCallback(i18n.CouldntPreviewForm()) {
                @Override
                public void onSuccess(Method method, String urlResponse) {
                    bus.fireEvent(new PreviewFormResponseEvent(urlResponse, language));
                }
            });
        } catch (FormEncodingException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntDecodeForm(), e));
        }
    }

    @Override
    public void saveMenuItem(String groupName, final FBMenuItem item) {
        Resource resource = new Resource(URLBuilder.getMenuItemsURL(this.contextPath));
        String xml = helper.asXml(groupName, item);
        try {
            resource.post().xml(XMLParser.parse(xml)).expect(201,409).send(new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntGenerateMenuItem(), exception));
                }
                @Override
                public void onResponseReceived(Request request, Response response) {
                    int code = response.getStatusCode();
                    NotificationEvent event;
                    if (code == Response.SC_CREATED) {
                        event = new NotificationEvent(Level.INFO, i18n.MenuItemSaved(item.getItemId()));
                    } else {
                        event = new NotificationEvent(Level.WARN, i18n.SaveMenuItemInvalidStatus(String.valueOf(code)));
                    }
                    bus.fireEvent(event);
                }
            });
        } catch (RequestException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntSaveMenuItem(), e));
        }
    }

    @Override
    public void deleteMenuItem(String groupName, FBMenuItem item) {
        Resource resource = new Resource(URLBuilder.getMenuItemsURL(this.contextPath));
        String xml = helper.asXml(groupName, item);
        try {
            resource.delete().xml(XMLParser.parse(xml)).send(new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingMenuItem(), exception));
                }
                @Override
                public void onResponseReceived(Request request, Response response) {
                    int code = response.getStatusCode();
                    if (code != Response.SC_ACCEPTED && code != Response.SC_NO_CONTENT && code != Response.SC_OK) {
                        bus.fireEvent(new NotificationEvent(Level.WARN, i18n.DeleteMenuItemUnkownStatus(String.valueOf(code))));
                    } else {
                        bus.fireEvent(new NotificationEvent(Level.INFO, i18n.MenuItemDeleted()));
                    }
                }
            });
        } catch (RequestException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorDeletingMenuItem(), e));
        }
    }

    @Override
    public void getExistingIoAssociations(final String filter) {
        String url = URLBuilder.getIoAssociationsURL(this.contextPath, this.packageName);
        Resource resource = new Resource(url);
        if (filter != null && !"".equals(filter)) {
            resource = resource.addQueryParam("q", filter);
        }
        resource.get().send(new SimpleTextCallback(i18n.CouldntReadTasks()) {
            @Override
            public void onSuccess(Method method, String response) {
                List<TaskRef> retval = helper.readTasks(response);
                bus.fireEventFromSource(new ExistingTasksResponseEvent(retval, filter), RestyFormBuilderModel.this);
            }
        });
    }

    @Override
    public void selectIoAssociation(String pkgName, String processName, String taskName) {
        String url = URLBuilder.getIoAssociationURL(this.contextPath, pkgName, processName, taskName);
        Resource resource = new Resource(url);
        resource.get().send(new SimpleTextCallback(i18n.CouldntReadSingleIO()) {
            @Override
            public void onSuccess(Method method, String response) {
                List<TaskRef> tasks = helper.readTasks(response);
                if (tasks.size() == 1) {
                    TaskRef singleTask = tasks.iterator().next();
                    bus.fireEvent(new TaskSelectedEvent(singleTask));
                }
            }
        });
    }

    @Override
    public void getExistingValidations() {
        Resource resource = new Resource(URLBuilder.getValidationsURL(this.contextPath));
        resource.get().send(new SimpleTextCallback(i18n.CouldntReadValidations()) {
            @Override
            public void onSuccess(Method method, String response) {
                try {
                    List<FBValidationItem> retval = helper.readValidations(response);
                    bus.fireEvent(new ExistingValidationsResponseEvent(retval));
                } catch (Exception e) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntDecodeValidations(), e));
                }
            }
        });
    }

    @Override
    public void getForm(final String formName) {
        String url = URLBuilder.getFormURL(this.contextPath, this.packageName, formName);
        Resource resource = new Resource(url);
        resource.get().send(new SimpleTextCallback(i18n.CouldntFindForm(formName)) {
            @Override
            public void onSuccess(Method method, String response) {
                if (method.getResponse().getStatusCode() == Response.SC_OK) {
                    List<FormRepresentation> list = helper.readForms(response);
                    bus.fireEvent(new LoadServerFormResponseEvent(list.isEmpty() ? null : list.iterator().next()));
                } else {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntFindForm404(formName)));
                }
            }
        });
    }

    @Override
    public void getForms() {
        String url = URLBuilder.getFormsURL(this.contextPath, this.packageName);
        Resource resource = new Resource(url);
        resource.get().send(new SimpleTextCallback(i18n.CouldntFindForms()) {
            @Override
            public void onSuccess(Method method, String response) {
                if (method.getResponse().getStatusCode() == Response.SC_OK) {
                    List<FormRepresentation> list = helper.readForms(response);
                    bus.fireEvent(new LoadServerFormResponseEvent(list));
                } else {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntFindForms404()));
                }
            }
        });
    }

    @Override
    public void populateRepresentationFactory() {
        String url = URLBuilder.getRepresentationMappingsURL(this.contextPath);
        Resource resource = new Resource(url);
        resource.get().send(new SimpleTextCallback(i18n.CouldntReadRepresentationMappings()) {
            @Override
            public void onSuccess(Method method, String response) {
                Map<String, String> repMap = helper.readPropertyMap(response);
                for (Map.Entry<String, String> entry : repMap.entrySet()) {
                    RepresentationFactory.registerItemClassName(entry.getKey(), entry.getValue());
                }
                bus.fireEvent(new RepresentationFactoryPopulatedEvent());
            }
        });
    }

    @Override
    public void loadFormTemplate(final FormRepresentation form, String language) {
        final String url = URLBuilder.loadFormTemplateURL(this.contextPath, language);
        Resource resource = new Resource(url);
        try {
            String xml = helper.asXml(form, null);
            resource.post().xml(XMLParser.parse(xml)).send(new SimpleTextCallback(i18n.CouldntExportTemplate()) {
                @Override
                public void onSuccess(Method method, String response) {
                    String fileName = helper.getFileName(response);
                    FormPanel auxiliarForm = new FormPanel();
                    auxiliarForm.setMethod(FormPanel.METHOD_GET);
                    auxiliarForm.setAction(url);
                    Hidden hidden1 = new Hidden("fileName");
                    hidden1.setValue(fileName);
                    Hidden hidden2 = new Hidden("formName");
                    hidden2.setValue(form.getName() == null || "".equals(form.getName()) ? "template" : form.getName());
                    VerticalPanel vPanel = new VerticalPanel();
                    vPanel.add(hidden1);
                    vPanel.add(hidden2);
                    auxiliarForm.add(vPanel);
                    RootPanel.get().add(auxiliarForm);
                    auxiliarForm.submit();
                }
            });
        } catch (FormEncodingException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntDecodeForm(), e));
        }
    }

    @Override
    public FormRepresentation toBasicForm(TaskRef task) {
        return mockFormService.createFormFromTask(task);
    }

    @Override
    public String getUploadFileURL() {
        return URLBuilder.uploadFileURL(this.contextPath, this.packageName);
    }
    
    @Override
    public String getUploadActionURL() {
        return URLBuilder.uploadActionURL();
    }
    
    private void populateMockFormService(FBMenuItem item) {
        String className = item.getClass().getName();
        List<String> effectClassNames = new ArrayList<String>();
        if (item.getFormEffects() != null) {
            for (FBFormEffect effect : item.getFormEffects()) {
                effectClassNames.add(effect.getClass().getName());
            }
        }
        if (item.getAllowedEvents() != null) {
            mockFormService.putActionsForItem(className, item.getAllowedEvents());
        }
        mockFormService.putEffectsForItem(className, effectClassNames);
    }
}
