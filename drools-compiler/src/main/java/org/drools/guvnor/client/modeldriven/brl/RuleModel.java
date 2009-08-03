package org.drools.guvnor.client.modeldriven.brl;

import java.util.ArrayList;
import java.util.List;

public class RuleModel
    implements
    PortableObject {

    /**
     * This name is generally not used - the asset name or the
     * file name is preferred (ie it could get out of sync with the name of the file it is in).
     */
    public String          name;
    public String          parentName;
    public String          modelVersion = "1.0";

    public RuleAttribute[] attributes   = new RuleAttribute[0];
    public RuleMetadata[]  metadataList = new RuleMetadata[0];

    public IPattern[]      lhs          = new IPattern[0];
    public IAction[]       rhs          = new IAction[0];

    /**
     * This will return the fact pattern that a variable is bound to.
     *
     * @param var The bound fact variable (NOT bound field).
     * @return null or the FactPattern found.
     */
    public FactPattern getBoundFact(final String var) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {

            if ( this.lhs[i] instanceof FactPattern ) {
                final FactPattern p = (FactPattern) this.lhs[i];
                if ( p.boundName != null && var.equals( p.boundName ) ) {
                    return p;
                }
            }
        }
        return null;
    }

    public String getFieldConstraint(final String var) {
        if ( this.lhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.lhs.length; i++ ) {

            if ( this.lhs[i] instanceof FactPattern ) {
                final FactPattern p = (FactPattern) this.lhs[i];
                for ( FieldConstraint z : p.getFieldConstraints() ) {
                    return giveFieldBinding( z,
                                             var );
                }
            }

        }
        return null;
    }

    private String giveFieldBinding(FieldConstraint f,
                                    String var) {
        if ( f instanceof SingleFieldConstraint ) {
            SingleFieldConstraint s = (SingleFieldConstraint) f;
            if ( s.isBound() == true && var.equals( s.fieldBinding ) ) {
                return s.fieldType;
            }
        }
        if ( f instanceof CompositeFieldConstraint ) {
            CompositeFieldConstraint s = (CompositeFieldConstraint) f;
            for ( FieldConstraint ss : s.constraints ) {
                return giveFieldBinding( s,
                                         var );
            }
        }
        return null;
    }

    /*
          * Get the bound fact of a rhs action
          * Fix nheron
          */
    public ActionInsertFact getRhsBoundFact(final String var) {
        if ( this.rhs == null ) {
            return null;
        }
        for ( int i = 0; i < this.rhs.length; i++ ) {

            if ( this.rhs[i] instanceof ActionInsertFact ) {
                final ActionInsertFact p = (ActionInsertFact) this.rhs[i];
                if ( p.getBoundName() != null && var.equals( p.getBoundName() ) ) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * @return A list of bound facts (String). Or empty list if none are found.
     */
    public List getBoundFacts() {
        if ( this.lhs == null ) {
            return null;
        }
        final List list = new ArrayList();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            if ( this.lhs[i] instanceof FactPattern ) {
                final FactPattern p = (FactPattern) this.lhs[i];
                if ( p.boundName != null ) {
                    list.add( p.boundName );
                }
                List<String> fieldBindings = getListFieldBinding( p );
                if ( fieldBindings != null ) {
                    list.addAll( fieldBindings );
                }
            }
        }
        return list;

    }

    private List<String> getListFieldBinding(FactPattern fact) {
        List<String> result = new ArrayList<String>();

        for ( int j = 0; j < fact.getFieldConstraints().length; j++ ) {
            FieldConstraint fc = fact.getFieldConstraints()[j];
            List<String> s = giveFieldBinding( fc );
            result.addAll( s );
        }
        return result;
    }

    private List<String> giveFieldBinding(FieldConstraint f) {
        List<String> result = new ArrayList<String>();
        if ( f instanceof SingleFieldConstraint ) {
            SingleFieldConstraint s = (SingleFieldConstraint) f;
            if ( s.isBound() == true ) {
                result.add( s.fieldBinding );
            }
        }
        if ( f instanceof CompositeFieldConstraint ) {
            CompositeFieldConstraint s = (CompositeFieldConstraint) f;
            for ( FieldConstraint ss : s.constraints ) {
                List<String> t = giveFieldBinding( s );
                result.addAll( t );
            }
        }
        return result;
    }

    /**
     * @return A list of bound facts of the rhs(String). Or empty list if none are found.
     *         Fix nheron
     */
    public List getRhsBoundFacts() {
        if ( this.rhs == null ) {
            return null;
        }
        final List list = new ArrayList();
        for ( int i = 0; i < this.rhs.length; i++ ) {
            if ( this.rhs[i] instanceof ActionInsertFact ) {
                final ActionInsertFact p = (ActionInsertFact) this.rhs[i];
                if ( p.getBoundName() != null ) {
                    list.add( p.getBoundName() );
                }
            }
        }
        return list;

    }

    /**
     * @param idx Remove this index from the LHS.
     *            returns false if it was NOT allowed to remove this item (ie
     *            it is used on the RHS).
     */
    public boolean removeLhsItem(final int idx) {

        final IPattern[] newList = new IPattern[this.lhs.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.lhs.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.lhs[i];
                newIdx++;
            } else {
                if ( this.lhs[i] instanceof FactPattern ) {
                    final FactPattern p = (FactPattern) this.lhs[i];
                    if ( p.boundName != null && isBoundFactUsed( p.boundName ) ) {
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
    public boolean isBoundFactUsed(final String binding) {
        if ( this.rhs == null ) {
            return false;
        }
        for ( int i = 0; i < this.rhs.length; i++ ) {
            if ( this.rhs[i] instanceof ActionSetField ) {
                final ActionSetField set = (ActionSetField) this.rhs[i];
                if ( set.variable.equals( binding ) ) {
                    return true;
                }
            } else if ( this.rhs[i] instanceof ActionRetractFact ) {
                final ActionRetractFact ret = (ActionRetractFact) this.rhs[i];
                if ( ret.variableName.equals( binding ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLhsItem(final IPattern pat) {
        if ( this.lhs == null ) {
            this.lhs = new IPattern[0];
        }

        final IPattern[] list = this.lhs;
        final IPattern[] newList = new IPattern[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = pat;

        this.lhs = newList;
    }

    public void addRhsItem(final IAction action) {
        if ( this.rhs == null ) {
            this.rhs = new IAction[0];
        }

        final IAction[] list = this.rhs;
        final IAction[] newList = new IAction[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = action;

        this.rhs = newList;
    }

    public void removeRhsItem(final int idx) {
        final IAction[] newList = new IAction[this.rhs.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.rhs.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.rhs[i];
                newIdx++;
            }

        }
        this.rhs = newList;
    }

    public void addAttribute(final RuleAttribute attribute) {

        final RuleAttribute[] list = this.attributes;
        final RuleAttribute[] newList = new RuleAttribute[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = attribute;

        this.attributes = newList;

    }

    public void removeAttribute(final int idx) {
        final RuleAttribute[] newList = new RuleAttribute[this.attributes.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.attributes.length; i++ ) {
            if ( i != idx ) {
                newList[newIdx] = this.attributes[i];
                newIdx++;
            }

        }
        this.attributes = newList;
    }

    /**
     * Add metaData
     *
     * @param metadata
     */
    public void addMetadata(final RuleMetadata metadata) {

        final RuleMetadata[] newList = new RuleMetadata[this.metadataList.length + 1];

        for ( int i = 0; i < this.metadataList.length; i++ ) {
            newList[i] = this.metadataList[i];
        }
        newList[this.metadataList.length] = metadata;

        this.metadataList = newList;
    }

    public void removeMetadata(final int idx) {
        final RuleMetadata[] newList = new RuleMetadata[this.metadataList.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.metadataList.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.metadataList[i];
                newIdx++;
            }

        }
        this.metadataList = newList;

    }

    /**
     * Locate metadata element
     *
     * @param attributeName - value to look for
     * @return null if not found
     */
    public RuleMetadata getMetaData(String attributeName) {

        if ( metadataList != null && attributeName != null ) {
            for ( int i = 0; i < metadataList.length; i++ ) {
                if ( attributeName.equals( metadataList[i].attributeName ) ) {
                    return metadataList[i];
                }
            }
        }
        return null;
    }

    /**
     * Update metaData element if it exists or add it otherwise
     *
     * @param target
     * @return true on update of existing element
     *         false on added of element
     */
    public boolean updateMetadata(final RuleMetadata target) {

        RuleMetadata metaData = getMetaData( target.attributeName );
        if ( metaData != null ) {
            metaData.value = target.value;
            return true;
        }

        addMetadata( target );
        return false;
    }

    /**
     * This uses a deceptively simple algorithm to determine
     * what bound variables are in scope for a given constraint (including connectives).
     * Does not take into account globals.
     */
    public List getBoundVariablesInScope(final ISingleFieldConstraint con) {
        final List result = new ArrayList();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            final IPattern pat = this.lhs[i];
            if ( pat instanceof FactPattern ) {
                final FactPattern fact = (FactPattern) pat;

                if ( fact.constraintList != null ) {
                    final FieldConstraint[] cons = fact.constraintList.constraints;
                    if ( cons != null ) {
                        for ( int k = 0; k < cons.length; k++ ) {
                            FieldConstraint fc = cons[k];
                            if ( fc instanceof SingleFieldConstraint ) {
                                final SingleFieldConstraint c = (SingleFieldConstraint) fc;
                                if ( c == con ) {
                                    return result;
                                }
                                if ( c.connectives != null ) {
                                    for ( int j = 0; j < c.connectives.length; j++ ) {
                                        if ( con == c.connectives[j] ) {
                                            return result;
                                        }
                                    }
                                }
                                if ( c.isBound() ) {
                                    result.add( c.fieldBinding );
                                }
                            }
                        }
                    }
                    if ( fact.isBound() ) {
                        result.add( fact.boundName );
                    }
                } else {
                    if ( fact.isBound() ) {
                        result.add( fact.boundName );
                    }
                }

            }
        }
        return result;
    }

    /**
     * This will get a list of all bound variables, including bound fields.
     */
    public List getAllVariables() {
        List result = new ArrayList();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = this.lhs[i];
            if ( pat instanceof FactPattern ) {
                FactPattern fact = (FactPattern) pat;
                if ( fact.isBound() ) {
                    result.add( fact.boundName );
                }

                for ( int j = 0; j < fact.getFieldConstraints().length; j++ ) {
                    FieldConstraint fc = fact.getFieldConstraints()[j];
                    if ( fc instanceof SingleFieldConstraint ) {
                        SingleFieldConstraint con = (SingleFieldConstraint) fc;
                        if ( con.isBound() ) {
                            result.add( con.fieldBinding );
                        }
                    }
                }
            }
        }
        for ( int i = 0; i < this.rhs.length; i++ ) {
            IAction pat = this.rhs[i];
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
     * Checks to see if a variable is used or not, includes fields
     * as well as facts.
     */
    public boolean isVariableNameUsed(String s) {
        return getAllVariables().contains( s );
    }

    /**
     * Returns true if any DSLSentences are used.
     */
    public boolean hasDSLSentences() {

        if ( this.lhs != null ) {
            for ( int i = 0; i < this.lhs.length; i++ ) {
                if ( lhs[i] instanceof DSLSentence ) {
                    return true;
                }
            }
        }

        if ( this.rhs != null ) {
            for ( int i = 0; i < this.rhs.length; i++ ) {
                if ( rhs[i] instanceof DSLSentence ) {
                    return true;
                }
            }
        }

        return false;
    }

}
