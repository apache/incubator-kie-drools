package org.drools.examples.cashflow;

import java.util.Date;

public class AccountPeriod {
    private Date start;
    private Date end;

    public AccountPeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AccountPeriod that = (AccountPeriod) o;

        if (!end.equals(that.end)) { return false; }
        if (!start.equals(that.start)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccountPeriod{" +
               "start=" + start +
               ", end=" + end +
               '}';
    }
}
