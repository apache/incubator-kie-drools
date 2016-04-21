/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.CommonTestMethodBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test attempts to test some of the logic for DROOLS-1109.
 * </p>
 * However, the majority of the tests for this logic will be found in the drools-wb and kie-wb-common modules, where
 * the Indexer implemenations are found which use the logic created in DROOLS-1109.
 *
 * See https://issues.jboss.org/browse/DROOLS-1109
 */
public class ResolutionOfRHSPartReferencesTest extends CommonTestMethodBase {

    protected static final transient Logger logger = LoggerFactory.getLogger(ResolutionOfRHSPartReferencesTest.class);

}
