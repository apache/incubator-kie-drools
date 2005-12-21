package org.drools.natural.ast;

/**
 * This represents a field/method of a rule parameter.
 * This is the same as left only, but can be treated with a higher precedence then other stuff, 
 * as it will be pretty common to operate on fields of a rule parameter. This involved the dictionary
 * telling us what stuff is from a rule parameter versus normal stuff.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleParamFieldNode extends LeftOnlyNode
{
     public RuleParamFieldNode(String originalVal, String valFromDictionary) {
         super(originalVal, valFromDictionary, 1);
     }

     public String render() {
         throw new UnsupportedOperationException("Not used yet.");
     }
     
}
