/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.examples.request;

import java.io.Serializable;

public class Request implements Serializable {
	
	private static final long serialVersionUID = 6L;
	
	private String id;
	private String personId;
	private Long amount;
	private boolean valid = true;
	private String invalidReason;
	private boolean canceled = false;

	public Request(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}
	
	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setInvalid(String reason) {
		this.valid = false;
		this.invalidReason = reason;
	}

	public boolean isValid() {
		return valid;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Request) {
			return this.id.equals(((Request) o).id);
		}
		return false;
	}
	
	public int hashCode() {
		return this.id.hashCode();
	}

}
