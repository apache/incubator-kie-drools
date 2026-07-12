/*
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

/**
 * This package contains performance-sensitive code.
 * Much of it is directly on the hot path of the solver.
 * It contains various micro-optimizations, the benefits of which have been confirmed by extensive benchmarking.
 * When it comes to this code, assumptions and pre-conceived notions of JVM performance should not be trusted.
 * Instead, any likely performance-altering modifications to this code should be carefully benchmarked.
 */
package org.optaplanner.constraint.streams.bavet.common;