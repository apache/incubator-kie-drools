package org.kie.builder;

import java.util.List;

public interface KieBuilder {

    List<Problem> build();

    boolean hasProblems();

    Problems getProblems();

    KieJar getKieJar();

    KieBuilder add(KieFileSystem kieFileSystem);
}
