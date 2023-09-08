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
package org.kie.dmn.openapi;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.openapi.impl.FEELSchemaEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELSchemaEnumTest extends BaseDMNOASTest {

    @Test
    public void testBasic() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, null, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNotNull().isEqualTo(new RangeImpl(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED));
    }

    @Test
    public void testInvalidRepeatedLB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, null, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }

    @Test
    public void testInvalidRepeatedUB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 50, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }
}
