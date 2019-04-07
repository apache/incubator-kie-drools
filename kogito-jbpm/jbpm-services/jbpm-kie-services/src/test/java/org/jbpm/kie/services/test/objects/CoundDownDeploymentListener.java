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
    
    private boolean deploy;
    private boolean undeploy;
    private boolean activate;
    private boolean deactivate;
    
    public CoundDownDeploymentListener() {
        this.latch = new CountDownLatch(0);
    }
    
    public CoundDownDeploymentListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }
    
    @Override
    public void onDeploy(DeploymentEvent event) {
        if (deploy) {
            this.latch.countDown();
        }
    }

    @Override
    public void onUnDeploy(DeploymentEvent event) {
        if (undeploy) {
            this.latch.countDown();
        }
    }

    @Override
    public void onActivate(DeploymentEvent event) {
        if (activate) {
            this.latch.countDown();
        }
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        if (deactivate) {
            this.latch.countDown();
        }
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

    
    public boolean isDeploy() {
        return deploy;
    }

    
    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }

    
    public boolean isUndeploy() {
        return undeploy;
    }

    
    public void setUndeploy(boolean undeploy) {
        this.undeploy = undeploy;
    }

    
    public boolean isActivate() {
        return activate;
    }

    
    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    
    public boolean isDeactivate() {
        return deactivate;
    }

    
    public void setDeactivate(boolean deactivate) {
        this.deactivate = deactivate;
    }
}