/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package it.pkg;

public class Measurement {
	private String id;
	private String val;
	
	public Measurement(String id, String val) {
		super();
		this.id = id;
		this.val = val;
	}
	
	public String getId() {
		return id;
	}
	
	public String getVal() {
		return val;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measurement [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (val != null)
			builder.append("val=").append(val);
		builder.append("]");
		return builder.toString();
	}
	
	
}
