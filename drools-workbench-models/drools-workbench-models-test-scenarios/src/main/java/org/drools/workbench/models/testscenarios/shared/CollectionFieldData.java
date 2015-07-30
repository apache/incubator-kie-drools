/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.testscenarios.shared;

import java.util.ArrayList;
import java.util.List;

public class CollectionFieldData implements Field {

    private String name;

    private List<FieldData> collectionFieldList = new ArrayList<FieldData>();

    @Override
    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public List<FieldData> getCollectionFieldList() {
        return collectionFieldList;
    }

    public void setCollectionFieldList( final List<FieldData> collectionFieldList ) {
        this.collectionFieldList = collectionFieldList;
    }
}
