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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Utility class used by the Tohu built-in rules for translating between <code>Question</code> answers and arbitrary Java classes
 * used in a custom domain model.
 * </p>
 * 
 * <p>
 * Default adapters are provided for all the standard Java classes but new ones can be plugged in by calling
 * <code>registerAdapter</code>.
 * </p>
 * 
 * @see org.drools.informer.domain.CharDomainModelAdapter
 * @see org.drools.informer.domain.DecimalDomainModelAdapter
 * @see org.drools.informer.domain.NumberDomainModelAdapter
 * @see org.drools.informer.domain.StraightThroughDomainModelAdapter
 * 
 * @author Damon Horrell
 */
public class DomainModelSupport {

	private static Map<String, DomainModelAdapter> adapters = new HashMap<String, DomainModelAdapter>();

	static {
		registerAdapter(new StraightThroughDomainModelAdapter(Question.TYPE_TEXT, String.class));
		registerAdapter(new CharDomainModelAdapter()); // TYPE_TEXT
		registerAdapter(new NumberDomainModelAdapter()); // TYPE_NUMBER
		registerAdapter(new DecimalDomainModelAdapter()); // TYPE_DECIMAL
		registerAdapter(new BooleanDomainModelAdapter()); // TYPE_BOOLEAN
		registerAdapter(new StraightThroughDomainModelAdapter(Question.TYPE_DATE, Date.class));
		registerAdapter(new ListDomainModelAdapter());
	}
	
	/**
	 * If necessary transform specific list implementations to the list interface name
	 */
	private static String createAdapterKey(Class<?> clazz) {
		String className = java.util.List.class.isAssignableFrom(clazz) ? "java.util.List" : clazz.getName();
		return className;
	}

	/**
	 * Registers a new Domain Model Adapter.
	 * 
	 * @see org.drools.informer.domain.DomainModelAdapter
	 * 
	 * @param adapter
	 */
	public static void registerAdapter(DomainModelAdapter adapter) {
		for (Class<?> clazz : adapter.getSupportedClasses()) {
			adapters.put(createAdapterKey(clazz), adapter);
		}
	}

	/**
	 * Converts an answer of type <code>answerType</code> to an instance of <code>clazz</code> using the registered
	 * DomainModelAdapter for this class.
	 * 
	 * <p>
	 * If no <code>DomainModelAdapter</code> is registered for the specified class then
	 * <code>java.lang.UnsupportedOperationException</code> is thrown.
	 * </p>
	 * 
	 * @param answerType
	 * @param answer
	 * @param clazz
	 * @return
	 */
	public static Object answerToObject(String answerType, Object answer, Class<?> clazz) {
		DomainModelAdapter adapter = adapters.get(createAdapterKey(clazz));
		if (adapter == null) {
			throw new UnsupportedOperationException("Unable to convert from answer type " + answerType + " to Java class "
					+ clazz.getName());
		}
		return adapter.answerToObject(answer, clazz);
	}

	/**
	 * Converts an arbitrary Java object to an answer of type <code>answerType</code> using the registered DomainModelAdapter
	 * for this object's class.
	 * 
	 * <p>
	 * If no <code>DomainModelAdapter</code> is registered for this object's class then
	 * <code>java.lang.UnsupportedOperationException</code> is thrown.
	 * </p>
	 * 
	 * @param object
	 * @param answerType
	 * @return
	 */
	public static Object objectToAnswer(Object object, String answerType) {
		if (object == null) {
			return null;
		}
		Class<? extends Object> clazz = object.getClass();
		DomainModelAdapter adapter = adapters.get(createAdapterKey(clazz));
		if (adapter == null) {
			throw new UnsupportedOperationException("Unable to convert from Java class " + clazz.getName() + " to answer type "
					+ answerType);
		}
		return adapter.objectToAnswer(object);
	}

	/**
	 * <p>
	 * Returns the <code>answerType</code> corresponding to the specified class.
	 * </p>
	 * 
	 * <p>
	 * If no <code>DomainModelAdapter</code> is registered for the specified class then
	 * <code>java.lang.UnsupportedOperationException</code> is thrown.
	 * </p>
	 * 
	 * @param clazz
	 * @return
	 */
	public static String classToAnswerType(Class<?> clazz) {
		DomainModelAdapter adapter = adapters.get(createAdapterKey(clazz));
		if (adapter == null) {

			throw new UnsupportedOperationException("Unable to support Java class " + clazz);
		}
		return adapter.getAnswerType();

	}
}
