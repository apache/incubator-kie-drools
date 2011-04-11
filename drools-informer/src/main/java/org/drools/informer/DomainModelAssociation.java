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
package org.drools.informer;

/**
 * Defines an association between the answer to a Question and a property on a real domain model object instance.
 * 
 * The Tohu built-in rules keeps the model in sync with the answer on the Question.
 * 
 * All the basic Java types are supported. Adapters can be plugged in for custom extensions. (See
 * <code>org.tohu.domain.DomainModelSupport.registerAdapter</code>.)
 * 
 * @author Damon Horrell
 */
public class DomainModelAssociation {

	private String questionId;

	private Object object;

	private String property;

	/**
	 * A copy is kept of the last know answer. This is necessary to determine whether it is the answer that has changed or the
	 * underlying domain model that has changed.
	 */
	private Object lastAnswer;

	public DomainModelAssociation() {
	}

	public DomainModelAssociation(String questionId, Object object, String property) {
		this.questionId = questionId;
		this.object = object;
		this.property = property;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getLastAnswer() {
		return lastAnswer;
	}

	public void setLastAnswer(Object lastAnswer) {
		this.lastAnswer = lastAnswer;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DomainModelAssociation other = (DomainModelAssociation) obj;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		return true;
	}


    @Override
    public String toString() {
        return "DomainModelAssociation{" +
                "questionId='" + questionId + '\'' +
                ", object=" + object +
                ", property='" + property + '\'' +
                ", lastAnswer=" + lastAnswer +
                '}';
    }
}
