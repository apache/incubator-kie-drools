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
package org.jbpm.formapi.shared.api.items;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class BorderPanelRepresentation extends FormItemRepresentation {

	public static enum Position {
		SOUTH, SOUTHWEST, WEST, NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST, CENTER;
	}
	
	private Map<Position, FormItemRepresentation> items = new HashMap<Position, FormItemRepresentation>();
	
	public BorderPanelRepresentation() {
		super("borderPanel");
	}

	public Map<Position, FormItemRepresentation> getItems() {
		return items;
	}

	public void setItems(Map<Position, FormItemRepresentation> items) {
		this.items = items;
	}

	public FormItemRepresentation putItem(Position key, FormItemRepresentation value) {
		return items.put(key, value);
	}
	
	
}
