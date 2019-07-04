/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.services.uow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;

public class DefaultUnitOfWorkManagerTest {
    
    private UnitOfWorkManager unitOfWorkManager;
    
    @BeforeEach
    public void setup() {
        this.unitOfWorkManager = new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory());
        
    }

    @Test
    public void testFallbackUnitOfWork() {
        
        UnitOfWork unit = unitOfWorkManager.currentUnitOfWork();
        assertThat(unit).isNotNull().isInstanceOf(PassThroughUnitOfWork.class);
    }
    
    @Test
    public void testUnitOfWorkStartEnd() {
        
        UnitOfWork unit = unitOfWorkManager.newUnitOfWork();
        assertThat(unit).isNotNull().isInstanceOf(ManagedUnitOfWork.class);
        assertThat(((ManagedUnitOfWork)unit).delegate()).isInstanceOf(CollectingUnitOfWork.class);
        
        final AtomicInteger counter = new AtomicInteger(0);
        assertThat(counter.get()).isEqualTo(0);
        
        BaseWorkUnit dummyWork = new BaseWorkUnit(counter, (d) -> ((AtomicInteger) d).incrementAndGet());
        unit.start();
        unit.intercept(dummyWork);
        unit.end();
        
        assertThat(counter.get()).isEqualTo(1);        
    }
    
    @Test
    public void testUnitOfWorkStartAbort() {
        
        UnitOfWork unit = unitOfWorkManager.newUnitOfWork();
        assertThat(unit).isNotNull().isInstanceOf(ManagedUnitOfWork.class);
        
        final AtomicInteger counter = new AtomicInteger(0);
        assertThat(counter.get()).isEqualTo(0);
        
        BaseWorkUnit dummyWork = new BaseWorkUnit(counter, (d) -> ((AtomicInteger) d).incrementAndGet());
        unit.start();
        unit.intercept(dummyWork);
        unit.abort();
        
        assertThat(counter.get()).isEqualTo(0);        
    }
    
    @Test
    public void testUnitOfWorkStartOnFinishedUnit() {
        
        UnitOfWork unit = unitOfWorkManager.newUnitOfWork();
        assertThat(unit).isNotNull().isInstanceOf(ManagedUnitOfWork.class);
        
        unit.start();        
        unit.abort();
        assertThrows(IllegalStateException.class, () -> unit.start(), "Cannot start already completed unit");
        assertThrows(IllegalStateException.class, () -> unit.end(), "Cannot end already completed unit");
        assertThrows(IllegalStateException.class, () -> unit.abort(), "Cannot abort already completed unit");
        assertThrows(IllegalStateException.class, () -> unit.intercept(null), "Cannot intercept on already completed unit");              
    }
    
    @Test
    public void testUnitOfWorkAddWorkOnNotStarted() {
        
        UnitOfWork unit = unitOfWorkManager.newUnitOfWork();
        assertThat(unit).isNotNull().isInstanceOf(ManagedUnitOfWork.class);
        
        final AtomicInteger counter = new AtomicInteger(0);
        assertThat(counter.get()).isEqualTo(0);
        
        WorkUnit<AtomicInteger> dummyWork = WorkUnit.create(counter, (d) -> d.incrementAndGet());

        
        assertThrows(IllegalStateException.class, () -> unit.intercept(dummyWork), "Cannot intercept on not started unit");
              
    }
}
