package org.drools.reteoo.test;

import java.util.List;

import org.drools.reteoo.test.dsl.NodeTestCase;

public interface NodeTestCasesSource {

    public List<NodeTestCase> getTestCases() throws Exception;

}