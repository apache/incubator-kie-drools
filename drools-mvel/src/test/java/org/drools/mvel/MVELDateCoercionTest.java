/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.drools.core.util.DateUtils;
import org.drools.mvel.expr.MVELDateCoercion;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELDateCoercionTest {

    @Test
    public void testDate() {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        assertThat(co.canConvertFrom(Number.class)).isFalse();

        Date d = new Date();
        assertThat(co.convertFrom(d)).isSameAs(d);
    }

    @Test
    public void testString() throws Exception {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        String dt = df.format(df.parse("10-Jul-1974"));
        Date dt_ = DateUtils.parseDate(dt);
        assertThat(co.convertFrom(dt)).isEqualTo(dt_);
    }

}
