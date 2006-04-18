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

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * 
 */
public class DRLElementTest extends TestCase
{

    public void testEscaping()
    {
        assertNotNull( "this test is not needed, as using CDATA now" );
        String snippet = "user.getAge() >=  20 && user.getAge() <= 31";
        String result = DRLElement.escapeSnippet( snippet );
        assertEquals( "user.getAge() &gt;=  20 &amp;&amp; user.getAge() &lt;= 31",
                      result );

        snippet = "nothing";
        assertEquals( snippet,
                      DRLElement.escapeSnippet( snippet ) );
    }

}