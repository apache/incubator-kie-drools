package org.drools.natural.ast;

/**
 * Literal nodes are for items that are not in any dictionary. They are included as is.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class LiteralNode extends BaseSyntaxNode
{
	
    
    
	public LiteralNode(String val) {
		super.originalValue = val;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof LiteralNode)) {
			return false;
		} else {
			LiteralNode in = (LiteralNode) obj;
			return in.originalValue.equals(super.originalValue);
		}
	}

    public boolean isSatisfied()
    {
        //always happy !
        return true;
    }


    protected void process()
    {
        //do nothing.
        return;
    }

    /**
     * if the previous node is also a literal or a sub, then
     * a space will be inserted to honour the intent of the original.
     */
    public String render()
    {
        if (prev != null) {
            return SPACE + super.originalValue;
        } else {
            return super.originalValue;
        }
    }
}
