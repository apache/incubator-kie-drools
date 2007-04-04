package org.drools.brms.client.modeldriven.brxml;

import java.util.ArrayList;
import java.util.List;

public class RuleModel implements PortableObject {

    public String name;
    public String modelVersion = "1.0";
    
    public RuleAttribute[] attributes = new RuleAttribute[0];
    
    public IPattern[] lhs = new IPattern[0];
    public IAction[] rhs = new IAction[0];
    
    /**
     * This will return the fact pattern that a variable is bound to. 
     * 
     * @param var The bound fact variable (NOT bound field).
     * @return null or the FactPattern found. 
     */
    public FactPattern getBoundFact(String var) {
        if (lhs == null ) return null;
        for ( int i = 0; i < lhs.length; i++ ) {
            
            if (lhs[i] instanceof FactPattern) {
                FactPattern p = (FactPattern) lhs[i];
                if (p.boundName != null && var.equals( p.boundName)) {
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
        if (lhs == null) return null;
        List list = new ArrayList();
        for ( int i = 0; i < lhs.length; i++ ) {
            if (lhs[i] instanceof FactPattern) {
                FactPattern p = (FactPattern) lhs[i];
                if (p.boundName != null)  list.add( p.boundName );
            }
        }
        return list;
        
    }

    /**
     * 
     * @param idx Remove this index from the LHS.
     * @param Returns false if it was NOT allowed to remove this item (ie 
     * it is used on the RHS).
     */
    public boolean removeLhsItem(int idx) {
        
        IPattern[] newList = new IPattern[lhs.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < lhs.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = lhs[i];
                newIdx++;
            } else {
                if (lhs[i] instanceof FactPattern) {
                    FactPattern p = (FactPattern) lhs[i];
                    if (p.boundName != null && isBoundFactUsed( p.boundName )) {
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
    public boolean isBoundFactUsed(String binding) {
        if (rhs == null) return false;
        for ( int i = 0; i < rhs.length; i++ ) {
            if (rhs[i] instanceof ActionSetField) {
                ActionSetField set = (ActionSetField) rhs[i];
                if (set.variable.equals( binding )) {
                    return true;
                }
            } else if (rhs[i] instanceof ActionRetractFact) {
                ActionRetractFact ret = (ActionRetractFact) rhs[i];
                if (ret.variableName.equals( binding )) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void addLhsItem(IPattern pat) {
        if (this.lhs == null) this.lhs = new IPattern[0];
        
        IPattern[] list = this.lhs;
        IPattern[] newList = new IPattern[list.length + 1];
        
        for ( int i = 0; i < list.length; i++ ) {
            newList[i] =  list[i];
        }
        newList[list.length] = pat; 
        
        this.lhs = newList;        
    }
    
    public void addRhsItem(IAction action) {
        if (this.rhs == null) this.rhs = new IAction[0];
        
        IAction[] list = this.rhs;
        IAction[] newList = new IAction[list.length + 1];
        
        for ( int i = 0; i < list.length; i++ ) {
            newList[i] =  list[i];
        }
        newList[list.length] = action; 
        
        this.rhs = newList;         
    }
    
    public void removeRhsItem(int idx) {
        IAction[] newList = new IAction[rhs.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < rhs.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = rhs[i];
                newIdx++;
            }
            
        }
        this.rhs = newList;
    }

    public void addAttribute(RuleAttribute attribute) {
        
        
        RuleAttribute[] list = this.attributes;
        RuleAttribute[] newList = new RuleAttribute[list.length + 1];
        
        for ( int i = 0; i < list.length; i++ ) {
            newList[i] =  list[i];
        }
        newList[list.length] = attribute; 
        
        this.attributes = newList;        
        
    }
    
    public void removeAttribute(int idx) {
        RuleAttribute[] newList = new RuleAttribute[attributes.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < attributes.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = attributes[i];
                newIdx++;
            }
            
        }
        this.attributes = newList;

    }

    public List getBoundVariablesInScope(Constraint con) {
        List result = new ArrayList();
        for ( int i = 0; i < this.lhs.length; i++ ) {
            IPattern pat = lhs[i];
            if (pat instanceof FactPattern) {
                FactPattern fact = (FactPattern) pat;

                if (fact.constraints != null) {
                    //for ( int j = 0; j < fact.constraints.length; j++ ) {
                        Constraint[] cons = fact.constraints;
                        for ( int k = 0; k < cons.length; k++ ) {
                            Constraint c = cons[k];
                            if (c == con) {
                                return result;
                            }                     

                            if (c.isBound()) {
                                result.add( c.fieldBinding );
                            }
                        }
                    //}
                    if (fact.isBound() ) {
                        result.add(fact.boundName);
                    }                       
                } else {
                    if (fact.isBound() ) {
                        result.add(fact.boundName);
                    }                       
                }
                        
                
            }
        }
        return result;
    }
    
}
