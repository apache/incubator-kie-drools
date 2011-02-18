/*
 * Copyright 2010 JBoss Inc
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
    
    public void updateCell(Cell cell, CellState state);

	public abstract String toString();

}
