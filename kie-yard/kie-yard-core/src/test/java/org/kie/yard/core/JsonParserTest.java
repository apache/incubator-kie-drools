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
package org.kie.yard.core;

import org.drools.util.IoUtils;
import org.junit.jupiter.api.Test;
import org.kie.yard.api.model.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class JsonParserTest {

    @Test
    public void testWhenThen() throws Exception {
        String s = new String(IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/insurance-base-price.json"), true));
        YaRD yaRD = new YaRD_JsonMapperImpl().fromJSON(s);

        assertEquals("Base price", yaRD.getElements().get(0).getName());
        final DecisionTable dtable = (DecisionTable) yaRD.getElements().get(0).getLogic();
        assertEquals(1, dtable.getRules().get(0).getRowNumber());
        assertEquals(2, dtable.getRules().get(1).getRowNumber());
        final WhenThenRule rule = (WhenThenRule) dtable.getRules().get(0);
        assertEquals("<21", rule.getWhen().get(0));
        assertEquals(new BigDecimal(800), rule.getThen());
    }

    @Test
    public void testInlineRule() throws Exception {
        String s = new String(IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/simplified-ticket-score.json"), true));
        YaRD yaRD = new YaRD_JsonMapperImpl().fromJSON(s);

        assertEquals("Level", yaRD.getElements().get(0).getName());
        DecisionTable dtable = (DecisionTable) yaRD.getElements().get(0).getLogic();
        assertInstanceOf(InlineRule.class, dtable.getRules().get(0));
    }

}
