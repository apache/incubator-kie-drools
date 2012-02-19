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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class ListWidget extends Widget implements Focusable {

	private final UListElement element = Document.get().createULElement();
	
	public ListWidget() {
		setElement(element);
	}
	
	public void addItem(String item) {
		LIElement liElem = Document.get().createLIElement();
		liElem.setInnerHTML(item);
		element.appendChild(liElem);
	}
	
	public List<String> getItems() {
		List<String> items = new ArrayList<String>();
		for (int index = 0; index < element.getChildCount(); index++) {
			Element elem = Element.as(element.getChild(index));
			items.add(elem.getInnerHTML());
		}
		return items;
	}
	
	public void removeItem(String item) {
		int index = 0;
		for (; index < element.getChildCount(); index++) {
			Node child = element.getChild(index);
			Element elemChild = Element.as(child);
			String elemHTML = elemChild.getInnerHTML();
			if (elemHTML != null && elemHTML.equals(item)) {
				removeItem(index);
				break;
			}
		}
	}
	
	public void removeItem(int index) {
		if (index < element.getChildCount()) {
			element.removeChild(element.getChild(index));
		}
	}

	public void setScrollTop(int scrollTop) {
		element.setScrollTop(scrollTop);
	}
	
	public int getScrollTop() {
		return element.getScrollTop();
	}

	public void setId(String id) {
		element.setId(id);
	}
	
	public String getId() {
		return element.getId();
	}

	public void setScrollLeft(int scrollLeft) {
		element.setScrollLeft(scrollLeft);
	}
	
	public int getScrollLeft() {
		return element.getScrollLeft();
	}
	
	public void setDir(String dir) {
		element.setDir(dir);
	}
	
	public String getDir() {
		return element.getDir();
	}

	@Override
	public int getTabIndex() {
		return element.getTabIndex();
	}

	@Override
	public void setTabIndex(int index) {
		element.setTabIndex(index);
	}

	@Override
	public void setAccessKey(char key) {
		setAccessKey(element, key);
	}
	
	public native void setAccessKey(Element elem, char key) /*-{
    	elem.accessKey = String.fromCharCode(key);
  	}-*/;

	@Override
	public void setFocus(boolean focused) {
		if (focused) {
			element.focus();
		} else {
			element.blur();
		}
	}
}
