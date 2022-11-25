package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Skill extends AbstractPersistable {

    private String code;

    public Skill() {
    }

    public Skill(long id, String code) {
        super(id);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

}
