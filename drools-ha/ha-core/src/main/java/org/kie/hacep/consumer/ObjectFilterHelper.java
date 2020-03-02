/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.hacep.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class ObjectFilterHelper {

  private ObjectFilterHelper() {}

  public static Collection getObjectsFilterByNamedQuery(String namedQuery, String objectName, Object[] params, KieSession kieSession) {
    QueryResults results = kieSession.getQueryResults(namedQuery, params);
    Iterator<QueryResultsRow> rowsIter = results.iterator();
    List objects = new ArrayList(results.size());
    while (rowsIter.hasNext()) {
      QueryResultsRow row = rowsIter.next();
      objects.add(row.get(objectName));
    }
    return objects;
  }

  public static Collection getObjectsFilterByClassType(Class clazzType, KieSession kieSession) {
    return kieSession.getObjects(new ClassObjectFilter(clazzType));
  }
}
