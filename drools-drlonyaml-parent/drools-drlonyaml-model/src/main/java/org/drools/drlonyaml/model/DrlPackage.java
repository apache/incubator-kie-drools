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

import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "unit", "dialect", "imports", "globals", "rules", "functions"})
public class DrlPackage {

    @JsonInclude(Include.NON_EMPTY)
    private String name = ""; // default empty, consistent with DRL parser.

    @JsonInclude(Include.NON_EMPTY)
    private String unit = "";

    @JsonInclude(Include.NON_EMPTY)
    private String dialect = "";

    @JsonInclude(Include.NON_EMPTY)
    private List<Import> imports = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private List<Global> globals = new ArrayList<>();

    private List<Rule> rules = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private List<Function> functions = new ArrayList<>();
    
    public static DrlPackage from(PackageDescr pkg) {
        Objects.requireNonNull(pkg);
        DrlPackage result = new DrlPackage();
        result.name = pkg.getName();

        if (pkg.getUnit() != null) {
            result.unit = pkg.getUnit().getTarget();
        }

        AttributeDescr dialectAttr = pkg.getAttribute("dialect");
        if (dialectAttr != null) {
            result.dialect = dialectAttr.getValue();
        }

        for (ImportDescr i : pkg.getImports()) {
            result.imports.add(Import.from(i));
        }
        for (GlobalDescr g : pkg.getGlobals()) {
            result.globals.add(Global.from(g));
        }
        for (RuleDescr r : pkg.getRules()) {
            result.rules.add(Rule.from(r));
        }
        for (FunctionDescr f : pkg.getFunctions()) {
            result.functions.add(Function.from(f));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getDialect() {
        return dialect;
    }

    public List<Import> getImports() {
        return imports;
    }
    
    public List<Global> getGlobals() {
        return globals;
    }

    public List<Rule> getRules() {
        return rules;
    }
    
    public List<Function> getFunctions() {
        return functions;
    }
}
