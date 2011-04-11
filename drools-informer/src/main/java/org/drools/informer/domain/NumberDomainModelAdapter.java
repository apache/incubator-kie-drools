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

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.drools.informer.Question;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain Model Adapter for the built-in number types:
 * 
 * <ul>
 * <li><code>byte</code></li>
 * <li><code>short</code></li>
 * <li><code>int</code></li>
 * <li><code>long</code></li>
 * <li><code>java.lang.Byte</code></li>
 * <li><code>java.lang.Short</code></li>
 * <li><code>java.lang.Integer</code></li>
 * <li><code>java.lang.Long</code></li>
 * <li><code>java.math.BigInteger</code></li>
 * </ul>
 * 
 * @author Damon Horrell
 */
public class NumberDomainModelAdapter implements DomainModelAdapter {

//	private static final Logger logger = LoggerFactory.getLogger(NumberDomainModelAdapter.class);
	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getSupportedClasses()
	 */
	public Set<Class<?>> getSupportedClasses() {
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { byte.class, short.class, int.class, long.class, Byte.class,
				Short.class, Integer.class, Long.class, BigInteger.class }));
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#getAnswerType()
	 */
	public String getAnswerType() {
		return Question.TYPE_NUMBER;
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#answerToObject(Object, Class)
	 */
	public Object answerToObject(Object answer, Class<?> clazz) {

//		logger.debug("Answer to Object is: " + answer);

		if (answer == null) {
			if (clazz.isPrimitive()) {
				answer = 0;
			} else {
				return null;
			}
		}
		if (clazz.isPrimitive()) {
			if (clazz.equals(byte.class)) {
				clazz = Byte.class;
			} else if (clazz.equals(short.class)) {
				clazz = Short.class;
			} else if (clazz.equals(int.class)) {
				clazz = Integer.class;
			} else if (clazz.equals(long.class)) {
				clazz = Long.class;
			}
		}
		try {
			return clazz.getConstructor(String.class).newInstance(answer.toString());
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * @see org.drools.informer.domain.DomainModelAdapter#objectToAnswer(Object)
	 */
	public Object objectToAnswer(Object object) {
		return new Long(object.toString());
	}

}
