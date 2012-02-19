/**
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
package org.jbpm.formapi.client.form;

import org.jbpm.formapi.client.form.FormEncodingClientFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;

import com.google.gwt.junit.client.GWTTestCase;

public class FormEncodingClientFactoryGwtTest extends GWTTestCase {

    private static final String JSON = "{\"name\": \"myForm\",\"action\": \"complete\","+
        "\"taskId\": \"myTask\",\"documentation\": \"This is documentation\",\"enctype\": \"multipart/form-data\","+
        "\"lastModified\": \"1309368553625\",\"method\": \"POST\",\"formItems\": [{\"styleClass\": null,"+
        "\"value\": \"Login Form Template\",\"typeId\": \"header\",\"input\": null,"+
        "\"@className\": \"org.jbpm.formbuilder.shared.api.items.HeaderRepresentation\","+
        "\"width\": null,\"height\": null,\"output\": null,\"cssId\": null,\"cssName\": null,"+
        "\"itemValidations\": []},{\"input\": null,\"width\": \"100%\",\"rows\": 3,\"output\": null,"+
        "\"itemValidations\": [],\"columns\": 2,\"@className\": \"org.jbpm.formbuilder.shared.api.items.TableRepresentation\","+
        "\"typeId\": \"table\",\"cellPadding\": 0,\"height\": \"200px\",\"borderWidth\": 1,\"cellSpacing\": 0,\"elements\": ["+
        "[{\"value\": \"Password\",\"typeId\": \"label\",\"input\": null,\"@className\": " +
        "\"org.jbpm.formbuilder.shared.api.items.LabelRepresentation\",\"width\": null,\"height\": null,\"output\": null,\"cssName\": null,"+
        "\"itemValidations\": [],\"id\": null},{\"maxLength\": null,\"typeId\": \"textField\",\"input\": null,\"@className\": " +
        "\"org.jbpm.formbuilder.shared.api.items.TextFieldRepresentation\",\"width\": \"160px\",\"defaultValue\": null,\"height\": \"21px\"," +
        "\"output\": null,\"itemValidations\": [],\"name\": \"usr\",\"id\": null}],[{\"value\": null,\"typeId\": \"label\",\"input\": null,"+
        "\"@className\": \"org.jbpm.formbuilder.shared.api.items.LabelRepresentation\",\"width\": null,\"height\": null,"+
        "\"output\": null,\"cssName\": null,\"itemValidations\": [],\"id\": null},{\"maxLength\": null,\"typeId\": \"textField\","+
        "\"input\": null,\"@className\": \"org.jbpm.formbuilder.shared.api.items.TextFieldRepresentation\",\"width\": \"160px\","+
        "\"defaultValue\": null,\"height\": \"21px\",\"output\": null,\"itemValidations\": [],\"name\": \"pwd\",\"id\": null"+
        "}],[null,{\"onClickScript\": {\"@className\": \"org.jbpm.formbuilder.shared.api.FBScript\", \"type\": \"text/javascript\"," +
        "\"documentation\": null,\"content\": \"document.forms[0].submit();\",\"src\": null,\"invokeFunction\": null,\"id\": null}," +
        "\"typeId\": \"completeButton\",\"input\": null,\"@className\": \"org.jbpm.formbuilder.shared.api.items.CompleteButtonRepresentation\"," +
        "\"text\": \"Login\",\"width\": \"140px\",\"height\": \"25px\",\"output\": null,\"itemValidations\": [],\"name\": null,\"id\": null"+
        "}]]}],\"formValidations\": [],\"inputs\": {\"in2\": {\"@className\": \"org.jbpm.formbuilder.shared.api.InputData\","+
        "\"mimeType\": null,\"value\": \"${process.dataY}\",\"name\": \"in2\",\"formatter\": null},\"in1\": {"+
        "\"@className\": \"org.jbpm.formbuilder.shared.api.InputData\",\"mimeType\": null,\"value\": \"${process.dataX}\","+
        "\"name\": \"in1\",\"formatter\": null}},\"outputs\": {\"process.dataZ\": {"+
        "\"@className\": \"org.jbpm.formbuilder.shared.api.OutputData\",\"mimeType\": null,\"value\": \"${pwd}\"," +
        "\"name\": \"process.dataZ\",\"formatter\": null}},\"onLoadScripts\": [],\"onSubmitScripts\": []}";
    
    @Override
    public String getModuleName() {
        return "org.jbpm.formbuilder.FormBuilder";
    }
    
    public void testComplexFormDecoding() throws Exception {
        String json = JSON;
        assertNotNull("json shouldn't be null", json);
        assertNotSame("json shouldn't be empty", "", json);
        
        FormRepresentationEncoder encoder = FormEncodingClientFactory.getEncoder();
        FormRepresentationDecoder decoder = FormEncodingClientFactory.getDecoder();
        
        FormRepresentation form = decoder.decode(json);
        assertNotNull("form shouldn't be null", form);
        String json2 = encoder.encode(form);
        FormRepresentation form2 = decoder.decode(json2);
        assertNotNull("json2 shouldn't be null", json2);
        assertNotSame("json2 shouldn't be empty", "", json2);
        
        assertNotNull("form2 shouldn't be null", form2);
        assertEquals("both forms should be the same in contents", form, form2);
    }

}
