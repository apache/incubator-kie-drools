/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.deployment;

import javax.ejb.EJBException;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.TestKjars;
import org.junit.Test;

public class EDeploymentTest extends RemoteEjbTest {

    @Test
    public void testDeployAndUndeploy() throws Exception {
        try {
            deployKjar(TestKjars.EVALUATION);
            Assertions.assertThat(RemoteEjbTest.ejb.isDeployed(TestKjars.EVALUATION.getGav())).isTrue();
        } finally {
            ejb.undeploy(TestKjars.EVALUATION.getGav());
            Assertions.assertThat(RemoteEjbTest.ejb.isDeployed(TestKjars.EVALUATION.getGav())).isFalse();
        }
    }

    @Test
    public void testDuplicateDeployAndUndeploy() throws Exception {
        try {
            deployKjar(TestKjars.EVALUATION);
            Assertions.assertThat(RemoteEjbTest.ejb.isDeployed(TestKjars.EVALUATION.getGav())).isTrue();
            try {
                deployKjar(TestKjars.EVALUATION);
                Assertions.failBecauseExceptionWasNotThrown(EJBException.class);
            } catch (Exception e) {
                Assertions.assertThat(e)
                        .isInstanceOf(EJBException.class)
                        .hasMessageEndingWith("Unit with id " + TestKjars.EVALUATION.getGav() + " is already deployed");
            }
        } finally {
            ejb.undeploy(TestKjars.EVALUATION.getGav());
            Assertions.assertThat(RemoteEjbTest.ejb.isDeployed(TestKjars.EVALUATION.getGav())).isFalse();
        }
    }

    @Test
    public void testUndeployNonExistent() throws Exception {
        ejb.undeploy(TestKjars.EVALUATION.getGav());
    }
}
