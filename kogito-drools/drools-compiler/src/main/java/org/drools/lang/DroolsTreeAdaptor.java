package org.drools.lang;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

/**
 * This tree adaptor is a factory for DroolsTree.
 * 
 * DRLParser must use this tree adaptor.
 */
public class DroolsTreeAdaptor extends CommonTreeAdaptor {

    /**
     * Based on token parameter it returns a new DroolsTree. Also set the char
     * offset info and editor type based on token type.
     * 
     * @param token
     *            token
     * @return DroolsTree object with char offset and editor type info
     */
    public Object create( Token token ) {
        DroolsTree tree = new DroolsTree( token );
        if ( null != token
                && (token.getClass().equals( CommonToken.class ) || token
                        .getClass().equals( DroolsToken.class )) ) {
            tree.setStartCharOffset( ((CommonToken) token).getStartIndex() );
            tree.setEndCharOffset( ((CommonToken) token).getStopIndex() );

            if ( token.getType() == DRLLexer.FLOAT
                        || token.getType() == DRLLexer.DECIMAL ) {
                tree.setEditorElementType( DroolsEditorType.NUMERIC_CONST );
            } else if ( token.getType() == DRLLexer.STRING ) {
                tree.setEditorElementType( DroolsEditorType.STRING_CONST );
            } else if ( token.getType() == DRLLexer.BOOL ) {
                tree.setEditorElementType( DroolsEditorType.BOOLEAN_CONST );
            } else if ( token.getType() == DRLLexer.NULL ) {
                tree.setEditorElementType( DroolsEditorType.NULL_CONST );
            } else if ( token.getType() == DRLLexer.MISC
                        || token.getType() == DRLLexer.DOUBLE_PIPE
                        || token.getType() == DRLLexer.DOUBLE_AMPER
                        || token.getType() == DRLLexer.DOT
                        || token.getType() == DRLLexer.COMMA
                        || token.getType() == DRLLexer.RIGHT_CURLY
                        || token.getType() == DRLLexer.LEFT_CURLY
                        || token.getType() == DRLLexer.RIGHT_SQUARE
                        || token.getType() == DRLLexer.LEFT_SQUARE
                        || token.getType() == DRLLexer.RIGHT_PAREN
                        || token.getType() == DRLLexer.LEFT_PAREN
                        || token.getType() == DRLLexer.ARROW
                        || token.getType() == DRLLexer.LESS_EQUALS
                        || token.getType() == DRLLexer.LESS
                        || token.getType() == DRLLexer.GREATER_EQUALS
                        || token.getType() == DRLLexer.GREATER
                        || token.getType() == DRLLexer.NOT_EQUALS
                        || token.getType() == DRLLexer.EQUALS
                        || token.getType() == DRLLexer.COLON
                        || token.getType() == DRLLexer.SEMICOLON ) {
                tree.setEditorElementType( DroolsEditorType.SYMBOL );
            } else if ( token.getType() == DRLLexer.ID ) {
                tree.setEditorElementType( DroolsEditorType.IDENTIFIER );
            }

        }
        return tree;
    }

    /**
     * Create a DroolsTree and keeps the char offset info.
     */
    public Object create( int tokenType,
                          Token fromToken,
                          String text ) {
        if ( fromToken instanceof DroolsToken ) {
            DroolsTree result = (DroolsTree) super.create( tokenType,
                                                           fromToken,
                                                           text );
            result
                    .setStartCharOffset( ((DroolsToken) fromToken)
                            .getStartIndex() );
            if ( text == null ) {
                result.setEndCharOffset( ((DroolsToken) fromToken)
                        .getStopIndex() );
            } else {
                result.setEndCharOffset( result.getStartCharOffset()
                                         + text.length() );
            }
            return result;
        }
        return super.create( tokenType,
                             fromToken,
                             text );
    }

    /**
     * Add a child to the tree t. If t does not have start offset info, it set t
     * start offset info from child. And always set t end char offset from
     * child.
     * 
     * The exception is RIGHT_PAREN, it is used just to keep the char set info
     * but not added on t.
     * 
     * @param t
     *            parent tree
     * @param child
     *            child tree
     */
    public void addChild( Object t,
                          Object child ) {
        if ( t != null && child != null ) {
            if ( t instanceof DroolsTree && child instanceof DroolsTree ) {
                DroolsTree tParent = (DroolsTree) t;
                DroolsTree tChild = (DroolsTree) child;

                if ( 0 >= tParent.getStartCharOffset() ) {
                    tParent.setStartCharOffset( tChild.getStartCharOffset() );
                    tParent.setEndCharOffset( tChild.getEndCharOffset() );
                }
                if ( 0 < tParent.getChildCount() ) {
                    tParent.setEndCharOffset( tChild.getEndCharOffset() );
                }
            }
            if ( DRLLexer.RIGHT_PAREN != ((Tree) child).getType() ) {
                ((Tree) t).addChild( (Tree) child );
            }
        }
    }

    /**
     * Overrides createToken, returning a DroolsToken instead of CommonToken
     */
    public Token createToken( int tokenType,
                              String text ) {
        return new DroolsToken( tokenType,
                                text );
    }

    /**
     * Overrides createToken, returning a DroolsToken instead of CommonToken
     */
    public Token createToken( Token fromToken ) {
        return new DroolsToken( fromToken );
    }
}
