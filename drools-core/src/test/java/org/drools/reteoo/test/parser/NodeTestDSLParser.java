/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $ANTLR 3.1.1 src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g 2010-01-26 19:56:36

	package org.drools.reteoo.test.parser;
	
	import java.util.Map;
	import java.util.HashMap;
	import java.util.Stack;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class NodeTestDSLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_TEST_CASE", "VT_PARAMS", "VT_QUALIFIED_ID", "VT_SYMBOL", "VT_CHUNK", "VK_TEST_CASE", "VK_IMPORT", "VK_SETUP", "VK_TEARDOWN", "VK_TEST", "STRING", "SEMI_COLON", "ID", "COLON", "COMMA", "INT", "FLOAT", "MISC", "STAR", "DOT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_PAREN", "RIGHT_PAREN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart"
    };
    public static final int COMMA=18;
    public static final int LEFT_PAREN=26;
    public static final int IdentifierPart=38;
    public static final int HexDigit=31;
    public static final int WS=29;
    public static final int VT_QUALIFIED_ID=6;
    public static final int MISC=21;
    public static final int STRING=14;
    public static final int FLOAT=20;
    public static final int VT_PARAMS=5;
    public static final int VT_CHUNK=8;
    public static final int DOT=23;
    public static final int VK_IMPORT=10;
    public static final int UnicodeEscape=32;
    public static final int IdentifierStart=37;
    public static final int VT_SYMBOL=7;
    public static final int EscapeSequence=30;
    public static final int VK_TEARDOWN=12;
    public static final int SEMI_COLON=15;
    public static final int INT=19;
    public static final int VT_TEST_CASE=4;
    public static final int EOF=-1;
    public static final int VK_SETUP=11;
    public static final int EOL=28;
    public static final int VK_TEST_CASE=9;
    public static final int LEFT_SQUARE=24;
    public static final int COLON=17;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int OctalEscape=33;
    public static final int MULTI_LINE_COMMENT=36;
    public static final int VK_TEST=13;
    public static final int STAR=22;
    public static final int RIGHT_PAREN=27;
    public static final int RIGHT_SQUARE=25;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int ID=16;

    // delegates
    // delegators


        public NodeTestDSLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public NodeTestDSLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return NodeTestDSLParser.tokenNames; }
    public String getGrammarFileName() { return "src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g"; }


    	private Stack<Map<DroolsParaphraseTypes, String>> paraphrases = new Stack<Map<DroolsParaphraseTypes, String>>();
    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
            

    	private boolean validateIdentifierKey(String text) {
    		return validateLT(1, text);
    	}
    	
    	private boolean validateLT(int LTNumber, String text) {
    		String text2Validate = retrieveLT( LTNumber );
    		return text2Validate != null && text2Validate.equalsIgnoreCase(text);
    	}
    	
    	private String retrieveLT(int LTNumber) {
          		if (null == input)
    			return null;
    		if (null == input.LT(LTNumber))
    			return null;
    		if (null == input.LT(LTNumber).getText())
    			return null;
    	
    		return input.LT(LTNumber).getText();
    	}
    	
    	public void reportError(RecognitionException ex) {
    		// if we've already reported an error and have not matched a token
    		// yet successfully, don't report any errors.
    		if (state.errorRecovery) {
    			return;
    		}
    		state.errorRecovery = true;
    	
    		errors.add(errorMessageFactory.createDroolsException(ex));
    	}
    	
    	// return the raw RecognitionException errors 
    	public List<DroolsParserException> getErrors() {
    		return errors;
    	}
    	
    	// Return a list of pretty strings summarising the errors 
    	public List<String> getErrorMessages() {
    		List<String> messages = new ArrayList<String>(errors.size());
    	
    		for (DroolsParserException activeException : errors) {
    			messages.add(activeException.getMessage());
    		}
    	
    		return messages;
    	}
    	
    	// return true if any parser errors were accumulated 
    	public boolean hasErrors() {
    		return !errors.isEmpty();
    	}
    	
    	// Method that adds a paraphrase type into paraphrases stack.
    	private void pushParaphrases(DroolsParaphraseTypes type) {
    		Map<DroolsParaphraseTypes, String> activeMap = new HashMap<DroolsParaphraseTypes, String>();
    		activeMap.put(type, "");
    		paraphrases.push(activeMap);
    	}

    	// Method that sets paraphrase value for a type into paraphrases stack.
    	private void setParaphrasesValue(DroolsParaphraseTypes type, String value) {
    		paraphrases.peek().put(type, value);
    	}
    	



    public static class compilation_unit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilation_unit"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:110:1: compilation_unit : test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* EOF -> ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* ) ;
    public final NodeTestDSLParser.compilation_unit_return compilation_unit() throws RecognitionException {
        NodeTestDSLParser.compilation_unit_return retval = new NodeTestDSLParser.compilation_unit_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF6=null;
        NodeTestDSLParser.test_case_statement_return test_case_statement1 = null;

        NodeTestDSLParser.import_statement_return import_statement2 = null;

        NodeTestDSLParser.setup_return setup3 = null;

        NodeTestDSLParser.teardown_return teardown4 = null;

        NodeTestDSLParser.test_return test5 = null;


        Object EOF6_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_setup=new RewriteRuleSubtreeStream(adaptor,"rule setup");
        RewriteRuleSubtreeStream stream_test_case_statement=new RewriteRuleSubtreeStream(adaptor,"rule test_case_statement");
        RewriteRuleSubtreeStream stream_import_statement=new RewriteRuleSubtreeStream(adaptor,"rule import_statement");
        RewriteRuleSubtreeStream stream_test=new RewriteRuleSubtreeStream(adaptor,"rule test");
        RewriteRuleSubtreeStream stream_teardown=new RewriteRuleSubtreeStream(adaptor,"rule teardown");
        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:111:2: ( test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* EOF -> ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:111:4: test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* EOF
            {
            pushFollow(FOLLOW_test_case_statement_in_compilation_unit107);
            test_case_statement1=test_case_statement();

            state._fsp--;

            stream_test_case_statement.add(test_case_statement1.getTree());
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:112:3: ( import_statement )*
            loop1:
            do {
                int alt1=2;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:112:3: import_statement
            	    {
            	    pushFollow(FOLLOW_import_statement_in_compilation_unit111);
            	    import_statement2=import_statement();

            	    state._fsp--;

            	    stream_import_statement.add(import_statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:113:3: ( setup )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==ID) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("Test")))||((validateIdentifierKey("TearDown")))))) {
                int LA2_1 = input.LA(2);

                if ( (((validateIdentifierKey("Setup")))) ) {
                    alt2=1;
                }
            }
            switch (alt2) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:113:3: setup
                    {
                    pushFollow(FOLLOW_setup_in_compilation_unit116);
                    setup3=setup();

                    state._fsp--;

                    stream_setup.add(setup3.getTree());

                    }
                    break;

            }

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:114:3: ( teardown )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==ID) && ((((validateIdentifierKey("Test")))||((validateIdentifierKey("TearDown")))))) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==EOF||LA3_1==ID) && (((validateIdentifierKey("TearDown"))))) {
                    alt3=1;
                }
            }
            switch (alt3) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:114:3: teardown
                    {
                    pushFollow(FOLLOW_teardown_in_compilation_unit121);
                    teardown4=teardown();

                    state._fsp--;

                    stream_teardown.add(teardown4.getTree());

                    }
                    break;

            }

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:115:3: ( test )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ID) && (((validateIdentifierKey("Test"))))) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:115:3: test
            	    {
            	    pushFollow(FOLLOW_test_in_compilation_unit126);
            	    test5=test();

            	    state._fsp--;

            	    stream_test.add(test5.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_compilation_unit131);  
            stream_EOF.add(EOF6);



            // AST REWRITE
            // elements: setup, test, import_statement, teardown, test_case_statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 117:3: -> ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* )
            {
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:117:6: ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_TEST_CASE, "VT_TEST_CASE"), root_1);

                adaptor.addChild(root_1, stream_test_case_statement.nextTree());
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:117:41: ( import_statement )*
                while ( stream_import_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_import_statement.nextTree());

                }
                stream_import_statement.reset();
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:117:59: ( setup )?
                if ( stream_setup.hasNext() ) {
                    adaptor.addChild(root_1, stream_setup.nextTree());

                }
                stream_setup.reset();
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:117:66: ( teardown )?
                if ( stream_teardown.hasNext() ) {
                    adaptor.addChild(root_1, stream_teardown.nextTree());

                }
                stream_teardown.reset();
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:117:76: ( test )*
                while ( stream_test.hasNext() ) {
                    adaptor.addChild(root_1, stream_test.nextTree());

                }
                stream_test.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch ( RecognitionException e) {

            		reportError( e );
            	
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "compilation_unit"

    public static class test_case_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test_case_statement"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:123:1: test_case_statement : test_case_key name= STRING ;
    public final NodeTestDSLParser.test_case_statement_return test_case_statement() throws RecognitionException {
        NodeTestDSLParser.test_case_statement_return retval = new NodeTestDSLParser.test_case_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        NodeTestDSLParser.test_case_key_return test_case_key7 = null;


        Object name_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:124:2: ( test_case_key name= STRING )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:124:5: test_case_key name= STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_test_case_key_in_test_case_statement172);
            test_case_key7=test_case_key();

            state._fsp--;

            root_0 = (Object)adaptor.becomeRoot(test_case_key7.getTree(), root_0);
            name=(Token)match(input,STRING,FOLLOW_STRING_in_test_case_statement177); 
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test_case_statement"

    public static class import_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_statement"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:127:1: import_statement : import_key import_target SEMI_COLON ;
    public final NodeTestDSLParser.import_statement_return import_statement() throws RecognitionException {
        NodeTestDSLParser.import_statement_return retval = new NodeTestDSLParser.import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMI_COLON10=null;
        NodeTestDSLParser.import_key_return import_key8 = null;

        NodeTestDSLParser.import_target_return import_target9 = null;


        Object SEMI_COLON10_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:128:2: ( import_key import_target SEMI_COLON )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:128:4: import_key import_target SEMI_COLON
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_import_key_in_import_statement190);
            import_key8=import_key();

            state._fsp--;

            root_0 = (Object)adaptor.becomeRoot(import_key8.getTree(), root_0);
            pushFollow(FOLLOW_import_target_in_import_statement193);
            import_target9=import_target();

            state._fsp--;

            adaptor.addChild(root_0, import_target9.getTree());
            SEMI_COLON10=(Token)match(input,SEMI_COLON,FOLLOW_SEMI_COLON_in_import_statement195); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_statement"

    public static class setup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setup"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:131:1: setup : setup_key ( step )* ;
    public final NodeTestDSLParser.setup_return setup() throws RecognitionException {
        NodeTestDSLParser.setup_return retval = new NodeTestDSLParser.setup_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.setup_key_return setup_key11 = null;

        NodeTestDSLParser.step_return step12 = null;



        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:132:2: ( setup_key ( step )* )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:132:4: setup_key ( step )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_setup_key_in_setup209);
            setup_key11=setup_key();

            state._fsp--;

            root_0 = (Object)adaptor.becomeRoot(setup_key11.getTree(), root_0);
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:132:15: ( step )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ID) ) {
                    int LA5_1 = input.LA(2);

                    if ( (LA5_1==COLON) ) {
                        alt5=1;
                    }


                }


                switch (alt5) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:132:15: step
            	    {
            	    pushFollow(FOLLOW_step_in_setup212);
            	    step12=step();

            	    state._fsp--;

            	    adaptor.addChild(root_0, step12.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "setup"

    public static class teardown_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "teardown"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:135:1: teardown : teardown_key ( step )* ;
    public final NodeTestDSLParser.teardown_return teardown() throws RecognitionException {
        NodeTestDSLParser.teardown_return retval = new NodeTestDSLParser.teardown_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.teardown_key_return teardown_key13 = null;

        NodeTestDSLParser.step_return step14 = null;



        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:136:2: ( teardown_key ( step )* )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:136:4: teardown_key ( step )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_teardown_key_in_teardown225);
            teardown_key13=teardown_key();

            state._fsp--;

            root_0 = (Object)adaptor.becomeRoot(teardown_key13.getTree(), root_0);
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:136:18: ( step )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ID) ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1==COLON) ) {
                        alt6=1;
                    }


                }


                switch (alt6) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:136:18: step
            	    {
            	    pushFollow(FOLLOW_step_in_teardown228);
            	    step14=step();

            	    state._fsp--;

            	    adaptor.addChild(root_0, step14.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "teardown"

    public static class test_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:139:1: test : test_key name= STRING ( step )* ;
    public final NodeTestDSLParser.test_return test() throws RecognitionException {
        NodeTestDSLParser.test_return retval = new NodeTestDSLParser.test_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        NodeTestDSLParser.test_key_return test_key15 = null;

        NodeTestDSLParser.step_return step16 = null;


        Object name_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:140:2: ( test_key name= STRING ( step )* )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:140:4: test_key name= STRING ( step )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_test_key_in_test242);
            test_key15=test_key();

            state._fsp--;

            root_0 = (Object)adaptor.becomeRoot(test_key15.getTree(), root_0);
            name=(Token)match(input,STRING,FOLLOW_STRING_in_test247); 
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:140:26: ( step )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ID) ) {
                    int LA7_2 = input.LA(2);

                    if ( (LA7_2==COLON) ) {
                        alt7=1;
                    }


                }


                switch (alt7) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:140:26: step
            	    {
            	    pushFollow(FOLLOW_step_in_test249);
            	    step16=step();

            	    state._fsp--;

            	    adaptor.addChild(root_0, step16.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test"

    public static class step_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "step"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:143:1: step : ID COLON ( params SEMI_COLON )+ ;
    public final NodeTestDSLParser.step_return step() throws RecognitionException {
        NodeTestDSLParser.step_return retval = new NodeTestDSLParser.step_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID17=null;
        Token COLON18=null;
        Token SEMI_COLON20=null;
        NodeTestDSLParser.params_return params19 = null;


        Object ID17_tree=null;
        Object COLON18_tree=null;
        Object SEMI_COLON20_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:144:2: ( ID COLON ( params SEMI_COLON )+ )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:144:4: ID COLON ( params SEMI_COLON )+
            {
            root_0 = (Object)adaptor.nil();

            ID17=(Token)match(input,ID,FOLLOW_ID_in_step263); 
            ID17_tree = (Object)adaptor.create(ID17);
            root_0 = (Object)adaptor.becomeRoot(ID17_tree, root_0);

            COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_step266); 
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:144:15: ( params SEMI_COLON )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                alt8 = dfa8.predict(input);
                switch (alt8) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:144:17: params SEMI_COLON
            	    {
            	    pushFollow(FOLLOW_params_in_step271);
            	    params19=params();

            	    state._fsp--;

            	    adaptor.addChild(root_0, params19.getTree());
            	    SEMI_COLON20=(Token)match(input,SEMI_COLON,FOLLOW_SEMI_COLON_in_step273); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "step"

    public static class params_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "params"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:147:1: params : param_chunk ( COMMA param_chunk )* -> ^( VT_PARAMS ( param_chunk )+ ) ;
    public final NodeTestDSLParser.params_return params() throws RecognitionException {
        NodeTestDSLParser.params_return retval = new NodeTestDSLParser.params_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA22=null;
        NodeTestDSLParser.param_chunk_return param_chunk21 = null;

        NodeTestDSLParser.param_chunk_return param_chunk23 = null;


        Object COMMA22_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_param_chunk=new RewriteRuleSubtreeStream(adaptor,"rule param_chunk");
        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:148:2: ( param_chunk ( COMMA param_chunk )* -> ^( VT_PARAMS ( param_chunk )+ ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:148:4: param_chunk ( COMMA param_chunk )*
            {
            pushFollow(FOLLOW_param_chunk_in_params290);
            param_chunk21=param_chunk();

            state._fsp--;

            stream_param_chunk.add(param_chunk21.getTree());
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:148:16: ( COMMA param_chunk )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:148:17: COMMA param_chunk
            	    {
            	    COMMA22=(Token)match(input,COMMA,FOLLOW_COMMA_in_params293);  
            	    stream_COMMA.add(COMMA22);

            	    pushFollow(FOLLOW_param_chunk_in_params295);
            	    param_chunk23=param_chunk();

            	    state._fsp--;

            	    stream_param_chunk.add(param_chunk23.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);



            // AST REWRITE
            // elements: param_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 149:2: -> ^( VT_PARAMS ( param_chunk )+ )
            {
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:149:6: ^( VT_PARAMS ( param_chunk )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PARAMS, "VT_PARAMS"), root_1);

                if ( !(stream_param_chunk.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_param_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_param_chunk.nextTree());

                }
                stream_param_chunk.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "params"

    public static class param_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "param_chunk"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:152:1: param_chunk : c= param_sequence -> VT_CHUNK[$c.text] ;
    public final NodeTestDSLParser.param_chunk_return param_chunk() throws RecognitionException {
        NodeTestDSLParser.param_chunk_return retval = new NodeTestDSLParser.param_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.param_sequence_return c = null;


        RewriteRuleSubtreeStream stream_param_sequence=new RewriteRuleSubtreeStream(adaptor,"rule param_sequence");
        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:153:2: (c= param_sequence -> VT_CHUNK[$c.text] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:153:4: c= param_sequence
            {
            pushFollow(FOLLOW_param_sequence_in_param_chunk324);
            c=param_sequence();

            state._fsp--;

            stream_param_sequence.add(c.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 154:2: -> VT_CHUNK[$c.text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_CHUNK, (c!=null?input.toString(c.start,c.stop):null)));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "param_chunk"

    public static class param_sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "param_sequence"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:157:1: param_sequence : ( param )+ ;
    public final NodeTestDSLParser.param_sequence_return param_sequence() throws RecognitionException {
        NodeTestDSLParser.param_sequence_return retval = new NodeTestDSLParser.param_sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.param_return param24 = null;



        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:158:2: ( ( param )+ )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:158:4: ( param )+
            {
            root_0 = (Object)adaptor.nil();

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:158:4: ( param )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==STRING||LA10_0==ID||(LA10_0>=INT && LA10_0<=LEFT_SQUARE)||LA10_0==LEFT_PAREN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:158:4: param
            	    {
            	    pushFollow(FOLLOW_param_in_param_sequence343);
            	    param24=param();

            	    state._fsp--;

            	    adaptor.addChild(root_0, param24.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "param_sequence"

    public static class param_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "param"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:161:1: param : ( ID | STRING | INT | FLOAT | MISC | STAR | square_chunk | paren_chunk | DOT ) ;
    public final NodeTestDSLParser.param_return param() throws RecognitionException {
        NodeTestDSLParser.param_return retval = new NodeTestDSLParser.param_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID25=null;
        Token STRING26=null;
        Token INT27=null;
        Token FLOAT28=null;
        Token MISC29=null;
        Token STAR30=null;
        Token DOT33=null;
        NodeTestDSLParser.square_chunk_return square_chunk31 = null;

        NodeTestDSLParser.paren_chunk_return paren_chunk32 = null;


        Object ID25_tree=null;
        Object STRING26_tree=null;
        Object INT27_tree=null;
        Object FLOAT28_tree=null;
        Object MISC29_tree=null;
        Object STAR30_tree=null;
        Object DOT33_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:162:2: ( ( ID | STRING | INT | FLOAT | MISC | STAR | square_chunk | paren_chunk | DOT ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:162:4: ( ID | STRING | INT | FLOAT | MISC | STAR | square_chunk | paren_chunk | DOT )
            {
            root_0 = (Object)adaptor.nil();

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:162:4: ( ID | STRING | INT | FLOAT | MISC | STAR | square_chunk | paren_chunk | DOT )
            int alt11=9;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt11=1;
                }
                break;
            case STRING:
                {
                alt11=2;
                }
                break;
            case INT:
                {
                alt11=3;
                }
                break;
            case FLOAT:
                {
                alt11=4;
                }
                break;
            case MISC:
                {
                alt11=5;
                }
                break;
            case STAR:
                {
                alt11=6;
                }
                break;
            case LEFT_SQUARE:
                {
                alt11=7;
                }
                break;
            case LEFT_PAREN:
                {
                alt11=8;
                }
                break;
            case DOT:
                {
                alt11=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:162:6: ID
                    {
                    ID25=(Token)match(input,ID,FOLLOW_ID_in_param359); 
                    ID25_tree = (Object)adaptor.create(ID25);
                    adaptor.addChild(root_0, ID25_tree);


                    }
                    break;
                case 2 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:163:5: STRING
                    {
                    STRING26=(Token)match(input,STRING,FOLLOW_STRING_in_param366); 
                    STRING26_tree = (Object)adaptor.create(STRING26);
                    adaptor.addChild(root_0, STRING26_tree);


                    }
                    break;
                case 3 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:164:5: INT
                    {
                    INT27=(Token)match(input,INT,FOLLOW_INT_in_param372); 
                    INT27_tree = (Object)adaptor.create(INT27);
                    adaptor.addChild(root_0, INT27_tree);


                    }
                    break;
                case 4 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:165:5: FLOAT
                    {
                    FLOAT28=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_param378); 
                    FLOAT28_tree = (Object)adaptor.create(FLOAT28);
                    adaptor.addChild(root_0, FLOAT28_tree);


                    }
                    break;
                case 5 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:166:5: MISC
                    {
                    MISC29=(Token)match(input,MISC,FOLLOW_MISC_in_param384); 
                    MISC29_tree = (Object)adaptor.create(MISC29);
                    adaptor.addChild(root_0, MISC29_tree);


                    }
                    break;
                case 6 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:167:5: STAR
                    {
                    STAR30=(Token)match(input,STAR,FOLLOW_STAR_in_param390); 
                    STAR30_tree = (Object)adaptor.create(STAR30);
                    adaptor.addChild(root_0, STAR30_tree);


                    }
                    break;
                case 7 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:168:5: square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_param396);
                    square_chunk31=square_chunk();

                    state._fsp--;

                    adaptor.addChild(root_0, square_chunk31.getTree());

                    }
                    break;
                case 8 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:169:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_param402);
                    paren_chunk32=paren_chunk();

                    state._fsp--;

                    adaptor.addChild(root_0, paren_chunk32.getTree());

                    }
                    break;
                case 9 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:170:5: DOT
                    {
                    DOT33=(Token)match(input,DOT,FOLLOW_DOT_in_param408); 
                    DOT33_tree = (Object)adaptor.create(DOT33);
                    adaptor.addChild(root_0, DOT33_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "param"

    public static class import_target_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_target"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:174:1: import_target : itm= import_target_matcher -> VT_QUALIFIED_ID[$itm.text] ;
    public final NodeTestDSLParser.import_target_return import_target() throws RecognitionException {
        NodeTestDSLParser.import_target_return retval = new NodeTestDSLParser.import_target_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.import_target_matcher_return itm = null;


        RewriteRuleSubtreeStream stream_import_target_matcher=new RewriteRuleSubtreeStream(adaptor,"rule import_target_matcher");
        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:175:2: (itm= import_target_matcher -> VT_QUALIFIED_ID[$itm.text] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:175:4: itm= import_target_matcher
            {
            pushFollow(FOLLOW_import_target_matcher_in_import_target428);
            itm=import_target_matcher();

            state._fsp--;

            stream_import_target_matcher.add(itm.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 176:2: -> VT_QUALIFIED_ID[$itm.text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_QUALIFIED_ID, (itm!=null?input.toString(itm.start,itm.stop):null)));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_target"

    public static class import_target_matcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_target_matcher"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:179:1: import_target_matcher : qualified_id_matcher ( DOT STAR )? ;
    public final NodeTestDSLParser.import_target_matcher_return import_target_matcher() throws RecognitionException {
        NodeTestDSLParser.import_target_matcher_return retval = new NodeTestDSLParser.import_target_matcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT35=null;
        Token STAR36=null;
        NodeTestDSLParser.qualified_id_matcher_return qualified_id_matcher34 = null;


        Object DOT35_tree=null;
        Object STAR36_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:180:2: ( qualified_id_matcher ( DOT STAR )? )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:180:4: qualified_id_matcher ( DOT STAR )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualified_id_matcher_in_import_target_matcher447);
            qualified_id_matcher34=qualified_id_matcher();

            state._fsp--;

            adaptor.addChild(root_0, qualified_id_matcher34.getTree());
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:180:25: ( DOT STAR )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==DOT) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:180:26: DOT STAR
                    {
                    DOT35=(Token)match(input,DOT,FOLLOW_DOT_in_import_target_matcher450); 
                    DOT35_tree = (Object)adaptor.create(DOT35);
                    adaptor.addChild(root_0, DOT35_tree);

                    STAR36=(Token)match(input,STAR,FOLLOW_STAR_in_import_target_matcher452); 
                    STAR36_tree = (Object)adaptor.create(STAR36);
                    adaptor.addChild(root_0, STAR36_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_target_matcher"

    public static class qualified_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualified_id"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:183:1: qualified_id : qim= qualified_id_matcher -> VT_QUALIFIED_ID[$qim.text] ;
    public final NodeTestDSLParser.qualified_id_return qualified_id() throws RecognitionException {
        NodeTestDSLParser.qualified_id_return retval = new NodeTestDSLParser.qualified_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.qualified_id_matcher_return qim = null;


        RewriteRuleSubtreeStream stream_qualified_id_matcher=new RewriteRuleSubtreeStream(adaptor,"rule qualified_id_matcher");
        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:184:2: (qim= qualified_id_matcher -> VT_QUALIFIED_ID[$qim.text] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:184:4: qim= qualified_id_matcher
            {
            pushFollow(FOLLOW_qualified_id_matcher_in_qualified_id470);
            qim=qualified_id_matcher();

            state._fsp--;

            stream_qualified_id_matcher.add(qim.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 185:2: -> VT_QUALIFIED_ID[$qim.text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_QUALIFIED_ID, (qim!=null?input.toString(qim.start,qim.stop):null)));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "qualified_id"

    public static class qualified_id_matcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualified_id_matcher"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:188:1: qualified_id_matcher : ID ( DOT ID )* ;
    public final NodeTestDSLParser.qualified_id_matcher_return qualified_id_matcher() throws RecognitionException {
        NodeTestDSLParser.qualified_id_matcher_return retval = new NodeTestDSLParser.qualified_id_matcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID37=null;
        Token DOT38=null;
        Token ID39=null;

        Object ID37_tree=null;
        Object DOT38_tree=null;
        Object ID39_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:189:2: ( ID ( DOT ID )* )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:189:4: ID ( DOT ID )*
            {
            root_0 = (Object)adaptor.nil();

            ID37=(Token)match(input,ID,FOLLOW_ID_in_qualified_id_matcher488); 
            ID37_tree = (Object)adaptor.create(ID37);
            adaptor.addChild(root_0, ID37_tree);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:189:7: ( DOT ID )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==DOT) ) {
                    int LA13_1 = input.LA(2);

                    if ( (LA13_1==ID) ) {
                        alt13=1;
                    }


                }


                switch (alt13) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:189:8: DOT ID
            	    {
            	    DOT38=(Token)match(input,DOT,FOLLOW_DOT_in_qualified_id_matcher491); 
            	    DOT38_tree = (Object)adaptor.create(DOT38);
            	    adaptor.addChild(root_0, DOT38_tree);

            	    ID39=(Token)match(input,ID,FOLLOW_ID_in_qualified_id_matcher493); 
            	    ID39_tree = (Object)adaptor.create(ID39);
            	    adaptor.addChild(root_0, ID39_tree);


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "qualified_id_matcher"

    public static class square_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "square_chunk"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:192:1: square_chunk : cm= collection_matcher ;
    public final NodeTestDSLParser.square_chunk_return square_chunk() throws RecognitionException {
        NodeTestDSLParser.square_chunk_return retval = new NodeTestDSLParser.square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.collection_matcher_return cm = null;



        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:193:2: (cm= collection_matcher )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:193:4: cm= collection_matcher
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_collection_matcher_in_square_chunk511);
            cm=collection_matcher();

            state._fsp--;

            adaptor.addChild(root_0, cm.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "square_chunk"

    public static class collection_matcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "collection_matcher"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:196:1: collection_matcher : LEFT_SQUARE ( params )? RIGHT_SQUARE ;
    public final NodeTestDSLParser.collection_matcher_return collection_matcher() throws RecognitionException {
        NodeTestDSLParser.collection_matcher_return retval = new NodeTestDSLParser.collection_matcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE40=null;
        Token RIGHT_SQUARE42=null;
        NodeTestDSLParser.params_return params41 = null;


        Object LEFT_SQUARE40_tree=null;
        Object RIGHT_SQUARE42_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:197:2: ( LEFT_SQUARE ( params )? RIGHT_SQUARE )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:197:4: LEFT_SQUARE ( params )? RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE40=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_collection_matcher524); 
            LEFT_SQUARE40_tree = (Object)adaptor.create(LEFT_SQUARE40);
            adaptor.addChild(root_0, LEFT_SQUARE40_tree);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:197:16: ( params )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==STRING||LA14_0==ID||(LA14_0>=INT && LA14_0<=LEFT_SQUARE)||LA14_0==LEFT_PAREN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:197:16: params
                    {
                    pushFollow(FOLLOW_params_in_collection_matcher526);
                    params41=params();

                    state._fsp--;

                    adaptor.addChild(root_0, params41.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE42=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_collection_matcher529); 
            RIGHT_SQUARE42_tree = (Object)adaptor.create(RIGHT_SQUARE42);
            adaptor.addChild(root_0, RIGHT_SQUARE42_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "collection_matcher"

    public static class paren_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "paren_chunk"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:200:1: paren_chunk : cm= paren_matcher ;
    public final NodeTestDSLParser.paren_chunk_return paren_chunk() throws RecognitionException {
        NodeTestDSLParser.paren_chunk_return retval = new NodeTestDSLParser.paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        NodeTestDSLParser.paren_matcher_return cm = null;



        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:201:2: (cm= paren_matcher )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:201:4: cm= paren_matcher
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_paren_matcher_in_paren_chunk544);
            cm=paren_matcher();

            state._fsp--;

            adaptor.addChild(root_0, cm.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "paren_chunk"

    public static class paren_matcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "paren_matcher"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:204:1: paren_matcher : LEFT_PAREN ( params )? RIGHT_PAREN ;
    public final NodeTestDSLParser.paren_matcher_return paren_matcher() throws RecognitionException {
        NodeTestDSLParser.paren_matcher_return retval = new NodeTestDSLParser.paren_matcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN43=null;
        Token RIGHT_PAREN45=null;
        NodeTestDSLParser.params_return params44 = null;


        Object LEFT_PAREN43_tree=null;
        Object RIGHT_PAREN45_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:205:2: ( LEFT_PAREN ( params )? RIGHT_PAREN )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:205:4: LEFT_PAREN ( params )? RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN43=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_matcher557); 
            LEFT_PAREN43_tree = (Object)adaptor.create(LEFT_PAREN43);
            adaptor.addChild(root_0, LEFT_PAREN43_tree);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:205:15: ( params )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==STRING||LA15_0==ID||(LA15_0>=INT && LA15_0<=LEFT_SQUARE)||LA15_0==LEFT_PAREN) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:205:15: params
                    {
                    pushFollow(FOLLOW_params_in_paren_matcher559);
                    params44=params();

                    state._fsp--;

                    adaptor.addChild(root_0, params44.getTree());

                    }
                    break;

            }

            RIGHT_PAREN45=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_matcher562); 
            RIGHT_PAREN45_tree = (Object)adaptor.create(RIGHT_PAREN45);
            adaptor.addChild(root_0, RIGHT_PAREN45_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "paren_matcher"

    public static class test_case_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test_case_key"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:208:1: test_case_key : {...}? =>id= ID -> VK_TEST_CASE[$id] ;
    public final NodeTestDSLParser.test_case_key_return test_case_key() throws RecognitionException {
        NodeTestDSLParser.test_case_key_return retval = new NodeTestDSLParser.test_case_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:209:2: ({...}? =>id= ID -> VK_TEST_CASE[$id] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:209:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey("TestCase")))) ) {
                throw new FailedPredicateException(input, "test_case_key", "(validateIdentifierKey(\"TestCase\"))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_test_case_key581);  
            stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 210:3: -> VK_TEST_CASE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TEST_CASE, id));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test_case_key"

    public static class import_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_key"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:213:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final NodeTestDSLParser.import_key_return import_key() throws RecognitionException {
        NodeTestDSLParser.import_key_return retval = new NodeTestDSLParser.import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:214:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:214:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey("import")))) ) {
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(\"import\"))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_import_key605);  
            stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 215:3: -> VK_IMPORT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_IMPORT, id));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_key"

    public static class setup_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setup_key"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:218:1: setup_key : {...}? =>id= ID -> VK_SETUP[$id] ;
    public final NodeTestDSLParser.setup_key_return setup_key() throws RecognitionException {
        NodeTestDSLParser.setup_key_return retval = new NodeTestDSLParser.setup_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:219:2: ({...}? =>id= ID -> VK_SETUP[$id] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:219:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey("Setup")))) ) {
                throw new FailedPredicateException(input, "setup_key", "(validateIdentifierKey(\"Setup\"))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_setup_key629);  
            stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 220:3: -> VK_SETUP[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_SETUP, id));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "setup_key"

    public static class teardown_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "teardown_key"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:223:1: teardown_key : {...}? =>id= ID -> VK_TEARDOWN[$id] ;
    public final NodeTestDSLParser.teardown_key_return teardown_key() throws RecognitionException {
        NodeTestDSLParser.teardown_key_return retval = new NodeTestDSLParser.teardown_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:224:2: ({...}? =>id= ID -> VK_TEARDOWN[$id] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:224:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey("TearDown")))) ) {
                throw new FailedPredicateException(input, "teardown_key", "(validateIdentifierKey(\"TearDown\"))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_teardown_key653);  
            stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 225:3: -> VK_TEARDOWN[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TEARDOWN, id));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "teardown_key"

    public static class test_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test_key"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:228:1: test_key : {...}? =>id= ID -> VK_TEST[$id] ;
    public final NodeTestDSLParser.test_key_return test_key() throws RecognitionException {
        NodeTestDSLParser.test_key_return retval = new NodeTestDSLParser.test_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:229:2: ({...}? =>id= ID -> VK_TEST[$id] )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSL.g:229:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey("Test")))) ) {
                throw new FailedPredicateException(input, "test_key", "(validateIdentifierKey(\"Test\"))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_test_key677);  
            stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 230:3: -> VK_TEST[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TEST, id));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test_key"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA1_eotS =
        "\12\uffff";
    static final String DFA1_eofS =
        "\1\2\1\5\1\uffff\1\11\6\uffff";
    static final String DFA1_minS =
        "\1\20\1\16\1\uffff\1\16\6\uffff";
    static final String DFA1_maxS =
        "\2\20\1\uffff\1\27\6\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\1\2\1\uffff\4\2\1\1\1\2";
    static final String DFA1_specialS =
        "\1\0\1\1\1\uffff\1\2\6\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1",
            "\1\4\1\uffff\1\3",
            "",
            "\1\7\1\10\1\11\1\6\5\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()* loopback of 112:3: ( import_statement )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_0 = input.LA(1);

                         
                        int index1_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_0==ID) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("Test")))||((validateIdentifierKey("import")))||((validateIdentifierKey("TearDown")))))) {s = 1;}

                        else if ( (LA1_0==EOF) ) {s = 2;}

                         
                        input.seek(index1_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_1 = input.LA(1);

                         
                        int index1_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_1==ID) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("import")))||((validateIdentifierKey("TearDown")))))) {s = 3;}

                        else if ( (LA1_1==STRING) && (((validateIdentifierKey("Test"))))) {s = 4;}

                        else if ( (LA1_1==EOF) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("TearDown")))))) {s = 5;}

                         
                        input.seek(index1_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_3 = input.LA(1);

                         
                        int index1_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_3==COLON) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("TearDown")))))) {s = 6;}

                        else if ( (LA1_3==STRING) && ((((validateIdentifierKey("Setup")))||((validateIdentifierKey("TearDown")))))) {s = 7;}

                        else if ( (LA1_3==SEMI_COLON||LA1_3==DOT) && (((validateIdentifierKey("import"))))) {s = 8;}

                        else if ( (LA1_3==EOF||LA1_3==ID) && (((validateIdentifierKey("Setup"))))) {s = 9;}

                         
                        input.seek(index1_3);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA8_eotS =
        "\16\uffff";
    static final String DFA8_eofS =
        "\1\2\1\6\2\uffff\1\10\5\uffff\1\6\3\uffff";
    static final String DFA8_minS =
        "\2\16\2\uffff\2\16\1\uffff\1\16\2\uffff\1\16\1\uffff\1\16\1\uffff";
    static final String DFA8_maxS =
        "\2\32\2\uffff\2\32\1\uffff\1\32\2\uffff\1\32\1\uffff\1\32\1\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\1\1\2\uffff\1\2\1\uffff\2\2\1\uffff\1\2\1\uffff\1"+
        "\2";
    static final String DFA8_specialS =
        "\1\uffff\1\5\2\uffff\1\3\1\1\1\uffff\1\2\2\uffff\1\4\1\uffff\1"+
        "\0\1\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\3\1\uffff\1\1\2\uffff\6\3\1\uffff\1\3",
            "\1\4\1\3\1\5\1\2\7\3\1\uffff\1\3",
            "",
            "",
            "\2\3\1\7\1\uffff\7\3\1\uffff\1\3",
            "\1\12\2\3\1\11\7\3\1\uffff\1\3",
            "",
            "\1\4\2\3\1\13\7\3\1\uffff\1\3",
            "",
            "",
            "\2\3\1\14\1\uffff\7\3\1\uffff\1\3",
            "",
            "\1\12\2\3\1\15\7\3\1\uffff\1\3",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "()+ loopback of 144:15: ( params SEMI_COLON )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_12 = input.LA(1);

                         
                        int index8_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_12==COLON) && (((validateIdentifierKey("TearDown"))))) {s = 13;}

                        else if ( (LA8_12==STRING) ) {s = 10;}

                        else if ( ((LA8_12>=SEMI_COLON && LA8_12<=ID)||(LA8_12>=COMMA && LA8_12<=LEFT_SQUARE)||LA8_12==LEFT_PAREN) ) {s = 3;}

                         
                        input.seek(index8_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA8_5 = input.LA(1);

                         
                        int index8_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_5==COLON) && (((validateIdentifierKey("TearDown"))))) {s = 9;}

                        else if ( (LA8_5==STRING) ) {s = 10;}

                        else if ( ((LA8_5>=SEMI_COLON && LA8_5<=ID)||(LA8_5>=COMMA && LA8_5<=LEFT_SQUARE)||LA8_5==LEFT_PAREN) ) {s = 3;}

                         
                        input.seek(index8_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA8_7 = input.LA(1);

                         
                        int index8_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_7==COLON) && (((validateIdentifierKey("Test"))))) {s = 11;}

                        else if ( (LA8_7==STRING) ) {s = 4;}

                        else if ( ((LA8_7>=SEMI_COLON && LA8_7<=ID)||(LA8_7>=COMMA && LA8_7<=LEFT_SQUARE)||LA8_7==LEFT_PAREN) ) {s = 3;}

                         
                        input.seek(index8_7);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA8_4 = input.LA(1);

                         
                        int index8_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_4==ID) ) {s = 7;}

                        else if ( (LA8_4==EOF) && (((validateIdentifierKey("Test"))))) {s = 8;}

                        else if ( ((LA8_4>=STRING && LA8_4<=SEMI_COLON)||(LA8_4>=COMMA && LA8_4<=LEFT_SQUARE)||LA8_4==LEFT_PAREN) ) {s = 3;}

                         
                        input.seek(index8_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA8_10 = input.LA(1);

                         
                        int index8_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_10==ID) ) {s = 12;}

                        else if ( (LA8_10==EOF) && (((validateIdentifierKey("TearDown"))))) {s = 6;}

                        else if ( ((LA8_10>=STRING && LA8_10<=SEMI_COLON)||(LA8_10>=COMMA && LA8_10<=LEFT_SQUARE)||LA8_10==LEFT_PAREN) ) {s = 3;}

                         
                        input.seek(index8_10);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA8_1 = input.LA(1);

                         
                        int index8_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA8_1==COLON) ) {s = 2;}

                        else if ( (LA8_1==STRING) ) {s = 4;}

                        else if ( (LA8_1==SEMI_COLON||(LA8_1>=COMMA && LA8_1<=LEFT_SQUARE)||LA8_1==LEFT_PAREN) ) {s = 3;}

                        else if ( (LA8_1==ID) ) {s = 5;}

                        else if ( (LA8_1==EOF) && (((validateIdentifierKey("TearDown"))))) {s = 6;}

                         
                        input.seek(index8_1);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_test_case_statement_in_compilation_unit107 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_import_statement_in_compilation_unit111 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_setup_in_compilation_unit116 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_teardown_in_compilation_unit121 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_test_in_compilation_unit126 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_case_key_in_test_case_statement172 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_test_case_statement177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement190 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_import_target_in_import_statement193 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_COLON_in_import_statement195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setup_key_in_setup209 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_step_in_setup212 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_teardown_key_in_teardown225 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_step_in_teardown228 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_test_key_in_test242 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_test247 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_step_in_test249 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ID_in_step263 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COLON_in_step266 = new BitSet(new long[]{0x0000000005F94000L});
    public static final BitSet FOLLOW_params_in_step271 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_COLON_in_step273 = new BitSet(new long[]{0x0000000005F94002L});
    public static final BitSet FOLLOW_param_chunk_in_params290 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_params293 = new BitSet(new long[]{0x0000000005F94000L});
    public static final BitSet FOLLOW_param_chunk_in_params295 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_param_sequence_in_param_chunk324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_param_in_param_sequence343 = new BitSet(new long[]{0x0000000005F94002L});
    public static final BitSet FOLLOW_ID_in_param359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_param366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_param372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_param378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MISC_in_param384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_param390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_in_param396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_param402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_param408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_target_matcher_in_import_target428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_matcher_in_import_target_matcher447 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_DOT_in_import_target_matcher450 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_STAR_in_import_target_matcher452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_matcher_in_qualified_id470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id_matcher488 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_DOT_in_qualified_id_matcher491 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_qualified_id_matcher493 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_collection_matcher_in_square_chunk511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_collection_matcher524 = new BitSet(new long[]{0x0000000007F94000L});
    public static final BitSet FOLLOW_params_in_collection_matcher526 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_collection_matcher529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_matcher_in_paren_chunk544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_matcher557 = new BitSet(new long[]{0x000000000DF94000L});
    public static final BitSet FOLLOW_params_in_paren_matcher559 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_matcher562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_test_case_key581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_setup_key629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_teardown_key653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_test_key677 = new BitSet(new long[]{0x0000000000000002L});

}