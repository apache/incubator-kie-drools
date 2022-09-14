/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.quarkus.quickstart.test.model;

public class Light {
    private final String name;
    private Boolean powered;
    
	public Light(String name, Boolean powered) {
		this.name = name;
		this.powered = powered;
	}

	public Boolean getPowered() {
		return powered;
	}

	public void setPowered(Boolean powered) {
		this.powered = powered;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Light [name=" + name + ", powered=" + powered + "]";
	}
}
