/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.models.guided.dtable.shared.model;

/**
 * A Fact Pattern used by the ActionInsertFactFieldsPage Wizard page adding
 * a flag indicating whether the Pattern is inserted Logically or not
 */
@SuppressWarnings("serial")
public class ActionInsertFactFieldsPattern extends Pattern52 {

    private boolean isInsertedLogically;

    public boolean isInsertedLogically() {
        return isInsertedLogically;
    }

    public void setInsertedLogically( boolean isInsertedLogically ) {
        this.isInsertedLogically = isInsertedLogically;
    }

}
