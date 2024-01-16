/**
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
package org.drools.quarkus.quickstart.test;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

import org.drools.quarkus.quickstart.test.model.Alert;
import org.drools.quarkus.quickstart.test.model.CCTV;
import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.quarkus.quickstart.test.model.Smartphone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@QuarkusTest
public class BeanTest {

    @Inject
    HomeAlertsBean alerts;

    @Test
    public void testRuleOutside() {
        Collection<Alert> computeAlerts = alerts.computeAlerts(List.of(new Light("living room", true), new Light("bedroom", false), new Light("bathroom", false)),
                Collections.emptyList(),
                Collections.emptyList());

        assertThat(computeAlerts).isNotEmpty().contains(new Alert("You might have forgot one light powered on: living room"));
    }
    
    @Test
    public void testRuleInside() {
        Collection<Alert> computeAlerts = alerts.computeAlerts(List.of(new Light("living room", true), new Light("bedroom", false), new Light("bathroom", false)),
                List.of(new CCTV("security camera 1", false), new CCTV("security camera 2", true)),
                List.of(new Smartphone("John Doe's phone")));
        
        assertThat(computeAlerts).isNotEmpty().contains(new Alert("One CCTV is still operating: security camera 2"));
    }
    
    @Test
    public void testNoAlerts() {
        Collection<Alert> computeAlerts = alerts.computeAlerts(List.of(new Light("living room", false), new Light("bedroom", false), new Light("bathroom", false)),
                List.of(new CCTV("security camera 1", true), new CCTV("security camera 2", true)),
                Collections.emptyList());
        
        assertThat(computeAlerts).isEmpty();
    }
}
