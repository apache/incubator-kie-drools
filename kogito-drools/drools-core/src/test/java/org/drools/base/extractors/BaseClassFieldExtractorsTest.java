/*
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

package org.drools.base.extractors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class BaseClassFieldExtractorsTest {

    public abstract void testGetBooleanValue();

    public abstract void testGetByteValue();

    public abstract void testGetCharValue();

    public abstract void testGetShortValue();

    public abstract void testGetIntValue();

    public abstract void testGetLongValue();

    public abstract void testGetFloatValue();

    public abstract void testGetDoubleValue();

    public abstract void testGetValue();
    
    public abstract void testIsNullValue();

}
