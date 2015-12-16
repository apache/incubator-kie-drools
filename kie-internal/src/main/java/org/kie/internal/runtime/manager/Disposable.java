/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.runtime.manager;

/**
 * Marker interface that indicates that given class is disposable - meaning shall be 
 * manually or automatically disposed on close events.
 *
 */
public interface Disposable {

	/**
	 * Actual logic that shall be executed on dispose.
	 */
    void dispose();
    
    /**
     * Allows to register listeners to be notified whenever instance is disposed so dependent
     * instances can take proper action on that occasion.
     * @param listener callback listener instance
     */
    void addDisposeListener(DisposeListener listener);
}
