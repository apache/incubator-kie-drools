/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.drl.ast.descr.ExistsDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Exists.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class Exists implements Base {
    @JsonProperty(required = true)
    private List<Base> exists = new ArrayList<>();
    
    public static Exists from(ExistsDescr o) {
        Objects.requireNonNull(o);
        Exists result = new Exists();
        result.exists = Utils.from(o.getDescrs());
        return result;
    }
    
    public List<Base> getExists() {
        return exists;
    }
}
