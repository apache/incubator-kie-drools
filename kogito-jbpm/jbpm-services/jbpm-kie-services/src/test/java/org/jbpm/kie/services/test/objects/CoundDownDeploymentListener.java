/*
 * Copyright 2015 JBoss by Red Hat.
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

package org.jbpm.kie.services.test.objects;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoundDownDeploymentListener implements DeploymentEventListener {
    private static Logger logger = LoggerFactory.getLogger(CoundDownDeploymentListener.class);

    private CountDownLatch latch;
    
    public CoundDownDeploymentListener() {
        this.latch = new CountDownLatch(0);
    }
    
    public CoundDownDeploymentListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }
    
    @Override
    public void onDeploy(DeploymentEvent event) {
        this.latch.countDown();
    }

    @Override
    public void onUnDeploy(DeploymentEvent event) {
        this.latch.countDown();
    }

    @Override
    public void onActivate(DeploymentEvent event) {
        this.latch.countDown();
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        this.latch.countDown();
    }
    
    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers notification/reassignment");
        }
    }
    
    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers notification/reassignment");
        }
    }
    
    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }
}