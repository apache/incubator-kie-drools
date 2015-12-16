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

package org.drools.examples.diagnostics;

public class WontStart {
    public static Question newQ1() {
        return new Question("1", "Starter cranks?");
    }

    public static Question newQ2() {
        return new Question("2", "Starter spins?");
    }

    public static Question newQ3() {
        return new Question("3", "Battery read over 12V?");
    }

    public static Question newQ4() {
        return new Question("4", "Cleaned terminals?");
    }
}
