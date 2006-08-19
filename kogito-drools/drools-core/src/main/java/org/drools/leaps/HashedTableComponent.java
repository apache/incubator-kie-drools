package org.drools.leaps;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.evaluators.Operator;
import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableIterator;
import org.drools.rule.Declaration;
import org.drools.rule.VariableConstraint;
import org.drools.spi.Tuple;

public class HashedTableComponent implements Serializable{
    private final ColumnConstraints constraints;

    private final int               numberOfVariableConstraints;

    private final Map               buckets;

    private final Comparator        comparator;

    private final static Integer    DEFAULT_HASH = new Integer( 0 );
    
    private final Table noConstraintsTable;

    public HashedTableComponent(ColumnConstraints constraints, Comparator comparator) {
        this.constraints = constraints;
        this.comparator = comparator;
        this.buckets = new HashMap( );
        this.numberOfVariableConstraints = this.constraints.getBetaContraints( ).length;
        this.noConstraintsTable = new Table(this.comparator);
    }

    public void add( LeapsFactHandle factHandle ) {
        Table table = this.getTable( factHandle, true );
        if (table != null) {
            table.add( factHandle );
        }
    }

    public void remove( LeapsFactHandle factHandle ) {
        Table table = this.getTable( factHandle, false );
        if (table != null) {
            table.remove( factHandle );
        }
    }

    public TableIterator reverseOrderIterator( Tuple tuple ) {
        Table table = this.getTable( tuple );
        if (table != null) {
            return table.reverseOrderIterator( );
        }
        else {
            return null;
        }
    }

    public TableIterator iteratorFromPositionToTableEnd( Tuple tuple,
                                                           LeapsFactHandle startFactHandle ) {
        Table table = this.getTable( tuple );
        if (table != null) {
            return table.iteratorFromPositionToTableEnd( startFactHandle );
        }
        else {
            return null;
        }
    }

    public TableIterator iteratorFromPositionToTableStart( Tuple tuple,
                                                           LeapsFactHandle startFactHandle,
                                                           LeapsFactHandle currentFactHandle ) {
        Table table = this.getTable( tuple );
        if (table != null) {
            return table.iteratorFromPositionToTableStart( startFactHandle,
                                                           currentFactHandle );
        }
        else {
            return null;
        }
    }

    private Table getTable( Tuple tuple ) {
        Table ret = null;
        if (this.numberOfVariableConstraints > 0) {
            Map currentMap = this.buckets;
            for (int i = 0; ( i < this.numberOfVariableConstraints )
                    && ( currentMap != null ); i++) {
                Integer hash = DEFAULT_HASH;
                if (this.constraints.getBetaContraints( )[i] instanceof VariableConstraint
                        && ( (VariableConstraint) this.constraints.getBetaContraints( )[i] ).getEvaluator( )
                                                                                            .getOperator( ) == Operator.EQUAL) {
                    Declaration declaration = this.constraints.getBetaContraints( )[i].getRequiredDeclarations( )[0];
                    final Object select = declaration.getValue( tuple.get( declaration.getColumn( )
                                                                                      .getFactIndex( ) )
                                                                     .getObject( ) );
                    if (select != null) {
                        hash = new Integer( select.hashCode( ) );
                    }
                }
                // put facts at the very bottom / last instance
                if (i != ( this.numberOfVariableConstraints - 1 )) {
                    // we can not have null as a value to the key
                    currentMap = (Map) currentMap.get( hash );
                }
                else {
                    ret = (Table) currentMap.get( hash );
                }
            }
        }
        else {
            ret = this.noConstraintsTable;
        }
        return ret;
    }

    private Table getTable( LeapsFactHandle factHandle, boolean createIfNotThere ) {
        Table ret = null;
        Map currentMap = this.buckets;
        if (this.constraints.isAllowedAlpha( factHandle, null, null )) {
            if (this.numberOfVariableConstraints > 0) {
                for (int i = 0; ( i < this.numberOfVariableConstraints )
                        && ( currentMap != null ); i++) {
                    Integer hash = DEFAULT_HASH;
                    if (this.constraints.getBetaContraints( )[i] instanceof VariableConstraint
                            && ( (VariableConstraint) this.constraints.getBetaContraints( )[i] ).getEvaluator( )
                                                                                                .getOperator( ) == Operator.EQUAL) {
                        final Object select = ( (VariableConstraint) this.constraints.getBetaContraints( )[i] ).getFieldExtractor( )
                                                                                                               .getValue( factHandle.getObject( ) );
                        if (select != null) {
                            hash = new Integer( select.hashCode( ) );
                        }
                    }
                    // put facts at the very bottom / last instance
                    if (i != ( this.numberOfVariableConstraints - 1 )) {
                        // we can not have null as a value to the key
                        Map map = (Map) currentMap.get( hash );
                        if (map == null && createIfNotThere) {
                            map = new HashMap( );
                            currentMap.put( hash, map );
                        }
                        currentMap = map;
                    }
                    else {
                        Table table = (Table) currentMap.get( hash );
                        if (table == null && createIfNotThere) {
                            table = new Table( this.comparator );
                            currentMap.put( hash, table );
                        }
                        ret = table;
                    }
                }
            }
            else {
                return this.noConstraintsTable;
            }
        }
        return ret;
    }

}
