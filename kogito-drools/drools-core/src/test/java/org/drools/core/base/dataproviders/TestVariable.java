/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.dataproviders;

import java.util.ArrayList;
import java.util.List;

public class TestVariable {

    public String helloWorld(final String a1,
                             final int a2,
                             final String a3) {
        return a1 + a2 + a3;
    }

    public List otherMethod() {
        final List list = new ArrayList();
        list.add( "boo" );
        return list;
    }

    public String helloWorld() {
        return "another one";
    }

}
