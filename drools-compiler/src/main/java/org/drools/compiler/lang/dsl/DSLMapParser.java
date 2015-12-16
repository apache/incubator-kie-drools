/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

// $ANTLR 3.5 src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g 2015-04-23 15:24:03

    package org.drools.compiler.lang.dsl;
    import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteEmptyStreamException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.drools.compiler.compiler.ParserError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@SuppressWarnings("all")
public class DSLMapParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "COLON", "COMMA", "DOT", "EOL", 
		"EQUALS", "EscapeSequence", "IdentifierPart", "LEFT_CURLY", "LEFT_SQUARE", 
		"LITERAL", "MISC", "RIGHT_CURLY", "RIGHT_SQUARE", "VT_ANY", "VT_CONDITION", 
		"VT_CONSEQUENCE", "VT_DSL_GRAMMAR", "VT_ENTRY", "VT_ENTRY_KEY", "VT_ENTRY_VAL", 
		"VT_KEYWORD", "VT_LITERAL", "VT_META", "VT_PATTERN", "VT_QUAL", "VT_SCOPE", 
		"VT_SPACE", "VT_VAR_DEF", "VT_VAR_REF", "WS"
	};
	public static final int EOF=-1;
	public static final int COLON=4;
	public static final int COMMA=5;
	public static final int DOT=6;
	public static final int EOL=7;
	public static final int EQUALS=8;
	public static final int EscapeSequence=9;
	public static final int IdentifierPart=10;
	public static final int LEFT_CURLY=11;
	public static final int LEFT_SQUARE=12;
	public static final int LITERAL=13;
	public static final int MISC=14;
	public static final int RIGHT_CURLY=15;
	public static final int RIGHT_SQUARE=16;
	public static final int VT_ANY=17;
	public static final int VT_CONDITION=18;
	public static final int VT_CONSEQUENCE=19;
	public static final int VT_DSL_GRAMMAR=20;
	public static final int VT_ENTRY=21;
	public static final int VT_ENTRY_KEY=22;
	public static final int VT_ENTRY_VAL=23;
	public static final int VT_KEYWORD=24;
	public static final int VT_LITERAL=25;
	public static final int VT_META=26;
	public static final int VT_PATTERN=27;
	public static final int VT_QUAL=28;
	public static final int VT_SCOPE=29;
	public static final int VT_SPACE=30;
	public static final int VT_VAR_DEF=31;
	public static final int VT_VAR_REF=32;
	public static final int WS=33;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public DSLMapParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DSLMapParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return DSLMapParser.tokenNames; }
	@Override public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g"; }


	    private List<ParserError> errors = new ArrayList<ParserError>();

	    public void reportError(RecognitionException ex) {
	        errors.add(new ParserError( "DSL parser error", ex.line, ex.charPositionInLine ) );
	    }

	    public List<ParserError> getErrors() {
	        return errors;
	    }

	    /** Override this method to not output mesages */
	    public void emitErrorMessage(String msg) {
	    }

	    private static final Pattern namePat = Pattern.compile( "[\\p{L}_$][\\p{L}_$\\d]*" );

	    private void isIdentifier( Token name ){
	        String nameString = name.getText();
	        if( ! namePat.matcher( nameString ).matches() ){
	            errors.add(new ParserError( "invalid variable identifier " + nameString,
	                                        name.getLine(), name.getCharPositionInLine() ) );
	        }
	    }

	    private boolean validateLT(int LTNumber, String text){
	        if (null == input) return false;
	        if (null == input.LT(LTNumber)) return false;
	        if (null == input.LT(LTNumber).getText()) return false;

	        String text2Validate = input.LT(LTNumber).getText();
	        if (text2Validate.startsWith("[") && text2Validate.endsWith("]")){
	            text2Validate = text2Validate.substring(1, text2Validate.length() - 1);
	        }

	        return text2Validate.equalsIgnoreCase(text);
	    }

	    private boolean validateIdentifierKey(String text){
	        return validateLT(1, text);
	    }




	public static class mapping_file_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "mapping_file"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:106:1: mapping_file : ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) ;
	public final DSLMapParser.mapping_file_return mapping_file() throws RecognitionException {
		DSLMapParser.mapping_file_return retval = new DSLMapParser.mapping_file_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope statement1 =null;

		RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:107:5: ( ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:107:7: ( statement )*
			{
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:107:7: ( statement )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==EOL||LA1_0==LEFT_SQUARE) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:107:7: statement
					{
					pushFollow(FOLLOW_statement_in_mapping_file275);
					statement1=statement();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_statement.add(statement1.getTree());
					}
					break;

				default :
					break loop1;
				}
			}

			// AST REWRITE
			// elements: statement
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 108:5: -> ^( VT_DSL_GRAMMAR ( statement )* )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:108:8: ^( VT_DSL_GRAMMAR ( statement )* )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_DSL_GRAMMAR, "VT_DSL_GRAMMAR"), root_1);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:108:25: ( statement )*
				while ( stream_statement.hasNext() ) {
					adaptor.addChild(root_1, stream_statement.nextTree());
				}
				stream_statement.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "mapping_file"


	public static class statement_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "statement"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:111:1: statement : ( entry | EOL !);
	public final DSLMapParser.statement_return statement() throws RecognitionException {
		DSLMapParser.statement_return retval = new DSLMapParser.statement_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EOL3=null;
		ParserRuleReturnScope entry2 =null;

		Object EOL3_tree=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:112:5: ( entry | EOL !)
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==LEFT_SQUARE) ) {
				alt2=1;
			}
			else if ( (LA2_0==EOL) ) {
				alt2=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:112:7: entry
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_entry_in_statement306);
					entry2=entry();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, entry2.getTree());

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:113:7: EOL !
					{
					root_0 = (Object)adaptor.nil();


					EOL3=(Token)match(input,EOL,FOLLOW_EOL_in_statement314); if (state.failed) return retval;
					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "statement"


	public static class entry_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "entry"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:1: entry : scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) ;
	public final DSLMapParser.entry_return entry() throws RecognitionException {
		DSLMapParser.entry_return retval = new DSLMapParser.entry_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EQUALS7=null;
		Token EOL9=null;
		Token EOF10=null;
		ParserRuleReturnScope scope_section4 =null;
		ParserRuleReturnScope meta_section5 =null;
		ParserRuleReturnScope key_section6 =null;
		ParserRuleReturnScope value_section8 =null;

		Object EQUALS7_tree=null;
		Object EOL9_tree=null;
		Object EOF10_tree=null;
		RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
		RewriteRuleTokenStream stream_EOL=new RewriteRuleTokenStream(adaptor,"token EOL");
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleSubtreeStream stream_meta_section=new RewriteRuleSubtreeStream(adaptor,"rule meta_section");
		RewriteRuleSubtreeStream stream_key_section=new RewriteRuleSubtreeStream(adaptor,"rule key_section");
		RewriteRuleSubtreeStream stream_scope_section=new RewriteRuleSubtreeStream(adaptor,"rule scope_section");
		RewriteRuleSubtreeStream stream_value_section=new RewriteRuleSubtreeStream(adaptor,"rule value_section");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:8: ( scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:10: scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF )
			{
			pushFollow(FOLLOW_scope_section_in_entry336);
			scope_section4=scope_section();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_scope_section.add(scope_section4.getTree());
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:24: ( meta_section )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==LEFT_SQUARE) ) {
				int LA3_1 = input.LA(2);
				if ( (LA3_1==LITERAL) ) {
					int LA3_3 = input.LA(3);
					if ( (LA3_3==RIGHT_SQUARE) ) {
						int LA3_5 = input.LA(4);
						if ( (synpred3_DSLMap()) ) {
							alt3=1;
						}
					}
				}
				else if ( (LA3_1==RIGHT_SQUARE) ) {
					int LA3_4 = input.LA(3);
					if ( (synpred3_DSLMap()) ) {
						alt3=1;
					}
				}
			}
			switch (alt3) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:24: meta_section
					{
					pushFollow(FOLLOW_meta_section_in_entry338);
					meta_section5=meta_section();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_meta_section.add(meta_section5.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_key_section_in_entry341);
			key_section6=key_section();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_key_section.add(key_section6.getTree());
			EQUALS7=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_entry343); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS7);

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:57: ( value_section )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( ((LA4_0 >= COLON && LA4_0 <= DOT)||LA4_0==EQUALS||(LA4_0 >= LEFT_CURLY && LA4_0 <= LITERAL)||LA4_0==RIGHT_SQUARE) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:57: value_section
					{
					pushFollow(FOLLOW_value_section_in_entry345);
					value_section8=value_section();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_value_section.add(value_section8.getTree());
					}
					break;

			}

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:72: ( EOL | EOF )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==EOL) ) {
				alt5=1;
			}
			else if ( (LA5_0==EOF) ) {
				alt5=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:73: EOL
					{
					EOL9=(Token)match(input,EOL,FOLLOW_EOL_in_entry349); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_EOL.add(EOL9);

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:77: EOF
					{
					EOF10=(Token)match(input,EOF,FOLLOW_EOF_in_entry351); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_EOF.add(EOF10);

					}
					break;

			}

			// AST REWRITE
			// elements: meta_section, scope_section, key_section, value_section
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 120:5: -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:120:8: ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY, "VT_ENTRY"), root_1);
				adaptor.addChild(root_1, stream_scope_section.nextTree());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:120:33: ( meta_section )?
				if ( stream_meta_section.hasNext() ) {
					adaptor.addChild(root_1, stream_meta_section.nextTree());
				}
				stream_meta_section.reset();

				adaptor.addChild(root_1, stream_key_section.nextTree());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:120:59: ( value_section )?
				if ( stream_value_section.hasNext() ) {
					adaptor.addChild(root_1, stream_value_section.nextTree());
				}
				stream_value_section.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch ( RecognitionException e ) {

			        reportError( e );
			    
		}
		catch ( RewriteEmptyStreamException e ) {

			    
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "entry"


	public static class scope_section_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "scope_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:130:1: scope_section : LEFT_SQUARE (value1= condition_key |value2= consequence_key |value3= keyword_key |value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) ;
	public final DSLMapParser.scope_section_return scope_section() throws RecognitionException {
		DSLMapParser.scope_section_return retval = new DSLMapParser.scope_section_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LEFT_SQUARE11=null;
		Token RIGHT_SQUARE12=null;
		ParserRuleReturnScope value1 =null;
		ParserRuleReturnScope value2 =null;
		ParserRuleReturnScope value3 =null;
		ParserRuleReturnScope value4 =null;

		Object LEFT_SQUARE11_tree=null;
		Object RIGHT_SQUARE12_tree=null;
		RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
		RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");
		RewriteRuleSubtreeStream stream_any_key=new RewriteRuleSubtreeStream(adaptor,"rule any_key");
		RewriteRuleSubtreeStream stream_condition_key=new RewriteRuleSubtreeStream(adaptor,"rule condition_key");
		RewriteRuleSubtreeStream stream_keyword_key=new RewriteRuleSubtreeStream(adaptor,"rule keyword_key");
		RewriteRuleSubtreeStream stream_consequence_key=new RewriteRuleSubtreeStream(adaptor,"rule consequence_key");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:131:5: ( LEFT_SQUARE (value1= condition_key |value2= consequence_key |value3= keyword_key |value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:131:7: LEFT_SQUARE (value1= condition_key |value2= consequence_key |value3= keyword_key |value4= any_key ) RIGHT_SQUARE
			{
			LEFT_SQUARE11=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_scope_section412); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE11);

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:132:9: (value1= condition_key |value2= consequence_key |value3= keyword_key |value4= any_key )
			int alt6=4;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==LITERAL) ) {
				int LA6_1 = input.LA(2);
				if ( (((validateIdentifierKey("condition")||validateIdentifierKey("when"))&&synpred6_DSLMap())) ) {
					alt6=1;
				}
				else if ( ((synpred7_DSLMap()&&(validateIdentifierKey("consequence")||validateIdentifierKey("then")))) ) {
					alt6=2;
				}
				else if ( ((synpred8_DSLMap()&&(validateIdentifierKey("keyword")))) ) {
					alt6=3;
				}
				else if ( ((validateIdentifierKey("*"))) ) {
					alt6=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:132:10: value1= condition_key
					{
					pushFollow(FOLLOW_condition_key_in_scope_section425);
					value1=condition_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_condition_key.add(value1.getTree());
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:133:11: value2= consequence_key
					{
					pushFollow(FOLLOW_consequence_key_in_scope_section439);
					value2=consequence_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_consequence_key.add(value2.getTree());
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:134:11: value3= keyword_key
					{
					pushFollow(FOLLOW_keyword_key_in_scope_section453);
					value3=keyword_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_keyword_key.add(value3.getTree());
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:135:11: value4= any_key
					{
					pushFollow(FOLLOW_any_key_in_scope_section467);
					value4=any_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_any_key.add(value4.getTree());
					}
					break;

			}

			RIGHT_SQUARE12=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_scope_section483); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE12);

			// AST REWRITE
			// elements: value2, value3, value1, value4
			// token labels: 
			// rule labels: retval, value3, value4, value1, value2
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
			RewriteRuleSubtreeStream stream_value3=new RewriteRuleSubtreeStream(adaptor,"rule value3",value3!=null?value3.getTree():null);
			RewriteRuleSubtreeStream stream_value4=new RewriteRuleSubtreeStream(adaptor,"rule value4",value4!=null?value4.getTree():null);
			RewriteRuleSubtreeStream stream_value1=new RewriteRuleSubtreeStream(adaptor,"rule value1",value1!=null?value1.getTree():null);
			RewriteRuleSubtreeStream stream_value2=new RewriteRuleSubtreeStream(adaptor,"rule value2",value2!=null?value2.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 138:5: -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:138:8: ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_SCOPE, LEFT_SQUARE11, "SCOPE SECTION"), root_1);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:138:51: ( $value1)?
				if ( stream_value1.hasNext() ) {
					adaptor.addChild(root_1, stream_value1.nextTree());
				}
				stream_value1.reset();

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:138:60: ( $value2)?
				if ( stream_value2.hasNext() ) {
					adaptor.addChild(root_1, stream_value2.nextTree());
				}
				stream_value2.reset();

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:138:69: ( $value3)?
				if ( stream_value3.hasNext() ) {
					adaptor.addChild(root_1, stream_value3.nextTree());
				}
				stream_value3.reset();

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:138:78: ( $value4)?
				if ( stream_value4.hasNext() ) {
					adaptor.addChild(root_1, stream_value4.nextTree());
				}
				stream_value4.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "scope_section"


	public static class meta_section_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "meta_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:144:1: meta_section : LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) ;
	public final DSLMapParser.meta_section_return meta_section() throws RecognitionException {
		DSLMapParser.meta_section_return retval = new DSLMapParser.meta_section_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LEFT_SQUARE13=null;
		Token LITERAL14=null;
		Token RIGHT_SQUARE15=null;

		Object LEFT_SQUARE13_tree=null;
		Object LITERAL14_tree=null;
		Object RIGHT_SQUARE15_tree=null;
		RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
		RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:145:5: ( LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:145:7: LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE
			{
			LEFT_SQUARE13=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_meta_section530); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE13);

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:145:19: ( LITERAL )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==LITERAL) ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:145:19: LITERAL
					{
					LITERAL14=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_meta_section532); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LITERAL.add(LITERAL14);

					}
					break;

			}

			RIGHT_SQUARE15=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_meta_section535); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE15);

			// AST REWRITE
			// elements: LITERAL
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 146:5: -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:146:8: ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_META, LEFT_SQUARE13, "META SECTION"), root_1);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:146:48: ( LITERAL )?
				if ( stream_LITERAL.hasNext() ) {
					adaptor.addChild(root_1, stream_LITERAL.nextNode());
				}
				stream_LITERAL.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "meta_section"


	public static class key_section_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "key_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:149:1: key_section : (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
	public final DSLMapParser.key_section_return key_section() throws RecognitionException {
		DSLMapParser.key_section_return retval = new DSLMapParser.key_section_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ks =null;

		RewriteRuleSubtreeStream stream_key_sentence=new RewriteRuleSubtreeStream(adaptor,"rule key_sentence");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:150:5: ( (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:150:7: (ks= key_sentence )+
			{
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:150:9: (ks= key_sentence )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==COLON||(LA8_0 >= LEFT_CURLY && LA8_0 <= LITERAL)||LA8_0==RIGHT_SQUARE) ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:150:9: ks= key_sentence
					{
					pushFollow(FOLLOW_key_sentence_in_key_section568);
					ks=key_sentence();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_key_sentence.add(ks.getTree());
					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			// AST REWRITE
			// elements: key_sentence
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 151:5: -> ^( VT_ENTRY_KEY ( key_sentence )+ )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:151:8: ^( VT_ENTRY_KEY ( key_sentence )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY_KEY, "VT_ENTRY_KEY"), root_1);
				if ( !(stream_key_sentence.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_key_sentence.hasNext() ) {
					adaptor.addChild(root_1, stream_key_sentence.nextTree());
				}
				stream_key_sentence.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "key_section"


	public static class key_sentence_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "key_sentence"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:154:1: key_sentence : ( variable_definition |cb= key_chunk -> VT_LITERAL[$cb.start, text] );
	public final DSLMapParser.key_sentence_return key_sentence() throws RecognitionException {
		DSLMapParser.key_sentence_return retval = new DSLMapParser.key_sentence_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cb =null;
		ParserRuleReturnScope variable_definition16 =null;

		RewriteRuleSubtreeStream stream_key_chunk=new RewriteRuleSubtreeStream(adaptor,"rule key_chunk");


		        String text = "";

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:158:5: ( variable_definition |cb= key_chunk -> VT_LITERAL[$cb.start, text] )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==LEFT_CURLY) ) {
				alt9=1;
			}
			else if ( (LA9_0==COLON||(LA9_0 >= LEFT_SQUARE && LA9_0 <= LITERAL)||LA9_0==RIGHT_SQUARE) ) {
				alt9=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:158:7: variable_definition
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_variable_definition_in_key_sentence608);
					variable_definition16=variable_definition();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_definition16.getTree());

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:159:7: cb= key_chunk
					{
					pushFollow(FOLLOW_key_chunk_in_key_sentence618);
					cb=key_chunk();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_key_chunk.add(cb.getTree());
					if ( state.backtracking==0 ) { text = (cb!=null?input.toString(cb.start,cb.stop):null); }
					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 160:5: -> VT_LITERAL[$cb.start, text]
					{
						adaptor.addChild(root_0, (Object)adaptor.create(VT_LITERAL, (cb!=null?(cb.start):null), text));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "key_sentence"


	public static class key_chunk_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "key_chunk"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:163:1: key_chunk : ( literal )+ ;
	public final DSLMapParser.key_chunk_return key_chunk() throws RecognitionException {
		DSLMapParser.key_chunk_return retval = new DSLMapParser.key_chunk_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope literal17 =null;


		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:5: ( ( literal )+ )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:7: ( literal )+
			{
			root_0 = (Object)adaptor.nil();


			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:7: ( literal )+
			int cnt10=0;
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==COLON||(LA10_0 >= LEFT_SQUARE && LA10_0 <= LITERAL)||LA10_0==RIGHT_SQUARE) ) {
					int LA10_2 = input.LA(2);
					if ( (synpred12_DSLMap()) ) {
						alt10=1;
					}

				}

				switch (alt10) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:7: literal
					{
					pushFollow(FOLLOW_literal_in_key_chunk646);
					literal17=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literal17.getTree());

					}
					break;

				default :
					if ( cnt10 >= 1 ) break loop10;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(10, input);
					throw eee;
				}
				cnt10++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "key_chunk"


	public static class value_section_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "value_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:167:1: value_section : ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
	public final DSLMapParser.value_section_return value_section() throws RecognitionException {
		DSLMapParser.value_section_return retval = new DSLMapParser.value_section_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope value_sentence18 =null;

		RewriteRuleSubtreeStream stream_value_sentence=new RewriteRuleSubtreeStream(adaptor,"rule value_sentence");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:168:5: ( ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:168:7: ( value_sentence )+
			{
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:168:7: ( value_sentence )+
			int cnt11=0;
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( ((LA11_0 >= COLON && LA11_0 <= DOT)||LA11_0==EQUALS||(LA11_0 >= LEFT_CURLY && LA11_0 <= LITERAL)||LA11_0==RIGHT_SQUARE) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:168:7: value_sentence
					{
					pushFollow(FOLLOW_value_sentence_in_value_section664);
					value_sentence18=value_sentence();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_value_sentence.add(value_sentence18.getTree());
					}
					break;

				default :
					if ( cnt11 >= 1 ) break loop11;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(11, input);
					throw eee;
				}
				cnt11++;
			}

			// AST REWRITE
			// elements: value_sentence
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 169:5: -> ^( VT_ENTRY_VAL ( value_sentence )+ )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:169:8: ^( VT_ENTRY_VAL ( value_sentence )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY_VAL, "VT_ENTRY_VAL"), root_1);
				if ( !(stream_value_sentence.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_value_sentence.hasNext() ) {
					adaptor.addChild(root_1, stream_value_sentence.nextTree());
				}
				stream_value_sentence.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "value_section"


	public static class value_sentence_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "value_sentence"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:172:1: value_sentence : ( variable_reference |vc= value_chunk -> VT_LITERAL[$vc.start, text] );
	public final DSLMapParser.value_sentence_return value_sentence() throws RecognitionException {
		DSLMapParser.value_sentence_return retval = new DSLMapParser.value_sentence_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope vc =null;
		ParserRuleReturnScope variable_reference19 =null;

		RewriteRuleSubtreeStream stream_value_chunk=new RewriteRuleSubtreeStream(adaptor,"rule value_chunk");


		        String text = "";

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:176:5: ( variable_reference |vc= value_chunk -> VT_LITERAL[$vc.start, text] )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==LEFT_CURLY) ) {
				alt12=1;
			}
			else if ( ((LA12_0 >= COLON && LA12_0 <= DOT)||LA12_0==EQUALS||(LA12_0 >= LEFT_SQUARE && LA12_0 <= LITERAL)||LA12_0==RIGHT_SQUARE) ) {
				alt12=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:176:7: variable_reference
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_variable_reference_in_value_sentence703);
					variable_reference19=variable_reference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_reference19.getTree());

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:177:7: vc= value_chunk
					{
					pushFollow(FOLLOW_value_chunk_in_value_sentence713);
					vc=value_chunk();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_value_chunk.add(vc.getTree());
					if ( state.backtracking==0 ) { text = (vc!=null?input.toString(vc.start,vc.stop):null); }
					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 178:5: -> VT_LITERAL[$vc.start, text]
					{
						adaptor.addChild(root_0, (Object)adaptor.create(VT_LITERAL, (vc!=null?(vc.start):null), text));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "value_sentence"


	public static class value_chunk_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "value_chunk"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:181:1: value_chunk : ( literal | EQUALS | COMMA | DOT )+ ;
	public final DSLMapParser.value_chunk_return value_chunk() throws RecognitionException {
		DSLMapParser.value_chunk_return retval = new DSLMapParser.value_chunk_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EQUALS21=null;
		Token COMMA22=null;
		Token DOT23=null;
		ParserRuleReturnScope literal20 =null;

		Object EQUALS21_tree=null;
		Object COMMA22_tree=null;
		Object DOT23_tree=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:5: ( ( literal | EQUALS | COMMA | DOT )+ )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:7: ( literal | EQUALS | COMMA | DOT )+
			{
			root_0 = (Object)adaptor.nil();


			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:7: ( literal | EQUALS | COMMA | DOT )+
			int cnt13=0;
			loop13:
			while (true) {
				int alt13=5;
				switch ( input.LA(1) ) {
				case COLON:
				case LEFT_SQUARE:
				case LITERAL:
				case RIGHT_SQUARE:
					{
					int LA13_2 = input.LA(2);
					if ( (synpred15_DSLMap()) ) {
						alt13=1;
					}

					}
					break;
				case EQUALS:
					{
					int LA13_3 = input.LA(2);
					if ( (synpred16_DSLMap()) ) {
						alt13=2;
					}

					}
					break;
				case COMMA:
					{
					int LA13_4 = input.LA(2);
					if ( (synpred17_DSLMap()) ) {
						alt13=3;
					}

					}
					break;
				case DOT:
					{
					int LA13_5 = input.LA(2);
					if ( (synpred18_DSLMap()) ) {
						alt13=4;
					}

					}
					break;
				}
				switch (alt13) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:8: literal
					{
					pushFollow(FOLLOW_literal_in_value_chunk742);
					literal20=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literal20.getTree());

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:16: EQUALS
					{
					EQUALS21=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_value_chunk744); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					EQUALS21_tree = (Object)adaptor.create(EQUALS21);
					adaptor.addChild(root_0, EQUALS21_tree);
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:23: COMMA
					{
					COMMA22=(Token)match(input,COMMA,FOLLOW_COMMA_in_value_chunk746); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					COMMA22_tree = (Object)adaptor.create(COMMA22);
					adaptor.addChild(root_0, COMMA22_tree);
					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:29: DOT
					{
					DOT23=(Token)match(input,DOT,FOLLOW_DOT_in_value_chunk748); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DOT23_tree = (Object)adaptor.create(DOT23);
					adaptor.addChild(root_0, DOT23_tree);
					}

					}
					break;

				default :
					if ( cnt13 >= 1 ) break loop13;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(13, input);
					throw eee;
				}
				cnt13++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "value_chunk"


	public static class literal_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "literal"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:185:1: literal : ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) ;
	public final DSLMapParser.literal_return literal() throws RecognitionException {
		DSLMapParser.literal_return retval = new DSLMapParser.literal_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set24=null;

		Object set24_tree=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:186:5: ( ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:
			{
			root_0 = (Object)adaptor.nil();


			set24=input.LT(1);
			if ( input.LA(1)==COLON||(input.LA(1) >= LEFT_SQUARE && input.LA(1) <= LITERAL)||input.LA(1)==RIGHT_SQUARE ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set24));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literal"


	public static class variable_definition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "variable_definition"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:190:1: variable_definition : lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) ;
	public final DSLMapParser.variable_definition_return variable_definition() throws RecognitionException {
		DSLMapParser.variable_definition_return retval = new DSLMapParser.variable_definition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token lc=null;
		Token name=null;
		Token q=null;
		Token rc=null;
		Token COLON25=null;
		Token COLON26=null;
		ParserRuleReturnScope pat =null;

		Object lc_tree=null;
		Object name_tree=null;
		Object q_tree=null;
		Object rc_tree=null;
		Object COLON25_tree=null;
		Object COLON26_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
		RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
		RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
		RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");


		        String text = "";
		        boolean hasSpaceBefore = false;
		        boolean hasSpaceAfter = false;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:196:5: (lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:196:7: lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY
			{
			lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition809); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

			if ( state.backtracking==0 ) {
			        CommonToken back2 =  (CommonToken)input.LT(-2);
			        if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true;
			        }
			name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition827); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LITERAL.add(name);

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:18: ( ( COLON q= LITERAL )? COLON pat= pattern )?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==COLON) ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:20: ( COLON q= LITERAL )? COLON pat= pattern
					{
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:20: ( COLON q= LITERAL )?
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==COLON) ) {
						int LA14_1 = input.LA(2);
						if ( (LA14_1==LITERAL) ) {
							int LA14_2 = input.LA(3);
							if ( (LA14_2==COLON) ) {
								int LA14_4 = input.LA(4);
								if ( (synpred22_DSLMap()) ) {
									alt14=1;
								}
							}
						}
					}
					switch (alt14) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:21: COLON q= LITERAL
							{
							COLON25=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition832); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COLON.add(COLON25);

							q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition836); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LITERAL.add(q);

							}
							break;

					}

					COLON26=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition840); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON26);

					pushFollow(FOLLOW_pattern_in_variable_definition844);
					pat=pattern();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_pattern.add(pat.getTree());
					if ( state.backtracking==0 ) {text = (pat!=null?input.toString(pat.start,pat.stop):null);}
					}
					break;

			}

			rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition853); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(rc);

			if ( state.backtracking==0 ) {
			      CommonToken rc1 = (CommonToken)input.LT(1);
			      if(!"=".equals(rc1.getText()) && ((CommonToken)rc).getStopIndex() < rc1.getStartIndex() - 1) hasSpaceAfter = true;
			      isIdentifier( name );
			    }
			// AST REWRITE
			// elements: name, name, q, q, name, name, q, q, name, name, name, q, name, q, q, q, q, name
			// token labels: q, name
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleTokenStream stream_q=new RewriteRuleTokenStream(adaptor,"token q",q);
			RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 208:5: -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
			if ( hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:208:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:208:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:208:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?(pat.start):null), text));
				adaptor.addChild(root_0, root_1);
				}

			}

			else // 209:5: -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
			if (!hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:209:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:209:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:209:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?(pat.start):null), text));
				adaptor.addChild(root_0, root_1);
				}

			}

			else // 210:5: -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
			if ( hasSpaceBefore                     && !hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:210:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:210:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:210:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}

			else // 211:5: -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
			if (!hasSpaceBefore                     && !hasSpaceAfter) {
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:211:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:211:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:211:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}

			else // 212:5: -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
			if ( hasSpaceBefore && !"".equals(text) &&  hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:212:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:212:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:212:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?(pat.start):null), text));
				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 213:5: -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
			if (!hasSpaceBefore && !"".equals(text) &&  hasSpaceAfter) {
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:213:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:213:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:213:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?(pat.start):null), text));
				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 214:5: -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
			if ( hasSpaceBefore &&                      hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:214:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:214:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:214:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 215:5: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
			if (!hasSpaceBefore &&                      hasSpaceAfter) {
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:215:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:215:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:215:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 216:5: -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:216:74: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);
				adaptor.addChild(root_1, stream_name.nextNode());
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:216:93: ^( VT_QUAL ( $q)? )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:216:104: ( $q)?
				if ( stream_q.hasNext() ) {
					adaptor.addChild(root_2, stream_q.nextNode());
				}
				stream_q.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "variable_definition"


	public static class pattern_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "pattern"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:219:1: pattern : ( literal | DOT | MISC | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ ;
	public final DSLMapParser.pattern_return pattern() throws RecognitionException {
		DSLMapParser.pattern_return retval = new DSLMapParser.pattern_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DOT28=null;
		Token MISC29=null;
		Token LEFT_CURLY30=null;
		Token RIGHT_CURLY32=null;
		Token LEFT_SQUARE33=null;
		Token RIGHT_SQUARE35=null;
		ParserRuleReturnScope literal27 =null;
		ParserRuleReturnScope literal31 =null;
		ParserRuleReturnScope pattern34 =null;

		Object DOT28_tree=null;
		Object MISC29_tree=null;
		Object LEFT_CURLY30_tree=null;
		Object RIGHT_CURLY32_tree=null;
		Object LEFT_SQUARE33_tree=null;
		Object RIGHT_SQUARE35_tree=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:9: ( ( literal | DOT | MISC | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:11: ( literal | DOT | MISC | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
			{
			root_0 = (Object)adaptor.nil();


			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:11: ( literal | DOT | MISC | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
			int cnt16=0;
			loop16:
			while (true) {
				int alt16=6;
				switch ( input.LA(1) ) {
				case RIGHT_SQUARE:
					{
					int LA16_2 = input.LA(2);
					if ( (synpred24_DSLMap()) ) {
						alt16=1;
					}

					}
					break;
				case LEFT_SQUARE:
					{
					int LA16_3 = input.LA(2);
					if ( (synpred24_DSLMap()) ) {
						alt16=1;
					}
					else if ( (synpred28_DSLMap()) ) {
						alt16=5;
					}

					}
					break;
				case DOT:
					{
					alt16=2;
					}
					break;
				case MISC:
					{
					alt16=3;
					}
					break;
				case LEFT_CURLY:
					{
					alt16=4;
					}
					break;
				case COLON:
				case LITERAL:
					{
					alt16=1;
					}
					break;
				}
				switch (alt16) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:13: literal
					{
					pushFollow(FOLLOW_literal_in_pattern1290);
					literal27=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literal27.getTree());

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:221:13: DOT
					{
					DOT28=(Token)match(input,DOT,FOLLOW_DOT_in_pattern1304); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DOT28_tree = (Object)adaptor.create(DOT28);
					adaptor.addChild(root_0, DOT28_tree);
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:222:13: MISC
					{
					MISC29=(Token)match(input,MISC,FOLLOW_MISC_in_pattern1318); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					MISC29_tree = (Object)adaptor.create(MISC29);
					adaptor.addChild(root_0, MISC29_tree);
					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:223:13: LEFT_CURLY literal RIGHT_CURLY
					{
					LEFT_CURLY30=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_pattern1332); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					LEFT_CURLY30_tree = (Object)adaptor.create(LEFT_CURLY30);
					adaptor.addChild(root_0, LEFT_CURLY30_tree);
					}

					pushFollow(FOLLOW_literal_in_pattern1334);
					literal31=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literal31.getTree());

					RIGHT_CURLY32=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_pattern1336); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					RIGHT_CURLY32_tree = (Object)adaptor.create(RIGHT_CURLY32);
					adaptor.addChild(root_0, RIGHT_CURLY32_tree);
					}

					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:224:13: LEFT_SQUARE pattern RIGHT_SQUARE
					{
					LEFT_SQUARE33=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern1350); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					LEFT_SQUARE33_tree = (Object)adaptor.create(LEFT_SQUARE33);
					adaptor.addChild(root_0, LEFT_SQUARE33_tree);
					}

					pushFollow(FOLLOW_pattern_in_pattern1352);
					pattern34=pattern();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern34.getTree());

					RIGHT_SQUARE35=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern1354); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					RIGHT_SQUARE35_tree = (Object)adaptor.create(RIGHT_SQUARE35);
					adaptor.addChild(root_0, RIGHT_SQUARE35_tree);
					}

					}
					break;

				default :
					if ( cnt16 >= 1 ) break loop16;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(16, input);
					throw eee;
				}
				cnt16++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "pattern"


	public static class variable_reference_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "variable_reference"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:228:1: variable_reference : lc= LEFT_CURLY name= variable_reference_expr rc= RIGHT_CURLY -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE -> ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) ;
	public final DSLMapParser.variable_reference_return variable_reference() throws RecognitionException {
		DSLMapParser.variable_reference_return retval = new DSLMapParser.variable_reference_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token lc=null;
		Token rc=null;
		ParserRuleReturnScope name =null;

		Object lc_tree=null;
		Object rc_tree=null;
		RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
		RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
		RewriteRuleSubtreeStream stream_variable_reference_expr=new RewriteRuleSubtreeStream(adaptor,"rule variable_reference_expr");


		        boolean hasSpaceBefore = false;
		        boolean hasSpaceAfter = false;
		        String text = "";

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:234:5: (lc= LEFT_CURLY name= variable_reference_expr rc= RIGHT_CURLY -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE -> ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:234:7: lc= LEFT_CURLY name= variable_reference_expr rc= RIGHT_CURLY
			{
			lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference1389); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

			if ( state.backtracking==0 ) {
			        CommonToken back2 =  (CommonToken)input.LT(-2);
			        if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true;
			        }
			pushFollow(FOLLOW_variable_reference_expr_in_variable_reference1409);
			name=variable_reference_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_variable_reference_expr.add(name.getTree());
			rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference1413); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(rc);

			if ( state.backtracking==0 ) {if(((CommonToken)rc).getStopIndex() < ((CommonToken)input.LT(1)).getStartIndex() - 1) hasSpaceAfter = true;}
			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 241:5: -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE
			if ( hasSpaceBefore &&  hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:241:54: ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(LITERAL, (name!=null?(name.start):null), (name!=null?input.toString(name.start,name.stop):null)));
				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 242:5: -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
			if ( hasSpaceBefore && !hasSpaceAfter) {
				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:242:54: ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(LITERAL, (name!=null?(name.start):null), (name!=null?input.toString(name.start,name.stop):null)));
				adaptor.addChild(root_0, root_1);
				}

			}

			else // 243:5: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF LITERAL[$name.start,$name.text] ) VT_SPACE
			if (!hasSpaceBefore &&  hasSpaceAfter) {
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:243:54: ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(LITERAL, (name!=null?(name.start):null), (name!=null?input.toString(name.start,name.stop):null)));
				adaptor.addChild(root_0, root_1);
				}

				adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
			}

			else // 244:5: -> ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
			{
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:244:54: ^( VT_VAR_REF LITERAL[$name.start,$name.text] )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(LITERAL, (name!=null?(name.start):null), (name!=null?input.toString(name.start,name.stop):null)));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "variable_reference"


	public static class variable_reference_expr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "variable_reference_expr"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:247:1: variable_reference_expr : ( LITERAL | EQUALS )+ ;
	public final DSLMapParser.variable_reference_expr_return variable_reference_expr() throws RecognitionException {
		DSLMapParser.variable_reference_expr_return retval = new DSLMapParser.variable_reference_expr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set36=null;

		Object set36_tree=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:248:5: ( ( LITERAL | EQUALS )+ )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:248:8: ( LITERAL | EQUALS )+
			{
			root_0 = (Object)adaptor.nil();


			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:248:8: ( LITERAL | EQUALS )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==EQUALS||LA17_0==LITERAL) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:
					{
					set36=input.LT(1);
					if ( input.LA(1)==EQUALS||input.LA(1)==LITERAL ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set36));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt17 >= 1 ) break loop17;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(17, input);
					throw eee;
				}
				cnt17++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "variable_reference_expr"


	public static class condition_key_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "condition_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:251:1: condition_key :{...}?value= LITERAL -> VT_CONDITION[$value] ;
	public final DSLMapParser.condition_key_return condition_key() throws RecognitionException {
		DSLMapParser.condition_key_return retval = new DSLMapParser.condition_key_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token value=null;

		Object value_tree=null;
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:252:5: ({...}?value= LITERAL -> VT_CONDITION[$value] )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:252:7: {...}?value= LITERAL
			{
			if ( !((validateIdentifierKey("condition")||validateIdentifierKey("when"))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "condition_key", "validateIdentifierKey(\"condition\")||validateIdentifierKey(\"when\")");
			}
			value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_condition_key1599); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LITERAL.add(value);

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 253:5: -> VT_CONDITION[$value]
			{
				adaptor.addChild(root_0, (Object)adaptor.create(VT_CONDITION, value));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "condition_key"


	public static class consequence_key_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "consequence_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:256:1: consequence_key :{...}?value= LITERAL -> VT_CONSEQUENCE[$value] ;
	public final DSLMapParser.consequence_key_return consequence_key() throws RecognitionException {
		DSLMapParser.consequence_key_return retval = new DSLMapParser.consequence_key_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token value=null;

		Object value_tree=null;
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:257:5: ({...}?value= LITERAL -> VT_CONSEQUENCE[$value] )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:257:7: {...}?value= LITERAL
			{
			if ( !((validateIdentifierKey("consequence")||validateIdentifierKey("then"))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "consequence_key", "validateIdentifierKey(\"consequence\")||validateIdentifierKey(\"then\")");
			}
			value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_consequence_key1631); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LITERAL.add(value);

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 258:5: -> VT_CONSEQUENCE[$value]
			{
				adaptor.addChild(root_0, (Object)adaptor.create(VT_CONSEQUENCE, value));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "consequence_key"


	public static class keyword_key_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "keyword_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:261:1: keyword_key :{...}?value= LITERAL -> VT_KEYWORD[$value] ;
	public final DSLMapParser.keyword_key_return keyword_key() throws RecognitionException {
		DSLMapParser.keyword_key_return retval = new DSLMapParser.keyword_key_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token value=null;

		Object value_tree=null;
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:262:5: ({...}?value= LITERAL -> VT_KEYWORD[$value] )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:262:7: {...}?value= LITERAL
			{
			if ( !((validateIdentifierKey("keyword"))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "keyword_key", "validateIdentifierKey(\"keyword\")");
			}
			value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_keyword_key1663); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LITERAL.add(value);

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 263:5: -> VT_KEYWORD[$value]
			{
				adaptor.addChild(root_0, (Object)adaptor.create(VT_KEYWORD, value));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "keyword_key"


	public static class any_key_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "any_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:266:1: any_key :{...}?value= LITERAL -> VT_ANY[$value] ;
	public final DSLMapParser.any_key_return any_key() throws RecognitionException {
		DSLMapParser.any_key_return retval = new DSLMapParser.any_key_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token value=null;

		Object value_tree=null;
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:267:5: ({...}?value= LITERAL -> VT_ANY[$value] )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:267:7: {...}?value= LITERAL
			{
			if ( !((validateIdentifierKey("*"))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "any_key", "validateIdentifierKey(\"*\")");
			}
			value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_any_key1695); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LITERAL.add(value);

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 268:5: -> VT_ANY[$value]
			{
				adaptor.addChild(root_0, (Object)adaptor.create(VT_ANY, value));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "any_key"

	// $ANTLR start synpred3_DSLMap
	public final void synpred3_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:24: ( meta_section )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:119:24: meta_section
		{
		pushFollow(FOLLOW_meta_section_in_synpred3_DSLMap338);
		meta_section();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred3_DSLMap

	// $ANTLR start synpred6_DSLMap
	public final void synpred6_DSLMap_fragment() throws RecognitionException {
		ParserRuleReturnScope value1 =null;


		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:132:10: (value1= condition_key )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:132:10: value1= condition_key
		{
		pushFollow(FOLLOW_condition_key_in_synpred6_DSLMap425);
		value1=condition_key();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred6_DSLMap

	// $ANTLR start synpred7_DSLMap
	public final void synpred7_DSLMap_fragment() throws RecognitionException {
		ParserRuleReturnScope value2 =null;


		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:133:11: (value2= consequence_key )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:133:11: value2= consequence_key
		{
		pushFollow(FOLLOW_consequence_key_in_synpred7_DSLMap439);
		value2=consequence_key();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred7_DSLMap

	// $ANTLR start synpred8_DSLMap
	public final void synpred8_DSLMap_fragment() throws RecognitionException {
		ParserRuleReturnScope value3 =null;


		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:134:11: (value3= keyword_key )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:134:11: value3= keyword_key
		{
		pushFollow(FOLLOW_keyword_key_in_synpred8_DSLMap453);
		value3=keyword_key();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred8_DSLMap

	// $ANTLR start synpred12_DSLMap
	public final void synpred12_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:7: ( literal )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:164:7: literal
		{
		pushFollow(FOLLOW_literal_in_synpred12_DSLMap646);
		literal();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred12_DSLMap

	// $ANTLR start synpred15_DSLMap
	public final void synpred15_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:8: ( literal )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:8: literal
		{
		pushFollow(FOLLOW_literal_in_synpred15_DSLMap742);
		literal();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred15_DSLMap

	// $ANTLR start synpred16_DSLMap
	public final void synpred16_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:16: ( EQUALS )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:16: EQUALS
		{
		match(input,EQUALS,FOLLOW_EQUALS_in_synpred16_DSLMap744); if (state.failed) return;

		}

	}
	// $ANTLR end synpred16_DSLMap

	// $ANTLR start synpred17_DSLMap
	public final void synpred17_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:23: ( COMMA )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:23: COMMA
		{
		match(input,COMMA,FOLLOW_COMMA_in_synpred17_DSLMap746); if (state.failed) return;

		}

	}
	// $ANTLR end synpred17_DSLMap

	// $ANTLR start synpred18_DSLMap
	public final void synpred18_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:29: ( DOT )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:182:29: DOT
		{
		match(input,DOT,FOLLOW_DOT_in_synpred18_DSLMap748); if (state.failed) return;

		}

	}
	// $ANTLR end synpred18_DSLMap

	// $ANTLR start synpred22_DSLMap
	public final void synpred22_DSLMap_fragment() throws RecognitionException {
		Token q=null;


		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:21: ( COLON q= LITERAL )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:201:21: COLON q= LITERAL
		{
		match(input,COLON,FOLLOW_COLON_in_synpred22_DSLMap832); if (state.failed) return;

		q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_synpred22_DSLMap836); if (state.failed) return;

		}

	}
	// $ANTLR end synpred22_DSLMap

	// $ANTLR start synpred24_DSLMap
	public final void synpred24_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:13: ( literal )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:220:13: literal
		{
		pushFollow(FOLLOW_literal_in_synpred24_DSLMap1290);
		literal();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred24_DSLMap

	// $ANTLR start synpred28_DSLMap
	public final void synpred28_DSLMap_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:224:13: ( LEFT_SQUARE pattern RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/dsl/DSLMap.g:224:13: LEFT_SQUARE pattern RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred28_DSLMap1350); if (state.failed) return;

		pushFollow(FOLLOW_pattern_in_synpred28_DSLMap1352);
		pattern();
		state._fsp--;
		if (state.failed) return;

		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred28_DSLMap1354); if (state.failed) return;

		}

	}
	// $ANTLR end synpred28_DSLMap

	// Delegated rules

	public final boolean synpred3_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred8_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred8_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred28_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred28_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred15_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred15_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred18_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred18_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred17_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred17_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred22_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred22_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred16_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred16_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred24_DSLMap() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred24_DSLMap_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_statement_in_mapping_file275 = new BitSet(new long[]{0x0000000000001082L});
	public static final BitSet FOLLOW_entry_in_statement306 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EOL_in_statement314 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_scope_section_in_entry336 = new BitSet(new long[]{0x0000000000013810L});
	public static final BitSet FOLLOW_meta_section_in_entry338 = new BitSet(new long[]{0x0000000000013810L});
	public static final BitSet FOLLOW_key_section_in_entry341 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_EQUALS_in_entry343 = new BitSet(new long[]{0x00000000000139F0L});
	public static final BitSet FOLLOW_value_section_in_entry345 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_EOL_in_entry349 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EOF_in_entry351 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_scope_section412 = new BitSet(new long[]{0x0000000000002000L});
	public static final BitSet FOLLOW_condition_key_in_scope_section425 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_consequence_key_in_scope_section439 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_keyword_key_in_scope_section453 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_any_key_in_scope_section467 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_scope_section483 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_meta_section530 = new BitSet(new long[]{0x0000000000012000L});
	public static final BitSet FOLLOW_LITERAL_in_meta_section532 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_meta_section535 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_key_sentence_in_key_section568 = new BitSet(new long[]{0x0000000000013812L});
	public static final BitSet FOLLOW_variable_definition_in_key_sentence608 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_key_chunk_in_key_sentence618 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_key_chunk646 = new BitSet(new long[]{0x0000000000013012L});
	public static final BitSet FOLLOW_value_sentence_in_value_section664 = new BitSet(new long[]{0x0000000000013972L});
	public static final BitSet FOLLOW_variable_reference_in_value_sentence703 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_value_chunk_in_value_sentence713 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_value_chunk742 = new BitSet(new long[]{0x0000000000013172L});
	public static final BitSet FOLLOW_EQUALS_in_value_chunk744 = new BitSet(new long[]{0x0000000000013172L});
	public static final BitSet FOLLOW_COMMA_in_value_chunk746 = new BitSet(new long[]{0x0000000000013172L});
	public static final BitSet FOLLOW_DOT_in_value_chunk748 = new BitSet(new long[]{0x0000000000013172L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition809 = new BitSet(new long[]{0x0000000000002000L});
	public static final BitSet FOLLOW_LITERAL_in_variable_definition827 = new BitSet(new long[]{0x0000000000008010L});
	public static final BitSet FOLLOW_COLON_in_variable_definition832 = new BitSet(new long[]{0x0000000000002000L});
	public static final BitSet FOLLOW_LITERAL_in_variable_definition836 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_COLON_in_variable_definition840 = new BitSet(new long[]{0x0000000000017850L});
	public static final BitSet FOLLOW_pattern_in_variable_definition844 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition853 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_pattern1290 = new BitSet(new long[]{0x0000000000017852L});
	public static final BitSet FOLLOW_DOT_in_pattern1304 = new BitSet(new long[]{0x0000000000017852L});
	public static final BitSet FOLLOW_MISC_in_pattern1318 = new BitSet(new long[]{0x0000000000017852L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_pattern1332 = new BitSet(new long[]{0x0000000000013010L});
	public static final BitSet FOLLOW_literal_in_pattern1334 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_pattern1336 = new BitSet(new long[]{0x0000000000017852L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern1350 = new BitSet(new long[]{0x0000000000017850L});
	public static final BitSet FOLLOW_pattern_in_pattern1352 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern1354 = new BitSet(new long[]{0x0000000000017852L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference1389 = new BitSet(new long[]{0x0000000000002100L});
	public static final BitSet FOLLOW_variable_reference_expr_in_variable_reference1409 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference1413 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LITERAL_in_condition_key1599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LITERAL_in_consequence_key1631 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LITERAL_in_keyword_key1663 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LITERAL_in_any_key1695 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_meta_section_in_synpred3_DSLMap338 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condition_key_in_synpred6_DSLMap425 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_consequence_key_in_synpred7_DSLMap439 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_keyword_key_in_synpred8_DSLMap453 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred12_DSLMap646 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred15_DSLMap742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_synpred16_DSLMap744 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COMMA_in_synpred17_DSLMap746 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred18_DSLMap748 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COLON_in_synpred22_DSLMap832 = new BitSet(new long[]{0x0000000000002000L});
	public static final BitSet FOLLOW_LITERAL_in_synpred22_DSLMap836 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred24_DSLMap1290 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred28_DSLMap1350 = new BitSet(new long[]{0x0000000000017850L});
	public static final BitSet FOLLOW_pattern_in_synpred28_DSLMap1352 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred28_DSLMap1354 = new BitSet(new long[]{0x0000000000000002L});
}
