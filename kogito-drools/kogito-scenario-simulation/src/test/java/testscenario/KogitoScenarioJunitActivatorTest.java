/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package testscenario;

import org.kogito.scenariosimulation.runner.KogitoJunitActivator;

/**
 * KogitoJunitActivator is a custom JUnit runner that enables the execution of Test Scenario files (*.scesim).
 * This activator class, when executed, will load all scesim files available in the project and run them.
 * Each row of the scenario will generate a test JUnit result.
 */
@org.junit.runner.RunWith(KogitoJunitActivator.class)
public class KogitoScenarioJunitActivatorTest {
}