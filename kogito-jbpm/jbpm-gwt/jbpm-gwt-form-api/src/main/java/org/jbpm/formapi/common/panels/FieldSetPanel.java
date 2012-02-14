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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * TODO: Just in case, FieldSetPanel is implemented using HTML4 components.
 */
public class FieldSetPanel extends FlowPanel {

	private HeadingElement legend = Document.get().createHElement(1);
	
	public FieldSetPanel() {
		super();
		Style divStyle = getElement().getStyle();
		Style lgndStyle = legend.getStyle();
		
		divStyle.setBorderWidth(2, Unit.PX);
		divStyle.setBorderStyle(BorderStyle.SOLID);
		divStyle.setMarginTop(0.5, Unit.EM);
		divStyle.setMarginBottom(0.5, Unit.EM);
		divStyle.setMarginRight(0, Unit.PX);
		divStyle.setMarginLeft(0, Unit.PX);
		divStyle.setPaddingTop(0, Unit.PX);
		divStyle.setPaddingBottom(0, Unit.PX);
		divStyle.setPaddingRight(0.5, Unit.EM);
		divStyle.setPaddingLeft(0.5, Unit.EM);

		lgndStyle.setFontSize(100.0, Unit.PCT);
		lgndStyle.setFontWeight(FontWeight.NORMAL);
		lgndStyle.setMarginTop(-0.5, Unit.EM);
		lgndStyle.setMarginRight(0, Unit.PX);
		lgndStyle.setMarginLeft(0, Unit.PX);
		lgndStyle.setMarginBottom(0, Unit.PX);
		lgndStyle.setBackgroundColor("white");
		lgndStyle.setColor("black");
		lgndStyle.setFloat(Style.Float.LEFT);
		lgndStyle.setPaddingTop(0, Unit.PX);
		lgndStyle.setPaddingBottom(0, Unit.PX);
		lgndStyle.setPaddingRight(2, Unit.PX);
		lgndStyle.setPaddingLeft(2, Unit.PX);
		
		getElement().appendChild(legend);
	}
	
	public void setLegend(String legend) {
		this.legend.setInnerHTML(legend);
	}
	
	public String getLegend() {
		return this.legend.getInnerHTML();
	}
	
	public void setId(String id) {
		getElement().setId(id);
	}
	
	public String getId() {
		return getElement().getId();
	}
}
