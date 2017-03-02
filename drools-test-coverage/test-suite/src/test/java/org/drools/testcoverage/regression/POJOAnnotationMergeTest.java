/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.definition.type.Position;
import org.kie.api.io.Resource;

import java.io.StringReader;

/**
 * Tests merging the POJO annotations (e.g. @Position) with fact declaration in
 * DRL.
 */
public class POJOAnnotationMergeTest {

    private static final String EVENT_CLASS_NAME = PositionAnnotatedEvent.class.getCanonicalName();

    // should add metadata to metadata already defined in POJO
    private static final String DRL =
            "package org.test\n" +
            "declare " + EVENT_CLASS_NAME + "\n" +
            "    @role(event)\n" +
            "end \n" +
            "rule 'sample rule' \n" +
            "when \n" +
            "  " + EVENT_CLASS_NAME + "( 'value1', 'value2'; )\n" +
            "then\n" +
            "end\n";

    /**
     * Tests adding metadata in DRL to the metadata already declared in a POJO.
     */
    @Test
    public void testPositionFromPOJOIgnored() {
        final Resource resource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
        resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        KieBaseUtil.getKieBuilderFromResources(true,resource);
    }

    /**
     * Sample event annotated with @Position metadata.
     */
    public static class PositionAnnotatedEvent {
        @Position(1)
        private String arg1;

        @Position(0)
        private String arg0;

        public String getArg1() {
            return arg1;
        }

        public void setArg1(final String arg1) {
            this.arg1 = arg1;
        }

        public String getArg0() {
            return arg0;
        }

        public void setArg0(final String arg0) {
            this.arg0 = arg0;
        }

    }
}
