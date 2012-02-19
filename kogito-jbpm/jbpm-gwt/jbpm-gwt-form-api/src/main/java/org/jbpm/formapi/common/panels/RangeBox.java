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

import com.google.gwt.dom.client.Element;
import com.google.gwt.text.client.DoubleParser;
import com.google.gwt.text.client.DoubleRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ValueBox;

public class RangeBox extends ValueBox<Double> {
	
	private static class ElementBuilder {
		private final Element ret;
		public ElementBuilder() {
			ret = DOM.createInputCheck();
		}
		public ElementBuilder setType(String type) {
			ret.setAttribute("type", type);
			return this;
		}
		public Element build() {
			return ret;
		}
	}
	
	public RangeBox() {
		this(new ElementBuilder().setType("range").build(), 
				DoubleRenderer.instance(), 
				DoubleParser.instance()
			);
	}
	
	protected RangeBox(Element element, Renderer<Double> renderer, Parser<Double> parser) {
		super(element, renderer, parser);
	}
	
	public void setMin(Double min) {
		DOM.setElementProperty(getElement(), "min", String.valueOf(min));
	}
	
	public void setMax(Double max) {
		DOM.setElementProperty(getElement(), "max", String.valueOf(max));
	}
	
	public void setStep(Double step) {
		DOM.setElementProperty(getElement(), "step", String.valueOf(step));
	}
}
