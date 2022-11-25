package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ShiftType extends AbstractPersistable implements Labeled {

    private String code;
    private int index;
    private String startTimeString;
    private String endTimeString;
    private boolean night;
    private String description;

    public ShiftType() {
    }

    public ShiftType(long id) {
        super(id);
    }

    public ShiftType(long id, String code, int index, String startTimeString, String endTimeString, boolean night,
            String description) {
        super(id);
        this.code = code;
        this.index = index;
        this.startTimeString = startTimeString;
        this.endTimeString = endTimeString;
        this.night = night;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public void setEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLabel() {
        return code + " (" + description + ")";
    }

    @Override
    public String toString() {
        return code;
    }

}
