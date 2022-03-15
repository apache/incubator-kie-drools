/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.xpath.tobeinstrumented.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FieldsSomeInterfacesSomeNot {

    private ArrayList myField = new ArrayList();
    private List myField2 = new ArrayList();
    private Collection myField3 = new ArrayList();

    public FieldsSomeInterfacesSomeNot() {
        myField = new ArrayList();
    }

    public List getMyField() {
        return myField;
    }

    public void setMyField(ArrayList myField) {
        this.myField = myField;
    }

    public List getMyField2() {
        return myField2;
    }

    public void setMyField2(List myField2) {
        this.myField2 = myField2;
    }

    public Collection getMyField3() {
        return myField3;
    }

    public void setMyField3(Collection myField3) {
        this.myField3 = myField3;
    }
}
