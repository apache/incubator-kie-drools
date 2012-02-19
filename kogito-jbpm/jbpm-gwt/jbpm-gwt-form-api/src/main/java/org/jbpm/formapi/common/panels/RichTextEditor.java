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
package org.jbpm.formapi.common.panels;

import com.gc.gwt.wysiwyg.client.Editor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TextAreaElement;

/**
 * Extends WYSIWYG editor to allow to set a name to the internal textArea
 */
public class RichTextEditor extends Editor {

    public RichTextEditor() {
        super();
    }
    
    public void setName(String name) {
        TextAreaElement element = getTextAreaElement(getElement());
        if (element == null) {
            GWT.log("Couldn't find internal text area for RichTextEditor");
        } else {
            element.setName(name);
        }
    }
    
    private TextAreaElement getTextAreaElement(Element element) {
        for (int index = 0; index < element.getChildCount(); index++) {
            Node child = element.getChild(index);
            if (child instanceof TextAreaElement) {
                return (TextAreaElement) child;
            } else if (child.hasChildNodes()) {
                TextAreaElement elem = getTextAreaElement(Element.as(child));
                if (elem != null) {
                    return elem;
                }
            }
        }
        return null;
    }
    
}
