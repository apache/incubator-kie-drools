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
package org.kie.dmn.feel.lang.types.impl;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.model.SupportRequest;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;

import static org.assertj.core.api.Assertions.assertThat;

class ImmutableFPAWrappingPOJOTest {

    @Test
    void supportRequest() {
        ImmutableFPAWrappingPOJO fpa = new ImmutableFPAWrappingPOJO(new SupportRequest("John Doe", "47", "info@redhat.com", "+1", "somewhere", "tech", "app crashed", false));

        assertThat(fpa.getFEELProperty("full name").toOptional()).contains("John Doe");
        assertThat(fpa.getFEELProperty("account").toOptional()).contains("47");
        assertThat(fpa.getFEELProperty("email").toOptional()).contains("info@redhat.com");
        assertThat(fpa.getFEELProperty("mobile").toOptional()).contains("+1");
        assertThat(fpa.getFEELProperty("mailing address").toOptional()).contains("somewhere");
        assertThat(fpa.getFEELProperty("area").toOptional()).contains("tech");
        assertThat(fpa.getFEELProperty("description").toOptional()).contains("app crashed");
        assertThat(fpa.getFEELProperty("premium").toOptional()).contains(false);

        assertThat(fpa.getFEELProperty("priority")).isInstanceOfSatisfying(PropertyValueResult.class, x -> assertThat(x.isDefined()).isTrue()); // property exists, but it's null.

        assertThat(fpa.getFEELProperty("unexisting").toOptional()).isEmpty();

        assertThat(fpa.allFEELProperties()).hasFieldOrPropertyWithValue("full name", "John Doe")
                                           .hasFieldOrPropertyWithValue("account", "47")
                                           .hasFieldOrPropertyWithValue("email", "info@redhat.com")
                                           .hasFieldOrPropertyWithValue("mobile", "+1")
                                           .hasFieldOrPropertyWithValue("mailing address", "somewhere")
                                           .hasFieldOrPropertyWithValue("area", "tech")
                                           .hasFieldOrPropertyWithValue("description", "app crashed")
                                           .hasFieldOrPropertyWithValue("premium", false);
    }
}

