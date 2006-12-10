// $ANTLR 3.0b5 /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g 2006-11-27 17:08:59

	package org.codehaus.jfdi.parser;
	import org.codehaus.jfdi.interpreter.*;
	import org.codehaus.jfdi.interpreter.operations.*;
	import java.util.List;
	import java.util.ArrayList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class JFDIParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENT", "INTEGER", "STRING", "FLOAT", "';'", "'{'", "'}'", "'for'", "'in'", "'='", "'||'", "'&&'", "'+'", "'-'", "'*'", "'/'", "'true'", "'false'", "'('", "')'", "','", "'['", "']'", "'.'", "'=>'"
    };
    public static final int INTEGER=5;
    public static final int IDENT=4;
    public static final int EOF=-1;
    public static final int STRING=6;
    public static final int FLOAT=7;

        public JFDIParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[18+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g"; }



    	private ValueHandlerFactory factory;
    	private TypeResolver typeResolver;
    	
    	public void setValueHandlerFactory(ValueHandlerFactory factory) {
    		this.factory = factory;
    	}
    	
    	public ValueHandlerFactory getValueHandlerFactory() {
    		return this.factory;
    	}
    	
    	public void setTypeResolver(TypeResolver typeResolver) {
    		this.typeResolver = typeResolver;
    	}
    	
    	public TypeResolver getTypeResolver() {
    		return typeResolver;
    	}
    	



    // $ANTLR start compilation_unit
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:39:1: compilation_unit : statements ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:41:3: ( statements )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:41:3: statements
            {
            pushFollow(FOLLOW_statements_in_compilation_unit41);
            statements();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end compilation_unit


    // $ANTLR start statements
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:45:1: statements : ( statement ';' )* ;
    public void statements() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:47:3: ( ( statement ';' )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:47:3: ( statement ';' )*
            {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:47:3: ( statement ';' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( ((LA1_0>=IDENT && LA1_0<=FLOAT)||LA1_0==9||LA1_0==11||(LA1_0>=20 && LA1_0<=22)||LA1_0==25) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:47:4: statement ';'
            	    {
            	    pushFollow(FOLLOW_statement_in_statements58);
            	    statement();
            	    _fsp--;
            	    if (failed) return ;
            	    match(input,8,FOLLOW_8_in_statements60); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end statements


    // $ANTLR start statement
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:50:1: statement : ( expr | for_in_statement ) ;
    public void statement() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:52:3: ( ( expr | for_in_statement ) )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:52:3: ( expr | for_in_statement )
            {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:52:3: ( expr | for_in_statement )
            int alt2=2;
            int LA2_0 = input.LA(1);
            if ( ((LA2_0>=IDENT && LA2_0<=FLOAT)||LA2_0==9||(LA2_0>=20 && LA2_0<=22)||LA2_0==25) ) {
                alt2=1;
            }
            else if ( (LA2_0==11) ) {
                alt2=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("52:3: ( expr | for_in_statement )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:52:5: expr
                    {
                    pushFollow(FOLLOW_expr_in_statement78);
                    expr();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:53:5: for_in_statement
                    {
                    pushFollow(FOLLOW_for_in_statement_in_statement84);
                    for_in_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start statement_block
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:57:1: statement_block : '{' statements '}' ;
    public void statement_block() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:59:3: ( '{' statements '}' )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:59:3: '{' statements '}'
            {
            match(input,9,FOLLOW_9_in_statement_block102); if (failed) return ;
            pushFollow(FOLLOW_statements_in_statement_block104);
            statements();
            _fsp--;
            if (failed) return ;
            match(input,10,FOLLOW_10_in_statement_block106); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end statement_block


    // $ANTLR start for_in_statement
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:62:1: for_in_statement : 'for' IDENT 'in' expr statement_block ;
    public void for_in_statement() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:64:3: ( 'for' IDENT 'in' expr statement_block )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:64:3: 'for' IDENT 'in' expr statement_block
            {
            match(input,11,FOLLOW_11_in_for_in_statement120); if (failed) return ;
            match(input,IDENT,FOLLOW_IDENT_in_for_in_statement122); if (failed) return ;
            match(input,12,FOLLOW_12_in_for_in_statement124); if (failed) return ;
            pushFollow(FOLLOW_expr_in_for_in_statement126);
            expr();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_statement_block_in_for_in_statement128);
            statement_block();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end for_in_statement


    // $ANTLR start assignment_statement
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:67:1: assignment_statement : object_expr '=' expr ;
    public void assignment_statement() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:69:3: ( object_expr '=' expr )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:69:3: object_expr '=' expr
            {
            pushFollow(FOLLOW_object_expr_in_assignment_statement141);
            object_expr();
            _fsp--;
            if (failed) return ;
            match(input,13,FOLLOW_13_in_assignment_statement143); if (failed) return ;
            pushFollow(FOLLOW_expr_in_assignment_statement145);
            expr();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return ;
    }
    // $ANTLR end assignment_statement


    // $ANTLR start expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:72:1: expr returns [Expr e] : ex= logical_or_expr ;
    public Expr expr() throws RecognitionException {   
        Expr e = null;

        Expr ex = null;



        		e = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:77:18: (ex= logical_or_expr )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:77:18: ex= logical_or_expr
            {
            pushFollow(FOLLOW_logical_or_expr_in_expr174);
            ex=logical_or_expr();
            _fsp--;
            if (failed) return e;
            if ( backtracking==0 ) {
               e = ex; 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end expr


    // $ANTLR start logical_or_expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:85:1: logical_or_expr returns [Expr e] : lhs= logical_and_expr ( '||' rhs= logical_and_expr )* ;
    public Expr logical_or_expr() throws RecognitionException {   
        Expr e = null;

        Expr lhs = null;

        Expr rhs = null;



        		e = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:90:3: (lhs= logical_and_expr ( '||' rhs= logical_and_expr )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:90:3: lhs= logical_and_expr ( '||' rhs= logical_and_expr )*
            {
            pushFollow(FOLLOW_logical_and_expr_in_logical_or_expr206);
            lhs=logical_and_expr();
            _fsp--;
            if (failed) return e;
            if ( backtracking==0 ) {
               e = lhs; 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:91:3: ( '||' rhs= logical_and_expr )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);
                if ( (LA3_0==14) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:91:5: '||' rhs= logical_and_expr
            	    {
            	    match(input,14,FOLLOW_14_in_logical_or_expr214); if (failed) return e;
            	    pushFollow(FOLLOW_logical_and_expr_in_logical_or_expr218);
            	    rhs=logical_and_expr();
            	    _fsp--;
            	    if (failed) return e;
            	    if ( backtracking==0 ) {
            	       e = new LogicalOrExpr( e, rhs ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end logical_or_expr


    // $ANTLR start logical_and_expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:96:1: logical_and_expr returns [Expr e] : lhs= additive_expr ( '&&' rhs= additive_expr )* ;
    public Expr logical_and_expr() throws RecognitionException {   
        Expr e = null;

        Expr lhs = null;

        Expr rhs = null;



        		e = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:101:3: (lhs= additive_expr ( '&&' rhs= additive_expr )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:101:3: lhs= additive_expr ( '&&' rhs= additive_expr )*
            {
            pushFollow(FOLLOW_additive_expr_in_logical_and_expr255);
            lhs=additive_expr();
            _fsp--;
            if (failed) return e;
            if ( backtracking==0 ) {
               e = lhs; 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:102:3: ( '&&' rhs= additive_expr )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( (LA4_0==15) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:102:5: '&&' rhs= additive_expr
            	    {
            	    match(input,15,FOLLOW_15_in_logical_and_expr263); if (failed) return e;
            	    pushFollow(FOLLOW_additive_expr_in_logical_and_expr267);
            	    rhs=additive_expr();
            	    _fsp--;
            	    if (failed) return e;
            	    if ( backtracking==0 ) {
            	       e = new LogicalAndExpr( e, rhs ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end logical_and_expr


    // $ANTLR start additive_expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:108:1: additive_expr returns [Expr e] : lhs= multiplicative_expr ( ( '+' | '-' ) rhs= multiplicative_expr )* ;
    public Expr additive_expr() throws RecognitionException {   
        Expr e = null;

        Expr lhs = null;

        Expr rhs = null;



        		e = null;
        		AdditiveExpr.Operator op = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:114:3: (lhs= multiplicative_expr ( ( '+' | '-' ) rhs= multiplicative_expr )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:114:3: lhs= multiplicative_expr ( ( '+' | '-' ) rhs= multiplicative_expr )*
            {
            pushFollow(FOLLOW_multiplicative_expr_in_additive_expr305);
            lhs=multiplicative_expr();
            _fsp--;
            if (failed) return e;
            if ( backtracking==0 ) {
               e = lhs; 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:115:3: ( ( '+' | '-' ) rhs= multiplicative_expr )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( ((LA6_0>=16 && LA6_0<=17)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:116:4: ( '+' | '-' ) rhs= multiplicative_expr
            	    {
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:116:4: ( '+' | '-' )
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);
            	    if ( (LA5_0==16) ) {
            	        alt5=1;
            	    }
            	    else if ( (LA5_0==17) ) {
            	        alt5=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return e;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("116:4: ( '+' | '-' )", 5, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:116:6: '+'
            	            {
            	            match(input,16,FOLLOW_16_in_additive_expr319); if (failed) return e;
            	            if ( backtracking==0 ) {
            	               op = AdditiveExpr.PLUS; 
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:117:6: '-'
            	            {
            	            match(input,17,FOLLOW_17_in_additive_expr328); if (failed) return e;
            	            if ( backtracking==0 ) {
            	               op = AdditiveExpr.MINUS; 
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplicative_expr_in_additive_expr343);
            	    rhs=multiplicative_expr();
            	    _fsp--;
            	    if (failed) return e;
            	    if ( backtracking==0 ) {
            	       e = new AdditiveExpr( e, rhs, op ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            if ( backtracking==0 ) {
              System.err.println( "add_expr returns " + e ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end additive_expr


    // $ANTLR start multiplicative_expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:125:1: multiplicative_expr returns [Expr e] : lhs= atom ( ( '*' | '/' ) rhs= atom )* ;
    public Expr multiplicative_expr() throws RecognitionException {   
        Expr e = null;

        Expr lhs = null;

        Expr rhs = null;



        		e = null;
        		MultiplicativeExpr.Operator op = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:131:3: (lhs= atom ( ( '*' | '/' ) rhs= atom )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:131:3: lhs= atom ( ( '*' | '/' ) rhs= atom )*
            {
            pushFollow(FOLLOW_atom_in_multiplicative_expr383);
            lhs=atom();
            _fsp--;
            if (failed) return e;
            if ( backtracking==0 ) {
               e = lhs; 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:132:3: ( ( '*' | '/' ) rhs= atom )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( ((LA8_0>=18 && LA8_0<=19)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:133:4: ( '*' | '/' ) rhs= atom
            	    {
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:133:4: ( '*' | '/' )
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);
            	    if ( (LA7_0==18) ) {
            	        alt7=1;
            	    }
            	    else if ( (LA7_0==19) ) {
            	        alt7=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return e;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("133:4: ( '*' | '/' )", 7, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:133:6: '*'
            	            {
            	            match(input,18,FOLLOW_18_in_multiplicative_expr396); if (failed) return e;
            	            if ( backtracking==0 ) {
            	               op = MultiplicativeExpr.MULT; 
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:134:6: '/'
            	            {
            	            match(input,19,FOLLOW_19_in_multiplicative_expr406); if (failed) return e;
            	            if ( backtracking==0 ) {
            	               op = MultiplicativeExpr.DIV; 
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_atom_in_multiplicative_expr421);
            	    rhs=atom();
            	    _fsp--;
            	    if (failed) return e;
            	    if ( backtracking==0 ) {
            	       e = new MultiplicativeExpr( e, rhs, op ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            if ( backtracking==0 ) {
              System.err.println( "mult_expr returns " + e ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end multiplicative_expr


    // $ANTLR start atom
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:142:1: atom returns [Expr e] : (i= INTEGER | s= STRING | f= FLOAT | 'true' | 'false' | '(' ex= expr ')' | l= list | m= map | ex= object_expr ) ;
    public Expr atom() throws RecognitionException {   
        Expr e = null;

        Token i=null;
        Token s=null;
        Token f=null;
        Expr ex = null;

        AnonListValue l = null;

        AnonMapValue m = null;



        		e = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:147:3: ( (i= INTEGER | s= STRING | f= FLOAT | 'true' | 'false' | '(' ex= expr ')' | l= list | m= map | ex= object_expr ) )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:147:3: (i= INTEGER | s= STRING | f= FLOAT | 'true' | 'false' | '(' ex= expr ')' | l= list | m= map | ex= object_expr )
            {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:147:3: (i= INTEGER | s= STRING | f= FLOAT | 'true' | 'false' | '(' ex= expr ')' | l= list | m= map | ex= object_expr )
            int alt9=9;
            switch ( input.LA(1) ) {
            case INTEGER:
                alt9=1;
                break;
            case STRING:
                alt9=2;
                break;
            case FLOAT:
                alt9=3;
                break;
            case 20:
                alt9=4;
                break;
            case 21:
                alt9=5;
                break;
            case 22:
                alt9=6;
                break;
            case 25:
                alt9=7;
                break;
            case 9:
                alt9=8;
                break;
            case IDENT:
                alt9=9;
                break;
            default:
                if (backtracking>0) {failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("147:3: (i= INTEGER | s= STRING | f= FLOAT | 'true' | 'false' | '(' ex= expr ')' | l= list | m= map | ex= object_expr )", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:147:5: i= INTEGER
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER,FOLLOW_INTEGER_in_atom463); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = factory.createLiteral( java.lang.Integer.class, i.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:148:5: s= STRING
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_atom473); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = factory.createLiteral( java.lang.String.class,  s.getText().substring( 1, s.getText().length()-1 ) ); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:149:5: f= FLOAT
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_atom484); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = factory.createLiteral( java.lang.Double.class,   f.getText() ); 
                    }

                    }
                    break;
                case 4 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:150:5: 'true'
                    {
                    match(input,20,FOLLOW_20_in_atom494); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = factory.createLiteral( java.lang.Boolean.class, "true" ); 
                    }

                    }
                    break;
                case 5 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:151:5: 'false'
                    {
                    match(input,21,FOLLOW_21_in_atom505); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = factory.createLiteral( java.lang.Boolean.class, "false" ); 
                    }

                    }
                    break;
                case 6 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:152:5: '(' ex= expr ')'
                    {
                    match(input,22,FOLLOW_22_in_atom515); if (failed) return e;
                    pushFollow(FOLLOW_expr_in_atom519);
                    ex=expr();
                    _fsp--;
                    if (failed) return e;
                    match(input,23,FOLLOW_23_in_atom521); if (failed) return e;
                    if ( backtracking==0 ) {
                       e = ex; 
                    }

                    }
                    break;
                case 7 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:153:5: l= list
                    {
                    pushFollow(FOLLOW_list_in_atom531);
                    l=list();
                    _fsp--;
                    if (failed) return e;
                    if ( backtracking==0 ) {
                       e = l; 
                    }

                    }
                    break;
                case 8 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:154:5: m= map
                    {
                    pushFollow(FOLLOW_map_in_atom541);
                    m=map();
                    _fsp--;
                    if (failed) return e;
                    if ( backtracking==0 ) {
                       e = m; 
                    }

                    }
                    break;
                case 9 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:155:5: ex= object_expr
                    {
                    pushFollow(FOLLOW_object_expr_in_atom551);
                    ex=object_expr();
                    _fsp--;
                    if (failed) return e;
                    if ( backtracking==0 ) {
                       e = ex; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              System.err.println( "atom returns " + e ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end atom


    // $ANTLR start arg_list
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:160:1: arg_list returns [List args] : first= expr ( ',' other= expr )* ;
    public List arg_list() throws RecognitionException {   
        List args = null;

        Expr first = null;

        Expr other = null;



        		args = new ArrayList();
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:165:3: (first= expr ( ',' other= expr )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:165:3: first= expr ( ',' other= expr )*
            {
            pushFollow(FOLLOW_expr_in_arg_list589);
            first=expr();
            _fsp--;
            if (failed) return args;
            if ( backtracking==0 ) {
               args.add( first ); 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:166:3: ( ',' other= expr )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);
                if ( (LA10_0==24) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:166:6: ',' other= expr
            	    {
            	    match(input,24,FOLLOW_24_in_arg_list599); if (failed) return args;
            	    pushFollow(FOLLOW_expr_in_arg_list603);
            	    other=expr();
            	    _fsp--;
            	    if (failed) return args;
            	    if ( backtracking==0 ) {
            	       args.add( other ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return args;
    }
    // $ANTLR end arg_list


    // $ANTLR start object_expr
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:171:1: object_expr returns [Expr e] : i= IDENT ( ( '[' ~ '[' )=> '[' expr ']' | '.' n= IDENT ( '(' (a= arg_list )? ')' )? )* ;
    public Expr object_expr() throws RecognitionException {   
        Expr e = null;

        Token i=null;
        Token n=null;
        List a = null;



        		e = null;
        		boolean isMethod = false;
        		Expr[] paramExprs = new Expr[0];
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:178:3: (i= IDENT ( ( '[' ~ '[' )=> '[' expr ']' | '.' n= IDENT ( '(' (a= arg_list )? ')' )? )* )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:178:3: i= IDENT ( ( '[' ~ '[' )=> '[' expr ']' | '.' n= IDENT ( '(' (a= arg_list )? ')' )? )*
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_object_expr640); if (failed) return e;
            if ( backtracking==0 ) {
               e = factory.createExternalVariable( i.getText() ); 
            }
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:179:3: ( ( '[' ~ '[' )=> '[' expr ']' | '.' n= IDENT ( '(' (a= arg_list )? ')' )? )*
            loop13:
            do {
                int alt13=3;
                int LA13_0 = input.LA(1);
                if ( (LA13_0==25) ) {
                    alt13=1;
                }
                else if ( (LA13_0==27) ) {
                    alt13=2;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:179:5: ( '[' ~ '[' )=> '[' expr ']'
            	    {
            	    match(input,25,FOLLOW_25_in_object_expr658); if (failed) return e;
            	    pushFollow(FOLLOW_expr_in_object_expr660);
            	    expr();
            	    _fsp--;
            	    if (failed) return e;
            	    match(input,26,FOLLOW_26_in_object_expr662); if (failed) return e;

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:180:5: '.' n= IDENT ( '(' (a= arg_list )? ')' )?
            	    {
            	    match(input,27,FOLLOW_27_in_object_expr668); if (failed) return e;
            	    n=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_object_expr672); if (failed) return e;
            	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:181:4: ( '(' (a= arg_list )? ')' )?
            	    int alt12=2;
            	    int LA12_0 = input.LA(1);
            	    if ( (LA12_0==22) ) {
            	        alt12=1;
            	    }
            	    switch (alt12) {
            	        case 1 :
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:181:6: '(' (a= arg_list )? ')'
            	            {
            	            match(input,22,FOLLOW_22_in_object_expr680); if (failed) return e;
            	            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:182:6: (a= arg_list )?
            	            int alt11=2;
            	            int LA11_0 = input.LA(1);
            	            if ( ((LA11_0>=IDENT && LA11_0<=FLOAT)||LA11_0==9||(LA11_0>=20 && LA11_0<=22)||LA11_0==25) ) {
            	                alt11=1;
            	            }
            	            switch (alt11) {
            	                case 1 :
            	                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:182:8: a= arg_list
            	                    {
            	                    pushFollow(FOLLOW_arg_list_in_object_expr691);
            	                    a=arg_list();
            	                    _fsp--;
            	                    if (failed) return e;
            	                    if ( backtracking==0 ) {

            	                      							paramExprs = new Expr[ a.size() ];
            	                      							for ( int j = 0 ; j < paramExprs.length ; ++j ) {
            	                      								paramExprs[j] = (Expr) a.get( j );
            	                      							} 
            	                      						
            	                    }

            	                    }
            	                    break;

            	            }

            	            match(input,23,FOLLOW_23_in_object_expr714); if (failed) return e;
            	            if ( backtracking==0 ) {
            	               isMethod = true; 
            	            }

            	            }
            	            break;

            	    }

            	    if ( backtracking==0 ) {

            	      				String name = n.getText();
            	      				if ( ! isMethod ) {
            	      					name = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
            	      				}
            	      				e = new MethodCall( e, name, paramExprs );
            	      				paramExprs = new Expr[0];
            	      				isMethod = false;
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return e;
    }
    // $ANTLR end object_expr


    // $ANTLR start cast
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:205:1: cast returns [Class type] : '(' i= IDENT ')' ;
    public Class cast() throws RecognitionException {   
        Class type = null;

        Token i=null;


        		type = null;
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:210:3: ( '(' i= IDENT ')' )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:210:3: '(' i= IDENT ')'
            {
            match(input,22,FOLLOW_22_in_cast766); if (failed) return type;
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_cast770); if (failed) return type;
            match(input,23,FOLLOW_23_in_cast772); if (failed) return type;
            if ( backtracking==0 ) {

              			try {
              				type = typeResolver.resolveType( i.getText() );
              				System.err.println( "CASTING TO " + type);
              			} catch (ClassNotFoundException e) {
              				System.err.println( e.getMessage() );
              			}
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return type;
    }
    // $ANTLR end cast


    // $ANTLR start map
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:221:1: map returns [AnonMapValue m] : '{' (k= expr '=>' v= expr ( ',' k= expr '=>' v= expr )* ( ',' )? )? '}' ;
    public AnonMapValue map() throws RecognitionException {   
        AnonMapValue m = null;

        Expr k = null;

        Expr v = null;



        		m = null;
        		List pairs = new ArrayList();
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:227:3: ( '{' (k= expr '=>' v= expr ( ',' k= expr '=>' v= expr )* ( ',' )? )? '}' )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:227:3: '{' (k= expr '=>' v= expr ( ',' k= expr '=>' v= expr )* ( ',' )? )? '}'
            {
            match(input,9,FOLLOW_9_in_map799); if (failed) return m;
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:228:4: (k= expr '=>' v= expr ( ',' k= expr '=>' v= expr )* ( ',' )? )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( ((LA16_0>=IDENT && LA16_0<=FLOAT)||LA16_0==9||(LA16_0>=20 && LA16_0<=22)||LA16_0==25) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:228:6: k= expr '=>' v= expr ( ',' k= expr '=>' v= expr )* ( ',' )?
                    {
                    pushFollow(FOLLOW_expr_in_map808);
                    k=expr();
                    _fsp--;
                    if (failed) return m;
                    match(input,28,FOLLOW_28_in_map810); if (failed) return m;
                    pushFollow(FOLLOW_expr_in_map814);
                    v=expr();
                    _fsp--;
                    if (failed) return m;
                    if ( backtracking==0 ) {
                       pairs.add( new AnonMapValue.KeyValuePair( k, v ) ); 
                    }
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:230:5: ( ',' k= expr '=>' v= expr )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);
                        if ( (LA14_0==24) ) {
                            int LA14_1 = input.LA(2);
                            if ( ((LA14_1>=IDENT && LA14_1<=FLOAT)||LA14_1==9||(LA14_1>=20 && LA14_1<=22)||LA14_1==25) ) {
                                alt14=1;
                            }


                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:230:7: ',' k= expr '=>' v= expr
                    	    {
                    	    match(input,24,FOLLOW_24_in_map829); if (failed) return m;
                    	    pushFollow(FOLLOW_expr_in_map839);
                    	    k=expr();
                    	    _fsp--;
                    	    if (failed) return m;
                    	    match(input,28,FOLLOW_28_in_map841); if (failed) return m;
                    	    pushFollow(FOLLOW_expr_in_map845);
                    	    v=expr();
                    	    _fsp--;
                    	    if (failed) return m;
                    	    if ( backtracking==0 ) {
                    	       pairs.add( new AnonMapValue.KeyValuePair( k, v ) ); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:234:5: ( ',' )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);
                    if ( (LA15_0==24) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:234:5: ','
                            {
                            match(input,24,FOLLOW_24_in_map866); if (failed) return m;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,10,FOLLOW_10_in_map878); if (failed) return m;
            if ( backtracking==0 ) {

              			m = new AnonMapValue( (AnonMapValue.KeyValuePair[]) pairs.toArray( new AnonMapValue.KeyValuePair[ pairs.size() ] ) );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return m;
    }
    // $ANTLR end map


    // $ANTLR start list
    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:243:1: list returns [AnonListValue l] : '[' (v= expr ( ',' v= expr )* ( ',' )? )? ']' ;
    public AnonListValue list() throws RecognitionException {   
        AnonListValue l = null;

        Expr v = null;



        		l = null;
        		List values = new ArrayList();
        	
        try {
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:249:3: ( '[' (v= expr ( ',' v= expr )* ( ',' )? )? ']' )
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:249:3: '[' (v= expr ( ',' v= expr )* ( ',' )? )? ']'
            {
            match(input,25,FOLLOW_25_in_list906); if (failed) return l;
            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:249:7: (v= expr ( ',' v= expr )* ( ',' )? )?
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( ((LA19_0>=IDENT && LA19_0<=FLOAT)||LA19_0==9||(LA19_0>=20 && LA19_0<=22)||LA19_0==25) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:249:9: v= expr ( ',' v= expr )* ( ',' )?
                    {
                    pushFollow(FOLLOW_expr_in_list912);
                    v=expr();
                    _fsp--;
                    if (failed) return l;
                    if ( backtracking==0 ) {
                       values.add( v ); 
                    }
                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:251:5: ( ',' v= expr )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);
                        if ( (LA17_0==24) ) {
                            int LA17_1 = input.LA(2);
                            if ( ((LA17_1>=IDENT && LA17_1<=FLOAT)||LA17_1==9||(LA17_1>=20 && LA17_1<=22)||LA17_1==25) ) {
                                alt17=1;
                            }


                        }


                        switch (alt17) {
                    	case 1 :
                    	    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:251:7: ',' v= expr
                    	    {
                    	    match(input,24,FOLLOW_24_in_list926); if (failed) return l;
                    	    pushFollow(FOLLOW_expr_in_list930);
                    	    v=expr();
                    	    _fsp--;
                    	    if (failed) return l;
                    	    if ( backtracking==0 ) {
                    	       values.add( v ); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:254:5: ( ',' )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);
                    if ( (LA18_0==24) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g:254:5: ','
                            {
                            match(input,24,FOLLOW_24_in_list951); if (failed) return l;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_list964); if (failed) return l;
            if ( backtracking==0 ) {

              			l = new AnonListValue( values );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return l;
    }
    // $ANTLR end list


 

    public static final BitSet FOLLOW_statements_in_compilation_unit41 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_statements58 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_statements60 = new BitSet(new long[]{0x0000000002700AF2L});
    public static final BitSet FOLLOW_expr_in_statement78 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_in_statement_in_statement84 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_statement_block102 = new BitSet(new long[]{0x0000000002700EF0L});
    public static final BitSet FOLLOW_statements_in_statement_block104 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_statement_block106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_for_in_statement120 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENT_in_for_in_statement122 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_for_in_statement124 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_for_in_statement126 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_statement_block_in_for_in_statement128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_object_expr_in_assignment_statement141 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_assignment_statement143 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_assignment_statement145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logical_or_expr_in_expr174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logical_and_expr_in_logical_or_expr206 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_logical_or_expr214 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_logical_and_expr_in_logical_or_expr218 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_additive_expr_in_logical_and_expr255 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_logical_and_expr263 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_additive_expr_in_logical_and_expr267 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_multiplicative_expr_in_additive_expr305 = new BitSet(new long[]{0x0000000000030002L});
    public static final BitSet FOLLOW_16_in_additive_expr319 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_17_in_additive_expr328 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_multiplicative_expr_in_additive_expr343 = new BitSet(new long[]{0x0000000000030002L});
    public static final BitSet FOLLOW_atom_in_multiplicative_expr383 = new BitSet(new long[]{0x00000000000C0002L});
    public static final BitSet FOLLOW_18_in_multiplicative_expr396 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_19_in_multiplicative_expr406 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_atom_in_multiplicative_expr421 = new BitSet(new long[]{0x00000000000C0002L});
    public static final BitSet FOLLOW_INTEGER_in_atom463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_atom473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_atom484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_atom494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_atom505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_atom515 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_atom519 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_atom521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_atom531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_map_in_atom541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_object_expr_in_atom551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_arg_list589 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_arg_list599 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_arg_list603 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_IDENT_in_object_expr640 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_25_in_object_expr658 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_object_expr660 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_object_expr662 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_27_in_object_expr668 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENT_in_object_expr672 = new BitSet(new long[]{0x000000000A400002L});
    public static final BitSet FOLLOW_22_in_object_expr680 = new BitSet(new long[]{0x0000000002F002F0L});
    public static final BitSet FOLLOW_arg_list_in_object_expr691 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_object_expr714 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_22_in_cast766 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENT_in_cast770 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_cast772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_map799 = new BitSet(new long[]{0x00000000027006F0L});
    public static final BitSet FOLLOW_expr_in_map808 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_map810 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_map814 = new BitSet(new long[]{0x0000000001000400L});
    public static final BitSet FOLLOW_24_in_map829 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_map839 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_map841 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_map845 = new BitSet(new long[]{0x0000000001000400L});
    public static final BitSet FOLLOW_24_in_map866 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_map878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_list906 = new BitSet(new long[]{0x00000000067002F0L});
    public static final BitSet FOLLOW_expr_in_list912 = new BitSet(new long[]{0x0000000005000000L});
    public static final BitSet FOLLOW_24_in_list926 = new BitSet(new long[]{0x00000000027002F0L});
    public static final BitSet FOLLOW_expr_in_list930 = new BitSet(new long[]{0x0000000005000000L});
    public static final BitSet FOLLOW_24_in_list951 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_list964 = new BitSet(new long[]{0x0000000000000002L});

}