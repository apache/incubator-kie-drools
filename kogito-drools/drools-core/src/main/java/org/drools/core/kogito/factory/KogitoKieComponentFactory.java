/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.kogito.factory;

import org.drools.core.common.AgendaFactory;
import org.drools.core.common.KogitoDefaultAgendaFactory;
import org.drools.core.common.KogitoPhreakWorkingMemoryFactory;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.spi.FactHandleFactory;

public class KogitoKieComponentFactory extends KieComponentFactory {

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return new KogitoFactHandleFactory();
    }

    private WorkingMemoryFactory wmFactory = KogitoPhreakWorkingMemoryFactory.getInstance();

    @Override
    public WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }

    private AgendaFactory agendaFactory = KogitoDefaultAgendaFactory.getInstance();

    @Override
    public AgendaFactory getAgendaFactory() {
        return agendaFactory;
    }

}
