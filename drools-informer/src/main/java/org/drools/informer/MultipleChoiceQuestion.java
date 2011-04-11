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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * An extension of <code>Question</code> which provides a list of possible answers. i.e. a multiple choice question.</code>
 * </p>
 * 
 * <p>
 * <code>presentationStyles</code> could be used to display the possible answers as e.g. radio buttons, or a drop down list.
 * </p>
 * 
 * @author Damon Horrell
 */
public class MultipleChoiceQuestion extends Question {

	private static final long serialVersionUID = 1L;

	/**
	 * Possible answers are represented internally as comma-delimited value/label pairs i.e. value1=label1,value2=label2,... for efficient XML transport.
	 * 
	 * Any commas within the labels are escaped to \,
	 * 
	 * Any equals sign within either the values or labels are escaped to \=
	 */
	private String possibleAnswers;

	public MultipleChoiceQuestion() {
	}

	public MultipleChoiceQuestion(String id) {
		super(id);
	}

	public MultipleChoiceQuestion(String id, String label) {
		super(id, label);
	}
	
	protected List<PossibleAnswer> getListOfPossibleAnswers() {
		List<PossibleAnswer> result = new ArrayList<PossibleAnswer>();
		String[] split = split(possibleAnswers, ",");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			String[] valueLabel = split(s, "=");
			String value = valueLabel[0];
			if (value.equals("null")) {
				value = null;
			}
			String label = valueLabel[1];
			if (label.equals("")) {
				label = null;
			}
			result.add(new PossibleAnswer(value, label));
		}
		return result;
	}

	/**
	 * Gets list of possible answers.
	 * 
	 * @return
	 */
	public PossibleAnswer[] getPossibleAnswers() {
		if (possibleAnswers == null) {
			return null;
		}
		List<PossibleAnswer> result = getListOfPossibleAnswers();
		return result.toArray(new PossibleAnswer[] {});
	}
	
	protected String formatValue(String valueStr) {
		if (valueStr != null) {
			if (valueStr.contains(",")) {
				throw new IllegalArgumentException();
			}
			valueStr = valueStr.replaceAll("=", "\\\\=");
		}
		return valueStr;
	}

	/**
	 * Sets list of possible answers.
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(PossibleAnswer[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < possibleAnswers.length; i++) {
				if (possibleAnswers[i] != null) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					String value = formatValue(possibleAnswers[i].value);
					sb.append(value);
					sb.append('=');
					if (possibleAnswers[i].label != null) {
						sb.append(possibleAnswers[i].label.replaceAll(",", "\\\\,").replaceAll("=", "\\\\="));
					}
				}
			}
			if (sb.length() > 0) {
				this.possibleAnswers = sb.toString();
			} else {
				this.possibleAnswers = null;
			}
		}
	}

	/**
	 * Sets list of possible answers.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * 
	 * <pre>
	 * question.setPossibleAnswers({
	 *   new PossibleAnswer(&quot;a&quot;, &quot;apple&quot;),
	 *   new PossibleAnswer(&quot;b&quot;, &quot;banana&quot;)
	 * });
	 * </pre>
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(Object[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			setPossibleAnswers((PossibleAnswer[]) Arrays.asList(possibleAnswers).toArray(new PossibleAnswer[] {}));
		}
	}
	
	/**
	 * Adds a possible answer.
	 * 
	 * This method is provided to support the dynamic alteration of possible answers.
	 * 
	 * <b>Do not use for creation of lists</b>. Instead use {@link #setPossibleAnswers(PossibleAnswer[])}
	 *
	 *
	 * @param theValue of the possibleAnswer
	 */
	public void removePossibleAnswer(String theValue) {
		List<PossibleAnswer> list = getListOfPossibleAnswers();
		PossibleAnswer pos = null;
		for (PossibleAnswer pa : list) {
			if ((pa.getValue() != null) && (pa.getValue().equals(theValue))) {
				pos = pa;
				break;
			}
		}
		if (pos != null) {
			if ((getAnswerType() != null) && (getAnswer() != null) && (getAnswer().equals(pos.getValue()))) {
				setAnswer(null);
			}
			list.remove(pos);
			setPossibleAnswers(list.toArray());
		}
	}

	/**
	 * Checks to see if there is a possible answer with the value passed in.
	 *
	 * This method is provided to support the dynamic alteration of possible answers.
	 * Uses String.indexOf internally.
	 *
	 * @param theValue of the possibleAnswer
	 * @return
	 */
	public boolean hasPossibleAnswer(String theValue) {
		String value = formatValue(theValue) + "=";
		if (possibleAnswers.startsWith(value)) {
			return true;
		}
		// try and avoid an issue where we have an = sign in the description
		value = "," + value;
		if (possibleAnswers.indexOf(value) >= 0) {
			return true;
		}
		return false;
	}


	/**
	 * Removes a possible answer.
	 *
	 * This method is provided to support the dynamic alteration of possible answers.
	 *
	 * <b>Do not use for creation of lists</b>. Instead use {@link #setPossibleAnswers(PossibleAnswer[])}
	 *
	 *
	 * @param possibleAnswer
	 * @param atIndex If >= size of array then the answer is added to the end
	 */
	public void insertPossibleAnswer(PossibleAnswer possibleAnswer, int atIndex) {
		if (possibleAnswers == null) {
			// Really should be discouraged from doing this! Least efficient way of building up the list.
			PossibleAnswer[] pa = new PossibleAnswer[1];
			pa[0] = possibleAnswer;
			setPossibleAnswers(pa);
			return;
		}
		if (atIndex < 0) {
			atIndex = 0;
		}
		List<PossibleAnswer> list = getListOfPossibleAnswers();
		if (list.size() <= atIndex) {
			list.add(possibleAnswer);
		}
		else {
			list.add(atIndex, possibleAnswer);
		}
		setPossibleAnswers(list.toArray());
	}

	/**
	 * Gets list of possible answers as a comma delimited string.
	 *
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 *
	 * @return
	 * @deprecated
	 */
	public String getPossibleAnswersAsString() {
		return possibleAnswers;
	}

	/**
	 * Gets list of item ids as a comma delimited string. Implemented for testing purpose only - package visibility
	 *
	 * @return
	 */
	String getInternalPossibleAnswersAsString() {
		return possibleAnswers;
	}

	/**
	 * Sets list of possible answers as a comma-delimited string.
	 *
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 *
	 * @param possibleAnswers
	 * @deprecated
	 */
	public void setPossibleAnswersAsString(String possibleAnswers) {
		if (possibleAnswers != null && possibleAnswers.equals("")) {
			possibleAnswers = null;
		}
		this.possibleAnswers = possibleAnswers;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " possibleAnswers=" + possibleAnswers;
	}

	public static class PossibleAnswer {

		private String value;

		private String label;

		public PossibleAnswer() {
		}

		public PossibleAnswer(String value) {
			this.value = value;
		}

		public PossibleAnswer(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			final PossibleAnswer other = (PossibleAnswer) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		/**
		 * @see Object#toString()
		 */
		@Override
		public String toString() {
			return value + "=" + label;
		}

	}
}
