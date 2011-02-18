/*
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

// $ANTLR 3.1.1 src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g 2010-01-26 19:56:37

	package org.drools.reteoo.test.parser;
	
	import org.drools.reteoo.test.dsl.*;



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


public class NodeTestDSLTree extends TreeParser {
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
    public static final int INT=19;
    public static final int SEMI_COLON=15;
    public static final int VK_TEARDOWN=12;
    public static final int VT_TEST_CASE=4;
    public static final int VK_SETUP=11;
    public static final int EOF=-1;
    public static final int VK_TEST_CASE=9;
    public static final int EOL=28;
    public static final int LEFT_SQUARE=24;
    public static final int COLON=17;
    public static final int OctalEscape=33;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int VK_TEST=13;
    public static final int MULTI_LINE_COMMENT=36;
    public static final int STAR=22;
    public static final int RIGHT_PAREN=27;
    public static final int RIGHT_SQUARE=25;
    public static final int ID=16;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;

    // delegates
    // delegators


        public NodeTestDSLTree(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public NodeTestDSLTree(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return NodeTestDSLTree.tokenNames; }
    public String getGrammarFileName() { return "src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g"; }


    	NodeTestDSLFactory factory = new NodeTestDSLFactory();
    	NodeTestCase testCase = null;
    	
    	public NodeTestCase getTestCase() {
    		return testCase;
    	}


    public static class compilation_unit_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilation_unit"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:25:1: compilation_unit : ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* ) ;
    public final NodeTestDSLTree.compilation_unit_return compilation_unit() throws RecognitionException {
        NodeTestDSLTree.compilation_unit_return retval = new NodeTestDSLTree.compilation_unit_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VT_TEST_CASE1=null;
        NodeTestDSLTree.test_case_statement_return test_case_statement2 = null;

        NodeTestDSLTree.import_statement_return import_statement3 = null;

        NodeTestDSLTree.setup_return setup4 = null;

        NodeTestDSLTree.teardown_return teardown5 = null;

        NodeTestDSLTree.test_return test6 = null;


        CommonTree VT_TEST_CASE1_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:2: ( ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:5: ^( VT_TEST_CASE test_case_statement ( import_statement )* ( setup )? ( teardown )? ( test )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VT_TEST_CASE1=(CommonTree)match(input,VT_TEST_CASE,FOLLOW_VT_TEST_CASE_in_compilation_unit50); 
            VT_TEST_CASE1_tree = (CommonTree)adaptor.dupNode(VT_TEST_CASE1);

            root_1 = (CommonTree)adaptor.becomeRoot(VT_TEST_CASE1_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_test_case_statement_in_compilation_unit52);
            test_case_statement2=test_case_statement();

            state._fsp--;

            adaptor.addChild(root_1, test_case_statement2.getTree());
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:40: ( import_statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==VK_IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:40: import_statement
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_import_statement_in_compilation_unit54);
            	    import_statement3=import_statement();

            	    state._fsp--;

            	    adaptor.addChild(root_1, import_statement3.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:58: ( setup )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==VK_SETUP) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:58: setup
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_setup_in_compilation_unit57);
                    setup4=setup();

                    state._fsp--;

                    adaptor.addChild(root_1, setup4.getTree());

                    }
                    break;

            }

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:65: ( teardown )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==VK_TEARDOWN) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:65: teardown
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_teardown_in_compilation_unit60);
                    teardown5=teardown();

                    state._fsp--;

                    adaptor.addChild(root_1, teardown5.getTree());

                    }
                    break;

            }

            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:75: ( test )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==VK_TEST) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:26:75: test
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_test_in_compilation_unit63);
            	    test6=test();

            	    state._fsp--;

            	    adaptor.addChild(root_1, test6.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "compilation_unit"

    public static class test_case_statement_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test_case_statement"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:29:1: test_case_statement : ^( VK_TEST_CASE name= STRING ) ;
    public final NodeTestDSLTree.test_case_statement_return test_case_statement() throws RecognitionException {
        NodeTestDSLTree.test_case_statement_return retval = new NodeTestDSLTree.test_case_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree VK_TEST_CASE7=null;

        CommonTree name_tree=null;
        CommonTree VK_TEST_CASE7_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:30:2: ( ^( VK_TEST_CASE name= STRING ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:30:5: ^( VK_TEST_CASE name= STRING )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VK_TEST_CASE7=(CommonTree)match(input,VK_TEST_CASE,FOLLOW_VK_TEST_CASE_in_test_case_statement79); 
            VK_TEST_CASE7_tree = (CommonTree)adaptor.dupNode(VK_TEST_CASE7);

            root_1 = (CommonTree)adaptor.becomeRoot(VK_TEST_CASE7_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            name=(CommonTree)match(input,STRING,FOLLOW_STRING_in_test_case_statement83); 
            name_tree = (CommonTree)adaptor.dupNode(name);

            adaptor.addChild(root_1, name_tree);


            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }

             testCase = factory.createTestCase( (name!=null?name.getText():null) ); 

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test_case_statement"

    public static class import_statement_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_statement"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:34:1: import_statement : ^( VK_IMPORT clazz= VT_QUALIFIED_ID ) ;
    public final NodeTestDSLTree.import_statement_return import_statement() throws RecognitionException {
        NodeTestDSLTree.import_statement_return retval = new NodeTestDSLTree.import_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree clazz=null;
        CommonTree VK_IMPORT8=null;

        CommonTree clazz_tree=null;
        CommonTree VK_IMPORT8_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:35:2: ( ^( VK_IMPORT clazz= VT_QUALIFIED_ID ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:35:4: ^( VK_IMPORT clazz= VT_QUALIFIED_ID )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VK_IMPORT8=(CommonTree)match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement102); 
            VK_IMPORT8_tree = (CommonTree)adaptor.dupNode(VK_IMPORT8);

            root_1 = (CommonTree)adaptor.becomeRoot(VK_IMPORT8_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            clazz=(CommonTree)match(input,VT_QUALIFIED_ID,FOLLOW_VT_QUALIFIED_ID_in_import_statement106); 
            clazz_tree = (CommonTree)adaptor.dupNode(clazz);

            adaptor.addChild(root_1, clazz_tree);


            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }

             testCase.addImport( (clazz!=null?clazz.getText():null) ); 

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_statement"

    public static class setup_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setup"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:39:1: setup : ^( VK_SETUP ( step )* ) ;
    public final NodeTestDSLTree.setup_return setup() throws RecognitionException {
        NodeTestDSLTree.setup_return retval = new NodeTestDSLTree.setup_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VK_SETUP9=null;
        NodeTestDSLTree.step_return step10 = null;


        CommonTree VK_SETUP9_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:40:2: ( ^( VK_SETUP ( step )* ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:40:4: ^( VK_SETUP ( step )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VK_SETUP9=(CommonTree)match(input,VK_SETUP,FOLLOW_VK_SETUP_in_setup132); 
            VK_SETUP9_tree = (CommonTree)adaptor.dupNode(VK_SETUP9);

            root_1 = (CommonTree)adaptor.becomeRoot(VK_SETUP9_tree, root_1);


             factory.createSetup(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:40:42: ( step )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ID) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:40:42: step
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_step_in_setup136);
                	    step10=step();

                	    state._fsp--;

                	    adaptor.addChild(root_1, step10.getTree());

                	    }
                	    break;

                	default :
                	    break loop5;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "setup"

    public static class teardown_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "teardown"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:43:1: teardown : ^( VK_TEARDOWN ( step )* ) ;
    public final NodeTestDSLTree.teardown_return teardown() throws RecognitionException {
        NodeTestDSLTree.teardown_return retval = new NodeTestDSLTree.teardown_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VK_TEARDOWN11=null;
        NodeTestDSLTree.step_return step12 = null;


        CommonTree VK_TEARDOWN11_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:44:2: ( ^( VK_TEARDOWN ( step )* ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:44:4: ^( VK_TEARDOWN ( step )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VK_TEARDOWN11=(CommonTree)match(input,VK_TEARDOWN,FOLLOW_VK_TEARDOWN_in_teardown151); 
            VK_TEARDOWN11_tree = (CommonTree)adaptor.dupNode(VK_TEARDOWN11);

            root_1 = (CommonTree)adaptor.becomeRoot(VK_TEARDOWN11_tree, root_1);


             factory.createTearDown(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:44:48: ( step )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ID) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:44:48: step
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_step_in_teardown155);
                	    step12=step();

                	    state._fsp--;

                	    adaptor.addChild(root_1, step12.getTree());

                	    }
                	    break;

                	default :
                	    break loop6;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "teardown"

    public static class test_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "test"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:47:1: test : ^( VK_TEST name= STRING ( step )* ) ;
    public final NodeTestDSLTree.test_return test() throws RecognitionException {
        NodeTestDSLTree.test_return retval = new NodeTestDSLTree.test_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree VK_TEST13=null;
        NodeTestDSLTree.step_return step14 = null;


        CommonTree name_tree=null;
        CommonTree VK_TEST13_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:48:2: ( ^( VK_TEST name= STRING ( step )* ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:48:4: ^( VK_TEST name= STRING ( step )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VK_TEST13=(CommonTree)match(input,VK_TEST,FOLLOW_VK_TEST_in_test171); 
            VK_TEST13_tree = (CommonTree)adaptor.dupNode(VK_TEST13);

            root_1 = (CommonTree)adaptor.becomeRoot(VK_TEST13_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            name=(CommonTree)match(input,STRING,FOLLOW_STRING_in_test175); 
            name_tree = (CommonTree)adaptor.dupNode(name);

            adaptor.addChild(root_1, name_tree);

             factory.createTest( VK_TEST13, name ); 
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:48:69: ( step )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ID) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:48:69: step
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_step_in_test179);
            	    step14=step();

            	    state._fsp--;

            	    adaptor.addChild(root_1, step14.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "test"

    public static class step_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "step"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:51:1: step : ^( ID (p+= params )+ ) ;
    public final NodeTestDSLTree.step_return step() throws RecognitionException {
        NodeTestDSLTree.step_return retval = new NodeTestDSLTree.step_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ID15=null;
        List list_p=null;
        RuleReturnScope p = null;
        CommonTree ID15_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:52:2: ( ^( ID (p+= params )+ ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:52:4: ^( ID (p+= params )+ )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            ID15=(CommonTree)match(input,ID,FOLLOW_ID_in_step195); 
            ID15_tree = (CommonTree)adaptor.dupNode(ID15);

            root_1 = (CommonTree)adaptor.becomeRoot(ID15_tree, root_1);



            match(input, Token.DOWN, null); 
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:52:10: (p+= params )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==VT_PARAMS) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:52:10: p+= params
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_params_in_step199);
            	    p=params();

            	    state._fsp--;

            	    adaptor.addChild(root_1, p.getTree());
            	    if (list_p==null) list_p=new ArrayList();
            	    list_p.add(p.getTree());


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

             factory.createStep( ID15, list_p ); 

            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "step"

    public static class params_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "params"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:55:1: params : ^( VT_PARAMS ( param_chunk )+ ) ;
    public final NodeTestDSLTree.params_return params() throws RecognitionException {
        NodeTestDSLTree.params_return retval = new NodeTestDSLTree.params_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VT_PARAMS16=null;
        NodeTestDSLTree.param_chunk_return param_chunk17 = null;


        CommonTree VT_PARAMS16_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:56:2: ( ^( VT_PARAMS ( param_chunk )+ ) )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:56:4: ^( VT_PARAMS ( param_chunk )+ )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            VT_PARAMS16=(CommonTree)match(input,VT_PARAMS,FOLLOW_VT_PARAMS_in_params215); 
            VT_PARAMS16_tree = (CommonTree)adaptor.dupNode(VT_PARAMS16);

            root_1 = (CommonTree)adaptor.becomeRoot(VT_PARAMS16_tree, root_1);



            match(input, Token.DOWN, null); 
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:56:16: ( param_chunk )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==VT_CHUNK) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:56:16: param_chunk
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_param_chunk_in_params217);
            	    param_chunk17=param_chunk();

            	    state._fsp--;

            	    adaptor.addChild(root_1, param_chunk17.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "params"

    public static class param_chunk_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "param_chunk"
    // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:59:1: param_chunk : VT_CHUNK ;
    public final NodeTestDSLTree.param_chunk_return param_chunk() throws RecognitionException {
        NodeTestDSLTree.param_chunk_return retval = new NodeTestDSLTree.param_chunk_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VT_CHUNK18=null;

        CommonTree VT_CHUNK18_tree=null;

        try {
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:60:2: ( VT_CHUNK )
            // src/test/resources/org/drools/reteoo/test/parser/NodeTestDSLTree.g:60:4: VT_CHUNK
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            VT_CHUNK18=(CommonTree)match(input,VT_CHUNK,FOLLOW_VT_CHUNK_in_param_chunk233); 
            VT_CHUNK18_tree = (CommonTree)adaptor.dupNode(VT_CHUNK18);

            adaptor.addChild(root_0, VT_CHUNK18_tree);


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "param_chunk"

    // Delegated rules


 

    public static final BitSet FOLLOW_VT_TEST_CASE_in_compilation_unit50 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_test_case_statement_in_compilation_unit52 = new BitSet(new long[]{0x0000000000003C08L});
    public static final BitSet FOLLOW_import_statement_in_compilation_unit54 = new BitSet(new long[]{0x0000000000003C08L});
    public static final BitSet FOLLOW_setup_in_compilation_unit57 = new BitSet(new long[]{0x0000000000003008L});
    public static final BitSet FOLLOW_teardown_in_compilation_unit60 = new BitSet(new long[]{0x0000000000002008L});
    public static final BitSet FOLLOW_test_in_compilation_unit63 = new BitSet(new long[]{0x0000000000002008L});
    public static final BitSet FOLLOW_VK_TEST_CASE_in_test_case_statement79 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_test_case_statement83 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUALIFIED_ID_in_import_statement106 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SETUP_in_setup132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_step_in_setup136 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_VK_TEARDOWN_in_teardown151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_step_in_teardown155 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_VK_TEST_in_test171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_test175 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_step_in_test179 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_ID_in_step195 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_params_in_step199 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_VT_PARAMS_in_params215 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_chunk_in_params217 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_VT_CHUNK_in_param_chunk233 = new BitSet(new long[]{0x0000000000000002L});

}
