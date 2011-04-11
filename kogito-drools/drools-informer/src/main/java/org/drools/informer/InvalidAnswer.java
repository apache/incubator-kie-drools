/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
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
package org.drools.informer;

/**
 * <p>
 * Represents a validation error with the answer given to a <code>Question</code>.
 * </p>
 * 
 * <p>
 * <code>InvalidAnswer</code> objects are created automatically by the Tohu built-in rules for:
 * </p>
 * 
 * <ul>
 * <li>Missing answers to required questions</li>
 * <li>Problems parsing numbers and dates</li>
 * </ul>
 * 
 * <p>
 * Custom rules can create <code>InvalidAnswer</code> objects for any type of input validation failure.
 * </p>
 * 
 * @author Damon Horrell
 */
public class InvalidAnswer extends TohuObject {

	private static final long serialVersionUID = 1L;

	private String questionId;

	private String reason;

	private String type;

	public InvalidAnswer() {
	}

	public InvalidAnswer(String questionId, String reason) {
		this.questionId = questionId;
		this.reason = reason;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		if (this.questionId != null && !this.questionId.equals(questionId)) {
			throw new IllegalStateException("questionId may not be changed");
		}
		this.questionId = questionId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		if (this.reason != null && !this.reason.equals(reason)) {
			throw new IllegalStateException("reason may not be changed");
		}
		this.reason = reason;
	}

	public String getType() {
		return type;
	}

	/**
	 * <p>
	 * Sets the type for this InvalidAnswer.
	 * </p>
	 * 
	 * <p>
	 * <code>type</code> may be used to control how the InvalidAnswer is rendered by the particular UI implementation. e.g.
	 * </p>
	 * 
	 * <ul>
	 * <li>hiding required field errors until the user tries to submit</li>
	 * <li>customising the error text/li>
	 * </ul>
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @see org.tohu.TohuObject#getId()
	 */
	public String getId() {
		return questionId + ":" + reason;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
		final InvalidAnswer other = (InvalidAnswer) obj;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		return true;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + ": questionId=" + questionId + " type=" + type + " reason=" + reason;
	}

}
