package org.optaplanner.examples.nurserostering.domain.pattern;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@XStreamAlias("Pattern")
@XStreamInclude({
        ShiftType2DaysPattern.class,
        ShiftType3DaysPattern.class,
        WorkBeforeFreeSequencePattern.class,
        FreeBefore2DaysWithAWorkDayPattern.class
})
public abstract class Pattern extends AbstractPersistable {

    protected String code;
    protected int weight;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return code;
    }

}
