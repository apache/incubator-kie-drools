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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.util.HttpHeaderNames;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.OutputData;
import org.jbpm.formapi.shared.api.items.CompleteButtonRepresentation;
import org.jbpm.formapi.shared.api.items.HeaderRepresentation;
import org.jbpm.formapi.shared.api.items.LabelRepresentation;
import org.jbpm.formapi.shared.api.items.TableRepresentation;
import org.jbpm.formapi.shared.api.items.TextFieldRepresentation;

import junit.framework.TestCase;

/**
 * 
 */
public abstract class RESTAbstractTest extends TestCase {

    public static FormRepresentation createMockForm(String title, String... params) {
        FormRepresentation form = new FormRepresentation();
        HeaderRepresentation header = new HeaderRepresentation();
        header.setValue(title);
        form.addFormItem(header);
        
        TableRepresentation table = new TableRepresentation();
        table.setRows(params.length);
        table.setColumns(2);
        table.setHeight("" + (params.length * 30) + "px");
        for (int index = 0; index < params.length; index++) {
            String paramName = params[index];
            LabelRepresentation labelName = new LabelRepresentation();
            labelName.setValue(paramName);
            labelName.setWidth("100px");
            table.setElement(index, 0, labelName);
            TextFieldRepresentation textField = new TextFieldRepresentation();
            textField.setWidth("200px");
            OutputData data = new OutputData();
            data.setName(paramName);
            data.setMimeType("multipart/form-data");
            data.setFormatter(null);
            textField.setOutput(data);
            table.setElement(index, 1, textField);
        }
        LabelRepresentation labelParams = new LabelRepresentation();
        labelParams.setValue("Parameters:");
        form.addFormItem(labelParams);
        form.addFormItem(table);
        
        CompleteButtonRepresentation completeButton = new CompleteButtonRepresentation();
        completeButton.setText("Complete");
        form.addFormItem(completeButton);
        form.setAction("complete");
        form.setEnctype("multipart/form-data");
        form.setMethod("POST");
        form.setName(title + "AutoForm");
        return form;
    }
    
    protected static void assertStatus(int status, Status expected) {
        assertEquals("resp.getStatus() should be " + expected + " but is " + Status.fromStatusCode(status), status, expected.getStatusCode());
    }

    protected static Object assertXmlOkResponse(Response resp) {
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        return entity;
    }
}
