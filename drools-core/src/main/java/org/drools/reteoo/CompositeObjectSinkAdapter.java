package org.drools.reteoo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

public class CompositeObjectSinkAdapter
    implements
    ObjectSinkPropagator {

    //    /** You can override this property via a system property (eg -Ddrools.hashThreshold=4) */
    //    public static final String HASH_THRESHOLD_SYSTEM_PROPERTY = "drools.hashThreshold";
    //
    //    /** The threshold for when hashing kicks in */
    //    public static final int    THRESHOLD_TO_HASH              = Integer.parseInt( System.getProperty( HASH_THRESHOLD_SYSTEM_PROPERTY,
    //                                                                                                      "3" ) );

    private static final long serialVersionUID = 320L;
    ObjectSinkNodeList        otherSinks;
    ObjectSinkNodeList        hashableSinks;

    LinkedList                hashedFieldIndexes;

    ObjectHashMap             hashedSinkMap;

    private HashKey           hashKey;

    private final int         alphaNodeHashingThreshold;

    public CompositeObjectSinkAdapter() {
        this( 3 );
    }

    public CompositeObjectSinkAdapter(final int alphaNodeHashingThreshold) {
        this.hashKey = new HashKey();
        this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
    }

    public void addObjectSink(final ObjectSink sink) {
        if ( sink instanceof AlphaNode ) {
            final AlphaNode alphaNode = (AlphaNode) sink;
            final AlphaNodeFieldConstraint fieldConstraint = alphaNode.getConstraint();

            if ( fieldConstraint instanceof LiteralConstraint ) {
                final LiteralConstraint literalConstraint = (LiteralConstraint) fieldConstraint;
                final Evaluator evaluator = literalConstraint.getEvaluator();

                if ( evaluator.getOperator() == Operator.EQUAL ) {
                    final int index = literalConstraint.getFieldExtractor().getIndex();
                    final FieldIndex fieldIndex = registerFieldIndex( index,
                                                                      literalConstraint.getFieldExtractor() );

                    if ( fieldIndex.getCount() >= this.alphaNodeHashingThreshold ) {
                        if ( !fieldIndex.isHashed() ) {
                            hashSinks( fieldIndex );
                        }
                        final FieldValue value = literalConstraint.getField();
                        // no need to check, we know  the sink  does not exist
                        this.hashedSinkMap.put( new HashKey( index,
                                                             value ),
                                                sink,
                                                false );
                    } else {
                        if ( this.hashableSinks == null ) {
                            this.hashableSinks = new ObjectSinkNodeList();
                        }
                        this.hashableSinks.add( (ObjectSinkNode) sink );
                    }
                    return;
                }

            }
        }

        if ( this.otherSinks == null ) {
            this.otherSinks = new ObjectSinkNodeList();
        }

        this.otherSinks.add( (ObjectSinkNode) sink );
    }

    public void removeObjectSink(final ObjectSink sink) {
        if ( sink instanceof AlphaNode ) {
            final AlphaNode alphaNode = (AlphaNode) sink;
            final AlphaNodeFieldConstraint fieldConstraint = alphaNode.getConstraint();

            if ( fieldConstraint instanceof LiteralConstraint ) {
                final LiteralConstraint literalConstraint = (LiteralConstraint) fieldConstraint;
                final Evaluator evaluator = literalConstraint.getEvaluator();
                final FieldValue value = literalConstraint.getField();

                if ( evaluator.getOperator() == Operator.EQUAL ) {
                    final int index = literalConstraint.getFieldExtractor().getIndex();
                    final FieldIndex fieldIndex = unregisterFieldIndex( index );

                    if ( fieldIndex.isHashed() ) {
                        this.hashKey.setValue( index,
                                               value );
                        this.hashedSinkMap.remove( this.hashKey );
                        if ( fieldIndex.getCount() <= this.alphaNodeHashingThreshold - 1 ) {
                            // we have less than three so unhash
                            unHashSinks( fieldIndex );
                        }
                    } else {
                        this.hashableSinks.remove( (ObjectSinkNode) sink );
                    }

                    if ( this.hashableSinks.isEmpty() ) {
                        this.hashableSinks = null;
                    }

                    return;
                }
            }
        }

        this.otherSinks.remove( (ObjectSinkNode) sink );

        if ( this.otherSinks.isEmpty() ) {
            this.otherSinks = null;
        }
    }

    public void hashSinks(final FieldIndex fieldIndex) {
        final int index = fieldIndex.getIndex();

        final List list = new ArrayList();

        if ( this.hashedSinkMap == null ) {
            this.hashedSinkMap = new ObjectHashMap();
        }

        for ( ObjectSinkNode sink = this.hashableSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
            final AlphaNode alphaNode = (AlphaNode) sink;
            final AlphaNodeFieldConstraint fieldConstraint = alphaNode.getConstraint();
            final LiteralConstraint literalConstraint = (LiteralConstraint) fieldConstraint;
            final Evaluator evaluator = literalConstraint.getEvaluator();
            if ( evaluator.getOperator() == Operator.EQUAL && index == literalConstraint.getFieldExtractor().getIndex() ) {
                final FieldValue value = literalConstraint.getField();
                list.add( sink );
                this.hashedSinkMap.put( new HashKey( index,
                                                     value ),
                                        sink );
            }
        }

        for ( final java.util.Iterator it = list.iterator(); it.hasNext(); ) {
            final ObjectSinkNode sink = (ObjectSinkNode) it.next();
            this.hashableSinks.remove( sink );
        }

        if ( this.hashableSinks.isEmpty() ) {
            this.hashableSinks = null;
        }

        fieldIndex.setHashed( true );
    }

    public void unHashSinks(final FieldIndex fieldIndex) {
        final int index = fieldIndex.getIndex();

        final List sinks = new ArrayList();

        //iterate twice as custom iterator is immutable
        final Iterator mapIt = this.hashedSinkMap.iterator();
        for ( ObjectHashMap.ObjectEntry e = (ObjectHashMap.ObjectEntry) mapIt.next(); e != null; ) {

            sinks.add( e.getValue() );
            e = (ObjectHashMap.ObjectEntry) mapIt.next();
        }

        for ( final java.util.Iterator iter = sinks.iterator(); iter.hasNext(); ) {
            final AlphaNode sink = (AlphaNode) iter.next();
            final AlphaNode alphaNode = sink;
            final AlphaNodeFieldConstraint fieldConstraint = alphaNode.getConstraint();
            final LiteralConstraint literalConstraint = (LiteralConstraint) fieldConstraint;
            final Evaluator evaluator = literalConstraint.getEvaluator();
            if ( evaluator.getOperator() == Operator.EQUAL && index == literalConstraint.getFieldExtractor().getIndex() ) {
                final FieldValue value = literalConstraint.getField();
                if ( this.hashableSinks == null ) {
                    this.hashableSinks = new ObjectSinkNodeList();
                }
                this.hashableSinks.add( sink );
                this.hashedSinkMap.remove( new HashKey( index,
                                                        value ) );
            };
        }

        if ( this.hashedSinkMap.isEmpty() ) {
            this.hashedSinkMap = null;
        }

        fieldIndex.setHashed( false );
    }

    /**
     * Returns a FieldIndex which Keeps a count on how many times a particular field is used with an equality check in the sinks.
     * @param index
     * @param fieldExtractor
     * @return
     */
    private FieldIndex registerFieldIndex(final int index,
                                          final FieldExtractor fieldExtractor) {
        FieldIndex fieldIndex = null;

        // is linkedlist null, if so create and add
        if ( this.hashedFieldIndexes == null ) {
            this.hashedFieldIndexes = new LinkedList();
            fieldIndex = new FieldIndex( index,
                                         fieldExtractor );
            this.hashedFieldIndexes.add( fieldIndex );
        }

        // still null, so see if it already exists
        if ( fieldIndex == null ) {
            fieldIndex = findFieldIndex( index );
        }

        // doesn't exist so create it
        if ( fieldIndex == null ) {
            fieldIndex = new FieldIndex( index,
                                         fieldExtractor );
            this.hashedFieldIndexes.add( fieldIndex );
        }

        fieldIndex.increaseCounter();

        return fieldIndex;
    }

    private FieldIndex unregisterFieldIndex(final int index) {
        final FieldIndex fieldIndex = findFieldIndex( index );
        fieldIndex.decreaseCounter();

        // if the fieldcount is 0 then remove it from the linkedlist
        if ( fieldIndex.getCount() == 0 ) {
            this.hashedFieldIndexes.remove( fieldIndex );

            // if the linkedlist is empty then null it
            if ( this.hashedFieldIndexes.isEmpty() ) {
                this.hashedFieldIndexes = null;
            }
        }

        return fieldIndex;
    }

    private FieldIndex findFieldIndex(final int index) {
        for ( FieldIndex node = (FieldIndex) this.hashedFieldIndexes.getFirst(); node != null; node = (FieldIndex) node.getNext() ) {
            if ( node.getIndex() == index ) {
                return node;
            }
        }

        return null;
    }

    public void propagateAssertObject(final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        final Object object = handle.getObject();

        // Iterates t he FieldIndex collection, which tells you if particularly field is hashed or not
        // if the field is hashed then it builds the hashkey to return the correct sink for the current objects slot's
        // value, one object may have multiple fields indexed.
        if ( this.hashedFieldIndexes != null ) {
            // Iterate the FieldIndexes to see if any are hashed        
            for ( FieldIndex fieldIndex = (FieldIndex) this.hashedFieldIndexes.getFirst(); fieldIndex != null; fieldIndex = (FieldIndex) fieldIndex.getNext() ) {
                if ( !fieldIndex.isHashed() ) {
                    continue;
                }
                // this field is hashed so set the existing hashKey and see if there is a sink for it
                final int index = fieldIndex.getIndex();
                final FieldExtractor extractor = fieldIndex.getFieldExtactor();
                this.hashKey.setValue( index,
                                       object,
                                       extractor );
                final ObjectSink sink = (ObjectSink) this.hashedSinkMap.get( this.hashKey );
                if ( sink != null ) {
                    // The sink exists so propagate
                    sink.assertObject( handle,
                                       context,
                                       workingMemory );
                }
            }
        }

        // propagate unhashed
        if ( this.hashableSinks != null ) {
            for ( ObjectSinkNode sink = this.hashableSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                sink.assertObject( handle,
                                   context,
                                   workingMemory );
            }
        }

        if ( this.otherSinks != null ) {
            // propagate others
            for ( ObjectSinkNode sink = this.otherSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                sink.assertObject( handle,
                                   context,
                                   workingMemory );
            }
        }

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
        if ( this.hashedFieldIndexes != null ) {
            if ( useHash && this.hashedSinkMap != null ) {
                final Object object = handle.getObject();
                // Iterate the FieldIndexes to see if any are hashed        
                for ( FieldIndex fieldIndex = (FieldIndex) this.hashedFieldIndexes.getFirst(); fieldIndex != null; fieldIndex = (FieldIndex) fieldIndex.getNext() ) {
                    // this field is hashed so set the existing hashKey and see if there is a sink for it
                    if ( !fieldIndex.isHashed() ) {
                        continue;
                    }

                    final int index = fieldIndex.getIndex();
                    final FieldExtractor extractor = fieldIndex.getFieldExtactor();
                    this.hashKey.setValue( index,
                                           object,
                                           extractor );
                    final ObjectSink sink = (ObjectSink) this.hashedSinkMap.get( this.hashKey );
                    if ( sink != null ) {
                        // The sink exists so propagate
                        sink.retractObject( handle,
                                            context,
                                            workingMemory );
                    }
                }
            } else if ( this.hashedSinkMap != null ) {
                final Iterator it = this.hashedSinkMap.iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    final ObjectSink sink = (ObjectSink) entry.getValue();
                    sink.retractObject( handle,
                                        context,
                                        workingMemory );
                }
            }
        }

        if ( this.hashableSinks != null ) {
            // we can't retrieve hashed sinks, as the field value might have changed, so we have to iterate and propagate to all hashed sinks
            for ( ObjectSinkNode sink = this.hashableSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                sink.retractObject( handle,
                                    context,
                                    workingMemory );
            }
        }

        if ( this.otherSinks != null ) {
            // propagate others
            for ( ObjectSinkNode sink = this.otherSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                sink.retractObject( handle,
                                    context,
                                    workingMemory );
            }
        }
    }

    public ObjectSink[] getSinks() {
        final List list = new ArrayList();

        if ( this.otherSinks != null ) {
            for ( ObjectSinkNode sink = this.otherSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                list.add( sink );
            }
        }

        if ( this.hashableSinks != null ) {
            for ( ObjectSinkNode sink = this.hashableSinks.getFirst(); sink != null; sink = sink.getNextObjectSinkNode() ) {
                list.add( sink );
            }
        }

        if ( this.hashedSinkMap != null ) {
            final Iterator it = this.hashedSinkMap.iterator();
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                final ObjectSink sink = (ObjectSink) entry.getValue();
                list.add( sink );
            }
        }

        return (ObjectSink[]) list.toArray( new ObjectSink[list.size()] );
    }

    public int size() {
        int size = 0;
        size += ((this.otherSinks != null) ? this.otherSinks.size() : 0);
        size += ((this.hashableSinks != null) ? this.hashableSinks.size() : 0);
        size += ((this.hashedSinkMap != null) ? this.hashedSinkMap.size() : 0);
        return size;
    }

    public static class HashKey
        implements
        Serializable {
        private static final long serialVersionUID = 1949191240975565186L;

        private static final byte OBJECT           = 1;
        private static final byte LONG             = 2;
        private static final byte DOUBLE           = 3;
        private static final byte BOOL             = 4;

        private int               index;

        private byte              type;
        private Object            ovalue;
        private long              lvalue;
        private boolean           bvalue;
        private double            dvalue;

        private int               hashCode;

        public HashKey() {
        }

        public HashKey(final int index,
                       final FieldValue value) {
            this.setValue( index,
                           value );
        }

        public HashKey(final int index,
                       final Object value,
                       final Extractor extractor) {
            this.setValue( index,
                           value,
                           extractor );
        }

        public int getIndex() {
            return this.index;
        }

        public void setValue(final int index,
                             final Object value,
                             final Extractor extractor) {
            this.index = index;
            final ValueType vtype = extractor.getValueType();
            if ( vtype.isBoolean() ) {
                this.bvalue = extractor.getBooleanValue( value );
                this.type = BOOL;
                this.setHashCode( this.bvalue ? 1231 : 1237 );
            } else if ( vtype.isIntegerNumber() ) {
                this.lvalue = extractor.getLongValue( value );
                this.type = LONG;
                this.setHashCode( (int) (this.lvalue ^ (this.lvalue >>> 32)) );
            } else if ( vtype.isFloatNumber() ) {
                this.dvalue = extractor.getDoubleValue( value );
                this.type = DOUBLE;
                final long temp = Double.doubleToLongBits( this.dvalue );
                this.setHashCode( (int) (temp ^ (temp >>> 32)) );
            } else {
                this.ovalue = extractor.getValue( value );
                this.type = OBJECT;
                this.setHashCode( this.ovalue.hashCode() );
            }
        }

        public void setValue(final int index,
                             final FieldValue value) {
            this.index = index;
            if ( value.isBooleanField() ) {
                this.bvalue = value.getBooleanValue();
                this.type = BOOL;
                this.setHashCode( this.bvalue ? 1231 : 1237 );
            } else if ( value.isIntegerNumberField() ) {
                this.lvalue = value.getLongValue();
                this.type = LONG;
                this.setHashCode( (int) (this.lvalue ^ (this.lvalue >>> 32)) );
            } else if ( value.isFloatNumberField() ) {
                this.dvalue = value.getDoubleValue();
                this.type = DOUBLE;
                final long temp = Double.doubleToLongBits( this.dvalue );
                this.setHashCode( (int) (temp ^ (temp >>> 32)) );
            } else {
                this.ovalue = value.getValue();
                this.type = OBJECT;
                this.setHashCode( this.ovalue.hashCode() );
            }
        }

        private void setHashCode(final int hashSeed) {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + hashSeed;
            result = PRIME * result + this.index;
            this.hashCode = result;
        }

        public boolean getBooleanValue() {
            switch ( this.type ) {
                case BOOL :
                    return this.bvalue;
                case OBJECT :
                    if ( this.ovalue instanceof Boolean ) {
                        return ((Boolean) this.ovalue).booleanValue();
                    } else if ( this.ovalue instanceof String ) {
                        return Boolean.valueOf( (String) this.ovalue ).booleanValue();
                    } else {
                        throw new ClassCastException( "Can't convert " + this.ovalue.getClass() + " to a boolean value." );
                    }
                case LONG :
                    throw new ClassCastException( "Can't convert long to a boolean value." );
                case DOUBLE :
                    throw new ClassCastException( "Can't convert double to a boolean value." );

            }
            return false;
        }

        public long getLongValue() {
            switch ( this.type ) {
                case BOOL :
                    return this.bvalue ? 1 : 0;
                case OBJECT :
                    if ( this.ovalue instanceof Number ) {
                        return ((Number) this.ovalue).longValue();
                    } else if ( this.ovalue instanceof String ) {
                        return Long.parseLong( (String) this.ovalue );
                    } else {
                        throw new ClassCastException( "Can't convert " + this.ovalue.getClass() + " to a long value." );
                    }
                case LONG :
                    return this.lvalue;
                case DOUBLE :
                    return (long) this.dvalue;

            }
            return 0;
        }

        public double getDoubleValue() {
            switch ( this.type ) {
                case BOOL :
                    return this.bvalue ? 1 : 0;
                case OBJECT :
                    if ( this.ovalue instanceof Number ) {
                        return ((Number) this.ovalue).doubleValue();
                    } else if ( this.ovalue instanceof String ) {
                        return Double.parseDouble( (String) this.ovalue );
                    } else {
                        throw new ClassCastException( "Can't convert " + this.ovalue.getClass() + " to a double value." );
                    }
                case LONG :
                    return this.lvalue;
                case DOUBLE :
                    return this.dvalue;
            }
            return 0;
        }

        public Object getObjectValue() {
            switch ( this.type ) {
                case BOOL :
                    return this.bvalue ? Boolean.TRUE : Boolean.FALSE;
                case OBJECT :
                    return this.ovalue;
                case LONG :
                    return new Long( this.lvalue );
                case DOUBLE :
                    return new Double( this.dvalue );
            }
            return null;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object object) {
            final HashKey other = (HashKey) object;

            switch ( this.type ) {
                case BOOL :
                    return (this.index == other.index) && (this.bvalue == other.getBooleanValue());
                case LONG :
                    return (this.index == other.index) && (this.lvalue == other.getLongValue());
                case DOUBLE :
                    return (this.index == other.index) && (this.dvalue == other.getDoubleValue());
                case OBJECT :
                    final Object otherValue = other.getObjectValue();
                    if ( (this.ovalue instanceof Number) && (otherValue instanceof Number) ) {
                        return (this.index == other.index) && (((Number) this.ovalue).doubleValue() == ((Number) otherValue).doubleValue());
                    }
                    return (this.index == other.index) && (this.ovalue.equals( otherValue ));
            }
            return false;
        }

    }

    public static class FieldIndex
        implements
        LinkedListNode {
        private static final long serialVersionUID = 3853708964744065172L;
        private final int         index;
        private FieldExtractor    fieldExtactor;

        private int               count;

        private boolean           hashed;

        private LinkedListNode    previous;
        private LinkedListNode    next;

        public FieldIndex(final int index,
                          final FieldExtractor fieldExtractor) {
            this.index = index;
            this.fieldExtactor = fieldExtractor;
        }

        public FieldExtractor getFieldExtractor() {
            return this.fieldExtactor;
        }

        public int getIndex() {
            return this.index;
        }

        public int getCount() {
            return this.count;
        }

        public FieldExtractor getFieldExtactor() {
            return this.fieldExtactor;
        }

        public boolean isHashed() {
            return this.hashed;
        }

        public void setHashed(final boolean hashed) {
            this.hashed = hashed;
        }

        public void increaseCounter() {
            this.count++;
        }

        public void decreaseCounter() {
            this.count--;
        }

        public LinkedListNode getNext() {
            return this.next;
        }

        public LinkedListNode getPrevious() {
            return this.previous;
        }

        public void setNext(final LinkedListNode next) {
            this.next = next;

        }

        public void setPrevious(final LinkedListNode previous) {
            this.previous = previous;
        }
    }
}
