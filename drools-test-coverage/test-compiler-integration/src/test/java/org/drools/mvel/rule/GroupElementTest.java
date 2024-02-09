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
package org.drools.mvel.rule;

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.GroupElementFactory;
import org.drools.base.rule.Pattern;
import org.drools.core.test.model.Person;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class GroupElementTest {

    @Test
    public void testPackNestedAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        and1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        and1.addChild( pattern2 );

        assertThat(and1.getChildren().size()).isEqualTo(2);
        assertThat(and1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(and1.getChildren().get(1)).isSameAs(pattern2);

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( and1 );

        and2.pack();
        assertThat(and2.getChildren().size()).isEqualTo(2);
        assertThat(and2.getChildren().get(0)).isSameAs(pattern1);
        assertThat(and2.getChildren().get(1)).isSameAs(pattern2);
    }
    
    @Test
    public void testDeclarationOrdering() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern( 0,
                                              new ClassObjectType( Person.class),
                                              "x" );
        and1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 2,
                                              new ClassObjectType( Person.class),
                                              "y"  );
        and1.addChild( pattern2 );
        
        Declaration x1 = and1.getInnerDeclarations().get("x");
        Declaration y1 = and1.getInnerDeclarations().get("y");
        Declaration z1 = and1.getInnerDeclarations().get("z");
        assertThat(x1).isNotNull();
        assertThat(y1).isNotNull();
        assertThat(z1).isNull();

        assertThat(and1.getChildren().size()).isEqualTo(2);
        assertThat(and1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(and1.getChildren().get(1)).isSameAs(pattern2);

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( and1 );
        
        final Pattern pattern3 = new Pattern( 3,
                                              new ClassObjectType( Person.class),
                                              "x" );        
        and2.addChild( pattern3 );
        
        and2.pack();
        assertThat(and2.getChildren().size()).isEqualTo(3);
        assertThat(and2.getChildren().get(0)).isSameAs(pattern1);
        assertThat(and2.getChildren().get(1)).isSameAs(pattern2);
        assertThat(and2.getChildren().get(2)).isSameAs(pattern3);        
        
        Declaration x2 = and2.getInnerDeclarations().get("x");
        Declaration y2 = and2.getInnerDeclarations().get("y");
        Declaration z2 = and2.getInnerDeclarations().get("z");
        assertThat(x2).isNotNull();        
        assertThat(y2).isNotNull();
        assertThat(z2).isNull();    
        
        assertThat(x2).isNotSameAs(x1);
        assertThat(pattern3.getDeclaration()).isSameAs(x2);
        assertThat(y2).isSameAs(y1);
        assertThat(z2).isSameAs(z1);
    }    

    @Test
    public void testPackNestedOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        assertThat(or1.getChildren().size()).isEqualTo(2);
        assertThat(or1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(or1.getChildren().get(1)).isSameAs(pattern2);

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( or1 );

        or2.pack();

        assertThat(or2.getChildren().size()).isEqualTo(2);
        assertThat(or2.getChildren().get(0)).isSameAs(pattern1);
        assertThat(or2.getChildren().get(1)).isSameAs(pattern2);
    }

    @Test
    public void testPackNestedExists() {
        final GroupElement exists1 = GroupElementFactory.newExistsInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        exists1.addChild( pattern1 );

        assertThat(exists1.getChildren().size()).isEqualTo(1);
        assertThat(exists1.getChildren().get(0)).isSameAs(pattern1);

        final GroupElement exists2 = GroupElementFactory.newExistsInstance();
        exists2.addChild( exists1 );

        exists2.pack();

        assertThat(exists2.getChildren().size()).isEqualTo(1);
        assertThat(exists2.getChildren().get(0)).isSameAs(pattern1);
    }

    @Test
    public void testAddMultipleChildsIntoNot() {
        final GroupElement not = GroupElementFactory.newNotInstance();

        final Pattern pattern1 = new Pattern( 0,
                                     null );
        try {
            not.addChild( pattern1 );
        } catch ( final RuntimeException rde ) {
            fail( "Adding a single child is not supposed to throw Exception for NOT GE: " + rde.getMessage() );
        }

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        try {
            not.addChild( pattern2 );
            fail( "Adding a second child into a NOT GE should throw Exception" );
        } catch ( final RuntimeException rde ) {
            // everything is fine
        }
    }

    @Test
    public void testAddSingleBranchAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern = new Pattern( 0,
                                    null );
        and1.addChild( pattern );
        assertThat(and1.getChildren().size()).isEqualTo(1);
        assertThat(and1.getChildren().get(0)).isSameAs(pattern);

        final GroupElement or1 = GroupElementFactory.newOrInstance();
        or1.addChild( and1 );

        or1.pack();
        assertThat(or1.getChildren().size()).isEqualTo(1);
        assertThat(or1.getChildren().get(0)).isSameAs(pattern);
    }

    @Test
    public void testAddSingleBranchOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern = new Pattern( 0,
                                    null );
        or1.addChild( pattern );
        assertThat(or1.getChildren().size()).isEqualTo(1);
        assertThat(or1.getChildren().get(0)).isSameAs(pattern);

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );

        and1.pack();
        assertThat(and1.getChildren().size()).isEqualTo(1);
        assertThat(and1.getChildren().get(0)).isSameAs(pattern);
    }

    /**
     * This test tests deep nested structures, and shall transform this:
     * 
     *    AND2
     *     |
     *    OR3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *   
     * Into this:
     * 
     *   OR1
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructure() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertThat(and1.getChildren().size()).isEqualTo(1);
        assertThat(and1.getChildren().get(0)).isSameAs(or1);

        assertThat(or1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(or1.getChildren().get(1)).isSameAs(pattern2);

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertThat(or2.getChildren().size()).isEqualTo(1);
        assertThat(or2.getChildren().get(0)).isSameAs(and1);

        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertThat(or2.getChildren().size()).isEqualTo(1);
        assertThat(or3.getChildren().get(0)).isSameAs(or2);

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        assertThat(and2.getChildren().size()).isEqualTo(1);
        assertThat(and2.getChildren().get(0)).isSameAs(or3);

        // Now pack the structure
        and2.pack();

        // and2 now is in fact transformed into an OR
        assertThat(and2.getType()).isEqualTo(GroupElement.Type.OR);

        assertThat(and2.getChildren().size()).isEqualTo(2);

        assertThat(and2.getChildren().get(0)).isSameAs(pattern1);
        assertThat(and2.getChildren().get(1)).isSameAs(pattern2);

    }

    /**
     * This test tests deep nested structures, and shall transform this:
     * 
     *      AND2
     *      / \ 
     *    OR3  C3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *   
     * Into this:
     * 
     *     AND2
     *     /  \
     *   OR1  C3
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructureWithMultipleElementsInRoot() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertThat(and1.getChildren().size()).isEqualTo(1);
        assertThat(and1.getChildren().get(0)).isSameAs(or1);

        assertThat(or1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(or1.getChildren().get(1)).isSameAs(pattern2);

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertThat(or2.getChildren().size()).isEqualTo(1);
        assertThat(or2.getChildren().get(0)).isSameAs(and1);

        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertThat(or2.getChildren().size()).isEqualTo(1);
        assertThat(or3.getChildren().get(0)).isSameAs(or2);

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        final Pattern pattern3 = new Pattern( 0,
                                     null );
        and2.addChild( pattern3 );

        assertThat(and2.getChildren().size()).isEqualTo(2);
        assertThat(and2.getChildren().get(0)).isSameAs(or3);
        assertThat(and2.getChildren().get(1)).isSameAs(pattern3);

        // Now pack the structure
        and2.pack();

        // and2 now is in fact transformed into an OR
        assertThat(and2.isAnd()).isTrue();

        assertThat(and2.getChildren().size()).isEqualTo(2);

        // order must be the same
        assertThat(and2.getChildren().get(0)).isSameAs(or1);
        assertThat(and2.getChildren().get(1)).isSameAs(pattern3);

        assertThat(or1.getChildren().size()).isEqualTo(2);
        assertThat(or1.getChildren().get(0)).isSameAs(pattern1);
        assertThat(or1.getChildren().get(1)).isSameAs(pattern2);

    }

}
