package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

/**
 * Not used during score calculation, so not inserted into the working memory.
 */
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Student extends AbstractPersistable {
    public Student() {
    }

    public Student(long id) {
        super(id);
    }
}
