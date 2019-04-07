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

package org.jbpm.test.container;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jbpm.test.container.archive.EJBTransactions;

import org.jbpm.test.container.archive.ejbtransactions.ProcessEJB;
import org.jbpm.test.container.archive.ejbtransactions.StatefulBMT;
import org.jbpm.test.container.archive.ejbtransactions.StatefulCMT;
import org.jbpm.test.container.archive.ejbtransactions.StatelessBMT;
import org.jbpm.test.container.archive.ejbtransactions.StatelessCMT;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.After;

/**
 * Wrapper for testing in EJB. Responsibilities: - tests are running in
 * container (arquillian) - provides access to all
 * supported EJBs - cleans up knowledge session after each test method - creates
 * data provider to enable usage of EJBs through test method parameter.
 */
public abstract class AbstractEJBTransactionsTest extends JbpmContainerTest {

    protected static EJBTransactions et = new EJBTransactions();

    @EJB
    private StatefulBMT statefulBeanWithBMT;

    @EJB
    private StatefulCMT statefulBeanWithCMT;

    @EJB
    private StatelessBMT statelessBeanWithBMT;

    @EJB
    private StatelessCMT statelessBeanWithCMT;

    @Deployment(name = "EJBTransactions")
    @TargetsContainer(REMOTE_CONTAINER)
    public static Archive<EnterpriseArchive> deployEJBTest() {

        EnterpriseArchive ear = et.buildArchive();

        System.out.println("### Deploying ear '" + ear + "'");

        return ear;

    }

    @After
    public void cleanup() {
        for (ProcessEJB ejb : getAllEJBs()) {
            if (ejb != null) {
                ejb.dispose();
            }
        }
    }

    protected ProcessEJB getStatefulBMT() {
        return statefulBeanWithBMT;
    }

    protected ProcessEJB getStatelessBMT() {
        return statelessBeanWithBMT;
    }

    protected ProcessEJB getStatefulCMT() {
        return statefulBeanWithCMT;
    }

    protected ProcessEJB getStatelessCMT() {
        return statelessBeanWithCMT;
    }

    protected List<ProcessEJB> getAllEJBs() {
        List<ProcessEJB> ejbs = new ArrayList<ProcessEJB>();
        ejbs.add(getStatefulBMT());
        ejbs.add(getStatelessBMT());
        ejbs.add(getStatefulCMT());
        ejbs.add(getStatelessCMT());

        return ejbs;
    }

}
