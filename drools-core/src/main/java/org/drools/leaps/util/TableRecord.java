package org.drools.leaps.util;

/**
 * @author ab
 *
 */
public class TableRecord {
	// left neigbor
	TableRecord left;

	// right neigbor
	TableRecord right;

	// content of the record
	Object object;

	TableRecord(Object o) {
		this.left = null;
		this.right = null;
		this.object = o;
	}

}
