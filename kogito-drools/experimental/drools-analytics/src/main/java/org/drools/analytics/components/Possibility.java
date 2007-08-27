package org.drools.analytics.components;

import java.util.Set;

import org.drools.analytics.result.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public interface Possibility extends Cause {

	public int getId();

	public Set<Cause> getItems();
}
