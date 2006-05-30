package org.drools.reteoo;

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

import org.drools.DroolsTestCase;
import org.drools.WorkingMemory;

public class RuleBaseImplTest extends DroolsTestCase {
    ReteooRuleBase ruleBase;

    WorkingMemory  wm1;
    WorkingMemory  wm2;
    WorkingMemory  wm3;
    WorkingMemory  wm4;

    public void setUp() {
        this.ruleBase = new ReteooRuleBase();

        this.wm1 = this.ruleBase.newWorkingMemory();
        this.wm2 = this.ruleBase.newWorkingMemory();
        this.wm3 = this.ruleBase.newWorkingMemory();
        this.wm4 = this.ruleBase.newWorkingMemory();
    }

    public void testKeepReference() throws Exception {
        /* Make sure the RuleBase is referencing all 4 Working Memories */
        assertLength( 4,
                      this.ruleBase.getWorkingMemories() );
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm1 ) );
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm2 ) );
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm3 ) );
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm4 ) );
    }

    public void testWeakReference() throws Exception {
        /* nulling these two so the keys should get garbage collected */
        this.wm2 = null;
        this.wm4 = null;

        /* Run GC */
        System.gc();
        Thread.sleep( 200 ); // Shouldn't need to sleep, but put it in anyway

        /* Check we now only have two keys */
        assertLength( 2,
                      this.ruleBase.getWorkingMemories() );

        /* Make sure the correct keys were removed */
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm1 ) );
        assertFalse( this.ruleBase.getWorkingMemories().contains( this.wm2 ) );
        assertTrue( this.ruleBase.getWorkingMemories().contains( this.wm3 ) );
        assertFalse( this.ruleBase.getWorkingMemories().contains( this.wm4 ) );

    }

    public void testDispose() throws Exception {
        /*
         * Now lets test the dispose method on the WorkingMemory itself. dispose
         * doesn't need GC
         */
        this.wm3.dispose();

        /* Check only wm3 was removed */
        assertLength( 3,
                      this.ruleBase.getWorkingMemories() );
        assertFalse( this.ruleBase.getWorkingMemories().contains( this.wm3 ) );
    }

    public void testNoKeepReference() throws Exception {
        final WorkingMemory wm5 = this.ruleBase.newWorkingMemory( false );
        final WorkingMemory wm6 = this.ruleBase.newWorkingMemory( false );
        assertLength( 4,
                      this.ruleBase.getWorkingMemories() );
        assertFalse( this.ruleBase.getWorkingMemories().contains( wm5 ) );
        assertFalse( this.ruleBase.getWorkingMemories().contains( wm6 ) );
    }

    public void testAddPackage() throws Exception {
        final org.drools.rule.Package pkg1 = new org.drools.rule.Package( "org.droos.test" );
        pkg1.addGlobal( "global1",
                        Object.class );
        pkg1.addGlobal( "global2",
                        Object.class );

        final org.drools.rule.Package pkg2 = new org.drools.rule.Package( "org.droos.test" );
        pkg2.addGlobal( "global1",
                        Object.class );
        pkg2.addGlobal( "global3",
                        Object.class );

        final org.drools.rule.Package pkg3 = new org.drools.rule.Package( "org.droos.test2" );
        pkg3.addGlobal( "global3",
                        Object.class );
        pkg3.addGlobal( "global4",
                        Object.class );

        this.ruleBase.addPackage( pkg1 );
        // one package
        assertLength( 1,
                      this.ruleBase.getPackages() );
        // two globals
        assertLength( 2,
                      this.ruleBase.getGlobals().values() );
        // two globals in the package also
        assertLength( 2,
                      this.ruleBase.getPackages()[0].getGlobals().values() );

        this.ruleBase.addPackage( pkg2 );
        // packages merged, so still 1 package
        assertLength( 1,
                      this.ruleBase.getPackages() );
        // globals merged, so 3 globals total
        assertLength( 3,
                      this.ruleBase.getGlobals().values() );
        // three globals in the package also
        assertLength( 3,
                      this.ruleBase.getPackages()[0].getGlobals().values() );

        this.ruleBase.addPackage( pkg3 );
        // new package, so now we have 2 package
        assertLength( 2,
                      this.ruleBase.getPackages() );
        // globals partially merged, so 4 globals total
        assertLength( 4,
                      this.ruleBase.getGlobals().values() );
        // two globals in the package
        final org.drools.rule.Package[] pkgs = this.ruleBase.getPackages();
        for ( int i = 0; i < pkgs.length; i++ ) {
            if ( pkgs[i].getName().equals( pkg3.getName() ) ) {
                assertLength( 2,
                              pkgs[i].getGlobals().values() );
            }
        }
    }

    public void testRemovePackage() throws Exception {
        final org.drools.rule.Package pkg1 = new org.drools.rule.Package( "org.droos.test" );
        pkg1.addGlobal( "global1",
                        Object.class );
        pkg1.addGlobal( "global2",
                        Object.class );

        final org.drools.rule.Package pkg2 = new org.drools.rule.Package( "org.droos.test" );
        pkg2.addGlobal( "global1",
                        Object.class );
        pkg2.addGlobal( "global3",
                        Object.class );

        final org.drools.rule.Package pkg3 = new org.drools.rule.Package( "org.droos.test2" );
        pkg3.addGlobal( "global3",
                        Object.class );
        pkg3.addGlobal( "global4",
                        Object.class );

        this.ruleBase.addPackage( pkg1 );
        this.ruleBase.addPackage( pkg2 );
        this.ruleBase.addPackage( pkg3 );

        this.ruleBase.removePackage( pkg1.getName() );
        // packages were partially merged when adding, so removal 
        // shall left only package 3 behind
        assertLength( 1,
                      this.ruleBase.getPackages() );
        assertLength( 2,
                      this.ruleBase.getGlobals().values() );

        this.ruleBase.removePackage( pkg3.getName() );
        assertLength( 0,
                      this.ruleBase.getPackages() );
        assertLength( 0,
                      this.ruleBase.getGlobals().values() );

    }

}