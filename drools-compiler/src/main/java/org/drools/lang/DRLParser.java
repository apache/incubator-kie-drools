// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-04-24 04:47:07

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.compiler.SwitchingCommonTokenStream;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "LOCK_ON_ACTIVE", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "ID", "OR", "LEFT_PAREN", "RIGHT_PAREN", "CONTAINS", "MATCHES", "EXCLUDES", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.'", "'.*'", "','", "':'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'&&'"
    };
    public static final int PACKAGE=4;
    public static final int FUNCTION=6;
    public static final int ACCUMULATE=28;
    public static final int RIGHT_SQUARE=45;
    public static final int ACTIVATION_GROUP=23;
    public static final int ATTRIBUTES=13;
    public static final int RIGHT_CURLY=43;
    public static final int CONTAINS=37;
    public static final int NO_LOOP=21;
    public static final int LOCK_ON_ACTIVE=27;
    public static final int AGENDA_GROUP=25;
    public static final int FLOAT=40;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=59;
    public static final int NOT=49;
    public static final int AND=46;
    public static final int ID=33;
    public static final int EOF=-1;
    public static final int HexDigit=56;
    public static final int DATE_EFFECTIVE=14;
    public static final int ACTION=30;
    public static final int RIGHT_PAREN=36;
    public static final int IMPORT=5;
    public static final int EOL=53;
    public static final int THEN=52;
    public static final int MATCHES=38;
    public static final int ENABLED=17;
    public static final int EXISTS=48;
    public static final int RULE=11;
    public static final int EXCLUDES=39;
    public static final int AUTO_FOCUS=22;
    public static final int NULL=41;
    public static final int BOOL=18;
    public static final int FORALL=51;
    public static final int SALIENCE=19;
    public static final int RULEFLOW_GROUP=24;
    public static final int RESULT=31;
    public static final int INT=20;
    public static final int MULTI_LINE_COMMENT=61;
    public static final int DURATION=26;
    public static final int WS=54;
    public static final int EVAL=50;
    public static final int TEMPLATE=10;
    public static final int WHEN=12;
    public static final int UnicodeEscape=57;
    public static final int LEFT_CURLY=42;
    public static final int OR=34;
    public static final int LEFT_PAREN=35;
    public static final int QUERY=8;
    public static final int MISC=62;
    public static final int FROM=47;
    public static final int END=9;
    public static final int GLOBAL=7;
    public static final int COLLECT=32;
    public static final int LEFT_SQUARE=44;
    public static final int INIT=29;
    public static final int OctalEscape=58;
    public static final int EscapeSequence=55;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=60;
    public static final int DATE_EXPIRES=16;
    public static final int STRING=15;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[192+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	
    	// THE FOLLOWING LINE IS A DUMMY ATTRIBUTE TO WORK AROUND AN ANTLR BUG
    	private BaseDescr from = null;
    	
    	public void setParserDebug(boolean parserDebug) {
    		this.parserDebug = parserDebug;
    	}
    	
    	public void debug(String message) {
    		if ( parserDebug ) 
    			System.err.println( "drl parser: " + message );
    	}
    	
    	public void setSource(String source) {
    		this.source = source;
    	}
    	public DescrFactory getFactory() {
    		return factory;
    	}	
    
    	public String getSource() {
    		return this.source;
    	}
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}
    	
    	private int offset(int line) {
    		return line + lineOffset;
    	}
    	
    	/**
    	 * This will set the offset to record when reparsing. Normally is zero of course 
    	 */
    	public void setLineOffset(int i) {
    	 	this.lineOffset = i;
    	}
    	
    	private String getString(Token token) {
    		String orig = token.getText();
    		return orig.substring( 1, orig.length() -1 );
    	}
    	
    	public void reportError(RecognitionException ex) {
    	        // if we've already reported an error and have not matched a token
                    // yet successfully, don't report any errors.
                    if ( errorRecovery ) {
                            return;
                    }
                    errorRecovery = true;
    
    		ex.line = offset(ex.line); //add the offset if there is one
    		errors.add( ex ); 
    	}
         	
         	/** return the raw RecognitionException errors */
         	public List getErrors() {
         		return errors;
         	}
         	
         	/** Return a list of pretty strings summarising the errors */
         	public List getErrorMessages() {
         		List messages = new ArrayList();
     		for ( Iterator errorIter = errors.iterator() ; errorIter.hasNext() ; ) {
         	     		messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
         	     	}
         	     	return messages;
         	}
         	
         	/** return true if any parser errors were accumulated */
         	public boolean hasErrors() {
      		return ! errors.isEmpty();
         	}
         	
         	/** This will take a RecognitionException, and create a sensible error message out of it */
         	public String createErrorMessage(RecognitionException e)
            {
    		StringBuffer message = new StringBuffer();		
                    message.append( source + ":"+e.line+":"+e.charPositionInLine+" ");
                    if ( e instanceof MismatchedTokenException ) {
                            MismatchedTokenException mte = (MismatchedTokenException)e;
                            message.append("mismatched token: "+
                                                               e.token+
                                                               "; expecting type "+
                                                               tokenNames[mte.expecting]);
                    }
                    else if ( e instanceof MismatchedTreeNodeException ) {
                            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                            message.append("mismatched tree node: "+
                                                               //mtne.foundNode+  //FIXME
                                                               "; expecting type "+
                                                               tokenNames[mtne.expecting]);
                    }
                    else if ( e instanceof NoViableAltException ) {
                            NoViableAltException nvae = (NoViableAltException)e;
    			message.append( "Unexpected token '" + e.token.getText() + "'" );
                            /*
                            message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
                                                               " state "+nvae.stateNumber+
                                                               " (decision="+nvae.decisionNumber+
                                                               ") no viable alt; token="+
                                                               e.token);
                                                               */
                    }
                    else if ( e instanceof EarlyExitException ) {
                            EarlyExitException eee = (EarlyExitException)e;
                            message.append("required (...)+ loop (decision="+
                                                               eee.decisionNumber+
                                                               ") did not match anything; token="+
                                                               e.token);
                    }
                    else if ( e instanceof MismatchedSetException ) {
                            MismatchedSetException mse = (MismatchedSetException)e;
                            message.append("mismatched token '"+
                                                               e.token+
                                                               "' expecting set "+mse.expecting);
                    }
                    else if ( e instanceof MismatchedNotSetException ) {
                            MismatchedNotSetException mse = (MismatchedNotSetException)e;
                            message.append("mismatched token '"+
                                                               e.token+
                                                               "' expecting set "+mse.expecting);
                    }
                    else if ( e instanceof FailedPredicateException ) {
                            FailedPredicateException fpe = (FailedPredicateException)e;
                            message.append("rule "+fpe.ruleName+" failed predicate: {"+
                                                               fpe.predicateText+"}?");
                    } else if (e instanceof GeneralParseException) {
    			message.append(" " + e.getMessage());
    		}
                   	return message.toString();
            }   
            
            void checkTrailingSemicolon(String text, int line) {
            	if (text.trim().endsWith( ";" ) ) {
            		this.errors.add( new GeneralParseException( "Trailing semi-colon not allowed", offset(line) ) );
            	}
            }
          



    // $ANTLR start opt_semicolon
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:168:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ';' )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ';' )?
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==63) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ';'
                    {
                    match(input,63,FOLLOW_63_in_opt_semicolon46); if (failed) return ;

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
    // $ANTLR end opt_semicolon


    // $ANTLR start compilation_unit
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:172:1: compilation_unit : prolog ( statement )+ ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: ( prolog ( statement )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: prolog ( statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:3: ( statement )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=IMPORT && LA2_0<=QUERY)||(LA2_0>=TEMPLATE && LA2_0<=RULE)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:5: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit65);
            	    statement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
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
    // $ANTLR end compilation_unit


    // $ANTLR start prolog
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:177:1: prolog : (n= package_statement )? ;
    public final void prolog() throws RecognitionException {
        String n = null;


        
        		String packageName = "";
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( (n= package_statement )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: (n= package_statement )?
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: (n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:6: n= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog90);
                    n=package_statement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       packageName = n; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			this.packageDescr = factory.createPackage( packageName ); 
              		
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
    // $ANTLR end prolog


    // $ANTLR start statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) ;
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case IMPORT:
                {
                int LA4_1 = input.LA(2);

                if ( (synpred4()) ) {
                    alt4=1;
                }
                else if ( (synpred5()) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("189:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 4, 1, input);

                    throw nvae;
                }
                }
                break;
            case GLOBAL:
                {
                alt4=3;
                }
                break;
            case FUNCTION:
                {
                alt4=4;
                }
                break;
            case TEMPLATE:
                {
                alt4=5;
                }
                break;
            case RULE:
                {
                alt4=6;
                }
                break;
            case QUERY:
                {
                alt4=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("189:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement114);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement120);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:191:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement126);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:192:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement132);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:193:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement146);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement155);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( r != null ) this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:195:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement167);
                    q=query();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( q != null ) this.packageDescr.addRule( q ); 
                    }

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


    // $ANTLR start package_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;


        
        		packageName = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement196); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement200);
            n=dotted_name(null);
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement203);
            opt_semicolon();
            _fsp--;
            if (failed) return packageName;
            if ( backtracking==0 ) {
              
              			packageName = n;
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return packageName;
    }
    // $ANTLR end package_statement


    // $ANTLR start import_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:211:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token imp=null;

        
                	ImportDescr importDecl = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: imp= IMPORT import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement235); if (failed) return ;
            if ( backtracking==0 ) {
              
              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement258);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement261);
            opt_semicolon();
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
    // $ANTLR end import_statement


    // $ANTLR start function_import_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:226:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token imp=null;

        
                	FunctionImportDescr importDecl = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement287); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement289); if (failed) return ;
            if ( backtracking==0 ) {
              
              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement312);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement315);
            opt_semicolon();
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
    // $ANTLR end function_import_statement


    // $ANTLR start import_name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:242:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( '.' id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token id = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: (id= identifier ( '.' id= identifier )* (star= '.*' )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: id= identifier ( '.' id= identifier )* (star= '.*' )?
            {
            pushFollow(FOLLOW_identifier_in_import_name343);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:3: ( '.' id= identifier )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==64) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:5: '.' id= identifier
            	    {
            	    match(input,64,FOLLOW_64_in_import_name355); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name359);
            	    id=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "." + id.getText(); 
            	      			importDecl.setTarget( name );
            	      		        importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:3: (star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==65) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,65,FOLLOW_65_in_import_name383); if (failed) return name;
                    if ( backtracking==0 ) {
                       
                      		        name = name + star.getText(); 
                      			importDecl.setTarget( name );
                      		        importDecl.setEndCharacter( ((CommonToken)star).getStopIndex() );
                      		    
                    }

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
        return name;
    }
    // $ANTLR end import_name


    // $ANTLR start global
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:270:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token loc=null;
        String type = null;

        Token id = null;


        
        	    GlobalDescr global = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global419); if (failed) return ;
            if ( backtracking==0 ) {
              
              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global430);
            type=dotted_name(null);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global442);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global444);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		    global.setIdentifier( id.getText() );
              		    global.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
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
    // $ANTLR end global


    // $ANTLR start function
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:293:1: function : loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public final void function() throws RecognitionException {
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: (loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function471); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:16: (retType= dotted_name[null] )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);

                if ( ((LA7_1>=PACKAGE && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||LA7_1==DURATION||(LA7_1>=ACCUMULATE && LA7_1<=OR)||(LA7_1>=CONTAINS && LA7_1<=EXCLUDES)||LA7_1==NULL||LA7_1==LEFT_SQUARE||(LA7_1>=AND && LA7_1<=THEN)||LA7_1==64) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:17: retType= dotted_name[null]
                    {
                    pushFollow(FOLLOW_dotted_name_in_function476);
                    retType=dotted_name(null);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function483);
            n=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              			//System.err.println( "function :: " + n.getText() );
              			f = factory.createFunction( n.getText(), retType );
              			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function492); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=PACKAGE && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||LA11_0==DURATION||(LA11_0>=ACCUMULATE && LA11_0<=OR)||(LA11_0>=CONTAINS && LA11_0<=EXCLUDES)||LA11_0==NULL||(LA11_0>=AND && LA11_0<=THEN)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: (paramType= dotted_name[null] )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:7: paramType= dotted_name[null]
                            {
                            pushFollow(FOLLOW_dotted_name_in_function502);
                            paramType=dotted_name(null);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function509);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:5: ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==66) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:7: ',' (paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,66,FOLLOW_66_in_function523); if (failed) return ;
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:11: (paramType= dotted_name[null] )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:12: paramType= dotted_name[null]
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function528);
                    	            paramType=dotted_name(null);
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function535);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	      
                    	      						f.addParameter( paramType, paramName );
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function559); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function565);
            body=curly_chunk(f);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              			//strip out '{','}'
              			f.setText( body.substring( 1, body.length()-1 ) );
              		
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
    // $ANTLR end function


    // $ANTLR start query
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:327:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
            {
            loc=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query597); if (failed) return query;
            pushFollow(FOLLOW_name_in_query601);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName ); 
              			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:341:3: ( normal_lhs_block[lhs] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:342:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query614);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query631); if (failed) return query;
            if ( backtracking==0 ) {
              
              			query.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return query;
    }
    // $ANTLR end query


    // $ANTLR start template
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:352:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: (loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template661); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template665);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template667);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:363:3: (slot= template_slot )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ID) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:364:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template682);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {
            	      
            	      				template.addFieldTemplate(slot);
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template699); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template701);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return template;
    }
    // $ANTLR end template


    // $ANTLR start template_slot
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:375:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;


        
        		field = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
            if ( backtracking==0 ) {
              
              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_dotted_name_in_template_slot747);
            fieldType=dotted_name(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {
              
              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot765);
            n=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot767);
            opt_semicolon();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {
              
              		        field.setName( n.getText() );
              			field.setLocation( offset(n.getLine()), n.getCharPositionInLine() );
              			field.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return field;
    }
    // $ANTLR end template_slot


    // $ANTLR start rule
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:396:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: (loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule800); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule804);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule813);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:3: (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:5: loc= WHEN ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule822); if (failed) return rule;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:14: ( ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==67) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ':'
                            {
                            match(input,67,FOLLOW_67_in_rule824); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:417:4: ( normal_lhs_block[lhs] )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:419:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule842);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule863);
            rhs_chunk(rule);
            _fsp--;
            if (failed) return rule;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rule;
    }
    // $ANTLR end rule


    // $ANTLR start rule_attributes
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;


        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ATTRIBUTES ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ATTRIBUTES) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes884); if (failed) return ;
                    match(input,67,FOLLOW_67_in_rule_attributes886); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:4: ( ( ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==DATE_EFFECTIVE||(LA17_0>=DATE_EXPIRES && LA17_0<=ENABLED)||LA17_0==SALIENCE||(LA17_0>=NO_LOOP && LA17_0<=LOCK_ON_ACTIVE)||LA17_0==66) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ',' )? a= rule_attribute
            	    {
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);

            	    if ( (LA16_0==66) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ','
            	            {
            	            match(input,66,FOLLOW_66_in_rule_attributes895); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes900);
            	    a=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      					rule.addAttribute( a );
            	      				
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:440:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active )
            int alt18=11;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt18=1;
                }
                break;
            case NO_LOOP:
                {
                alt18=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt18=3;
                }
                break;
            case DURATION:
                {
                alt18=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt18=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt18=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt18=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt18=8;
                }
                break;
            case ENABLED:
                {
                alt18=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt18=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt18=11;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("440:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute941);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute951);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:447:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute962);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute975);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:449:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute989);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:450:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1000);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:451:5: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1011);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:452:5: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1021);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:453:5: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1031);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
                    }

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:454:5: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1041);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:455:5: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1051);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end rule_attribute


    // $ANTLR start date_effective
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:459:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:3: (loc= DATE_EFFECTIVE val= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1082); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1086); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "date-effective", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end date_effective


    // $ANTLR start date_expires
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:474:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:3: (loc= DATE_EXPIRES val= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1119); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1123); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "date-expires", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end date_expires


    // $ANTLR start enabled
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:490:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:4: (loc= ENABLED t= BOOL )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1158); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1162); if (failed) return d;
            if ( backtracking==0 ) {
              
              				d = new AttributeDescr( "enabled", t.getText() );
              				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
              			
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end enabled


    // $ANTLR start salience
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:508:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:3: (loc= SALIENCE i= INT )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1207); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1211); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "salience", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end salience


    // $ANTLR start no_loop
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:522:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==NO_LOOP) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==EOF||LA19_1==WHEN||LA19_1==DATE_EFFECTIVE||(LA19_1>=DATE_EXPIRES && LA19_1<=ENABLED)||LA19_1==SALIENCE||(LA19_1>=NO_LOOP && LA19_1<=LOCK_ON_ACTIVE)||LA19_1==THEN||LA19_1==66) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("522:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("522:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: (loc= NO_LOOP )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: (loc= NO_LOOP )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:528:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1249); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "no-loop", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:3: (loc= NO_LOOP t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:3: (loc= NO_LOOP t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:538:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1277); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1281); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "no-loop", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end no_loop


    // $ANTLR start auto_focus
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==AUTO_FOCUS) ) {
                int LA20_1 = input.LA(2);

                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==EOF||LA20_1==WHEN||LA20_1==DATE_EFFECTIVE||(LA20_1>=DATE_EXPIRES && LA20_1<=ENABLED)||LA20_1==SALIENCE||(LA20_1>=NO_LOOP && LA20_1<=LOCK_ON_ACTIVE)||LA20_1==THEN||LA20_1==66) ) {
                    alt20=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("550:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("550:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: (loc= AUTO_FOCUS )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: (loc= AUTO_FOCUS )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:556:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1330); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "auto-focus", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:565:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:565:3: (loc= AUTO_FOCUS t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1358); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1362); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "auto-focus", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end auto_focus


    // $ANTLR start activation_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:578:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:583:3: (loc= ACTIVATION_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:583:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1407); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1411); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "activation-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end activation_group


    // $ANTLR start ruleflow_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:592:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:597:3: (loc= RULEFLOW_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:597:3: loc= RULEFLOW_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1443); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1447); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "ruleflow-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end ruleflow_group


    // $ANTLR start agenda_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:606:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:611:3: (loc= AGENDA_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:611:3: loc= AGENDA_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1479); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1483); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "agenda-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end agenda_group


    // $ANTLR start duration
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:621:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: (loc= DURATION i= INT )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: loc= DURATION i= INT
            {
            loc=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1518); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1522); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "duration", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end duration


    // $ANTLR start lock_on_active
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==LOCK_ON_ACTIVE) ) {
                int LA21_1 = input.LA(2);

                if ( (LA21_1==BOOL) ) {
                    alt21=2;
                }
                else if ( (LA21_1==EOF||LA21_1==WHEN||LA21_1==DATE_EFFECTIVE||(LA21_1>=DATE_EXPIRES && LA21_1<=ENABLED)||LA21_1==SALIENCE||(LA21_1>=NO_LOOP && LA21_1<=LOCK_ON_ACTIVE)||LA21_1==THEN||LA21_1==66) ) {
                    alt21=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("637:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 21, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("637:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: (loc= LOCK_ON_ACTIVE )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: (loc= LOCK_ON_ACTIVE )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:643:4: loc= LOCK_ON_ACTIVE
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1564); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "lock-on-active", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:652:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:652:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:653:4: loc= LOCK_ON_ACTIVE t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1592); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1596); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      				d = new AttributeDescr( "lock-on-active", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lock_on_active


    // $ANTLR start normal_lhs_block
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:665:1: normal_lhs_block[AndDescr descr] : (d= lhs[descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: ( (d= lhs[descr] )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: (d= lhs[descr] )*
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: (d= lhs[descr] )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==ID||LA22_0==LEFT_PAREN||(LA22_0>=EXISTS && LA22_0<=FORALL)) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:5: d= lhs[descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1634);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descr.addDescr( d ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop22;
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
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:673:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:677:4: (l= lhs_or )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:677:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1671);
            l=lhs_or();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = l; 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs


    // $ANTLR start lhs_pattern
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:681:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:685:4: (f= fact_binding | f= fact )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==ID) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==67) ) {
                    alt23=1;
                }
                else if ( (LA23_1==LEFT_PAREN||LA23_1==LEFT_SQUARE||LA23_1==64) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("681:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("681:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:685:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern1699);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:686:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern1708);
                    f=fact();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_pattern


    // $ANTLR start from_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:689:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:694:2: (ds= from_source[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:694:2: ds= from_source[d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement1735);
            ds=from_source(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              			d.setDataSource(ds);
              		
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end from_statement


    // $ANTLR start from_source
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:704:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:3: (ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:3: ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source1777);
            ident=identifier();
            _fsp--;
            if (failed) return ds;
            if ( backtracking==0 ) {
              
              			ad = new AccessorDescr(ident.getText());	
              			ad.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );
              			ad.setStartCharacter( ((CommonToken)ident).getStartIndex() );
              			ad.setEndCharacter( ((CommonToken)ident).getStopIndex() );
              			ds = ad;
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:3: (args= paren_chunk[from] )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==LEFT_PAREN) ) {
                int LA24_1 = input.LA(2);

                if ( (synpred38()) ) {
                    alt24=1;
                }
            }
            switch (alt24) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: args= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source1788);
                    args=paren_chunk(from);
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {
                      
                      			if( args != null ) {
                      				ad.setVariableName( null );
                      				FunctionCallDescr fc = new FunctionCallDescr(ident.getText());
                      				fc.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );			
                      				fc.setArguments(args);
                      				fc.setStartCharacter( ((CommonToken)ident).getStartIndex() );
                      				fc.setEndCharacter( ((CommonToken)ident).getStopIndex() );
                      				ad.addInvoker(fc);
                      			}
                      		
                    }

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:731:3: ( expression_chain[from, ad] )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==64) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: expression_chain[from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1802);
                    expression_chain(from,  ad);
                    _fsp--;
                    if (failed) return ds;

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
        return ds;
    }
    // $ANTLR end from_source


    // $ANTLR start expression_chain
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:734:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )?
            {
            match(input,64,FOLLOW_64_in_expression_chain1827); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain1831);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:747:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
            int alt26=3;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==LEFT_SQUARE) && (synpred40())) {
                alt26=1;
            }
            else if ( (LA26_0==LEFT_PAREN) ) {
                int LA26_2 = input.LA(2);

                if ( (synpred41()) ) {
                    alt26=2;
                }
            }
            switch (alt26) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:748:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1862);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1896);
                    paarg=paren_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	    	  ma = new MethodAccessDescr( field.getText(), paarg );	
                      		  ma.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
                      		  ma.setStartCharacter( ((CommonToken)field).getStartIndex() );
                      		
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              
              	      if( ma != null ) {
              	          as.addInvoker( ma );
              	      } else {
              	          as.addInvoker( fa );
              	      }
              	  
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:767:4: ( expression_chain[from, as] )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==64) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1917);
                    expression_chain(from,  as);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

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
        return ;
    }
    // $ANTLR end expression_chain


    // $ANTLR start accumulate_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:771:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:10: (loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:10: loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement1958); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1968); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_accumulate_statement1972);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement1974); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourcePattern( (PatternDescr)pattern );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement1983); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1987);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement1990); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement1999); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2003);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement2006); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2015); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2019);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2024); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setResultCode( text.substring(1, text.length()-1) );
              			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end accumulate_statement


    // $ANTLR start collect_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:800:1: collect_statement returns [CollectDescr d] : loc= COLLECT '(' pattern= lhs_pattern loc= ')' ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;


        
        		d = factory.createCollect();
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: (loc= COLLECT '(' pattern= lhs_pattern loc= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: loc= COLLECT '(' pattern= lhs_pattern loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement2067); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2077); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_collect_statement2081);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2085); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourcePattern( (PatternDescr)pattern );
              			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end collect_statement


    // $ANTLR start fact_binding
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:823:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:823:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2119); if (failed) return d;
            match(input,67,FOLLOW_67_in_fact_binding2121); if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2134);
            fe=fact_expression(id.getText());
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // override previously instantiated pattern
               			d=fe;
               			if( d != null ) {
                 			    d.setStartCharacter( ((CommonToken)id).getStartIndex() );
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
        return d;
    }
    // $ANTLR end fact_binding


    // $ANTLR start fact_expression
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:839:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* );
    public final BaseDescr fact_expression(String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:5: ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LEFT_PAREN) ) {
                alt29=1;
            }
            else if ( (LA29_0==ID) ) {
                alt29=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("839:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:5: '(' fe= fact_expression[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2166); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2170);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2173); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:845:6: f= fact ( ( OR | '||' ) f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2184);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((PatternDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:4: ( ( OR | '||' ) f= fact )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==OR||LA28_0==68) ) {
                            int LA28_6 = input.LA(2);

                            if ( (synpred45()) ) {
                                alt28=1;
                            }


                        }


                        switch (alt28) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: ( OR | '||' ) f= fact
                    	    {
                    	    if ( input.LA(1)==OR||input.LA(1)==68 ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2196);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2214);
                    	    f=fact();
                    	    _fsp--;
                    	    if (failed) return pd;
                    	    if ( backtracking==0 ) {
                    	      
                    	       				((PatternDescr)f).setIdentifier( id );
                    	       				((OrDescr)pd).addDescr( f );
                    	       			
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return pd;
    }
    // $ANTLR end fact_expression


    // $ANTLR start fact
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN ;
    public final BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;


        
        		d=null;
        		PatternDescr pattern = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:11: (id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:11: id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
               			pattern = new PatternDescr( );
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2275);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        pattern.setObjectType( id );
               		        pattern.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2289); if (failed) return d;
            if ( backtracking==0 ) {
              
               				pattern.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        pattern.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:4: ( constraints[pattern] )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0>=PACKAGE && LA30_0<=ATTRIBUTES)||LA30_0==ENABLED||LA30_0==SALIENCE||LA30_0==DURATION||(LA30_0>=ACCUMULATE && LA30_0<=LEFT_PAREN)||(LA30_0>=CONTAINS && LA30_0<=EXCLUDES)||LA30_0==NULL||(LA30_0>=AND && LA30_0<=THEN)) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2299);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2312); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        if( endLoc.getType() == RIGHT_PAREN ) {
              				pattern.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
              				pattern.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
               			        pattern.setRightParentCharacter( ((CommonToken)endLoc).getStartIndex() );
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
        return d;
    }
    // $ANTLR end fact


    // $ANTLR start constraints
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:897:1: constraints[PatternDescr pattern] : ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )*
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( constraint[pattern] | predicate[pattern] )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( ((LA31_0>=PACKAGE && LA31_0<=ATTRIBUTES)||LA31_0==ENABLED||LA31_0==SALIENCE||LA31_0==DURATION||(LA31_0>=ACCUMULATE && LA31_0<=OR)||(LA31_0>=CONTAINS && LA31_0<=EXCLUDES)||LA31_0==NULL||(LA31_0>=AND && LA31_0<=THEN)) ) {
                alt31=1;
            }
            else if ( (LA31_0==LEFT_PAREN) ) {
                alt31=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("898:4: ( constraint[pattern] | predicate[pattern] )", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:5: constraint[pattern]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2333);
                    constraint(pattern);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:25: predicate[pattern]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2336);
                    predicate(pattern);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:3: ( ',' ( constraint[pattern] | predicate[pattern] ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==66) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:5: ',' ( constraint[pattern] | predicate[pattern] )
            	    {
            	    match(input,66,FOLLOW_66_in_constraints2344); if (failed) return ;
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:9: ( constraint[pattern] | predicate[pattern] )
            	    int alt32=2;
            	    int LA32_0 = input.LA(1);

            	    if ( ((LA32_0>=PACKAGE && LA32_0<=ATTRIBUTES)||LA32_0==ENABLED||LA32_0==SALIENCE||LA32_0==DURATION||(LA32_0>=ACCUMULATE && LA32_0<=OR)||(LA32_0>=CONTAINS && LA32_0<=EXCLUDES)||LA32_0==NULL||(LA32_0>=AND && LA32_0<=THEN)) ) {
            	        alt32=1;
            	    }
            	    else if ( (LA32_0==LEFT_PAREN) ) {
            	        alt32=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("899:9: ( constraint[pattern] | predicate[pattern] )", 32, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt32) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:10: constraint[pattern]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2347);
            	            constraint(pattern);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:30: predicate[pattern]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2350);
            	            predicate(pattern);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop33;
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
    // $ANTLR end constraints


    // $ANTLR start constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:902:1: constraint[PatternDescr pattern] : (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )? ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {
        Token fb=null;
        Token con=null;
        Token f = null;

        RestrictionDescr rd = null;


        
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: ( (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )?
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: (fb= ID ':' )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) ) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==67) ) {
                    alt34=1;
                }
            }
            switch (alt34) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2379); if (failed) return ;
                    match(input,67,FOLLOW_67_in_constraint2381); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( fb.getText() );
                      			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
                      			pattern.addDescr( fbd );
                      
                      		    
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_constraint2402);
            f=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		    if( f != null ) {
              		    
              			if ( fbd != null ) {
              			    fbd.setFieldName( f.getText() );
               			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			
              			// it must be a field constraint, as it is not a binding
              			if( fb == null ) {
              			    pattern.addDescr( fc );
              			}
              		    }
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:936:3: ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )?
            int alt36=3;
            int LA36_0 = input.LA(1);

            if ( ((LA36_0>=CONTAINS && LA36_0<=EXCLUDES)||(LA36_0>=72 && LA36_0<=77)) ) {
                alt36=1;
            }
            else if ( (LA36_0==71) ) {
                alt36=2;
            }
            switch (alt36) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:4: (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:4: (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:6: rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2420);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					fc.addRestriction(rd);
                      					// we must add now as we didn't before
                      					if( fb != null) {
                      					    pattern.addDescr( fc );
                      					}
                      				
                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:5: (con= ( '&' | '|' ) rd= constraint_expression )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( ((LA35_0>=69 && LA35_0<=70)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:6: con= ( '&' | '|' ) rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=69 && input.LA(1)<=70) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2441);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      
                    	      						if (con.getText().equals("&") ) {								
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      						} else {
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      						}							
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2461);
                    	    rd=constraint_expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	      
                    	      					        if( rd != null ) {
                    	      							fc.addRestriction(rd);
                    	      					        }
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:963:4: '->' predicate[pattern]
                    {
                    match(input,71,FOLLOW_71_in_constraint2489); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_constraint2491);
                    predicate(pattern);
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
    // $ANTLR end constraint


    // $ANTLR start constraint_expression
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:967:1: constraint_expression returns [RestrictionDescr rd] : op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public final RestrictionDescr constraint_expression() throws RecognitionException {
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:969:3: (op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:969:3: op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=CONTAINS && input.LA(1)<=EXCLUDES)||(input.LA(1)>=72 && input.LA(1)<=77) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2526);    throw mse;
            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:979:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt37=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==EOF||LA37_1==RIGHT_PAREN||LA37_1==66||(LA37_1>=69 && LA37_1<=70)) ) {
                    alt37=1;
                }
                else if ( (LA37_1==64) ) {
                    alt37=2;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("979:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 37, 1, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt37=3;
                }
                break;
            case LEFT_PAREN:
                {
                alt37=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("979:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:979:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2595); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:984:4: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2611);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:4: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2634);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2648);
                    rvc=retval_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op.getText(), rvc);							
                      			
                    }

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
        return rd;
    }
    // $ANTLR end constraint_expression


    // $ANTLR start literal_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1000:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt38=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt38=1;
                }
                break;
            case INT:
                {
                alt38=2;
                }
                break;
            case FLOAT:
                {
                alt38=3;
                }
                break;
            case BOOL:
                {
                alt38=4;
                }
                break;
            case NULL:
                {
                alt38=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1004:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2687); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1005:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2698); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1006:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2711); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1007:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2722); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1008:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint2734); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = null; 
                    }

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
        return text;
    }
    // $ANTLR end literal_constraint


    // $ANTLR start enum_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1012:1: enum_constraint returns [String text] : id= ID ( '.' ident= identifier )+ ;
    public final String enum_constraint() throws RecognitionException {
        String text = null;

        Token id=null;
        Token ident = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:3: (id= ID ( '.' ident= identifier )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:3: id= ID ( '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2769); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:32: ( '.' ident= identifier )+
            int cnt39=0;
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==64) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:34: '.' ident= identifier
            	    {
            	    match(input,64,FOLLOW_64_in_enum_constraint2775); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint2779);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ident.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return text;
    }
    // $ANTLR end enum_constraint


    // $ANTLR start predicate
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1021:1: predicate[PatternDescr pattern] : text= paren_chunk[d] ;
    public final void predicate(PatternDescr pattern) throws RecognitionException {
        String text = null;


        
        		PredicateDescr d = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:3: (text= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {
              
              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate2821);
            text=paren_chunk(d);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		        if( text != null ) {
              			        String body = text.substring(1, text.length()-1);
              			        d.setContent( body );
              				pattern.addDescr( d );
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
        return ;
    }
    // $ANTLR end predicate


    // $ANTLR start paren_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1039:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public final String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1045:10: (loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1045:10: loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2870); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:3: (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);

                if ( ((LA40_0>=PACKAGE && LA40_0<=OR)||(LA40_0>=CONTAINS && LA40_0<=78)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_PAREN) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1056:4: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=OR)||(input.LA(1)>=CONTAINS && input.LA(1)<=78) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2886);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1061:4: chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2910);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);

            if ( backtracking==0 ) {
              
              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2947); if (failed) return text;
            if ( backtracking==0 ) {
              
                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
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
        return text;
    }
    // $ANTLR end paren_chunk


    // $ANTLR start curly_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1083:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public final String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: (loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk2998); if (failed) return text;
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1097:3: (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);

                if ( ((LA41_0>=PACKAGE && LA41_0<=NULL)||(LA41_0>=LEFT_SQUARE && LA41_0<=78)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_CURLY) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1098:4: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=78) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk3014);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1103:4: chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk3038);
            	    chunk=curly_chunk(descr);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);

            if ( backtracking==0 ) {
              
              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk3075); if (failed) return text;
            if ( backtracking==0 ) {
              
                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start square_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1125:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public final String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1131:10: (loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1131:10: loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3138); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:3: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )*
            loop42:
            do {
                int alt42=3;
                int LA42_0 = input.LA(1);

                if ( ((LA42_0>=PACKAGE && LA42_0<=RIGHT_CURLY)||(LA42_0>=AND && LA42_0<=78)) ) {
                    alt42=1;
                }
                else if ( (LA42_0==LEFT_SQUARE) ) {
                    alt42=2;
                }


                switch (alt42) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1142:4: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=78) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3154);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:4: chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3178);
            	    chunk=square_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);

            if ( backtracking==0 ) {
              
              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3215); if (failed) return text;
            if ( backtracking==0 ) {
              
                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
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
        return text;
    }
    // $ANTLR end square_chunk


    // $ANTLR start retval_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public final String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: (c= paren_chunk[null] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3260);
            c=paren_chunk(null);
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
               text = c.substring(1, c.length()-1); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return text;
    }
    // $ANTLR end retval_constraint


    // $ANTLR start lhs_or
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1177:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( OR | '||' ) right= lhs_and )* ;
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:3: (left= lhs_and ( ( OR | '||' ) right= lhs_and )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:3: left= lhs_and ( ( OR | '||' ) right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3288);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:3: ( ( OR | '||' ) right= lhs_and )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==OR||LA43_0==68) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:5: ( OR | '||' ) right= lhs_and
            	    {
            	    if ( input.LA(1)==OR||input.LA(1)==68 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3296);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3307);
            	    right=lhs_and();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {
            	      
            	      				if ( or == null ) {
            	      					or = new OrDescr();
            	      					or.addDescr( left );
            	      					d = or;
            	      				}
            	      				
            	      				or.addDescr( right );
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop43;
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )* ;
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:3: (left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:3: left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3343);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1205:3: ( ( AND | '&&' ) right= lhs_unary )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==AND||LA44_0==78) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1205:5: ( AND | '&&' ) right= lhs_unary
            	    {
            	    if ( input.LA(1)==AND||input.LA(1)==78 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3351);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3362);
            	    right=lhs_unary();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {
            	      
            	      				if ( and == null ) {
            	      					and = new AndDescr();
            	      					and.addDescr( left );
            	      					d = and;
            	      				}
            	      				
            	      				and.addDescr( right );
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop44;
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )
            int alt47=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                {
                alt47=1;
                }
                break;
            case NOT:
                {
                alt47=2;
                }
                break;
            case EVAL:
                {
                alt47=3;
                }
                break;
            case ID:
                {
                alt47=4;
                }
                break;
            case FORALL:
                {
                alt47=5;
                }
                break;
            case LEFT_PAREN:
                {
                alt47=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1223:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3399);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3407);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1225:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3415);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1226:5: u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_unary3423);
                    u=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1226:19: ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==FROM) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1227:13: FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary3439); if (failed) return d;
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            int alt45=3;
                            int LA45_0 = input.LA(1);

                            if ( (LA45_0==ACCUMULATE) && ((synpred90()||synpred87()))) {
                                int LA45_1 = input.LA(2);

                                if ( (synpred87()) ) {
                                    alt45=1;
                                }
                                else if ( (synpred90()) ) {
                                    alt45=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 45, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA45_0==COLLECT) && ((synpred90()||synpred88()))) {
                                int LA45_2 = input.LA(2);

                                if ( (synpred88()) ) {
                                    alt45=2;
                                }
                                else if ( (synpred90()) ) {
                                    alt45=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 45, 2, input);

                                    throw nvae;
                                }
                            }
                            else if ( ((LA45_0>=PACKAGE && LA45_0<=ATTRIBUTES)||LA45_0==ENABLED||LA45_0==SALIENCE||LA45_0==DURATION||(LA45_0>=INIT && LA45_0<=RESULT)||(LA45_0>=ID && LA45_0<=OR)||(LA45_0>=CONTAINS && LA45_0<=EXCLUDES)||LA45_0==NULL||(LA45_0>=AND && LA45_0<=THEN)) && (synpred90())) {
                                alt45=3;
                            }
                            else {
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 45, 0, input);

                                throw nvae;
                            }
                            switch (alt45) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:32: (ac= accumulate_statement )
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3467);
                                    ac=accumulate_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      ac.setResultPattern((PatternDescr) u); u=ac;
                                    }

                                    }


                                    }
                                    break;
                                case 2 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:29: (cs= collect_statement )
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary3496);
                                    cs=collect_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      cs.setResultPattern((PatternDescr) u); u=cs;
                                    }

                                    }


                                    }
                                    break;
                                case 3 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:14: (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:43: (fm= from_statement )
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:44: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary3531);
                                    fm=from_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      fm.setPattern((PatternDescr) u); u=fm;
                                    }

                                    }


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1233:5: u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary3570);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3578); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary3582);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3584); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3594);
            opt_semicolon();
            _fsp--;
            if (failed) return d;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_unary


    // $ANTLR start lhs_exist
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1239:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1243:4: (loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1243:4: loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist3618); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new ExistsDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==LEFT_PAREN) ) {
                alt48=1;
            }
            else if ( (LA48_0==ID) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1249:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:12: ( '(' pattern= lhs_or end= ')' )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:12: ( '(' pattern= lhs_or end= ')' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:14: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3638); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3642);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3674); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1254:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist3724);
                    pattern=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      	                	if ( pattern != null ) {
                      	                		((ExistsDescr)d).addDescr( pattern );
                      	                		d.setEndCharacter( pattern.getEndCharacter() );
                      	                	}
                      	                
                    }

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
        return d;
    }
    // $ANTLR end lhs_exist


    // $ANTLR start lhs_not
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1264:1: lhs_not returns [NotDescr d] : loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1268:4: (loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1268:4: loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not3778); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new NotDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==LEFT_PAREN) ) {
                alt49=1;
            }
            else if ( (LA49_0==ID) ) {
                alt49=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1274:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:5: ( '(' pattern= lhs_or end= ')' )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:5: ( '(' pattern= lhs_or end= ')' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:7: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3791); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3795);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) d.addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3828); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1280:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not3865);
                    pattern=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      	                	if ( pattern != null ) {
                      	                		d.addDescr( pattern );
                      	                		d.setEndCharacter( pattern.getEndCharacter() );
                      	                	}
                      	                
                    }

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
        return d;
    }
    // $ANTLR end lhs_not


    // $ANTLR start lhs_eval
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1290:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = new EvalDescr( );
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:3: (loc= EVAL c= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:3: loc= EVAL c= paren_chunk[d]
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval3913); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3917);
            c=paren_chunk(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        if( c != null ) {
              		            String body = c.substring(1, c.length()-1);
              			    checkTrailingSemicolon( body, offset(loc.getLine()) );
              			    ((EvalDescr) d).setContent( body );
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
        return d;
    }
    // $ANTLR end lhs_eval


    // $ANTLR start lhs_forall
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1306:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr base = null;

        BaseDescr pattern = null;


        
        		d = factory.createForall();
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1310:4: (loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1310:4: loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall3946); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall3948); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall3952);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:3: ( ( ',' )? pattern= lhs_pattern )+
            int cnt51=0;
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==ID||LA51_0==66) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:5: ( ',' )? pattern= lhs_pattern
            	    {
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:5: ( ',' )?
            	    int alt50=2;
            	    int LA50_0 = input.LA(1);

            	    if ( (LA50_0==66) ) {
            	        alt50=1;
            	    }
            	    switch (alt50) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:6: ','
            	            {
            	            match(input,66,FOLLOW_66_in_lhs_forall3966); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall3972);
            	    pattern=lhs_pattern();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {
            	      
            	      		        // adding additional patterns
            	      			d.addDescr( pattern );
            	      		
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt51 >= 1 ) break loop51;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(51, input);
                        throw eee;
                }
                cnt51++;
            } while (true);

            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall3987); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_forall


    // $ANTLR start dotted_name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1329:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:3: (id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:3: id= ID ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name4018); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1342:3: ( '.' ident= identifier )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==64) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1342:5: '.' ident= identifier
            	    {
            	    match(input,64,FOLLOW_64_in_dotted_name4030); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4034);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "." + ident.getText(); 
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)ident).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:3: ( '[' loc= ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==LEFT_SQUARE) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4056); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4060); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop53;
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
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start argument
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        Token id = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:3: (id= identifier ( '[' ']' )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:3: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument4099);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:40: ( '[' ']' )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==LEFT_SQUARE) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:42: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument4105); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument4107); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop54;
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
        return name;
    }
    // $ANTLR end argument


    // $ANTLR start rhs_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1368:1: rhs_chunk[RuleDescr rule] : start= THEN (~ END )* loc= END ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token start=null;
        Token loc=null;

        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:10: (start= THEN (~ END )* loc= END )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:10: start= THEN (~ END )* loc= END
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4151); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1380:3: (~ END )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( ((LA55_0>=PACKAGE && LA55_0<=QUERY)||(LA55_0>=TEMPLATE && LA55_0<=78)) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1381:6: ~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=78) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4163);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop55;
                }
            } while (true);

            if ( backtracking==0 ) {
              
              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk4200); if (failed) return ;
            if ( backtracking==0 ) {
              
                                  // ignoring first line in the consequence
                                  int index = 0;
                                  while( (index < buf.length() ) && Character.isWhitespace( buf.charAt( index ) ) &&
                                         (buf.charAt( index ) != 10 ) && (buf.charAt( index ) != 13 ))
                                             index++;
                                  if( (index < buf.length() ) && ( buf.charAt( index ) == '\r' ) )
                                      index++;
                                  if( (index < buf.length() ) && ( buf.charAt( index ) == '\n' ) )
                                      index++;
                                  
              		    rule.setConsequence( buf.substring( index ) );
                   		    rule.setConsequenceLocation(offset(start.getLine()), start.getCharPositionInLine());
               		    rule.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                              
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
    // $ANTLR end rhs_chunk


    // $ANTLR start name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1411:1: name returns [String s] : (tok= ID | str= STRING ) ;
    public final String name() throws RecognitionException {
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: ( (tok= ID | str= STRING ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: (tok= ID | str= STRING )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: (tok= ID | str= STRING )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==ID) ) {
                alt56=1;
            }
            else if ( (LA56_0==STRING) ) {
                alt56=2;
            }
            else {
                if (backtracking>0) {failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("1413:2: (tok= ID | str= STRING )", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1414:6: tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4244); if (failed) return s;
                    if ( backtracking==0 ) {
                      
                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1419:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4263); if (failed) return s;
                    if ( backtracking==0 ) {
                      
                      	       s = getString( str );
                      	    
                    }

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
        return s;
    }
    // $ANTLR end name


    // $ANTLR start identifier
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1426:1: identifier returns [Token tok] : (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END ) ;
    public final Token identifier() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: ( (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )
            int alt57=31;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt57=1;
                }
                break;
            case PACKAGE:
                {
                alt57=2;
                }
                break;
            case FUNCTION:
                {
                alt57=3;
                }
                break;
            case GLOBAL:
                {
                alt57=4;
                }
                break;
            case IMPORT:
                {
                alt57=5;
                }
                break;
            case RULE:
                {
                alt57=6;
                }
                break;
            case QUERY:
                {
                alt57=7;
                }
                break;
            case TEMPLATE:
                {
                alt57=8;
                }
                break;
            case ATTRIBUTES:
                {
                alt57=9;
                }
                break;
            case ENABLED:
                {
                alt57=10;
                }
                break;
            case SALIENCE:
                {
                alt57=11;
                }
                break;
            case DURATION:
                {
                alt57=12;
                }
                break;
            case FROM:
                {
                alt57=13;
                }
                break;
            case ACCUMULATE:
                {
                alt57=14;
                }
                break;
            case INIT:
                {
                alt57=15;
                }
                break;
            case ACTION:
                {
                alt57=16;
                }
                break;
            case RESULT:
                {
                alt57=17;
                }
                break;
            case COLLECT:
                {
                alt57=18;
                }
                break;
            case OR:
                {
                alt57=19;
                }
                break;
            case AND:
                {
                alt57=20;
                }
                break;
            case CONTAINS:
                {
                alt57=21;
                }
                break;
            case EXCLUDES:
                {
                alt57=22;
                }
                break;
            case MATCHES:
                {
                alt57=23;
                }
                break;
            case NULL:
                {
                alt57=24;
                }
                break;
            case EXISTS:
                {
                alt57=25;
                }
                break;
            case NOT:
                {
                alt57=26;
                }
                break;
            case EVAL:
                {
                alt57=27;
                }
                break;
            case FORALL:
                {
                alt57=28;
                }
                break;
            case WHEN:
                {
                alt57=29;
                }
                break;
            case THEN:
                {
                alt57=30;
                }
                break;
            case END:
                {
                alt57=31;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return tok;}
                NoViableAltException nvae =
                    new NoViableAltException("1428:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:10: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier4301); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1429:4: t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier4314); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1430:4: t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier4321); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1431:4: t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier4328); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1432:4: t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier4335); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1433:4: t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier4344); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1434:4: t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier4351); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1435:17: t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier4372); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1436:17: t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier4400); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1437:17: t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier4426); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1438:17: t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier4455); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1439:17: t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier4477); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1440:17: t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier4499); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1441:17: t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier4528); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1442:17: t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier4550); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1443:17: t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier4579); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1444:17: t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier4608); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1445:17: t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier4637); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1446:17: t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier4666); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1447:17: t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier4695); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1448:17: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier4724); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1449:17: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier4746); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1450:17: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier4768); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1451:17: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier4797); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1452:17: t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier4826); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1453:17: t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier4855); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1454:17: t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier4884); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1455:17: t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier4913); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1456:17: t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier4951); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1457:17: t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier4983); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1458:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier5012); if (failed) return tok;

                    }
                    break;

            }

            if ( backtracking==0 ) {
              
              	    tok = t;
              	
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return tok;
    }
    // $ANTLR end identifier

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: ( function_import_statement )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: function_import_statement
        {
        pushFollow(FOLLOW_function_import_statement_in_synpred4114);
        function_import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: ( import_statement )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: import_statement
        {
        pushFollow(FOLLOW_import_statement_in_synpred5120);
        import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred38
    public final void synpred38_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: ( paren_chunk[from] )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: paren_chunk[from]
        {
        pushFollow(FOLLOW_paren_chunk_in_synpred381788);
        paren_chunk(from);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred38

    // $ANTLR start synpred40
    public final void synpred40_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:748:6: ( LEFT_SQUARE )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:748:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred401854); if (failed) return ;

        }
    }
    // $ANTLR end synpred40

    // $ANTLR start synpred41
    public final void synpred41_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:6: ( LEFT_PAREN )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred411888); if (failed) return ;

        }
    }
    // $ANTLR end synpred41

    // $ANTLR start synpred45
    public final void synpred45_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: ( ( OR | '||' ) fact )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: ( OR | '||' ) fact
        {
        if ( input.LA(1)==OR||input.LA(1)==68 ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred452196);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred452214);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred45

    // $ANTLR start synpred87
    public final void synpred87_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:14: ( ACCUMULATE )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred873458); if (failed) return ;

        }
    }
    // $ANTLR end synpred87

    // $ANTLR start synpred88
    public final void synpred88_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:14: ( COLLECT )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred883487); if (failed) return ;

        }
    }
    // $ANTLR end synpred88

    // $ANTLR start synpred90
    public final void synpred90_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:14: (~ ( ACCUMULATE | COLLECT ) )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:16: ~ ( ACCUMULATE | COLLECT )
        {
        if ( (input.LA(1)>=PACKAGE && input.LA(1)<=LOCK_ON_ACTIVE)||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=ID && input.LA(1)<=78) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred903517);    throw mse;
        }


        }
    }
    // $ANTLR end synpred90

    public final boolean synpred45() {
        backtracking++;
        int start = input.mark();
        try {
            synpred45_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred38() {
        backtracking++;
        int start = input.mark();
        try {
            synpred38_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred90() {
        backtracking++;
        int start = input.mark();
        try {
            synpred90_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred87() {
        backtracking++;
        int start = input.mark();
        try {
            synpred87_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred5() {
        backtracking++;
        int start = input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred88() {
        backtracking++;
        int start = input.mark();
        try {
            synpred88_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred40() {
        backtracking++;
        int start = input.mark();
        try {
            synpred40_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred41() {
        backtracking++;
        int start = input.mark();
        try {
            synpred41_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred4() {
        backtracking++;
        int start = input.mark();
        try {
            synpred4_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA8_eotS =
        "\6\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\2\4\1\uffff\1\55\1\uffff\1\4";
    static final String DFA8_maxS =
        "\1\64\1\102\1\uffff\1\55\1\uffff\1\102";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA8_specialS =
        "\6\uffff}>";
    static final String[] DFA8_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\1\uffff\5\2\1\1\1"+
            "\2\2\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\1\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\13\uffff\1\4\1"+
            "\uffff\1\2",
            "",
            "\1\5",
            "",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\1\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\15\uffff\1\2"
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
            return "307:6: (paramType= dotted_name[null] )?";
        }
    }
    static final String DFA9_eotS =
        "\6\uffff";
    static final String DFA9_eofS =
        "\6\uffff";
    static final String DFA9_minS =
        "\2\4\1\uffff\1\55\1\uffff\1\4";
    static final String DFA9_maxS =
        "\1\64\1\102\1\uffff\1\55\1\uffff\1\102";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA9_specialS =
        "\6\uffff}>";
    static final String[] DFA9_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\1\uffff\5\2\1\1\1"+
            "\2\2\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\1\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\13\uffff\1\4\1"+
            "\uffff\1\2",
            "",
            "\1\5",
            "",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\1\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\15\uffff\1\2"
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "311:11: (paramType= dotted_name[null] )?";
        }
    }
 

    public static final BitSet FOLLOW_63_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x0000000000000DE0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit65 = new BitSet(new long[]{0x0000000000000DE2L});
    public static final BitSet FOLLOW_package_statement_in_prolog90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement196 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement200 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement235 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement258 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement287 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement289 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement312 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name343 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_64_in_import_name355 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_import_name359 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_65_in_import_name383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global419 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_dotted_name_in_global430 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_global442 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function471 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function476 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_function483 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function492 = new BitSet(new long[]{0x001FC2F7F40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function502 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_argument_in_function509 = new BitSet(new long[]{0x0000001000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_function523 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function528 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_argument_in_function535 = new BitSet(new long[]{0x0000001000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function559 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query597 = new BitSet(new long[]{0x0000000200008000L});
    public static final BitSet FOLLOW_name_in_query601 = new BitSet(new long[]{0x000F000A00000200L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query614 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_END_in_query631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template661 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template665 = new BitSet(new long[]{0x8000000200000000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template667 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_template_slot_in_template682 = new BitSet(new long[]{0x0000000200000200L});
    public static final BitSet FOLLOW_END_in_template699 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot747 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot765 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule800 = new BitSet(new long[]{0x0000000200008000L});
    public static final BitSet FOLLOW_name_in_rule804 = new BitSet(new long[]{0x001000000FEB7000L,0x0000000000000004L});
    public static final BitSet FOLLOW_rule_attributes_in_rule813 = new BitSet(new long[]{0x0010000000001000L});
    public static final BitSet FOLLOW_WHEN_in_rule822 = new BitSet(new long[]{0x001F000A00000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_rule824 = new BitSet(new long[]{0x001F000A00000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule842 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_rule_attributes886 = new BitSet(new long[]{0x000000000FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_rule_attributes895 = new BitSet(new long[]{0x000000000FEB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes900 = new BitSet(new long[]{0x000000000FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_salience_in_rule_attribute941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1082 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1119 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1158 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1207 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_salience1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1277 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1358 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1407 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1443 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1479 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1518 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_duration1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1592 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1634 = new BitSet(new long[]{0x000F000A00000002L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern1699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1777 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1788 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_expression_chain1827 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain1831 = new BitSet(new long[]{0x0000100800000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1862 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1896 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement1958 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1968 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_accumulate_statement1972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement1974 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement1983 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1987 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement1990 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement1999 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement2006 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2015 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2019 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2067 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2077 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_collect_statement2081 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2119 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_fact_binding2121 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2166 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2170 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2184 = new BitSet(new long[]{0x0000000400000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_set_in_fact_expression2196 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_fact_in_fact_expression2214 = new BitSet(new long[]{0x0000000400000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_fact2275 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2289 = new BitSet(new long[]{0x001FC2FFF40A3FF0L});
    public static final BitSet FOLLOW_constraints_in_fact2299 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2333 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_constraints2336 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_constraints2344 = new BitSet(new long[]{0x001FC2EFF40A3FF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2347 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_constraints2350 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_constraint2379 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_constraint2381 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_constraint2402 = new BitSet(new long[]{0x000000E000000002L,0x0000000000003F80L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2420 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000060L});
    public static final BitSet FOLLOW_set_in_constraint2441 = new BitSet(new long[]{0x000000E000000000L,0x0000000000003F00L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2461 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000060L});
    public static final BitSet FOLLOW_71_in_constraint2489 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_predicate_in_constraint2491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2526 = new BitSet(new long[]{0x0000030A00148000L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint2734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_enum_constraint2775 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint2779 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2870 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2886 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2910 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2998 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk3014 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3038 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3138 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3154 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3178 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3288 = new BitSet(new long[]{0x0000000400000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_set_in_lhs_or3296 = new BitSet(new long[]{0x000F000A00000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3307 = new BitSet(new long[]{0x0000000400000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3343 = new BitSet(new long[]{0x0000400000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_set_in_lhs_and3351 = new BitSet(new long[]{0x000F000A00000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3362 = new BitSet(new long[]{0x0000400000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3399 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3407 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3415 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_unary3423 = new BitSet(new long[]{0x8000800000000002L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary3439 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3467 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3496 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3531 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3570 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3578 = new BitSet(new long[]{0x000F000A00000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary3582 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3584 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist3618 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3638 = new BitSet(new long[]{0x000F000A00000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3642 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist3724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not3778 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3791 = new BitSet(new long[]{0x000F000A00000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3795 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not3865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval3913 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall3946 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall3948 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall3952 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_lhs_forall3966 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall3972 = new BitSet(new long[]{0x0000001200000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name4018 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_dotted_name4030 = new BitSet(new long[]{0x001FC2E7F40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4034 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4056 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4060 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_identifier_in_argument4099 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4105 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4107 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4151 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4163 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier4301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier4314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier4321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier4328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier4335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier4344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier4351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier4372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier4400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier4426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier4455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier4477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier4499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier4528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier4550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier4579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier4608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier4637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier4666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier4695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier4724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier4746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier4768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier4797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier4826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier4855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier4884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier4951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier4983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred4114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred5120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred381788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred401854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred411888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred452196 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_fact_in_synpred452214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred873458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred883487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred903517 = new BitSet(new long[]{0x0000000000000002L});

}