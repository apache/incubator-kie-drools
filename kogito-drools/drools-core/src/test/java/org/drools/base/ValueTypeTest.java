/**
 * Copyright 2010 JBoss Inc
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

package org.drools.base;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ValueTypeTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsBoolean() {
        Assert.assertTrue( ValueType.BOOLEAN_TYPE.isBoolean() );
        Assert.assertTrue( ValueType.PBOOLEAN_TYPE.isBoolean() );
    }

    public void testIsNumber() {
        Assert.assertTrue( ValueType.PBYTE_TYPE.isNumber() );
        Assert.assertTrue( ValueType.PSHORT_TYPE.isNumber() );
        Assert.assertTrue( ValueType.PINTEGER_TYPE.isNumber() );
        Assert.assertTrue( ValueType.PLONG_TYPE.isNumber() );
        Assert.assertTrue( ValueType.PFLOAT_TYPE.isNumber() );
        Assert.assertTrue( ValueType.PDOUBLE_TYPE.isNumber() );
        Assert.assertTrue( ValueType.BYTE_TYPE.isNumber() );
        Assert.assertTrue( ValueType.SHORT_TYPE.isNumber() );
        Assert.assertTrue( ValueType.INTEGER_TYPE.isNumber() );
        Assert.assertTrue( ValueType.LONG_TYPE.isNumber() );
        Assert.assertTrue( ValueType.FLOAT_TYPE.isNumber() );
        Assert.assertTrue( ValueType.DOUBLE_TYPE.isNumber() );

    }

}
