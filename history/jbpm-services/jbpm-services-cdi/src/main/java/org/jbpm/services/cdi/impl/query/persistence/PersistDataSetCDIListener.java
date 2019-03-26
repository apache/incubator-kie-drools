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

package org.jbpm.services.cdi.impl.query.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.jbpm.kie.services.impl.query.persistence.PersistDataSetListener;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@ApplicationScoped
public class PersistDataSetCDIListener extends PersistDataSetListener {

    public PersistDataSetCDIListener() {
        
    }
    
    @Inject
    public PersistDataSetCDIListener(TransactionalCommandService commandService) {
        super(commandService);
    }

    
    public void onDataSetDefStale(@Observes DataSetStaleEvent event) {
        super.onDataSetDefStale(event.getDataSetDef());
    }

    
    public void onDataSetDefModified(@Observes DataSetDefModifiedEvent event) {
        super.onDataSetDefModified(event.getOldDataSetDef(), event.getNewDataSetDef());
    }

    
    public void onDataSetDefRegistered(@Observes DataSetDefRegisteredEvent event) {
        super.onDataSetDefRegistered(event.getDataSetDef());
    }

    
    public void onDataSetDefRemoved(@Observes DataSetDefRemovedEvent event) {
        super.onDataSetDefRemoved(event.getDataSetDef());
    }

}
