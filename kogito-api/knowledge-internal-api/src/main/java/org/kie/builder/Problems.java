package org.kie.builder;

import java.util.List;

public interface Problems {
    List<Problem> getInsertedProblems();
    List<Problem> getDeletedProblems();
}
