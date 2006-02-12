package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.io.Serializable;
import java.util.Comparator;

import org.drools.leaps.util.Table;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores rule handles
 * 
 * @author Alexander Bagerman
 * 
 */
class RuleTable extends Table implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RuleTable(Comparator ruleConflictResolver) {
		super(ruleConflictResolver);
	}
}
