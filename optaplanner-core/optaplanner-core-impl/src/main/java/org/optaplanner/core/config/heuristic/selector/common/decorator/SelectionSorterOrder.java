package org.optaplanner.core.config.heuristic.selector.common.decorator;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;

/**
 * @see SelectionSorter
 */

@XmlEnum
public enum SelectionSorterOrder {
    /**
     * For example: 0, 1, 2, 3.
     */
    ASCENDING,
    /**
     * For example: 3, 2, 1, 0.
     */
    DESCENDING;

    public static SelectionSorterOrder resolve(SelectionSorterOrder sorterOrder) {
        if (sorterOrder == null) {
            return ASCENDING;
        }
        return sorterOrder;
    }

}
