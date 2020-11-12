/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model.functions;

/**
 * Represents a lambda expression with a stable fingerprint.
 * This field is used by org.drools.model.functions.IntrospectableLambda to leverage some features
 * such as FromNode sharing.
 * See org.drools.compiler.integrationtests.operators.FromTest#testFromSharing
 * and org.drools.compiler.integrationtests.operators.FromOnlyExecModelTest#testFromSharingWithNativeImage
 */
public interface HashedExpression {

    String getExpressionHash();

}
