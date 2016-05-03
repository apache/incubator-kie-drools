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

package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.BaseDescr;

/**
 * This Descr is used in jBPM code (jbpm-flow-builder) as part of the syntax tree
 * for the scripts used in BPMN2 definitions.
 */
public class ReturnValueDescr extends BaseDescr {
    private String text;

    public ReturnValueDescr() { }

    public ReturnValueDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
