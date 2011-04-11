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

import java.io.Serializable;

/**
 * A temporary object representing the answer to a question.
 * 
 * The answer is always a String since it models what the user has typed in.
 * 
 * When an Answer is inserted into the working memory, the Tohu built-in rules automatically converts copies the value onto the
 * corresponding Question (converting the data type as necessary). The Answer is then retracted from the working memory. If the
 * data type conversion fails then an InvalidAnswer object is asserted.
 * 
 * @author Damon Horrell
 */
public class Answer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String questionId;

	private String value;

	public Answer() {
	}

	public Answer(String questionId, String value) {
		this.questionId = questionId;
		this.value = value;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getValue() {
		return value;
	}

	@Deprecated
	public void setvalue(String value) {
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
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
		final Answer other = (Answer) obj;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		return true;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + ": questionId=" + getQuestionId() + " value=" + getValue();
	}

}
