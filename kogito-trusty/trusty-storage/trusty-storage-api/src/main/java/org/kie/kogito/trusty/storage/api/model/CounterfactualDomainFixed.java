/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.storage.api.model;

/**
 * CounterfactualExplainer works on discrete lists of parameters; one for goals, one for search domains and one for whether the input is fixed.
 * It is important these lists have equal amounts of entries as the index of elements in each list is the unifying identifier. Therefore,
 * even though it is possible to define a search domain as fixed i.e. equal to the original input, a search domain is still required.
 * This class acts as a place-holder to pad the list of search domains to ensure the integrity of indexes.
 */
public final class CounterfactualDomainFixed extends CounterfactualDomain {

    public static final String TYPE = "FIXED";

}
