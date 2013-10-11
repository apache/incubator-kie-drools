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
package org.jbpm.kie.services.impl.form.model.forms;

import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.form.model.FormItemRepresentation;
import org.jbpm.kie.services.impl.form.model.FormRepresentation;
import org.jbpm.kie.services.impl.form.model.menu.MenuItemDescription;

public interface FormRepresentationDecoder {

    FormRepresentation decode(String json) throws FormEncodingException;

    FormItemRepresentation decodeItem(String json) throws FormEncodingException;

    Object decode(Map<String, Object> data) throws FormEncodingException;

    Map<String, List<MenuItemDescription>> decodeMenuItemsMap(String json) throws FormEncodingException;

}
