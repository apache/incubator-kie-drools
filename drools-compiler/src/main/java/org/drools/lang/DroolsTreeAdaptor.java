package org.drools.lang;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

/**
 * This tree adaptor is a factory for DroolsTree.
 * 
 * DRLParser must use this tree adaptor.
 * 
 * @author porcelli
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
	public Object create(Token token) {
		DroolsTree tree = new DroolsTree(token);
		if (null != token
				&& (token.getClass().equals(CommonToken.class) || token
						.getClass().equals(DroolsToken.class))) {
			tree.setStartCharOffset(((CommonToken) token).getStartIndex());
			tree.setEndCharOffset(((CommonToken) token).getStopIndex());

			if (token.getType() == DRL5xLexer.VK_END
					|| token.getType() == DRL5xLexer.VK_EVAL
					|| token.getType() == DRL5xLexer.THEN
					|| token.getType() == DRL5xLexer.WHEN
					|| token.getType() == DRL5xLexer.ACCUMULATE
					|| token.getType() == DRL5xLexer.VK_ACTION
					|| token.getType() == DRL5xLexer.VK_ACTIVATION_GROUP
					|| token.getType() == DRL5xLexer.VK_AGENDA_GROUP
					|| token.getType() == DRL5xLexer.VK_AND
					|| token.getType() == DRL5xLexer.VK_ATTRIBUTES
					|| token.getType() == DRL5xLexer.VK_AUTO_FOCUS
					|| token.getType() == DRL5xLexer.COLLECT
					|| token.getType() == DRL5xLexer.VK_DATE_EFFECTIVE
					|| token.getType() == DRL5xLexer.VK_DATE_EXPIRES
					|| token.getType() == DRL5xLexer.VK_DECLARE
					|| token.getType() == DRL5xLexer.VK_DIALECT
					|| token.getType() == DRL5xLexer.VK_TIMER
					|| token.getType() == DRL5xLexer.VK_ENABLED
					|| token.getType() == DRL5xLexer.VK_ENTRY_POINT
					|| token.getType() == DRL5xLexer.VK_EXISTS
					|| token.getType() == DRL5xLexer.VK_FORALL
					|| token.getType() == DRL5xLexer.FROM
					|| token.getType() == DRL5xLexer.VK_FUNCTION
					|| token.getType() == DRL5xLexer.VK_GLOBAL
					|| token.getType() == DRL5xLexer.VK_IMPORT
					|| token.getType() == DRL5xLexer.VK_IN
					|| token.getType() == DRL5xLexer.VK_INIT
					|| token.getType() == DRL5xLexer.VK_LOCK_ON_ACTIVE
					|| token.getType() == DRL5xLexer.VK_NO_LOOP
					|| token.getType() == DRL5xLexer.VK_NOT
					|| token.getType() == DRL5xLexer.VK_OR
					|| token.getType() == DRL5xLexer.VK_PACKAGE
					|| token.getType() == DRL5xLexer.VK_QUERY
					|| token.getType() == DRL5xLexer.VK_RESULT
					|| token.getType() == DRL5xLexer.VK_REVERSE
					|| token.getType() == DRL5xLexer.VK_RULE
					|| token.getType() == DRL5xLexer.VK_RULEFLOW_GROUP
					|| token.getType() == DRL5xLexer.VK_SALIENCE) {
				tree.setEditorElementType(DroolsEditorType.KEYWORD);
			} else if (token.getType() == DRL5xLexer.FLOAT
					|| token.getType() == DRL5xLexer.INT) {
				tree.setEditorElementType(DroolsEditorType.NUMERIC_CONST);
			} else if (token.getType() == DRL5xLexer.STRING) {
				tree.setEditorElementType(DroolsEditorType.STRING_CONST);
			} else if (token.getType() == DRL5xLexer.BOOL) {
				tree.setEditorElementType(DroolsEditorType.BOOLEAN_CONST);
			} else if (token.getType() == DRL5xLexer.NULL) {
				tree.setEditorElementType(DroolsEditorType.NULL_CONST);
			} else if (token.getType() == DRL5xLexer.VT_SQUARE_CHUNK
					|| token.getType() == DRL5xLexer.VT_PAREN_CHUNK
					|| token.getType() == DRL5xLexer.VT_CURLY_CHUNK
					|| token.getType() == DRL5xLexer.VT_RHS_CHUNK) {
				tree.setEditorElementType(DroolsEditorType.CODE_CHUNK);
			} else if (token.getType() == DRL5xLexer.MISC
					|| token.getType() == DRL5xLexer.DOUBLE_PIPE
					|| token.getType() == DRL5xLexer.DOUBLE_AMPER
					|| token.getType() == DRL5xLexer.DOT
					|| token.getType() == DRL5xLexer.COMMA
					|| token.getType() == DRL5xLexer.RIGHT_CURLY
					|| token.getType() == DRL5xLexer.LEFT_CURLY
					|| token.getType() == DRL5xLexer.RIGHT_SQUARE
					|| token.getType() == DRL5xLexer.LEFT_SQUARE
					|| token.getType() == DRL5xLexer.RIGHT_PAREN
					|| token.getType() == DRL5xLexer.LEFT_PAREN
					|| token.getType() == DRL5xLexer.ARROW
					|| token.getType() == DRL5xLexer.LESS_EQUAL
					|| token.getType() == DRL5xLexer.LESS
					|| token.getType() == DRL5xLexer.GREATER_EQUAL
					|| token.getType() == DRL5xLexer.GREATER
					|| token.getType() == DRL5xLexer.NOT_EQUAL
					|| token.getType() == DRL5xLexer.EQUALS
					|| token.getType() == DRL5xLexer.COLON
					|| token.getType() == DRL5xLexer.SEMICOLON) {
				tree.setEditorElementType(DroolsEditorType.SYMBOL);
			} else if (token.getType() == DRL5xLexer.ID
					|| token.getType() == DRL5xLexer.DOT_STAR
					|| token.getType() == DRL5xLexer.VT_GLOBAL_ID
					|| token.getType() == DRL5xLexer.VT_FUNCTION_ID
					|| token.getType() == DRL5xLexer.VT_QUERY_ID
					|| token.getType() == DRL5xLexer.VT_RULE_ID
					|| token.getType() == DRL5xLexer.VT_ENTRYPOINT_ID) {
				tree.setEditorElementType(DroolsEditorType.IDENTIFIER);
			} else if (token.getType() == DRL5xLexer.VT_DATA_TYPE) {
				tree.setEditorElementType(DroolsEditorType.IDENTIFIER_TYPE);
			} else if (token.getType() == DRL5xLexer.VT_PATTERN_TYPE) {
				tree.setEditorElementType(DroolsEditorType.IDENTIFIER_PATTERN);
			}

		}
		return tree;
	}

	/**
	 * Create a DroolsTree and keeps the char offset info.
	 */
	public Object create(int tokenType, Token fromToken, String text) {
		if (fromToken instanceof DroolsToken) {
			DroolsTree result = (DroolsTree) super.create(tokenType, fromToken,
					text);
			result
					.setStartCharOffset(((DroolsToken) fromToken)
							.getStartIndex());
			if (text == null) {
				result.setEndCharOffset(((DroolsToken) fromToken)
						.getStopIndex());
			} else {
				result.setEndCharOffset(result.getStartCharOffset()
						+ text.length());
			}
			return result;
		}
		return super.create(tokenType, fromToken, text);
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
	public void addChild(Object t, Object child) {
		if (t != null && child != null) {
			if (t instanceof DroolsTree && child instanceof DroolsTree) {
				DroolsTree tParent = (DroolsTree) t;
				DroolsTree tChild = (DroolsTree) child;

				if (0 >= tParent.getStartCharOffset()) {
					tParent.setStartCharOffset(tChild.getStartCharOffset());
					tParent.setEndCharOffset(tChild.getEndCharOffset());
				}
				if (0 < tParent.getChildCount()) {
					tParent.setEndCharOffset(tChild.getEndCharOffset());
				}
			}
			if (DRL5xLexer.RIGHT_PAREN != ((Tree) child).getType()) {
				((Tree) t).addChild((Tree) child);
			}
		}
	}

	/**
	 * Overrides createToken, returning a DroolsToken instead of CommonToken
	 */
	public Token createToken(int tokenType, String text) {
		return new DroolsToken(tokenType, text);
	}

	/**
	 * Overrides createToken, returning a DroolsToken instead of CommonToken
	 */
	public Token createToken(Token fromToken) {
		return new DroolsToken(fromToken);
	}
}