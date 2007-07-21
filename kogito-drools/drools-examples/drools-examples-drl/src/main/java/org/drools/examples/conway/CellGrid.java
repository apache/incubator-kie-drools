package org.drools.examples.conway;

import org.drools.examples.conway.patterns.ConwayPattern;

public interface CellGrid {

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#getCellAt(int, int)
	 */
	public abstract Cell getCellAt(final int row, final int column);

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#getNumberOfRows()
	 */
	public abstract int getNumberOfRows();

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#getNumberOfColumns()
	 */
	public abstract int getNumberOfColumns();

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#nextGeneration()
	 */
	public abstract boolean nextGeneration();

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#killAll()
	 */
	public abstract void killAll();

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#setPattern(org.drools.examples.conway.patterns.ConwayPattern)
	 */
	public abstract void setPattern(final ConwayPattern pattern);

	/* (non-Javadoc)
	 * @see org.drools.examples.conway.CellGrid#dispose()
	 */
	public abstract void dispose();

	public abstract String toString();

}