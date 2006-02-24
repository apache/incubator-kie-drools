package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import org.drools.common.AgendaItem;

/**
 * This is wrapper for drools agendaItem to keep track of activations that were
 * revoked from the agenda due to the fact retraction
 * 
 * @author Alexander Bagerman
 * 
 */
class PostedActivation {
	final AgendaItem agendaItem;

	boolean valid;

	private final boolean existsCountRelevant;

	private int existsCount;

	public PostedActivation(AgendaItem agendaItem, boolean existsCountRelevant,
			int existsCount) {
		this.valid = true;
		this.existsCountRelevant = existsCountRelevant;
		this.existsCount = existsCount;
		this.agendaItem = agendaItem;
	}

	protected AgendaItem getAgendaItem() {
		return this.agendaItem;
	}

	protected boolean isValid() {
		boolean ret = this.valid;

		if (ret && this.existsCountRelevant) {
			ret = (this.existsCount > 0);
		}
		return ret;
	}

	protected void invalidate() {
		this.valid = false;
	}

	protected void decrementExistsCount() {
		if (this.existsCountRelevant) {
			this.existsCount--;
		}
	}
}
