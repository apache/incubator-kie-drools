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

package org.drools.assistant.option;

public class ReplaceAssistantOption extends AssistantOption {
	
	public ReplaceAssistantOption(String display, String content, Integer offset, Integer length, Integer position) {
		this.display = display;
		this.content = content;
		this.offset = offset;
		this.length = length;
		this.position = position;
	}
	
}