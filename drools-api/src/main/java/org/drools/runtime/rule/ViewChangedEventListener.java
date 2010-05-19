package org.drools.runtime.rule;

public interface ViewChangedEventListener {
	
	public void rowAdded(Row row);
	
	public void rowRemoved(Row row);
	
	public void rowUpdated(Row row);
}
