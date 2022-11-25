package org.optaplanner.examples.nurserostering.domain.pattern;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShiftType2DaysPattern.class, name = "shiftType2"),
        @JsonSubTypes.Type(value = ShiftType3DaysPattern.class, name = "shiftType3"),
        @JsonSubTypes.Type(value = WorkBeforeFreeSequencePattern.class, name = "workBeforeFree"),
        @JsonSubTypes.Type(value = FreeBefore2DaysWithAWorkDayPattern.class, name = "freeBeforeWork"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public abstract class Pattern extends AbstractPersistable {

    protected String code;
    protected int weight;

    protected Pattern() {
    }

    protected Pattern(long id, String code) {
        super(id);
        this.code = code;
    }

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
