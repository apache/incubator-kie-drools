package org.optaplanner.examples.tennis.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TennisDay")
public class Day extends AbstractPersistable {

    private int dateIndex;

    public Day() {
    }

    public Day(long id, int dateIndex) {
        super(id);
        this.dateIndex = dateIndex;
    }

    public int getDateIndex() {
        return dateIndex;
    }

    public void setDateIndex(int dateIndex) {
        this.dateIndex = dateIndex;
    }

    public String getLabel() {
        return "day " + dateIndex;
    }

}
