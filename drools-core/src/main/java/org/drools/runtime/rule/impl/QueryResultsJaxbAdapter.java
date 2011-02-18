/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.runtime.rule.QueryResults;


public class QueryResultsJaxbAdapter extends XmlAdapter<NativeQueryResults, FlatQueryResults>{

    @Override
    public NativeQueryResults marshal(FlatQueryResults v) throws Exception {
        return null;
    }

    @Override
    public FlatQueryResults unmarshal(NativeQueryResults v) throws Exception {
        return new FlatQueryResults(((NativeQueryResults)v).getResults());
    }


}
