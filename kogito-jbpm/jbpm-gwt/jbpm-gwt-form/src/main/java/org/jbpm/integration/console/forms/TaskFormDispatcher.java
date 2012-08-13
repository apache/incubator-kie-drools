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

package org.jbpm.integration.console.forms;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;

import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jbpm.integration.console.TaskClientFactory;
import org.jbpm.integration.console.shared.PropertyLoader;
import org.jbpm.task.Content;
import org.jbpm.task.I18NText;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 * @author Kris Verlaenen
 */
public class TaskFormDispatcher extends AbstractFormDispatcher {

    private static int clientCounter = 0;
    
    private TaskService service;

    public void connect() {
        if (service == null) {

            Properties properties = PropertyLoader.getJbpmConsoleProperties();

            service =TaskClientFactory.newInstance(properties, "org.jbpm.integration.console.forms.TaskFormDispatcher"+clientCounter);
            clientCounter++;
        }
    }

    public DataHandler provideForm(FormAuthorityRef ref) {
        connect();
        Task task = service.getTask(new Long(ref.getReferenceId()));
        
        Object input = null;
        long contentId = task.getTaskData().getDocumentContentId();
        if (contentId != -1) {
            Content content = null;
            
            content = service.getContent(contentId);
            input = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        }

        // check if a template exists
        String name = null;
        List<I18NText> names = task.getNames();
        for (I18NText text: names) {
            if ("en-UK".equals(text.getLanguage())) {
                name = text.getText();
            }
        }
        InputStream template = getTemplate(name);
        if (template == null) {
            template = TaskFormDispatcher.class.getResourceAsStream("/DefaultTask.ftl");
        }

        // merge template with process variables
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("task", task);
        renderContext.put("content", input);
        if (input instanceof Map) {
            Map<?, ?> map = (Map) input;
            for (Map.Entry<?, ?> entry: map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    renderContext.put((String) entry.getKey(), entry.getValue());
                }
            }
        }
     
        return processTemplate(name, template, renderContext);
    }

}
