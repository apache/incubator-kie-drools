/*
 * Copyright 2014 JBoss Inc
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

package org.kie.internal.runtime.conf;

/**
 * Defines merging strategy of two descriptors:
 * <ul>
 * 	<li>KEEP_ALL mean the 'master' descriptor is kept</li>
 * 	<li>OVERRIDE_ALL means the 'slave' descriptor is returned</li>
 * 	<li>OVERRIDE_EMPTY mean the 'slave' non empty value override corresponding values of the master, including collections</li>
 * 	<li>MERGE_COLLECTIONS means same as OVERRIDE_EMPTY but merges collections instead of overriding them</li>
 * </ul>
 *
 */
public enum MergeMode {
	KEEP_ALL,
	OVERRIDE_ALL,
	OVERRIDE_EMPTY,
	MERGE_COLLECTIONS;
}