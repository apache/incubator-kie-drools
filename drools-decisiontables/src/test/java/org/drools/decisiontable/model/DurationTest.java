package org.drools.decisiontable.model;
/*
 * Copyright 2005 JBoss Inc
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

import junit.framework.TestCase;

public class DurationTest extends TestCase
{

    /**
     * Test basic rendering and parsing of arguments
     */
    public void testDurationRender() {
        Duration duration = new Duration();
        duration.setSnippet("1234");
        DRLOutput out = new DRLOutput();
        duration.renderDRL(out);
        String res = out.getDRL();
        System.out.println(res);
        assertEquals("\tduration 1234\n", res);

    }
    
}
