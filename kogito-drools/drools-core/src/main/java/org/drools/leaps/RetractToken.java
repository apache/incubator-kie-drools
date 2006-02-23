package org.drools.leaps;

/**
 * This is wrapper for drools agendaItem to keep track of activations that were
 * revoked from the agenda due to the fact retraction
 * 
 * @author Alexander Bagerman
 * 
 */
class RetractToken {
	boolean valid;

	private final boolean existsCountRelevant;

	private int existsCount;

	private final boolean notCountRelevant;

	private int notCount;

	public RetractToken(boolean existsCountRelevant, int existsCount,
			boolean notCountRelevant, int notCount) {
		this.valid = true;
		this.existsCountRelevant = existsCountRelevant;
		this.existsCount = existsCount;
		this.notCountRelevant = notCountRelevant;
		this.notCount = notCount;
	}

	protected boolean isValid() {
		boolean ret = this.valid;
		if (ret && this.notCountRelevant) {
			ret = (this.notCount == 0);
		}
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

	protected void decrementNotCount() {
		if (this.notCountRelevant) {
			this.notCount--;
		}
	}
}
