package org.drools.base;

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;

public class DelegateJavaFactHandlerTest extends TestCase {

    public void test1Entry() throws Exception {
        final DelegateJavaFactHandler handler = new DelegateJavaFactHandler();

        final Field field = handler.getClass().getDeclaredField( "entries" );
        field.setAccessible( true );

        final WorkingMemory wm1 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        handler.register( wm1 );

        JavaFactRegistryEntry[] entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 1,
                      entries.length );

        assertTrue( handler.isRegistered( wm1 ) );

        assertEquals( 1,
                      handler.listWorkingMemories().length );
        assertSame( wm1,
                    handler.listWorkingMemories()[0].getWorkingMemory() );

        final WorkingMemory wm2 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        assertFalse( handler.isRegistered( wm2 ) );

        handler.unregister( wm1 );
        assertFalse( handler.isRegistered( wm1 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertNull( entries );
    }

    public void test2Entries() throws Exception {
        final DelegateJavaFactHandler handler = new DelegateJavaFactHandler();

        final Field field = handler.getClass().getDeclaredField( "entries" );
        field.setAccessible( true );

        final WorkingMemory wm1 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        final WorkingMemory wm2 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        handler.register( wm1 );
        handler.register( wm2 );

        JavaFactRegistryEntry[] entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 2,
                      entries.length );

        assertTrue( handler.isRegistered( wm1 ) );
        assertTrue( handler.isRegistered( wm2 ) );

        assertEquals( 2,
                      handler.listWorkingMemories().length );
        assertSame( wm1,
                    handler.listWorkingMemories()[0].getWorkingMemory() );
        assertSame( wm2,
                    handler.listWorkingMemories()[1].getWorkingMemory() );

        handler.unregister( wm1 );
        assertFalse( handler.isRegistered( wm1 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 1,
                      entries.length );

        handler.unregister( wm2 );
        assertFalse( handler.isRegistered( wm2 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertNull( entries );

        // check  revererse
        handler.register( wm1 );
        handler.register( wm2 );

        handler.unregister( wm2 );
        assertFalse( handler.isRegistered( wm2 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 1,
                      entries.length );

        handler.unregister( wm1 );
        assertFalse( handler.isRegistered( wm1 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertNull( entries );
    }

    public void test3Entries() throws Exception {
        final DelegateJavaFactHandler handler = new DelegateJavaFactHandler();

        final Field field = handler.getClass().getDeclaredField( "entries" );
        field.setAccessible( true );

        final WorkingMemory wm1 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        final WorkingMemory wm2 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        final WorkingMemory wm3 = RuleBaseFactory.newRuleBase().newWorkingMemory();
        handler.register( wm1 );
        handler.register( wm2 );
        handler.register( wm3 );

        JavaFactRegistryEntry[] entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 3,
                      entries.length );

        assertTrue( handler.isRegistered( wm1 ) );
        assertTrue( handler.isRegistered( wm2 ) );
        assertTrue( handler.isRegistered( wm3 ) );

        assertEquals( 3,
                      handler.listWorkingMemories().length );
        assertSame( wm1,
                    handler.listWorkingMemories()[0].getWorkingMemory() );
        assertSame( wm2,
                    handler.listWorkingMemories()[1].getWorkingMemory() );
        assertSame( wm3,
                    handler.listWorkingMemories()[2].getWorkingMemory() );

        handler.unregister( wm2 );
        assertFalse( handler.isRegistered( wm2 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 2,
                      entries.length );

        handler.unregister( wm1 );
        assertFalse( handler.isRegistered( wm1 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertEquals( 1,
                      entries.length );

        handler.unregister( wm3 );
        assertFalse( handler.isRegistered( wm3 ) );
        entries = (JavaFactRegistryEntry[]) field.get( handler );
        assertNull( entries );
    }
}
