/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.inliner;

import java.util.List;
import java.util.function.Supplier;

/**
 * This interface exists so that justifications can be created lazily
 * and for that pattern to naturally spread throughout the codebase.
 *
 * Justifications must be specifically enabled, and that is usually done outside of the hot path,
 * as that code is expensive.
 * Therefore constructing lists of justifications in case they are ever needed is a waste of CPU cycles.
 */
@FunctionalInterface
public interface JustificationsSupplier extends Supplier<List<Object>> {
}
