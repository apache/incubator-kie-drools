/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;

public class RuleModel implements HasImports,
                                  HasPackageName {

    /**
     * This name is generally not used - the asset name or the file name is
     * preferred (ie it could get out of sync with the name of the file it is
     * in).
     */
    public String name;
    public String parentName;
    public String modelVersion = "1.0";
    public RuleAttribute[] attributes = new RuleAttribute[ 0 ];
    public RuleMetadata[] metadataList = new RuleMetadata[ 0 ];
    public IPattern[] lhs = new IPattern[ 0 ];
    public IAction[] rhs = new IAction[ 0 ];

    private Imports imports = new Imports();

    private String packageName;

    //Is the Rule to be negated (i.e. "not ( PatternX, PatternY... )"
    private boolean isNegated;

    public RuleModel() {
    }

    /**
     * This will return a List<String> of all FactPattern bindings
     * @return The bindings or an empty list if no bindings are found.
     */
    public List<String> getLHSBoundFacts() {
        if ( this.lhs == null ) {
            return Collections.emptyList();
        }
        final List<String> list = new ArrayList<String>();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];
            if ( pat instanceof FromCompositeFactPattern ) {
                pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
            }
            if ( pat instanceof FactPattern ) {
                final FactPattern p = (FactPattern) pat;
                if ( p.getBoundName() != null ) {
                    list.add( p.getBoundName() );
                }
            }
        }
        return list;
    }

    /**
     * This will return the FactPattern that a variable is bound Eto.
     * @param var The bound fact variable (NOT bound field).
     * @return null or the FactPattern found.
     */
    public FactPattern getLHSBoundFact( final String var ) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];
            if ( pat instanceof FromCompositeFactPattern ) {
                pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
            }
            if ( pat instanceof FactPattern ) {
                final FactPattern p = (FactPattern) pat;
                if ( p.getBoundName() != null && var.equals( p.getBoundName() ) ) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * This will return the FieldConstraint that a variable is bound to.
     * @param var The bound field variable (NOT bound fact).
     * @return null or the FieldConstraint found.
     */
    public SingleFieldConstraint getLHSBoundField( final String var ) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];

            SingleFieldConstraint fieldConstraint = getLHSBoundField( pat, var );
            if ( fieldConstraint != null ) {
                return fieldConstraint;
            }
        }

        return null;
    }

    private SingleFieldConstraint getLHSBoundField( IPattern pat,
                                                    String var ) {
        if ( pat instanceof FromCompositeFactPattern ) {
            pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
        }

        if ( pat instanceof CompositeFactPattern ) {
            for ( IPattern iPattern : ( (CompositeFactPattern) pat ).getPatterns() ) {
                SingleFieldConstraint fieldConstraint = getLHSBoundField( iPattern, var );
                if ( fieldConstraint != null ) {
                    return fieldConstraint;
                }
            }
        }

        if ( pat instanceof FactPattern ) {
            final FactPattern p = (FactPattern) pat;
            for ( int j = 0; j < p.getFieldConstraints().length; j++ ) {
                if ( p.getFieldConstraints()[ j ] instanceof SingleFieldConstraint ) {
                    SingleFieldConstraint fc = (SingleFieldConstraint) p.getFieldConstraints()[ j ];

                    List<String> fieldBindings = getFieldBinding( fc );
                    if ( fieldBindings.contains( var ) ) {
                        return fc;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the data-type associated with the binding
     * @param var
     * @return The data-type, or null if the binding could not be found
     */
    public String getLHSBindingType( final String var ) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {
            String type = getLHSBindingType( this.lhs[ i ], var );
            if ( type != null ) {
                return type;
            }
        }
        return null;
    }

    private String getLHSBindingType( IPattern pat,
                                      String var ) {
        if ( pat instanceof FromCompositeFactPattern ) {
            pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
        }

        if ( pat instanceof CompositeFactPattern ) {
            for ( IPattern iPattern : ( (CompositeFactPattern) pat ).getPatterns() ) {
                String type = getLHSBindingType( iPattern, var );
                if ( type != null ) {
                    return type;
                }
            }
        }
        if ( pat instanceof FactPattern ) {
            final FactPattern p = (FactPattern) pat;
            if ( p.isBound() && var.equals( p.getBoundName() ) ) {
                return p.getFactType();
            }
            for ( FieldConstraint fc : p.getFieldConstraints() ) {
                String type = getFieldBinding( fc,
                                               var );
                if ( type != null ) {
                    return type;
                }
            }
        }
        return null;
    }

    public String getFieldBinding( FieldConstraint fc,
                                   String var ) {
        String fieldType = null;
        if ( fc instanceof SingleFieldConstraint ) {
            SingleFieldConstraint s = (SingleFieldConstraint) fc;
            if ( s.isBound() && var.equals( s.getFieldBinding() ) ) {
                fieldType = s.getFieldType();
            }
        }
        if ( fc instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide s = (SingleFieldConstraintEBLeftSide) fc;
            if ( s.isBound() && var.equals( s.getFieldBinding() ) ) {
                fieldType = s.getExpressionLeftSide().getGenericType();
            }
        }
        if ( fc instanceof CompositeFieldConstraint ) {
            CompositeFieldConstraint s = (CompositeFieldConstraint) fc;
            if ( s.getConstraints() != null ) {
                for ( FieldConstraint ss : s.getConstraints() ) {
                    fieldType = getFieldBinding( ss,
                                                 var );
                }
            }
        }
        return fieldType;
    }

    /**
     * This will return a List<String> of all ActionInsertFact bindings
     * @return The bindings or an empty list if no bindings are found.
     */
    public List<String> getRHSBoundFacts() {
        if ( this.rhs == null ) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        for ( int i = 0; i < this.rhs.length; i++ ) {
            if ( this.rhs[ i ] instanceof ActionInsertFact ) {
                final ActionInsertFact p = (ActionInsertFact) this.rhs[ i ];
                if ( p.getBoundName() != null ) {
                    list.add( p.getBoundName() );
                }
            }
        }
        return list;
    }

    /**
     * This will return the ActionInsertFact that a variable is bound to.
     * @param var The bound fact variable (NOT bound field).
     * @return null or the ActionInsertFact found.
     */
    public ActionInsertFact getRHSBoundFact( final String var ) {
        if ( this.rhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.rhs.length; i++ ) {
            if ( this.rhs[ i ] instanceof ActionInsertFact ) {
                final ActionInsertFact p = (ActionInsertFact) this.rhs[ i ];
                if ( p.getBoundName() != null && var.equals( p.getBoundName() ) ) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * This will return the FactPattern that a variable is bound to. If the
     * variable is bound to a FieldConstraint the parent FactPattern will be
     * returned.
     * @param var The variable binding
     * @return null or the FactPattern found.
     */
    public FactPattern getLHSParentFactPatternForBinding( final String var ) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];
            if ( pat instanceof FromCompositeFactPattern ) {
                pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
            }
            if ( pat instanceof FactPattern ) {
                final FactPattern p = (FactPattern) pat;
                if ( p.getBoundName() != null && var.equals( p.getBoundName() ) ) {
                    return p;
                }
                for ( int j = 0; j < p.getFieldConstraints().length; j++ ) {
                    FieldConstraint fc = p.getFieldConstraints()[ j ];
                    List<String> fieldBindings = getFieldBinding( fc );
                    if ( fieldBindings.contains( var ) ) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This will get a list of all LHS bound variables, including bound fields..
     */
    public List<String> getAllLHSVariables() {
        List<String> result = new ArrayList<String>();

        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];
            if ( pat instanceof FromCompositeFactPattern ) {
                pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
            }
            if ( pat instanceof FactPattern ) {
                FactPattern fact = (FactPattern) pat;
                if ( fact.isBound() ) {
                    result.add( fact.getBoundName() );
                }

                for ( int j = 0; j < fact.getFieldConstraints().length; j++ ) {
                    FieldConstraint fc = fact.getFieldConstraints()[ j ];
                    if ( fc instanceof SingleFieldConstraintEBLeftSide ) {
                        SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) fc;
                        if ( exp.getExpressionLeftSide() != null && exp.getExpressionLeftSide().isBound() ) {
                            result.add( exp.getExpressionLeftSide().getBinding() );
                        }
                    } else if ( fc instanceof SingleFieldConstraint ) {
                        SingleFieldConstraint con = (SingleFieldConstraint) fc;
                        if ( con.isBound() ) {
                            result.add( con.getFieldBinding() );
                        }
                        if ( con.getExpressionValue() != null && con.getExpressionValue().isBound() ) {
                            result.add( con.getExpressionValue().getBinding() );
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * This will get a list of all RHS bound variables.
     */
    public List<String> getAllRHSVariables() {
        List<String> result = new ArrayList<String>();

        for ( int i = 0; i < this.rhs.length; i++ ) {
            IAction pat = this.rhs[ i ];
            if ( pat instanceof ActionInsertFact ) {
                ActionInsertFact fact = (ActionInsertFact) pat;
                if ( fact.isBound() ) {
                    result.add( fact.getBoundName() );
                }
            }
        }

        return result;
    }

    /**
     * This will get a list of all bound variables (LHS and RHS), including bound fields..
     */
    public List<String> getAllVariables() {
        List<String> result = new ArrayList<String>();
        result.addAll( this.getAllLHSVariables() );
        result.addAll( this.getAllRHSVariables() );
        return result;
    }

    public List<String> getFieldBinding( FieldConstraint f ) {
        List<String> result = new ArrayList<String>();
        if ( f instanceof SingleFieldConstraint ) {
            SingleFieldConstraint con = (SingleFieldConstraint) f;
            if ( con.isBound() ) {
                result.add( con.getFieldBinding() );
            }
            if ( con.getExpressionValue() != null && con.getExpressionValue().isBound() ) {
                result.add( con.getExpressionValue().getBinding() );
            }
            if ( con instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) con;
                if ( exp.getExpressionLeftSide() != null && exp.getExpressionLeftSide().isBound() ) {
                    result.add( exp.getExpressionLeftSide().getBinding() );
                }
            }

        } else if ( f instanceof CompositeFieldConstraint ) {
            CompositeFieldConstraint cfc = (CompositeFieldConstraint) f;
            if ( cfc.getConstraints() != null ) {
                for ( FieldConstraint ss : cfc.getConstraints() ) {
                    List<String> t = getFieldBinding( ss );
                    result.addAll( t );
                }
            }
        }
        return result;
    }

    /**
     * @param idx Remove this index from the LHS. returns false if it was NOT
     * allowed to remove this item (ie it is used on the RHS).
     */
    public boolean removeLhsItem( final int idx ) {

        final IPattern[] newList = new IPattern[ this.lhs.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.lhs.length; i++ ) {

            if ( i != idx ) {
                newList[ newIdx ] = this.lhs[ i ];
                newIdx++;
            } else {
                IPattern pat = this.lhs[ i ];
                if ( pat instanceof FromCompositeFactPattern ) {
                    pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
                }
                if ( pat instanceof FactPattern ) {
                    final FactPattern p = (FactPattern) pat;
                    if ( p.getBoundName() != null && isBoundFactUsed( p.getBoundName() ) ) {
                        return false;
                    }
                }

            }

        }
        this.lhs = newList;
        return true;
    }

    /**
     * @param binding The name of the LHS fact binding.
     * @return Returns true if the specified binding is used on the RHS.
     */
    public boolean isBoundFactUsed( final String binding ) {
        if ( this.rhs == null ) {
            return false;
        }
        for ( int i = 0; i < this.rhs.length; i++ ) {
            if ( this.rhs[ i ] instanceof ActionSetField ) {
                final ActionSetField set = (ActionSetField) this.rhs[ i ];
                if ( set.getVariable().equals( binding ) ) {
                    return true;
                }
            } else if ( this.rhs[ i ] instanceof ActionRetractFact ) {
                final ActionRetractFact ret = (ActionRetractFact) this.rhs[ i ];
                if ( ret.getVariableName().equals( binding ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLhsItem( final IPattern pat ) {
        this.addLhsItem( pat,
                         true );
    }

    public void addLhsItem( final IPattern pat,
                            boolean append ) {
        this.addLhsItem( pat,
                         append ? this.lhs.length : 0 );
    }

    public void addLhsItem( final IPattern pat,
                            int position ) {
        if ( this.lhs == null ) {
            this.lhs = new IPattern[ 0 ];
        }

        if ( position < 0 ) {
            position = 0;
        } else if ( position > this.lhs.length ) {
            position = this.lhs.length;
        }

        final IPattern[] list = this.lhs;
        final IPattern[] newList = new IPattern[ list.length + 1 ];

        for ( int i = 0; i < newList.length; i++ ) {
            if ( i < position ) {
                newList[ i ] = list[ i ];
            } else if ( i > position ) {
                newList[ i ] = list[ i - 1 ];
            } else {
                newList[ i ] = pat;
            }

        }

        this.lhs = newList;
    }

    public void moveLhsItemDown( int itemIndex ) {
        if ( this.lhs == null ) {
            this.lhs = new IPattern[ 0 ];
        }

        this.moveItemDown( this.lhs,
                           itemIndex );
    }

    public void moveLhsItemUp( int itemIndex ) {
        if ( this.lhs == null ) {
            this.lhs = new IPattern[ 0 ];
        }

        this.moveItemUp( this.lhs,
                         itemIndex );
    }

    public void moveRhsItemDown( int itemIndex ) {
        if ( this.rhs == null ) {
            this.rhs = new IAction[ 0 ];
        }

        this.moveItemDown( this.rhs,
                           itemIndex );
    }

    public void moveRhsItemUp( int itemIndex ) {
        if ( this.rhs == null ) {
            this.rhs = new IAction[ 0 ];
        }

        this.moveItemUp( this.rhs,
                         itemIndex );
    }

    private void moveItemDown( Object[] array,
                               int itemIndex ) {

        for ( int i = 0; i < array.length; i++ ) {
            if ( i == itemIndex ) {
                if ( array.length > ( i + 1 ) ) {
                    Object tmp = array[ i + 1 ];
                    array[ i + 1 ] = array[ i ];
                    array[ i ] = tmp;
                    i++;
                }
            }
        }
    }

    private void moveItemUp( Object[] array,
                             int itemIndex ) {
        if ( itemIndex == 0 ) {
            return;
        }
        for ( int i = 0; i < array.length; i++ ) {
            if ( i == itemIndex ) {
                Object tmp = array[ i - 1 ];
                array[ i - 1 ] = array[ i ];
                array[ i ] = tmp;
            }
        }
    }

    public void addRhsItem( final IAction action ) {
        this.addRhsItem( action,
                         true );
    }

    public void addRhsItem( final IAction action,
                            boolean append ) {
        this.addRhsItem( action,
                         append ? this.rhs.length : 0 );
    }

    public void addRhsItem( final IAction action,
                            int position ) {
        if ( this.rhs == null ) {
            this.rhs = new IAction[ 0 ];
        }

        if ( position < 0 ) {
            position = 0;
        } else if ( position > this.rhs.length ) {
            position = this.rhs.length;
        }

        final IAction[] list = this.rhs;
        final IAction[] newList = new IAction[ list.length + 1 ];

        for ( int i = 0; i < newList.length; i++ ) {
            if ( i < position ) {
                newList[ i ] = list[ i ];
            } else if ( i > position ) {
                newList[ i ] = list[ i - 1 ];
            } else {
                newList[ i ] = action;
            }
        }

        this.rhs = newList;
    }

    public void removeRhsItem( final int idx ) {
        final IAction[] newList = new IAction[ this.rhs.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.rhs.length; i++ ) {

            if ( i != idx ) {
                newList[ newIdx ] = this.rhs[ i ];
                newIdx++;
            }

        }
        this.rhs = newList;
    }

    public void addAttribute( final RuleAttribute attribute ) {

        final RuleAttribute[] list = this.attributes;
        final RuleAttribute[] newList = new RuleAttribute[ list.length + 1 ];

        for ( int i = 0; i < list.length; i++ ) {
            newList[ i ] = list[ i ];
        }
        newList[ list.length ] = attribute;

        this.attributes = newList;

    }

    public void removeAttribute( final int idx ) {
        final RuleAttribute[] newList = new RuleAttribute[ this.attributes.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.attributes.length; i++ ) {
            if ( i != idx ) {
                newList[ newIdx ] = this.attributes[ i ];
                newIdx++;
            }

        }
        this.attributes = newList;
    }

    /**
     * Add metaData
     * @param metadata
     */
    public void addMetadata( final RuleMetadata metadata ) {

        final RuleMetadata[] newList = new RuleMetadata[ this.metadataList.length + 1 ];

        for ( int i = 0; i < this.metadataList.length; i++ ) {
            newList[ i ] = this.metadataList[ i ];
        }
        newList[ this.metadataList.length ] = metadata;

        this.metadataList = newList;
    }

    public void removeMetadata( final int idx ) {
        final RuleMetadata[] newList = new RuleMetadata[ this.metadataList.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.metadataList.length; i++ ) {

            if ( i != idx ) {
                newList[ newIdx ] = this.metadataList[ i ];
                newIdx++;
            }

        }
        this.metadataList = newList;

    }

    /**
     * Locate metadata element
     * @param attributeName - value to look for
     * @return null if not found
     */
    public RuleMetadata getMetaData( String attributeName ) {

        if ( metadataList != null && attributeName != null ) {
            for ( int i = 0; i < metadataList.length; i++ ) {
                if ( attributeName.equals( metadataList[ i ].getAttributeName() ) ) {
                    return metadataList[ i ];
                }
            }
        }
        return null;
    }

    /**
     * Update metaData element if it exists or add it otherwise
     * @param target
     * @return true on update of existing element false on added of element
     */
    public boolean updateMetadata( final RuleMetadata target ) {

        RuleMetadata metaData = getMetaData( target.getAttributeName() );
        if ( metaData != null ) {
            metaData.setValue( target.getValue() );
            return true;
        }

        addMetadata( target );
        return false;
    }

    /**
     * This uses a deceptively simple algorithm to determine what bound
     * variables are in scope for a given constraint (including connectives).
     * Does not take into account globals.
     */
    public List<String> getBoundVariablesInScope( final BaseSingleFieldConstraint con ) {
        final List<String> result = new ArrayList<String>();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[ i ];
            if ( findBoundVariableNames( con, result, pat ) ) {
                return result;
            }
        }
        return result;
    }

    private boolean findBoundVariableNames( BaseSingleFieldConstraint con,
                                            List<String> result,
                                            IPattern pat ) {
        if ( pat instanceof FromCompositeFactPattern ) {
            pat = ( (FromCompositeFactPattern) pat ).getFactPattern();
        }

        if ( pat instanceof CompositeFactPattern ) {
            for ( IFactPattern p : ( (CompositeFactPattern) pat ).getPatterns() ) {
                findBoundVariableNames( con, result, p );
            }
        }

        if ( pat instanceof FactPattern ) {
            final FactPattern fact = (FactPattern) pat;

            if ( findBoundVariableNames( con, result, fact ) ) {
                return true;
            }

        }
        return false;
    }

    private boolean findBoundVariableNames( BaseSingleFieldConstraint con,
                                            List<String> result,
                                            FactPattern fact ) {
        if ( fact.getConstraintList() != null ) {
            final FieldConstraint[] cons = fact.getConstraintList().getConstraints();
            if ( cons != null ) {
                for ( int k = 0; k < cons.length; k++ ) {
                    FieldConstraint fc = cons[ k ];
                    if ( fc instanceof SingleFieldConstraint ) {
                        final SingleFieldConstraint c = (SingleFieldConstraint) fc;
                        if ( c == con ) {
                            return true;
                        }
                        if ( c.getConnectives() != null ) {
                            for ( int j = 0; j < c.getConnectives().length; j++ ) {
                                if ( con == c.getConnectives()[ j ] ) {
                                    return true;
                                }
                            }
                        }
                        if ( c.isBound() ) {
                            result.add( c.getFieldBinding() );
                        }
                    }
                }
            }
            if ( fact.isBound() ) {
                result.add( fact.getBoundName() );
            }
        } else {
            if ( fact.isBound() ) {
                result.add( fact.getBoundName() );
            }
        }
        return false;
    }

    /**
     * Checks to see if a variable is used or not, includes fields as well as
     * facts.
     */
    public boolean isVariableNameUsed( String s ) {
        return getAllVariables().contains( s );
    }

    /**
     * Returns true if any DSLSentences are used.
     */
    public boolean hasDSLSentences() {

        if ( this.lhs != null ) {
            for ( IPattern pattern : this.lhs ) {
                if ( pattern instanceof DSLSentence ) {
                    return true;
                }
            }
        }

        if ( this.rhs != null ) {
            for ( IAction action : this.rhs ) {
                if ( action instanceof DSLSentence ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Is the Rule to be negated, i.e. "not ( PatternX, PatternY... )"
     * @return
     */
    public boolean isNegated() {
        return isNegated;
    }

    /**
     * Set whether the Rule is to be negated
     * @param isNegated
     */
    public void setNegated( boolean isNegated ) {
        this.isNegated = isNegated;
    }

    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = imports;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

}
