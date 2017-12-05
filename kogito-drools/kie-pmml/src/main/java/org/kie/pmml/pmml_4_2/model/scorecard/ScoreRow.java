/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.scorecard;

public class ScoreRow extends Score {
	private String characteristic;
	private String reasonCode;
	private String id;
	
	
	public ScoreRow() {
		super();
	}
	
	public ScoreRow(String scoreCard, Double value, String characteristic, String reasonCode, String id) {
		super(scoreCard, value);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
		this.id = id;
	}

	public ScoreRow(String scoreCard, String characteristic, String reasonCode, String id) {
		super(scoreCard);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
		this.id = id;
	}

	public ScoreRow(String scoreCard, Double value, String characteristic, String reasonCode) {
		super(scoreCard, value);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
	}

	public ScoreRow(String scoreCard, String characteristic, String reasonCode) {
		super(scoreCard);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
	}

	public ScoreRow(String scoreCard, Double value, String characteristic) {
		super(scoreCard, value);
		this.characteristic = characteristic;
	}

	public ScoreRow(String scoreCard, String characteristic) {
		super(scoreCard);
		this.characteristic = characteristic;
	}

	public String getCharacteristic() {
		return characteristic;
	}
	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((characteristic == null) ? 0 : characteristic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoreRow other = (ScoreRow) obj;
		if (characteristic == null) {
			if (other.characteristic != null)
				return false;
		} else if (!characteristic.equals(other.characteristic))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("ScoreRow( ");
		bldr.append("scoreCard=").append(getScoreCard()).append(", ");
		bldr.append("value=").append(getValue()).append(", ");
		bldr.append("characteristic=").append(characteristic).append(", ");
		bldr.append("reasonCode=").append(reasonCode).append(", ");
		bldr.append("id=").append(id).append(" )");
		return bldr.toString();
	}
	
	
}
