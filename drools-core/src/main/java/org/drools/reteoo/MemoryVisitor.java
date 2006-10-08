package org.drools.reteoo;

import java.lang.reflect.Field;

import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.examples.manners.Context;
import org.drools.reteoo.TerminalNode.TerminalNodeMemory;
import org.drools.util.AbstractHashTable;
import org.drools.util.Entry;
import org.drools.util.FactHashTable;
import org.drools.util.FieldIndexHashTable;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ReflectiveVisitor;
import org.drools.util.TupleHashTable;
import org.drools.util.AbstractHashTable.FactEntry;
import org.drools.util.FieldIndexHashTable.FieldIndexEntry;
import org.drools.util.ObjectHashMap.ObjectEntry;

public class MemoryVisitor extends ReflectiveVisitor {
    private InternalWorkingMemory workingMemory;
    private int  indent = 0;

    /**
     * Constructor.
     */
    public MemoryVisitor(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    /**
     * RuleBaseImpl visits its Rete.
     */
    public void visitReteooRuleBase(final ReteooRuleBase ruleBase) {
        visit( (ruleBase).getRete() );
    }

    /**
     * Rete visits each of its ObjectTypeNodes.
     */
    public void visitRete(final Rete rete) {
        ObjectHashMap map = rete.getObjectTypeNodes();

        Iterator it = map.iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            visit( entry.getValue() );
        }

        //            this.rootVertex = (ReteNodeVertex) this.visitedNodes.get( dotId( rete ) );
        //            if ( this.rootVertex == null ) {
        //                this.rootVertex = new ReteNodeVertex( rete );
        //                this.visitedNodes.put( dotId( rete ),
        //                                       this.rootVertex );
        //            }
        //
        //            this.graph.addVertex( this.rootVertex );
        //            this.parentVertex = this.rootVertex;
        //            for ( final Iterator i = rete.objectTypeNodeIterator(); i.hasNext(); ) {
        //                final Object nextNode = i.next();
        //                visitNode( nextNode );
        //            }
    }

    public void visitObjectTypeNode(final ObjectTypeNode node) {        
        if  ( Context.class != ( ( ClassObjectType ) node.getObjectType() ).getClassType() ){
            return;
        }
        System.out.println( indent() + node );        
        
        FactHashTable memory  = ( FactHashTable ) this.workingMemory.getNodeMemory( node );   
        checkObjectHashTable(memory);          

        indent++;
        try {
            Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            ObjectSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        indent--;
    }

    public void visitAlphaNode(final AlphaNode node) {
        System.out.println( indent() + node );
        
        FactHashTable memory  = ( FactHashTable ) this.workingMemory.getNodeMemory( node );  
        checkObjectHashTable(memory);
      

        indent++;
        try {
            Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            ObjectSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        indent--;
    }

    public void visitLeftInputAdapterNode(final LeftInputAdapterNode node) {
        System.out.println( indent() + node );

        indent++;
        try {
            Field field = TupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            TupleSinkPropagator sink = (TupleSinkPropagator) field.get( node );
            TupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        indent--;
    }

    public void visitJoinNode(final JoinNode node) {
        System.out.println( indent() + node );
        
        
        BetaMemory memory  = ( BetaMemory ) this.workingMemory.getNodeMemory( node );   
        checkObjectHashTable( memory.getObjectMemory() );          
        checkTupleMemory( memory.getTupleMemory() );
        
        indent++;
        try {
            Field field = TupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            TupleSinkPropagator sink = (TupleSinkPropagator) field.get( node );
            TupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        indent--;
    }

    public void visitNotNode(final NotNode node) {
        System.out.println( indent() + node );
        
        BetaMemory memory  = ( BetaMemory ) this.workingMemory.getNodeMemory( node );   
        checkObjectHashTable( memory.getObjectMemory() );          
        checkTupleMemory( memory.getTupleMemory() );

        indent++;
        try {
            Field field = TupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            TupleSinkPropagator sink = (TupleSinkPropagator) field.get( node );
            TupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        indent--;
    }

    public void visitTerminalNode(final TerminalNode node) {
        System.out.println( indent() + node );
        TerminalNodeMemory memory  = ( TerminalNodeMemory ) this.workingMemory.getNodeMemory( node );   
        checkTupleMemory( memory.getTupleMemory() );          
    }
    
    private void checkObjectHashMap(ObjectHashMap map) {
        Entry[] entries = map.getTable();
        int count = 0;
        for( int i = 0, length = entries.length; i < length; i++ ) {
            if ( entries[i] != null ) {
                count++;
            }
        }
        
        System.out.println( "ObjectHashMap: "+ indent() + map.size()  + ":" + count);
        if ( map.size() != count ) {
            System.out.println( indent() + "error" );
        } 
    }
    
    private void checkObjectHashTable(ObjectHashTable memory) {
        if ( memory instanceof FactHashTable) {
            checkFactHashTable( (FactHashTable )memory);
        } else if ( memory instanceof FieldIndexHashTable ) {
            checkFieldIndexHashTable( ( FieldIndexHashTable )memory);
        } else {
            throw new RuntimeException( memory.getClass() + " should not be here" );
        }
    }
    
    private void checkFactHashTable(FactHashTable memory) {
        Entry[] entries = memory.getTable();
        int count = 0;
        for( int i = 0, length = entries.length; i < length; i++ ) {
            if ( entries[i] != null ) {                
                Entry entry = entries[i];
                while ( entry != null )  {
                    count++;
                    entry = entry.getNext();
                }
            }
        }
        
        System.out.println( indent() + "FactHashTable: " +   memory.size()  + ":" + count);
        if ( memory.size() != count ) {
            System.out.println( indent() + "error" );
        }  
    }
    
    private void checkFieldIndexHashTable(FieldIndexHashTable memory) {
        Entry[] entries = memory.getTable();
        int factCount = 0;
        int bucketCount = 0;
        for( int i = 0, length = entries.length; i < length; i++ ) {
            if ( entries[i] != null ) {
                FieldIndexEntry fieldIndexEntry = ( FieldIndexEntry ) entries[i];                
                while ( fieldIndexEntry != null )  {                        
                    if ( fieldIndexEntry.getFirst() != null ) {
                        Entry entry = fieldIndexEntry.getFirst();
                        while ( entry != null )  {
                            entry = entry.getNext();
                            factCount++;
                        }
                    } else {
                        System.out.println( "error : fieldIndexHashTable cannot have empty FieldIndexEntry objects" );
                    }
                    fieldIndexEntry = ( FieldIndexEntry ) fieldIndexEntry.getNext();
                    bucketCount++;
                }
            }
        }
        
        try {
            Field field =  AbstractHashTable.class.getDeclaredField( "size" );
            field.setAccessible( true );                
            System.out.println( indent() + "FieldIndexBuckets: "+  ( ( Integer ) field.get( memory ) ).intValue()  + ":" + bucketCount );
            if ( ( ( Integer ) field.get( memory ) ).intValue()  != bucketCount ) {
                System.out.println( indent() + "error" );    
            }
        } catch ( Exception e )  {
            e.printStackTrace();
        }
        
        System.out.println( indent() + "FieldIndexFacts: " +  memory.size()  + ":" + factCount);
        if ( memory.size() != factCount ) {
            System.out.println( indent() + "error" );
        }    
    }    
    
    private void checkTupleMemory(TupleHashTable memory){
        Entry[] entries = memory.getTable();
        int count = 0;
        for( int i = 0, length = entries.length; i < length; i++ ) {
            if ( entries[i] != null ) {                
                Entry entry = entries[i];
                while ( entry != null )  {
                    count++;
                    entry = entry.getNext();
                }
            }
        }
        
        System.out.println( indent() + "TupleMemory: " + memory.size()  + ":" + count);
        if ( memory.size() != count ) {
            System.out.println( indent() + "error" );
        }          
    }
    
    private String  indent() {
        StringBuffer buffer  = new  StringBuffer();
        for ( int i  =  0; i <  indent; i++ ) {
            buffer.append( "  " );
        }
        return buffer.toString();
    }
}
