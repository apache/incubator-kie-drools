package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Not used during score calculation, so not inserted into the working memory.
 */
@XStreamAlias("Student")
public class Student extends AbstractPersistable {
    public Student() {
    }

    public Student(long id) {
        super(id);
    }
}
