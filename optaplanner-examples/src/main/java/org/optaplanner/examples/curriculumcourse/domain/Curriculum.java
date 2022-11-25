package org.optaplanner.examples.curriculumcourse.domain;

import static java.util.Objects.requireNonNull;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Curriculum.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Curriculum extends AbstractPersistable implements Labeled {

    private String code;

    public Curriculum() {
    }

    public Curriculum(int id, String code) {
        super(id);
        this.code = requireNonNull(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getLabel() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

}
