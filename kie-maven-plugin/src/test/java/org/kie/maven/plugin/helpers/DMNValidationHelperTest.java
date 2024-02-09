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
package org.kie.maven.plugin.helpers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
import org.kie.dmn.validation.DMNValidator;

import static org.assertj.core.api.Assertions.assertThat;
public class DMNValidationHelperTest {

    private final static Log log = new SystemStreamLog();

    @Test
    public void testFlagsOK() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("VALIDATE_SCHEMA,VALIDATE_MODEL", log);
        assertThat(result).isNotNull()
                .hasSize(2)
                .contains(DMNValidator.Validation.VALIDATE_SCHEMA, DMNValidator.Validation.VALIDATE_MODEL);
    }

    @Test
    public void testFlagsDisable() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("disabled", log);
        assertThat(result).isNotNull()
                .hasSize(0);
    }

    @Test
    public void testFlagsUnknown() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("VALIDATE_SCHEMA,boh", log);
        assertThat(result).isNotNull()
                .hasSize(0);
    }
}