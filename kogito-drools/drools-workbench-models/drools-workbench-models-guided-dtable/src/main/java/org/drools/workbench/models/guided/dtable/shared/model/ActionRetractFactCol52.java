/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

/**
 * A column representing the retraction of a Fact.
 */
public class ActionRetractFactCol52 extends ActionCol52 {

    private static final long serialVersionUID = 510l;

    //Columns not implementing LimitedEntryCol are for Extended Entry Decision Tables 
    //for which the values are held in the table data. Consequentially the identifier 
    //for the Fact being retracted will be contained in the table data. This class
    //is therefore effectively a marker-interface for this type of action.

}
