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
package org.drools.decisiontable.parser;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RhsBuilderTest {

    @Test
    public void testConsBuilding() {
        RhsBuilder builder = new RhsBuilder(ActionType.Code.ACTION, 9, 1, "foo");
        builder.addTemplate(10, 1, "setFoo($param)");
        builder.addCellValue(10,1, "42");


        assertThat(builder.getResult()).isEqualTo("foo.setFoo(42);");
        
        builder.clearValues();
        builder.addCellValue(10, 1, "33");
        
        assertThat(builder.getResult()).isEqualTo("foo.setFoo(33);");
    }
    
    @Test
    public void testClassicMode() {
        RhsBuilder builder = new RhsBuilder(ActionType.Code.ACTION, 9, 1, "");
        builder.addTemplate(10, 1, "p.setSomething($param);");
        builder.addTemplate(10, 2, "drools.clearAgenda();");
                
        builder.addCellValue(12, 1, "42");

        assertThat(builder.getResult()).isEqualTo("p.setSomething(42);");
                
        builder.addCellValue(12, 2, "Y");

        assertThat(builder.getResult()).isEqualTo("p.setSomething(42);\ndrools.clearAgenda();");
    }

    @Test
    public void testMetadata() {
        RhsBuilder builder = new RhsBuilder(ActionType.Code.METADATA, 9, 1, "");
        builder.addTemplate(10, 1, "Author($param)");
                
        builder.addCellValue(12, 1, "A. U. Thor");
        
        assertThat(builder.getResult()).isEqualTo("Author(A. U. Thor)");
        
        builder.clearValues();
        builder.addCellValue( 13, 1, "P. G. Wodehouse" );
        
        assertThat(builder.getResult()).isEqualTo("Author(P. G. Wodehouse)");
    }

    @Test
    public void testEmptyCellData() {
        RhsBuilder builder = new RhsBuilder(ActionType.Code.ACTION, 9, 1, "Foo");
        builder.addTemplate(10, 1, "p.setSomething($param);");
        
        assertThat(builder.hasValues()).isFalse();
    }
    
}
