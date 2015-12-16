/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.commons.backend.rule;

import java.util.Map;

/**
 * Utility interface to define an operation to build DRL for a given set of
 * parameters. The only existing use-case at present is to supplement CEP
 * operators with additional meta-information.
 */
public interface OperatorParameterDRLBuilder {

    /**
     * Generate Operator DRL for the given parameters. The parameter Map is not
     * pruned and contains all entries made by the Widget corresponding to the
     * type of operator for which DRL is being built.
     * 
     * @param parameters
     * @return
     */
    public StringBuilder buildDRL( Map<String, String> parameters );

}
