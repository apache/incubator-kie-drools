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

package org.drools.core.reteoo.test.dsl;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

/**
 * A class to describe a test case for reteoo nodes
 */
public class NodeTestCase {

    public static final String SUFFIX = ".nodeTestCase";

    private String             name;
    private List<String>       imports;
    private List<DslStep>      setup;
    private List<DslStep>      tearDown;
    private List<NodeTestDef>  tests;
    private List<String>       errors;
    private Description        description;
    private String             fileName;

    public NodeTestCase() {
        this( "" );
    }

    public NodeTestCase(String name) {
        this.name = name;
        this.imports = new ArrayList<String>();
        this.setup = new ArrayList<DslStep>();
        this.tearDown = new ArrayList<DslStep>();
        this.tests = new ArrayList<NodeTestDef>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addImport(String clazz) {
        this.imports.add( clazz );
    }

    public List<String> getImports() {
        return this.imports;
    }

    public List<DslStep> getSetup() {
        return setup;
    }

    public void addSetupStep(DslStep step) {
        this.setup.add( step );
    }

    public List<DslStep> getTearDown() {
        return tearDown;
    }

    public void addTearDownStep(DslStep step) {
        this.tearDown.add( step );
    }

    public List<NodeTestDef> getTests() {
        return tests;
    }

    public void addTest(NodeTestDef test) {
        this.tests.add( test );
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return this.errors != null && !this.errors.isEmpty();
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public void setSetup(List<DslStep> setup) {
        this.setup = setup;
    }

    public void setTearDown(List<DslStep> tearDown) {
        this.tearDown = tearDown;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = removeSuffix( fileName );
    }

    private String removeSuffix(String name) {
//        // removes the suffix, if present.
//        if ( name.endsWith( SUFFIX ) ) {
//            return name.substring( 0,
//                                   name.indexOf( SUFFIX ) );
//        }

        return name;

    }

}
