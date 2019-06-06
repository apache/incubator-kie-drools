/*
 * Copyright 2005 JBoss Inc
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

package org.kie.kogito.codegen.data;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnitMemory;
import org.kie.kogito.rules.impl.ListDataSource;

public class AdultUnit implements RuleUnitMemory {
    private int adultAge = 18;
    private DataSource<Person> persons = new ListDataSource<>();
    private DataSource<Results> results = new ListDataSource<>();

    public AdultUnit( ) {
        this( new ListDataSource<>() );
    }

    public AdultUnit( DataSource<Person> persons ) {
        this.persons = persons;
    }

    public DataSource<Person> getPersons() {
        return persons;
    }

    public int getAdultAge() {
        return adultAge;
    }

    public DataSource<Results> getResults() {
        return results;
    }
}
