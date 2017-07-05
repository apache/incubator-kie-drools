/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.marshalling;


import org.kie.api.marshalling.ObjectMarshallingStrategy;

/**
 * In order to allow multiple marshalling strategies of the same class, this interface must be used
 * to avoid class name collision.
 */
public interface NamedObjectMarshallingStrategy extends ObjectMarshallingStrategy {

	/**
	 * Ensure that the name returned by this method has something related to the exact objects it handles.
	 * Use this name to avoid collisions of the same class implementing marshalling strategy but handling different objects 
	 * or targets (Different persistence units, Different Document Content storages, etc ).
	 * @return The unique name in the project environment for the marshalling strategy represented
	 */
	public String getName();
}
