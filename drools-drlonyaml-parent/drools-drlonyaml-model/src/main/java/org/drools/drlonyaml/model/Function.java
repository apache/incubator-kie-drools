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

import org.drools.drl.ast.descr.FunctionDescr;

public class Function {
    private String name;
    private String returnType;
    private List<Parameter> parameters = new ArrayList<>();
    private String body;
    
    public static Function from(FunctionDescr f) {
        Objects.requireNonNull(f);
        Function result = new Function();
        result.name = f.getName();
        result.returnType = f.getReturnType();
        for (int i = 0; i < f.getParameterNames().size(); i++) {
            Parameter p = new Parameter();
            p.name = f.getParameterNames().get(i);
            p.type = f.getParameterTypes().get(i);
            result.parameters.add(p);
        }
        result.body = f.getBody();
        return result;
    }
    
    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }
    
    public static class Parameter {
        private String name;
        private String type;
        
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
    }
}
