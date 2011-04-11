/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.informer.domain;

import org.drools.informer.Question;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain Model Adapter for the built-in boolean types:
 * 
 * <ul>
 * <li><code>boolean</code></li>
 * <li><code>java.lang.Boolean</code></li>
 * </ul>
 * 
 * @author Damon Horrell
 */
public class BooleanDomainModelAdapter implements DomainModelAdapter {

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getSupportedClasses()
	 */
	public Set<Class<?>> getSupportedClasses() {
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { boolean.class, Boolean.class }));
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getAnswerType()
	 */
	public String getAnswerType() {
		return Question.TYPE_BOOLEAN;
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#answerToObject(Object, Class)
	 */
	public Object answerToObject(Object answer, Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return answer == null ? false : answer;
		} else {
			return answer;
		}
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#objectToAnswer(Object)
	 */
	public Object objectToAnswer(Object object) {
		return object;
	}

}
