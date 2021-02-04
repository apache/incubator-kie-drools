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

package org.jbpm.bpmn2.objects;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.Transition;

public class TestWorkItemHandler implements KogitoWorkItemHandler {

    private List<KogitoWorkItem> workItems = new ArrayList<>();

    public void executeWorkItem( KogitoWorkItem workItem, KogitoWorkItemManager manager ) {
        workItems.add( workItem );

        if ( workItem instanceof HumanTaskWorkItem ) {
            HumanTaskWorkItemImpl humanTaskWorkItem = ( HumanTaskWorkItemImpl ) workItem;

            humanTaskWorkItem.setPhaseId( Active.ID );
            humanTaskWorkItem.setPhaseStatus( Active.STATUS );
        }
    }

    public void abortWorkItem( KogitoWorkItem workItem, KogitoWorkItemManager manager ) {
    }

    public KogitoWorkItem getWorkItem() {
        if ( workItems.size() == 0 ) {
            return null;
        }
        if ( workItems.size() == 1 ) {
            KogitoWorkItem result = workItems.get( 0 );
            this.workItems.clear();
            return result;
        } else {
            throw new IllegalArgumentException( "More than one work item active" );
        }
    }

    public List<KogitoWorkItem> getWorkItems() {
        List<KogitoWorkItem> result = new ArrayList<>( workItems );
        workItems.clear();
        return result;
    }

    @Override
    public void transitionToPhase( KogitoWorkItem workItem, KogitoWorkItemManager manager, Transition<?> transition ) {


        if ( transition.phase().equals( Complete.ID ) ) {
            (( org.kie.kogito.process.workitems.KogitoWorkItemManager ) manager).internalCompleteWorkItem( ( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem );
        }
    }
}

