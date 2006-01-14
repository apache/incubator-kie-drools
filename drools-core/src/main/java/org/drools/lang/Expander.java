package org.drools.lang;

/**
 * Expanders provide just in time expansion for expressions in DRL.
 * 
 * Expanders should ideally not make presumptions on any embedded semantic 
 * language. For instance, java aware pre processing should be done in
 * drools-java semantic module, not in the parser itself. Expanders should 
 * be reusable across semantic languages. 
 * 
 * @author Michael Neale
 *
 */
public interface Expander {

}
