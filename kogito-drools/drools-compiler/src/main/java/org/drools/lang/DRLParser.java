// $ANTLR 3.0b7 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-03-28 22:28:05

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "ID", "OR", "LEFT_PAREN", "RIGHT_PAREN", "CONTAINS", "MATCHES", "EXCLUDES", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.'", "'.*'", "','", "':'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'&&'"
    };
    public static final int EXISTS=47;
    public static final int AUTO_FOCUS=22;
    public static final int END=9;
    public static final int HexDigit=55;
    public static final int FORALL=50;
    public static final int TEMPLATE=10;
    public static final int MISC=61;
    public static final int FLOAT=39;
    public static final int QUERY=8;
    public static final int THEN=51;
    public static final int RULE=11;
    public static final int INIT=28;
    public static final int IMPORT=5;
    public static final int DATE_EFFECTIVE=14;
    public static final int PACKAGE=4;
    public static final int OR=33;
    public static final int AND=45;
    public static final int FUNCTION=6;
    public static final int GLOBAL=7;
    public static final int EscapeSequence=54;
    public static final int INT=20;
    public static final int DATE_EXPIRES=16;
    public static final int LEFT_SQUARE=43;
    public static final int CONTAINS=36;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=58;
    public static final int ATTRIBUTES=13;
    public static final int RESULT=30;
    public static final int LEFT_CURLY=41;
    public static final int FROM=46;
    public static final int ID=32;
    public static final int ACTIVATION_GROUP=23;
    public static final int LEFT_PAREN=34;
    public static final int RIGHT_CURLY=42;
    public static final int BOOL=18;
    public static final int EXCLUDES=38;
    public static final int WHEN=12;
    public static final int RULEFLOW_GROUP=24;
    public static final int WS=53;
    public static final int STRING=15;
    public static final int ACTION=29;
    public static final int COLLECT=31;
    public static final int NO_LOOP=21;
    public static final int ACCUMULATE=27;
    public static final int UnicodeEscape=56;
    public static final int DURATION=26;
    public static final int EVAL=49;
    public static final int MATCHES=37;
    public static final int EOF=-1;
    public static final int EOL=52;
    public static final int AGENDA_GROUP=25;
    public static final int NULL=40;
    public static final int OctalEscape=57;
    public static final int SALIENCE=19;
    public static final int MULTI_LINE_COMMENT=60;
    public static final int NOT=48;
    public static final int RIGHT_PAREN=35;
    public static final int ENABLED=17;
    public static final int RIGHT_SQUARE=44;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=59;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[173+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
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
                                                               mtne.foundNode+
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:168:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ( ';' )=> ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ';' )=> ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ';' )=> ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==62) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ';' )=> ';'
                    {
                    match(input,62,FOLLOW_62_in_opt_semicolon46); if (failed) return ;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:172:1: compilation_unit : prolog ( ( statement )=> statement )+ ;
    public void compilation_unit() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: ( prolog ( ( statement )=> statement )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: prolog ( ( statement )=> statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:3: ( ( statement )=> statement )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:5: ( statement )=> statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:177:1: prolog : ( ( package_statement )=>n= package_statement )? ;
    public void prolog() throws RecognitionException {
        String n = null;


        
        		String packageName = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( ( package_statement )=>n= package_statement )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( package_statement )=>n= package_statement )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( package_statement )=>n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:6: ( package_statement )=>n= package_statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:1: statement : ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case IMPORT:
                if ( (synpred4()) ) {
                    alt4=1;
                }
                else if ( (synpred5()) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("189:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 1, input);

                    throw nvae;
                }
                break;
            case GLOBAL:
                alt4=3;
                break;
            case FUNCTION:
                alt4=4;
                break;
            case TEMPLATE:
                alt4=5;
                break;
            case RULE:
                alt4=6;
                break;
            case QUERY:
                alt4=7;
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("189:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: ( import_statement )=> import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement114);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:10: ( function_import_statement )=> function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement126);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:191:4: ( global )=> global
                    {
                    pushFollow(FOLLOW_global_in_statement132);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:192:4: ( function )=> function
                    {
                    pushFollow(FOLLOW_function_in_statement138);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:193:10: ( template )=>t= template
                    {
                    pushFollow(FOLLOW_template_in_statement152);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: ( rule )=>r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement161);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( r != null ) this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:195:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement173);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;


        
        		packageName = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement202); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement206);
            n=dotted_name(null);
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement209);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:211:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public void import_statement() throws RecognitionException {
        Token imp=null;

        
                	ImportDescr importDecl = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: imp= IMPORT import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement241); if (failed) return ;
            if ( backtracking==0 ) {
              
              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement264);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement267);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:226:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public void function_import_statement() throws RecognitionException {
        Token imp=null;

        
                	FunctionImportDescr importDecl = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement293); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement295); if (failed) return ;
            if ( backtracking==0 ) {
              
              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement318);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement321);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:242:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token id = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: (id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )?
            {
            pushFollow(FOLLOW_identifier_in_import_name349);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:3: ( ( '.' identifier )=> '.' id= identifier )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==63) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:5: ( '.' identifier )=> '.' id= identifier
            	    {
            	    match(input,63,FOLLOW_63_in_import_name361); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name365);
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:3: ( ( '.*' )=>star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==64) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:5: ( '.*' )=>star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,64,FOLLOW_64_in_import_name389); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:270:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public void global() throws RecognitionException {
        Token loc=null;
        String type = null;

        Token id = null;


        
        	    GlobalDescr global = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global425); if (failed) return ;
            if ( backtracking==0 ) {
              
              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global436);
            type=dotted_name(null);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global448);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global450);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:293:1: function : loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public void function() throws RecognitionException {
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: (loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function477); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:16: ( ( dotted_name[null] )=>retType= dotted_name[null] )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( ((LA7_1>=PACKAGE && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||(LA7_1>=DURATION && LA7_1<=OR)||(LA7_1>=CONTAINS && LA7_1<=EXCLUDES)||LA7_1==NULL||LA7_1==LEFT_SQUARE||(LA7_1>=AND && LA7_1<=THEN)||LA7_1==63) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:17: ( dotted_name[null] )=>retType= dotted_name[null]
                    {
                    pushFollow(FOLLOW_dotted_name_in_function482);
                    retType=dotted_name(null);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function489);
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
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function498); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( ((LA11_0>=PACKAGE && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||(LA11_0>=DURATION && LA11_0<=OR)||(LA11_0>=CONTAINS && LA11_0<=EXCLUDES)||LA11_0==NULL||(LA11_0>=AND && LA11_0<=THEN)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:7: ( dotted_name[null] )=>paramType= dotted_name[null]
                            {
                            pushFollow(FOLLOW_dotted_name_in_function508);
                            paramType=dotted_name(null);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function515);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:5: ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==65) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:7: ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,65,FOLLOW_65_in_function529); if (failed) return ;
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:12: ( dotted_name[null] )=>paramType= dotted_name[null]
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function534);
                    	            paramType=dotted_name(null);
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function541);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function565); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function571);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:327:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
            {
            loc=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query603); if (failed) return query;
            pushFollow(FOLLOW_name_in_query607);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:341:3: ( normal_lhs_block[lhs] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:342:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query620);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query637); if (failed) return query;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:352:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: (loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template667); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template671);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template673);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:363:3: ( ( template_slot )=>slot= template_slot )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:364:4: ( template_slot )=>slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template688);
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
            match(input,END,FOLLOW_END_in_template705); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template707);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:375:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;


        
        		field = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
            if ( backtracking==0 ) {
              
              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_dotted_name_in_template_slot753);
            fieldType=dotted_name(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {
              
              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot771);
            n=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot773);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:396:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: (loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule806); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule810);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule819);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:3: ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:5: ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule828); if (failed) return rule;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:14: ( ( ':' )=> ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==66) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ':' )=> ':'
                            {
                            match(input,66,FOLLOW_66_in_rule830); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:417:4: ( normal_lhs_block[lhs] )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:419:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule848);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule869);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:1: rule_attributes[RuleDescr rule] : ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==ATTRIBUTES) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:5: ( ATTRIBUTES ':' )=> ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes890); if (failed) return ;
                    match(input,66,FOLLOW_66_in_rule_attributes892); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==DATE_EFFECTIVE||(LA17_0>=DATE_EXPIRES && LA17_0<=ENABLED)||LA17_0==SALIENCE||(LA17_0>=NO_LOOP && LA17_0<=DURATION)||LA17_0==65) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ( ',' )=> ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==65) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ',' )=> ','
            	            {
            	            match(input,65,FOLLOW_65_in_rule_attributes901); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes906);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:440:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | a= ruleflow_group );
    public AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | a= ruleflow_group )
            int alt18=10;
            switch ( input.LA(1) ) {
            case SALIENCE:
                alt18=1;
                break;
            case NO_LOOP:
                alt18=2;
                break;
            case AGENDA_GROUP:
                alt18=3;
                break;
            case DURATION:
                alt18=4;
                break;
            case ACTIVATION_GROUP:
                alt18=5;
                break;
            case AUTO_FOCUS:
                alt18=6;
                break;
            case DATE_EFFECTIVE:
                alt18=7;
                break;
            case DATE_EXPIRES:
                alt18=8;
                break;
            case ENABLED:
                alt18=9;
                break;
            case RULEFLOW_GROUP:
                alt18=10;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("440:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | a= ruleflow_group );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( salience )=>a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute947);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:5: ( no_loop )=>a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute957);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:447:5: ( agenda_group )=>a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute968);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:5: ( duration )=>a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute981);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:449:5: ( activation_group )=>a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute995);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:450:5: ( auto_focus )=>a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1006);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:451:11: ( date_effective )=>a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1023);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:452:5: ( date_expires )=>a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1033);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:453:11: ( enabled )=>a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1049);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
                    }

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:454:11: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1065);
                    a=ruleflow_group();
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:458:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:463:3: (loc= DATE_EFFECTIVE val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:463:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1097); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1101); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:473:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:478:3: (loc= DATE_EXPIRES val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:478:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1134); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1138); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:489:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:494:4: (loc= ENABLED t= BOOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:494:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1173); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1177); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:507:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:3: (loc= SALIENCE i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1222); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1226); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:521:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:526:3: ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==NO_LOOP) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==EOF||LA19_1==WHEN||LA19_1==DATE_EFFECTIVE||(LA19_1>=DATE_EXPIRES && LA19_1<=ENABLED)||LA19_1==SALIENCE||(LA19_1>=NO_LOOP && LA19_1<=DURATION)||LA19_1==THEN||LA19_1==65) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("521:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("521:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:526:3: ( ( NO_LOOP ) )=> (loc= NO_LOOP )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:526:3: (loc= NO_LOOP )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1264); if (failed) return d;
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:536:3: (loc= NO_LOOP t= BOOL )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:536:3: (loc= NO_LOOP t= BOOL )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1292); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1296); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:549:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:3: ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==AUTO_FOCUS) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==EOF||LA20_1==WHEN||LA20_1==DATE_EFFECTIVE||(LA20_1>=DATE_EXPIRES && LA20_1<=ENABLED)||LA20_1==SALIENCE||(LA20_1>=NO_LOOP && LA20_1<=DURATION)||LA20_1==THEN||LA20_1==65) ) {
                    alt20=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("549:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("549:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:3: ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:3: (loc= AUTO_FOCUS )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1345); if (failed) return d;
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:564:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:564:3: (loc= AUTO_FOCUS t= BOOL )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:565:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1373); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1377); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:577:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:582:3: (loc= ACTIVATION_GROUP n= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:582:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1422); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1426); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:591:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:596:3: (loc= RULEFLOW_GROUP n= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:596:3: loc= RULEFLOW_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1458); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1462); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:605:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:610:3: (loc= AGENDA_GROUP n= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:610:3: loc= AGENDA_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1494); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1498); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:620:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:3: (loc= DURATION i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:3: loc= DURATION i= INT
            {
            loc=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1533); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1537); if (failed) return d;
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


    // $ANTLR start normal_lhs_block
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:635:1: normal_lhs_block[AndDescr descr] : ( ( lhs[descr] )=>d= lhs[descr] )* ;
    public void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:3: ( ( ( lhs[descr] )=>d= lhs[descr] )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:3: ( ( lhs[descr] )=>d= lhs[descr] )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:3: ( ( lhs[descr] )=>d= lhs[descr] )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_PAREN||(LA21_0>=EXISTS && LA21_0<=FORALL)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:5: ( lhs[descr] )=>d= lhs[descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1564);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descr.addDescr( d ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop21;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:643:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:647:4: (l= lhs_or )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:647:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1601);
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


    // $ANTLR start lhs_column
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:651:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:655:4: ( ( fact_binding )=>f= fact_binding | f= fact )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==ID) ) {
                int LA22_1 = input.LA(2);
                if ( (LA22_1==66) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_PAREN||LA22_1==LEFT_SQUARE||LA22_1==63) ) {
                    alt22=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("651:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("651:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:655:4: ( fact_binding )=>f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1629);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:656:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_column1638);
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
    // $ANTLR end lhs_column


    // $ANTLR start from_statement
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:659:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:664:2: (ds= from_source[d] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:664:2: ds= from_source[d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement1665);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:674:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )? ;
    public DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:3: (ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:3: ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source1707);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:3: ( ( paren_chunk[from] )=>args= paren_chunk[from] )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( (LA23_0==LEFT_PAREN) ) {
                if ( (synpred36()) ) {
                    alt23=1;
                }
            }
            switch (alt23) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: ( paren_chunk[from] )=>args= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source1718);
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:701:3: ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )?
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==63) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[from, ad] )=> expression_chain[from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1732);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:704:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? ) ;
    public void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )?
            {
            match(input,63,FOLLOW_63_in_expression_chain1757); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain1761);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:717:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
            int alt25=3;
            int LA25_0 = input.LA(1);
            if ( (LA25_0==LEFT_SQUARE) ) {
                alt25=1;
            }
            else if ( (LA25_0==LEFT_PAREN) ) {
                if ( (synpred39()) ) {
                    alt25=2;
                }
            }
            switch (alt25) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1792);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:723:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1826);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:737:4: ( ( expression_chain[from, as] )=> expression_chain[from, as] )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( (LA26_0==63) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[from, as] )=> expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1847);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:741:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:746:10: (loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:746:10: loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement1888); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1898); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1902);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,65,FOLLOW_65_in_accumulate_statement1904); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourceColumn( (ColumnDescr)column );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement1913); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1917);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,65,FOLLOW_65_in_accumulate_statement1920); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement1929); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1933);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,65,FOLLOW_65_in_accumulate_statement1936); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement1945); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1949);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1954); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:770:1: collect_statement returns [CollectDescr d] : loc= COLLECT '(' column= lhs_column loc= ')' ;
    public CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:775:10: (loc= COLLECT '(' column= lhs_column loc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:775:10: loc= COLLECT '(' column= lhs_column loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement1997); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2007); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_collect_statement2011);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2015); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourceColumn( (ColumnDescr)column );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:787:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:793:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:793:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2049); if (failed) return d;
            match(input,66,FOLLOW_66_in_fact_binding2051); if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // handling incomplete parsing
               		        d = new ColumnDescr( );
               		        ((ColumnDescr) d).setIdentifier( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2064);
            fe=fact_expression(id.getText());
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // override previously instantiated column
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:809:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );
    public BaseDescr fact_expression(String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:814:5: ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* )
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( (LA28_0==LEFT_PAREN) ) {
                alt28=1;
            }
            else if ( (LA28_0==ID) ) {
                alt28=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("809:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:814:5: ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2096); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2100);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2103); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:815:6: f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2114);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:820:4: ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);
                        if ( (LA27_0==OR||LA27_0==67) ) {
                            if ( (synpred42()) ) {
                                alt27=1;
                            }


                        }


                        switch (alt27) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:820:6: ( (OR|'||') fact )=> (OR|'||')f= fact
                    	    {
                    	    if ( input.LA(1)==OR||input.LA(1)==67 ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2127);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2144);
                    	    f=fact();
                    	    _fsp--;
                    	    if (failed) return pd;
                    	    if ( backtracking==0 ) {
                    	      
                    	       				((ColumnDescr)f).setIdentifier( id );
                    	       				((OrDescr)pd).addDescr( f );
                    	       			
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop27;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:836:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN ;
    public BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;


        
        		d=null;
        		ColumnDescr column = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:842:11: (id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:842:11: id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
               			column = new ColumnDescr( );
               			d = column; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2205);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        column.setObjectType( id );
               		        column.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2219); if (failed) return d;
            if ( backtracking==0 ) {
              
               				column.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        column.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:855:4: ( ( constraints[column] )=> constraints[column] )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( ((LA29_0>=PACKAGE && LA29_0<=ATTRIBUTES)||LA29_0==ENABLED||LA29_0==SALIENCE||(LA29_0>=DURATION && LA29_0<=LEFT_PAREN)||(LA29_0>=CONTAINS && LA29_0<=EXCLUDES)||LA29_0==NULL||(LA29_0>=AND && LA29_0<=THEN)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:855:6: ( constraints[column] )=> constraints[column]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2229);
                    constraints(column);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2242); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        if( endLoc.getType() == RIGHT_PAREN ) {
              				column.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
              				column.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
               			        column.setRightParentCharacter( ((CommonToken)endLoc).getStartIndex() );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:867:1: constraints[ColumnDescr column] : ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* ;
    public void constraints(ColumnDescr column) throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:4: ( ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:4: ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( ((LA30_0>=PACKAGE && LA30_0<=ATTRIBUTES)||LA30_0==ENABLED||LA30_0==SALIENCE||(LA30_0>=DURATION && LA30_0<=OR)||(LA30_0>=CONTAINS && LA30_0<=EXCLUDES)||LA30_0==NULL||(LA30_0>=AND && LA30_0<=THEN)) ) {
                alt30=1;
            }
            else if ( (LA30_0==LEFT_PAREN) ) {
                alt30=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("868:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:5: ( constraint[column] )=> constraint[column]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2263);
                    constraint(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:24: predicate[column]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2266);
                    predicate(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:869:3: ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( (LA32_0==65) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:869:5: ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] )
            	    {
            	    match(input,65,FOLLOW_65_in_constraints2274); if (failed) return ;
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:869:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);
            	    if ( ((LA31_0>=PACKAGE && LA31_0<=ATTRIBUTES)||LA31_0==ENABLED||LA31_0==SALIENCE||(LA31_0>=DURATION && LA31_0<=OR)||(LA31_0>=CONTAINS && LA31_0<=EXCLUDES)||LA31_0==NULL||(LA31_0>=AND && LA31_0<=THEN)) ) {
            	        alt31=1;
            	    }
            	    else if ( (LA31_0==LEFT_PAREN) ) {
            	        alt31=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("869:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 31, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:869:10: ( constraint[column] )=> constraint[column]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2277);
            	            constraint(column);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:869:29: predicate[column]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2280);
            	            predicate(column);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop32;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:1: constraint[ColumnDescr column] : ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? ;
    public void constraint(ColumnDescr column) throws RecognitionException {
        Token fb=null;
        Token con=null;
        Token f = null;

        RestrictionDescr rd = null;


        
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:878:3: ( ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:878:3: ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:878:3: ( ( ID ':' )=>fb= ID ':' )?
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( (LA33_0==ID) ) {
                int LA33_1 = input.LA(2);
                if ( (LA33_1==66) ) {
                    alt33=1;
                }
            }
            switch (alt33) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:878:5: ( ID ':' )=>fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2309); if (failed) return ;
                    match(input,66,FOLLOW_66_in_constraint2311); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( fb.getText() );
                      			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
                      			column.addDescr( fbd );
                      
                      		    
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_constraint2332);
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
              			    column.addDescr( fc );
              			}
              		    }
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:906:3: ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            int alt35=3;
            int LA35_0 = input.LA(1);
            if ( ((LA35_0>=CONTAINS && LA35_0<=EXCLUDES)||(LA35_0>=71 && LA35_0<=76)) ) {
                alt35=1;
            }
            else if ( (LA35_0==70) ) {
                alt35=2;
            }
            switch (alt35) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:907:4: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:907:4: (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:907:6: rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2350);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					fc.addRestriction(rd);
                      					// we must add now as we didn't before
                      					if( fb != null) {
                      					    column.addDescr( fc );
                      					}
                      				
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:915:5: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);
                        if ( ((LA34_0>=68 && LA34_0<=69)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:916:6: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=68 && input.LA(1)<=69) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2372);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      
                    	      						if (con.getText().equals("&") ) {								
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      						} else {
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      						}							
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2391);
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
                    	    break loop34;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:933:4: ( '->' predicate[column] )=> '->' predicate[column]
                    {
                    match(input,70,FOLLOW_70_in_constraint2419); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_constraint2421);
                    predicate(column);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:939:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:939:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=CONTAINS && input.LA(1)<=EXCLUDES)||(input.LA(1)>=71 && input.LA(1)<=76) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2458);    throw mse;
            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:949:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            int alt36=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA36_1 = input.LA(2);
                if ( (LA36_1==63) ) {
                    alt36=2;
                }
                else if ( (LA36_1==EOF||LA36_1==RIGHT_PAREN||LA36_1==65||(LA36_1>=68 && LA36_1<=69)) ) {
                    alt36=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("949:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 36, 1, input);

                    throw nvae;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                alt36=3;
                break;
            case LEFT_PAREN:
                alt36=4;
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("949:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:949:5: ( ID )=>bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2525); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:954:4: ( enum_constraint )=>lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2541);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:959:4: ( literal_constraint )=>lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2564);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:963:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2578);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) ;
    public String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:974:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:974:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:974:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
            int alt37=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt37=1;
                break;
            case INT:
                alt37=2;
                break;
            case FLOAT:
                alt37=3;
                break;
            case BOOL:
                alt37=4;
                break;
            case NULL:
                alt37=5;
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("974:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:974:6: ( STRING )=>t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2617); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:975:5: ( INT )=>t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2628); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:5: ( FLOAT )=>t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2641); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:977:5: ( BOOL )=>t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2652); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:978:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint2664); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:982:1: enum_constraint returns [String text] : id= ID ( ( '.' identifier )=> '.' ident= identifier )+ ;
    public String enum_constraint() throws RecognitionException {
        String text = null;

        Token id=null;
        Token ident = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:987:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:987:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2699); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:987:32: ( ( '.' identifier )=> '.' ident= identifier )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);
                if ( (LA38_0==63) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:987:34: ( '.' identifier )=> '.' ident= identifier
            	    {
            	    match(input,63,FOLLOW_63_in_enum_constraint2705); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint2709);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ident.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt38 >= 1 ) break loop38;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:1: predicate[ColumnDescr column] : text= paren_chunk[d] ;
    public void predicate(ColumnDescr column) throws RecognitionException {
        String text = null;


        
        		PredicateDescr d = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:996:3: (text= paren_chunk[d] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:996:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {
              
              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate2751);
            text=paren_chunk(d);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		        if( text != null ) {
              			        String body = text.substring(1, text.length()-1);
              			        d.setContent( body );
              				column.addDescr( d );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1015:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1015:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2800); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1025:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )*
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);
                if ( ((LA39_0>=PACKAGE && LA39_0<=OR)||(LA39_0>=CONTAINS && LA39_0<=77)) ) {
                    alt39=1;
                }
                else if ( (LA39_0==LEFT_PAREN) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=OR)||(input.LA(1)>=CONTAINS && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2816);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1031:4: ( paren_chunk[null] )=>chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2840);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop39;
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2877); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1059:3: (loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1059:3: loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk2928); if (failed) return text;
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1067:3: ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);
                if ( ((LA40_0>=PACKAGE && LA40_0<=NULL)||(LA40_0>=LEFT_SQUARE && LA40_0<=77)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_CURLY) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1068:4: (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk2944);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1073:4: ( curly_chunk[descr] )=>chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk2968);
            	    chunk=curly_chunk(descr);
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk3005); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1095:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1101:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1101:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3068); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1111:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);
                if ( ((LA41_0>=PACKAGE && LA41_0<=RIGHT_CURLY)||(LA41_0>=AND && LA41_0<=77)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_SQUARE) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1112:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3084);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1117:4: ( square_chunk[null] )=>chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3108);
            	    chunk=square_chunk(null);
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3145); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1144:3: (c= paren_chunk[null] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1144:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3190);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1153:3: (left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1153:3: left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3218);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1154:3: ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( (LA42_0==OR||LA42_0==67) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1154:5: ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==OR||input.LA(1)==67 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3227);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3237);
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
            	    break loop42;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1168:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: (left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3273);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1175:3: ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( (LA43_0==AND||LA43_0==77) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1175:5: ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary
            	    {
            	    if ( input.LA(1)==AND||input.LA(1)==77 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3282);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3292);
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
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1189:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )
            int alt46=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                alt46=1;
                break;
            case NOT:
                alt46=2;
                break;
            case EVAL:
                alt46=3;
                break;
            case ID:
                alt46=4;
                break;
            case FORALL:
                alt46=5;
                break;
            case LEFT_PAREN:
                alt46=6;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1193:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:6: ( lhs_exist )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3329);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1194:5: ( lhs_not )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3337);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:5: ( lhs_eval )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3345);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1196:5: ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary3353);
                    u=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1196:18: ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);
                    if ( (LA45_0==FROM) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1197:13: ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary3369); if (failed) return d;
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1197:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )
                            int alt44=3;
                            switch ( input.LA(1) ) {
                            case ACCUMULATE:
                                if ( (synpred70()) ) {
                                    alt44=1;
                                }
                                else if ( (synpred72()) ) {
                                    alt44=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1197:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )", 44, 1, input);

                                    throw nvae;
                                }
                                break;
                            case COLLECT:
                                if ( (synpred71()) ) {
                                    alt44=2;
                                }
                                else if ( (synpred72()) ) {
                                    alt44=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1197:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )", 44, 2, input);

                                    throw nvae;
                                }
                                break;
                            case PACKAGE:
                            case IMPORT:
                            case FUNCTION:
                            case GLOBAL:
                            case QUERY:
                            case END:
                            case TEMPLATE:
                            case RULE:
                            case WHEN:
                            case ATTRIBUTES:
                            case ENABLED:
                            case SALIENCE:
                            case DURATION:
                            case INIT:
                            case ACTION:
                            case RESULT:
                            case ID:
                            case OR:
                            case CONTAINS:
                            case MATCHES:
                            case EXCLUDES:
                            case NULL:
                            case AND:
                            case FROM:
                            case EXISTS:
                            case NOT:
                            case EVAL:
                            case FORALL:
                            case THEN:
                                alt44=3;
                                break;
                            default:
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1197:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )", 44, 0, input);

                                throw nvae;
                            }

                            switch (alt44) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:32: (ac= accumulate_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3397);
                                    ac=accumulate_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      ac.setResultColumn((ColumnDescr) u); u=ac;
                                    }

                                    }


                                    }
                                    break;
                                case 2 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:29: (cs= collect_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary3426);
                                    cs=collect_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      cs.setResultColumn((ColumnDescr) u); u=cs;
                                    }

                                    }


                                    }
                                    break;
                                case 3 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:14: (~ (ACCUMULATE|COLLECT))=> (fm= from_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:43: (fm= from_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:44: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary3461);
                                    fm=from_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      fm.setColumn((ColumnDescr) u); u=fm;
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1203:5: ( lhs_forall )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary3500);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3508); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary3512);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3514); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3524);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1209:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1213:4: (loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1213:4: loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist3548); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new ExistsDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:10: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
            int alt47=2;
            int LA47_0 = input.LA(1);
            if ( (LA47_0==LEFT_PAREN) ) {
                alt47=1;
            }
            else if ( (LA47_0==ID) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1219:10: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:12: ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:12: ( '(' column= lhs_or end= ')' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:14: '(' column= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3568); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3572);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( column != null ) ((ExistsDescr)d).addDescr( column ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3604); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:12: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3654);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      	                	if ( column != null ) {
                      	                		((ExistsDescr)d).addDescr( column );
                      	                		d.setEndCharacter( column.getEndCharacter() );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:1: lhs_not returns [NotDescr d] : loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1238:4: (loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1238:4: loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not3708); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new NotDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1244:3: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
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
                    new NoViableAltException("1244:3: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1244:5: ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1244:5: ( '(' column= lhs_or end= ')' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1244:7: '(' column= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3721); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3725);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( column != null ) d.addDescr( column ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3758); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:3: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3795);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      	                	if ( column != null ) {
                      	                		d.addDescr( column );
                      	                		d.setEndCharacter( column.getEndCharacter() );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1260:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = new EvalDescr( );
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1265:3: (loc= EVAL c= paren_chunk[d] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1265:3: loc= EVAL c= paren_chunk[d]
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval3843); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3847);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1276:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')' ;
    public ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr base = null;

        BaseDescr column = null;


        
        		d = factory.createForall();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1280:4: (loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1280:4: loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall3876); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall3878); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_lhs_forall3882);
            base=lhs_column();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        // adding the base column
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:3: ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+
            int cnt50=0;
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( (LA50_0==ID||LA50_0==65) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:5: ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:5: ( ( ',' )=> ',' )?
            	    int alt49=2;
            	    int LA49_0 = input.LA(1);
            	    if ( (LA49_0==65) ) {
            	        alt49=1;
            	    }
            	    switch (alt49) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:6: ( ',' )=> ','
            	            {
            	            match(input,65,FOLLOW_65_in_lhs_forall3896); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_column_in_lhs_forall3902);
            	    column=lhs_column();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {
            	      
            	      		        // adding additional columns
            	      			d.addDescr( column );
            	      		
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt50 >= 1 ) break loop50;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(50, input);
                        throw eee;
                }
                cnt50++;
            } while (true);

            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall3917); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1299:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* ;
    public String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1304:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1304:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3948); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1312:3: ( ( '.' identifier )=> '.' ident= identifier )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);
                if ( (LA51_0==63) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1312:5: ( '.' identifier )=> '.' ident= identifier
            	    {
            	    match(input,63,FOLLOW_63_in_dotted_name3960); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name3964);
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
            	    break loop51;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1320:3: ( ( '[' ']' )=> '[' loc= ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);
                if ( (LA52_0==LEFT_SQUARE) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1320:5: ( '[' ']' )=> '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name3986); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name3990); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop52;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1330:1: argument returns [String name] : id= identifier ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {
        String name = null;

        Token id = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1335:3: (id= identifier ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1335:3: id= identifier ( ( '[' ']' )=> '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument4029);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1335:40: ( ( '[' ']' )=> '[' ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);
                if ( (LA53_0==LEFT_SQUARE) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1335:42: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument4035); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument4037); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
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
    // $ANTLR end argument


    // $ANTLR start rhs_chunk
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1338:1: rhs_chunk[RuleDescr rule] : start= THEN ( (~ END )=>~ END )* loc= END ;
    public void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token start=null;
        Token loc=null;

        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1344:10: (start= THEN ( (~ END )=>~ END )* loc= END )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1344:10: start= THEN ( (~ END )=>~ END )* loc= END
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4081); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:3: ( (~ END )=>~ END )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( ((LA54_0>=PACKAGE && LA54_0<=QUERY)||(LA54_0>=TEMPLATE && LA54_0<=77)) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1351:6: (~ END )=>~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4093);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop54;
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
            match(input,END,FOLLOW_END_in_rhs_chunk4130); if (failed) return ;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1381:1: name returns [String s] : ( ( ID )=>tok= ID | str= STRING ) ;
    public String name() throws RecognitionException {
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1383:2: ( ( ( ID )=>tok= ID | str= STRING ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1383:2: ( ( ID )=>tok= ID | str= STRING )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1383:2: ( ( ID )=>tok= ID | str= STRING )
            int alt55=2;
            int LA55_0 = input.LA(1);
            if ( (LA55_0==ID) ) {
                alt55=1;
            }
            else if ( (LA55_0==STRING) ) {
                alt55=2;
            }
            else {
                if (backtracking>0) {failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("1383:2: ( ( ID )=>tok= ID | str= STRING )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1384:6: ( ID )=>tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4174); if (failed) return s;
                    if ( backtracking==0 ) {
                      
                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1389:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4193); if (failed) return s;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1396:1: identifier returns [Token tok] : ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) ;
    public Token identifier() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1398:2: ( ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1398:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1398:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
            int alt56=31;
            switch ( input.LA(1) ) {
            case ID:
                alt56=1;
                break;
            case PACKAGE:
                alt56=2;
                break;
            case FUNCTION:
                alt56=3;
                break;
            case GLOBAL:
                alt56=4;
                break;
            case IMPORT:
                alt56=5;
                break;
            case RULE:
                alt56=6;
                break;
            case QUERY:
                alt56=7;
                break;
            case TEMPLATE:
                alt56=8;
                break;
            case ATTRIBUTES:
                alt56=9;
                break;
            case ENABLED:
                alt56=10;
                break;
            case SALIENCE:
                alt56=11;
                break;
            case DURATION:
                alt56=12;
                break;
            case FROM:
                alt56=13;
                break;
            case ACCUMULATE:
                alt56=14;
                break;
            case INIT:
                alt56=15;
                break;
            case ACTION:
                alt56=16;
                break;
            case RESULT:
                alt56=17;
                break;
            case COLLECT:
                alt56=18;
                break;
            case OR:
                alt56=19;
                break;
            case AND:
                alt56=20;
                break;
            case CONTAINS:
                alt56=21;
                break;
            case EXCLUDES:
                alt56=22;
                break;
            case MATCHES:
                alt56=23;
                break;
            case NULL:
                alt56=24;
                break;
            case EXISTS:
                alt56=25;
                break;
            case NOT:
                alt56=26;
                break;
            case EVAL:
                alt56=27;
                break;
            case FORALL:
                alt56=28;
                break;
            case WHEN:
                alt56=29;
                break;
            case THEN:
                alt56=30;
                break;
            case END:
                alt56=31;
                break;
            default:
                if (backtracking>0) {failed=true; return tok;}
                NoViableAltException nvae =
                    new NoViableAltException("1398:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1398:10: ( ID )=>t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier4231); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1399:4: ( PACKAGE )=>t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier4244); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1400:4: ( FUNCTION )=>t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier4251); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1401:4: ( GLOBAL )=>t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier4258); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1402:4: ( IMPORT )=>t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier4265); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1403:4: ( RULE )=>t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier4274); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1404:4: ( QUERY )=>t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier4281); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1405:17: ( TEMPLATE )=>t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier4302); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1406:17: ( ATTRIBUTES )=>t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier4330); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1407:17: ( ENABLED )=>t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier4356); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1408:17: ( SALIENCE )=>t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier4385); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1409:17: ( DURATION )=>t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier4407); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1410:17: ( FROM )=>t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier4429); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1411:17: ( ACCUMULATE )=>t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier4458); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1412:17: ( INIT )=>t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier4480); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:17: ( ACTION )=>t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier4509); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1414:17: ( RESULT )=>t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier4538); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1415:17: ( COLLECT )=>t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier4567); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1416:17: ( OR )=>t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier4596); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1417:17: ( AND )=>t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier4625); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1418:17: ( CONTAINS )=>t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier4654); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1419:17: ( EXCLUDES )=>t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier4676); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1420:17: ( MATCHES )=>t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier4698); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1421:17: ( NULL )=>t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier4727); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1422:17: ( EXISTS )=>t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier4756); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1423:17: ( NOT )=>t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier4785); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1424:17: ( EVAL )=>t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier4814); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1425:17: ( FORALL )=>t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier4843); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1426:17: ( WHEN )=>t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier4881); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1427:17: ( THEN )=>t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier4913); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier4942); if (failed) return tok;

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
    public void synpred4_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: ( import_statement )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: import_statement
        {
        pushFollow(FOLLOW_import_statement_in_synpred4114);
        import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public void synpred5_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:10: ( function_import_statement )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:10: function_import_statement
        {
        pushFollow(FOLLOW_function_import_statement_in_synpred5126);
        function_import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred36
    public void synpred36_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: ( paren_chunk[from] )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: paren_chunk[from]
        {
        pushFollow(FOLLOW_paren_chunk_in_synpred361718);
        paren_chunk(from);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred36

    // $ANTLR start synpred39
    public void synpred39_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:723:6: ( LEFT_PAREN )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:723:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred391818); if (failed) return ;

        }
    }
    // $ANTLR end synpred39

    // $ANTLR start synpred42
    public void synpred42_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:820:6: ( (OR|'||') fact )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:820:6: (OR|'||') fact
        {
        if ( input.LA(1)==OR||input.LA(1)==67 ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred422127);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred422144);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred42

    // $ANTLR start synpred70
    public void synpred70_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:14: ( ACCUMULATE )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred703388); if (failed) return ;

        }
    }
    // $ANTLR end synpred70

    // $ANTLR start synpred71
    public void synpred71_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:14: ( COLLECT )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred713417); if (failed) return ;

        }
    }
    // $ANTLR end synpred71

    // $ANTLR start synpred72
    public void synpred72_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:14: (~ (ACCUMULATE|COLLECT))
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:16: ~ (ACCUMULATE|COLLECT)
        {
        if ( (input.LA(1)>=PACKAGE && input.LA(1)<=DURATION)||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=ID && input.LA(1)<=77) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred723447);    throw mse;
        }


        }
    }
    // $ANTLR end synpred72

    public boolean synpred4() {
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
    public boolean synpred72() {
        backtracking++;
        int start = input.mark();
        try {
            synpred72_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred39() {
        backtracking++;
        int start = input.mark();
        try {
            synpred39_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred70() {
        backtracking++;
        int start = input.mark();
        try {
            synpred70_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred5() {
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
    public boolean synpred36() {
        backtracking++;
        int start = input.mark();
        try {
            synpred36_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred71() {
        backtracking++;
        int start = input.mark();
        try {
            synpred71_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred42() {
        backtracking++;
        int start = input.mark();
        try {
            synpred42_fragment(); // can never throw exception
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
    public static final String DFA8_eotS =
        "\6\uffff";
    public static final String DFA8_eofS =
        "\6\uffff";
    public static final String DFA8_minS =
        "\2\4\2\uffff\1\54\1\4";
    public static final String DFA8_maxS =
        "\1\63\1\101\2\uffff\1\54\1\101";
    public static final String DFA8_acceptS =
        "\2\uffff\1\2\1\1\2\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\6\2\1\1\1\2\2\uffff\3\2\1"+
        "\uffff\1\2\4\uffff\7\2",
        "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\10\3\1\uffff\1\2\3\3\1\uffff"+
        "\1\3\2\uffff\1\4\1\uffff\7\3\13\uffff\1\3\1\uffff\1\2",
        "",
        "",
        "\1\5",
        "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\10\3\1\uffff\1\2\3\3\1\uffff"+
        "\1\3\2\uffff\1\4\1\uffff\7\3\15\uffff\1\2"
    };

    class DFA8 extends DFA {
        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA.unpackEncodedString(DFA8_eotS);
            this.eof = DFA.unpackEncodedString(DFA8_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
            this.accept = DFA.unpackEncodedString(DFA8_acceptS);
            this.special = DFA.unpackEncodedString(DFA8_specialS);
            int numStates = DFA8_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA8_transition[i]);
            }
        }
        public String getDescription() {
            return "307:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }
    public static final String DFA9_eotS =
        "\6\uffff";
    public static final String DFA9_eofS =
        "\6\uffff";
    public static final String DFA9_minS =
        "\2\4\1\uffff\1\54\1\uffff\1\4";
    public static final String DFA9_maxS =
        "\1\63\1\101\1\uffff\1\54\1\uffff\1\101";
    public static final String DFA9_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\6\2\1\1\1\2\2\uffff\3\2\1"+
        "\uffff\1\2\4\uffff\7\2",
        "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\10\4\1\uffff\1\2\3\4\1\uffff"+
        "\1\4\2\uffff\1\3\1\uffff\7\4\13\uffff\1\4\1\uffff\1\2",
        "",
        "\1\5",
        "",
        "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\10\4\1\uffff\1\2\3\4\1\uffff"+
        "\1\4\2\uffff\1\3\1\uffff\7\4\15\uffff\1\2"
    };

    class DFA9 extends DFA {
        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA.unpackEncodedString(DFA9_eotS);
            this.eof = DFA.unpackEncodedString(DFA9_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
            this.accept = DFA.unpackEncodedString(DFA9_acceptS);
            this.special = DFA.unpackEncodedString(DFA9_specialS);
            int numStates = DFA9_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA9_transition[i]);
            }
        }
        public String getDescription() {
            return "311:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }
 

    public static final BitSet FOLLOW_62_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x0000000000000DE0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit65 = new BitSet(new long[]{0x0000000000000DE2L});
    public static final BitSet FOLLOW_package_statement_in_prolog90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement202 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement206 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement241 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement264 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement293 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement295 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement318 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name349 = new BitSet(new long[]{0x8000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_63_in_import_name361 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_import_name365 = new BitSet(new long[]{0x8000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_import_name389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global425 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_dotted_name_in_global436 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_global448 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function477 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function482 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_function489 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function498 = new BitSet(new long[]{0x000FE17BFC0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function508 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_argument_in_function515 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_function529 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function534 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_argument_in_function541 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function565 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query603 = new BitSet(new long[]{0x0000000100008000L});
    public static final BitSet FOLLOW_name_in_query607 = new BitSet(new long[]{0x0007800500000200L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query620 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_END_in_query637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template667 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template671 = new BitSet(new long[]{0x4000000100000000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template673 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_template_slot_in_template688 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_END_in_template705 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot753 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot771 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule806 = new BitSet(new long[]{0x0000000100008000L});
    public static final BitSet FOLLOW_name_in_rule810 = new BitSet(new long[]{0x0008000007EB7000L,0x0000000000000002L});
    public static final BitSet FOLLOW_rule_attributes_in_rule819 = new BitSet(new long[]{0x0008000000001000L});
    public static final BitSet FOLLOW_WHEN_in_rule828 = new BitSet(new long[]{0x000F800500000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_rule830 = new BitSet(new long[]{0x000F800500000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule848 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes890 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_rule_attributes892 = new BitSet(new long[]{0x0000000007EB4002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule_attributes901 = new BitSet(new long[]{0x0000000007EB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes906 = new BitSet(new long[]{0x0000000007EB4002L,0x0000000000000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1097 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1134 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1173 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1222 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_salience1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1292 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1373 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1422 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1458 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1494 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1533 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_duration1537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1564 = new BitSet(new long[]{0x0007800500000002L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1707 = new BitSet(new long[]{0x8000000400000002L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1718 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_expression_chain1757 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain1761 = new BitSet(new long[]{0x8000080400000002L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1792 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1826 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement1888 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1898 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1902 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_accumulate_statement1904 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement1913 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_accumulate_statement1920 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement1929 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1933 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_accumulate_statement1936 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement1945 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1949 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement1997 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2007 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement2011 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2049 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_fact_binding2051 = new BitSet(new long[]{0x0000000500000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2096 = new BitSet(new long[]{0x0000000500000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2100 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2114 = new BitSet(new long[]{0x0000000200000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_fact_expression2127 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_fact_in_fact_expression2144 = new BitSet(new long[]{0x0000000200000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_dotted_name_in_fact2205 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2219 = new BitSet(new long[]{0x000FE17FFC0A3FF0L});
    public static final BitSet FOLLOW_constraints_in_fact2229 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2266 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_constraints2274 = new BitSet(new long[]{0x000FE177FC0A3FF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2277 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2280 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_constraint2309 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_constraint2311 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_constraint2332 = new BitSet(new long[]{0x0000007000000002L,0x0000000000001FC0L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2350 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_set_in_constraint2372 = new BitSet(new long[]{0x0000007000000000L,0x0000000000001F80L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2391 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_70_in_constraint2419 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_predicate_in_constraint2421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2458 = new BitSet(new long[]{0x0000018500148000L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint2664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2699 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_enum_constraint2705 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint2709 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2800 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2816 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2840 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2928 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk2944 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2968 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3068 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3084 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3108 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3218 = new BitSet(new long[]{0x0000000200000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_lhs_or3227 = new BitSet(new long[]{0x0007800500000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3237 = new BitSet(new long[]{0x0000000200000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3273 = new BitSet(new long[]{0x0000200000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_set_in_lhs_and3282 = new BitSet(new long[]{0x0007800500000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3292 = new BitSet(new long[]{0x0000200000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3329 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3337 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3345 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3353 = new BitSet(new long[]{0x4000400000000002L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary3369 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3397 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3426 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3461 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3500 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3508 = new BitSet(new long[]{0x0007800500000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary3512 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3514 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist3548 = new BitSet(new long[]{0x0000000500000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3568 = new BitSet(new long[]{0x0007800500000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3572 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not3708 = new BitSet(new long[]{0x0000000500000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3721 = new BitSet(new long[]{0x0007800500000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3725 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval3843 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall3876 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall3878 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3882 = new BitSet(new long[]{0x0000000100000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_lhs_forall3896 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3902 = new BitSet(new long[]{0x0000000900000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3948 = new BitSet(new long[]{0x8000080000000002L});
    public static final BitSet FOLLOW_63_in_dotted_name3960 = new BitSet(new long[]{0x000FE173FC0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name3964 = new BitSet(new long[]{0x8000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name3986 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name3990 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_identifier_in_argument4029 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4035 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4037 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4081 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4093 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier4231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier4244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier4251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier4258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier4265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier4281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier4330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier4356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier4385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier4407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier4429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier4458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier4480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier4509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier4538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier4567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier4596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier4625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier4654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier4676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier4698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier4727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier4756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier4785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier4814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier4843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier4881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier4942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred4114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred5126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred361718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred391818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred422127 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_fact_in_synpred422144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred703388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred713417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred723447 = new BitSet(new long[]{0x0000000000000002L});

}