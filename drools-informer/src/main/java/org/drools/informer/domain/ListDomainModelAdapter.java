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

import java.util.*;

/**
 * Domain Model Adapter for List<String>
 * 
 * <ul>
 * <li><code>java.util.List&lt;String&gt;</code></li>
 * </ul>
 * 
 * @author David Plumpton
 */
public class ListDomainModelAdapter implements DomainModelAdapter {

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getSupportedClasses()
	 */
	public Set<Class<?>> getSupportedClasses() {
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { List.class }));
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getAnswerType()
	 */
	public String getAnswerType() {
		return Question.TYPE_LIST;
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#answerToObject(Object, Class)
	 */
	public Object answerToObject(Object answer, Class<?> clazz) {
		return answer == null ? new ArrayList<String>() : (List<String>) Arrays.asList(String.valueOf(answer).split(","));
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#objectToAnswer(Object)
	 */
	public Object objectToAnswer(Object object) {
		StringBuilder b = new StringBuilder();
		List<String> l = (List<String>) object;
		String delimiter = "";
		for (String s : l) {
			b.append(delimiter);
			b.append(s);
			delimiter = ",";
		}
		return b.toString();
	}

}
