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

import java.util.Set;

/**
 * <p>
 * Interface defining a Domain Model Adapter for translating between <code>Question</code> answers and any arbitrary Java class.
 * </p>
 * 
 * <p>
 * <code>Question</code> supports generic data types like Text, Number, Date. Default implementations support all the standard
 * Java classes e.g. <code>int</code>, <code>long</code>, <code>java.util.Date</code> but this can be extended to cater
 * for other representations e.g. 3rd-party date libraries.
 * </p>
 * 
 * @see org.drools.informer.Question
 * @see org.drools.informer.domain.DomainModelSupport
 * 
 * @author Damon Horrell
 */
interface DomainModelAdapter {

	/**
	 * Returns the classes supported by this adapter. 
	 * 
	 * @return
	 */
	Set<Class<?>> getSupportedClasses();

	/**
	 * Returns the <code>Question</code> answer type supported by this adapter. 
	 * 
	 * @return
	 */
	String getAnswerType();

	/**
	 * Converts an answer of the type given by <code>getAnswerType</code> to an instance of <code>clazz</code>.
	 * 
	 * @param answer
	 * @param clazz
	 * @return
	 */
	Object answerToObject(Object answer, Class<?> clazz);

	/**
	 * Converts an object to an answer of the type given by <code>getAnswerType</code>.
	 * 
	 * @param object
	 * @return
	 */
	Object objectToAnswer(Object object);

}
