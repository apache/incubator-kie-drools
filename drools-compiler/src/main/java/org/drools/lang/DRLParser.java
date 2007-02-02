// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-02-02 11:54:37

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "AGENDA_GROUP", "DURATION", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "ID", "OR", "LEFT_PAREN", "RIGHT_PAREN", "CONTAINS", "MATCHES", "EXCLUDES", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.'", "'.*'", "','", "':'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'&&'"
    };
    public static final int EXISTS=46;
    public static final int AUTO_FOCUS=22;
    public static final int END=9;
    public static final int HexDigit=54;
    public static final int FORALL=49;
    public static final int TEMPLATE=10;
    public static final int MISC=60;
    public static final int FLOAT=38;
    public static final int QUERY=8;
    public static final int THEN=50;
    public static final int RULE=11;
    public static final int INIT=27;
    public static final int IMPORT=5;
    public static final int DATE_EFFECTIVE=14;
    public static final int PACKAGE=4;
    public static final int OR=32;
    public static final int AND=44;
    public static final int FUNCTION=6;
    public static final int GLOBAL=7;
    public static final int EscapeSequence=53;
    public static final int INT=20;
    public static final int DATE_EXPIRES=16;
    public static final int LEFT_SQUARE=42;
    public static final int CONTAINS=35;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=57;
    public static final int ATTRIBUTES=13;
    public static final int RESULT=29;
    public static final int LEFT_CURLY=40;
    public static final int FROM=45;
    public static final int ID=31;
    public static final int ACTIVATION_GROUP=23;
    public static final int LEFT_PAREN=33;
    public static final int RIGHT_CURLY=41;
    public static final int BOOL=18;
    public static final int EXCLUDES=37;
    public static final int WHEN=12;
    public static final int WS=52;
    public static final int STRING=15;
    public static final int ACTION=28;
    public static final int COLLECT=30;
    public static final int NO_LOOP=21;
    public static final int ACCUMULATE=26;
    public static final int UnicodeEscape=55;
    public static final int DURATION=25;
    public static final int EVAL=48;
    public static final int MATCHES=36;
    public static final int EOF=-1;
    public static final int EOL=51;
    public static final int AGENDA_GROUP=24;
    public static final int NULL=39;
    public static final int OctalEscape=56;
    public static final int SALIENCE=19;
    public static final int MULTI_LINE_COMMENT=59;
    public static final int NOT=47;
    public static final int RIGHT_PAREN=34;
    public static final int ENABLED=17;
    public static final int RIGHT_SQUARE=43;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=58;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[172+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
    	private ExpanderResolver expanderResolver;
    	private Expander expander;
    	private boolean expanderDebug = false;
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	
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
    
    	/**
    	 * This may be set to enable debuggin of DSLs/expanders.
    	 * If set to true, expander stuff will be sent to the Std out.
    	 */	
    	public void setExpanderDebug(boolean status) {
    		expanderDebug = status;
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
    	
    	public void setExpanderResolver(ExpanderResolver expanderResolver) {
    		this.expanderResolver = expanderResolver;
    	}
    	
    	public ExpanderResolver getExpanderResolver() {
    		return expanderResolver;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:183:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:184:4: ( ( ( ';' )=> ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:184:4: ( ( ';' )=> ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:184:4: ( ( ';' )=> ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==61) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ';' )=> ';'
                    {
                    match(input,61,FOLLOW_61_in_opt_semicolon46); if (failed) return ;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:1: compilation_unit : prolog ( ( statement )=> statement )+ ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:188:4: ( prolog ( ( statement )=> statement )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:188:4: prolog ( ( statement )=> statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:3: ( ( statement )=> statement )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:5: ( statement )=> statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:192:1: prolog : ( ( package_statement )=>n= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String n = null;


        
        		String packageName = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:196:4: ( ( ( package_statement )=>n= package_statement )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:196:4: ( ( package_statement )=>n= package_statement )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:196:4: ( ( package_statement )=>n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:196:6: ( package_statement )=>n= package_statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:202:1: statement : ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:2: ( ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            int alt4=7;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:4: ( import_statement )=> import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement114);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:205:10: ( function_import_statement )=> function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement126);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:206:4: ( global )=> global
                    {
                    pushFollow(FOLLOW_global_in_statement132);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:207:4: ( function )=> function
                    {
                    pushFollow(FOLLOW_function_in_statement138);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:10: ( template )=>t= template
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:209:4: ( rule )=>r= rule
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:210:4: q= query
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:214:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String n = null;


        
        		packageName = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:219:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:219:3: PACKAGE n= dotted_name[null] opt_semicolon
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:226:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        Token imp=null;

        
                	ImportDescr importDecl = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: imp= IMPORT import_name[importDecl] opt_semicolon
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:241:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        Token imp=null;

        
                	FunctionImportDescr importDecl = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:245:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:245:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:257:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name(ImportDescr importDecl) throws RecognitionException {   
        String name = null;

        Token star=null;
        Token id = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:262:3: (id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:262:3: id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )?
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:268:3: ( ( '.' identifier )=> '.' id= identifier )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==62) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:268:5: ( '.' identifier )=> '.' id= identifier
            	    {
            	    match(input,62,FOLLOW_62_in_import_name361); if (failed) return name;
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: ( ( '.*' )=>star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==63) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:5: ( '.*' )=>star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,63,FOLLOW_63_in_import_name389); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:285:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public void global() throws RecognitionException {   
        Token loc=null;
        String type = null;

        Token id = null;


        
        	    GlobalDescr global = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:290:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:290:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:308:1: function : loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public void function() throws RecognitionException {   
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:313:3: (loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:313:3: loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function477); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:313:16: ( ( dotted_name[null] )=>retType= dotted_name[null] )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( ((LA7_1>=PACKAGE && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||(LA7_1>=DURATION && LA7_1<=OR)||(LA7_1>=CONTAINS && LA7_1<=EXCLUDES)||LA7_1==NULL||LA7_1==LEFT_SQUARE||(LA7_1>=AND && LA7_1<=THEN)||LA7_1==62) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:313:17: ( dotted_name[null] )=>retType= dotted_name[null]
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:322:4: ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( ((LA11_0>=PACKAGE && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||(LA11_0>=DURATION && LA11_0<=OR)||(LA11_0>=CONTAINS && LA11_0<=EXCLUDES)||LA11_0==NULL||(LA11_0>=AND && LA11_0<=THEN)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:322:6: ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:322:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:322:7: ( dotted_name[null] )=>paramType= dotted_name[null]
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:5: ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==64) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:7: ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,64,FOLLOW_64_in_function529); if (failed) return ;
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:12: ( dotted_name[null] )=>paramType= dotted_name[null]
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:342:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:356:3: ( normal_lhs_block[lhs] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:4: normal_lhs_block[lhs]
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:372:3: (loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:372:3: loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:378:3: ( ( template_slot )=>slot= template_slot )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:379:4: ( template_slot )=>slot= template_slot
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:390:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;


        
        		field = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:395:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:395:11: fieldType= dotted_name[field] n= identifier opt_semicolon
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:418:3: (loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:418:3: loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:426:3: ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:426:5: ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule828); if (failed) return rule;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:426:14: ( ( ':' )=> ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==65) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ':' )=> ':'
                            {
                            match(input,65,FOLLOW_65_in_rule830); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:432:4: ( normal_lhs_block[lhs] )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:434:5: normal_lhs_block[lhs]
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:443:1: rule_attributes[RuleDescr rule] : ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==ATTRIBUTES) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:5: ( ATTRIBUTES ':' )=> ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes890); if (failed) return ;
                    match(input,65,FOLLOW_65_in_rule_attributes892); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==DATE_EFFECTIVE||(LA17_0>=DATE_EXPIRES && LA17_0<=ENABLED)||LA17_0==SALIENCE||(LA17_0>=NO_LOOP && LA17_0<=DURATION)||LA17_0==64) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:6: ( ( ',' )=> ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==64) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ',' )=> ','
            	            {
            	            match(input,64,FOLLOW_64_in_rule_attributes901); if (failed) return ;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:455:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:460:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled )
            int alt18=9;
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
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("455:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:460:4: ( salience )=>a= salience
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:461:5: ( no_loop )=>a= no_loop
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:462:5: ( agenda_group )=>a= agenda_group
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:463:5: ( duration )=>a= duration
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:5: ( activation_group )=>a= activation_group
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:5: ( auto_focus )=>a= auto_focus
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:466:11: ( date_effective )=>a= date_effective
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:467:5: ( date_expires )=>a= date_expires
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:468:11: a= enabled
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:472:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public AttributeDescr date_effective() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:477:3: (loc= DATE_EFFECTIVE val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:477:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1081); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1085); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:487:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public AttributeDescr date_expires() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:492:3: (loc= DATE_EXPIRES val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:492:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1118); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1122); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:503:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public AttributeDescr enabled() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:508:4: (loc= ENABLED t= BOOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:508:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1157); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1161); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:521:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:526:3: (loc= SALIENCE i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:526:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1206); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1210); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:535:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:540:3: ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==NO_LOOP) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==EOF||LA19_1==WHEN||LA19_1==DATE_EFFECTIVE||(LA19_1>=DATE_EXPIRES && LA19_1<=ENABLED)||LA19_1==SALIENCE||(LA19_1>=NO_LOOP && LA19_1<=DURATION)||LA19_1==THEN||LA19_1==64) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("535:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("535:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:540:3: ( ( NO_LOOP ) )=> (loc= NO_LOOP )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:540:3: (loc= NO_LOOP )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:541:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1248); if (failed) return d;
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:3: (loc= NO_LOOP t= BOOL )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:3: (loc= NO_LOOP t= BOOL )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:551:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1276); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1280); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:568:3: ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==AUTO_FOCUS) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==EOF||LA20_1==WHEN||LA20_1==DATE_EFFECTIVE||(LA20_1>=DATE_EXPIRES && LA20_1<=ENABLED)||LA20_1==SALIENCE||(LA20_1>=NO_LOOP && LA20_1<=DURATION)||LA20_1==THEN||LA20_1==64) ) {
                    alt20=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("563:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("563:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:568:3: ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:568:3: (loc= AUTO_FOCUS )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:569:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1329); if (failed) return d;
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:578:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:578:3: (loc= AUTO_FOCUS t= BOOL )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:579:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1357); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1361); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:591:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:596:3: (loc= ACTIVATION_GROUP n= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:596:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1406); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1410); if (failed) return d;
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
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1442); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1446); if (failed) return d;
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
            match(input,DURATION,FOLLOW_DURATION_in_duration1481); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1485); if (failed) return d;
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
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1512);
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
            pushFollow(FOLLOW_lhs_or_in_lhs1549);
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
                if ( (LA22_1==65) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_PAREN||LA22_1==LEFT_SQUARE||LA22_1==62) ) {
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
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1577);
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
                    pushFollow(FOLLOW_fact_in_lhs_column1586);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:659:1: from_statement returns [FromDescr d] : ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:664:2: (ds= from_source )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:664:2: ds= from_source
            {
            pushFollow(FOLLOW_from_source_in_from_statement1613);
            ds=from_source();
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:674:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? ;
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token functionName = null;

        String args = null;

        Token var = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:3: ( ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )
            int alt23=2;
            switch ( input.LA(1) ) {
            case ID:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 1, input);

                    throw nvae;
                }
                break;
            case PACKAGE:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 2, input);

                    throw nvae;
                }
                break;
            case FUNCTION:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 3, input);

                    throw nvae;
                }
                break;
            case GLOBAL:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 4, input);

                    throw nvae;
                }
                break;
            case IMPORT:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 5, input);

                    throw nvae;
                }
                break;
            case RULE:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 6, input);

                    throw nvae;
                }
                break;
            case QUERY:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 7, input);

                    throw nvae;
                }
                break;
            case TEMPLATE:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 8, input);

                    throw nvae;
                }
                break;
            case ATTRIBUTES:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 9, input);

                    throw nvae;
                }
                break;
            case ENABLED:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 10, input);

                    throw nvae;
                }
                break;
            case SALIENCE:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 11, input);

                    throw nvae;
                }
                break;
            case DURATION:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 12, input);

                    throw nvae;
                }
                break;
            case FROM:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 13, input);

                    throw nvae;
                }
                break;
            case ACCUMULATE:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 14, input);

                    throw nvae;
                }
                break;
            case INIT:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 15, input);

                    throw nvae;
                }
                break;
            case ACTION:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 16, input);

                    throw nvae;
                }
                break;
            case RESULT:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 17, input);

                    throw nvae;
                }
                break;
            case COLLECT:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 18, input);

                    throw nvae;
                }
                break;
            case OR:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 19, input);

                    throw nvae;
                }
                break;
            case AND:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 20, input);

                    throw nvae;
                }
                break;
            case CONTAINS:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 21, input);

                    throw nvae;
                }
                break;
            case EXCLUDES:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 22, input);

                    throw nvae;
                }
                break;
            case MATCHES:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 23, input);

                    throw nvae;
                }
                break;
            case NULL:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 24, input);

                    throw nvae;
                }
                break;
            case EXISTS:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 25, input);

                    throw nvae;
                }
                break;
            case NOT:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 26, input);

                    throw nvae;
                }
                break;
            case EVAL:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 27, input);

                    throw nvae;
                }
                break;
            case FORALL:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 28, input);

                    throw nvae;
                }
                break;
            case WHEN:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 29, input);

                    throw nvae;
                }
                break;
            case THEN:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 30, input);

                    throw nvae;
                }
                break;
            case END:
                if ( (synpred36()) ) {
                    alt23=1;
                }
                else if ( (synpred37()) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 31, input);

                    throw nvae;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ds;}
                NoViableAltException nvae =
                    new NoViableAltException("680:3: ( ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk ) | ( ( identifier ~ LEFT_PAREN )=>var= identifier ) )", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )=> ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: ( ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:6: ( identifier LEFT_PAREN )=>functionName= identifier args= paren_chunk
                    {
                    pushFollow(FOLLOW_identifier_in_from_source1665);
                    functionName=identifier();
                    _fsp--;
                    if (failed) return ds;
                    pushFollow(FOLLOW_paren_chunk_in_from_source1669);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {
                      
                       				ad = new AccessorDescr();	
                      				ad.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );
                      				ad.setStartCharacter( ((CommonToken)functionName).getStartIndex() );
                      				ad.setEndCharacter( ((CommonToken)functionName).getStopIndex() );
                      				ds = ad;
                      				FunctionCallDescr fc = new FunctionCallDescr(functionName.getText());
                      				fc.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );			
                      				fc.setArguments(args);
                      				fc.setStartCharacter( ((CommonToken)functionName).getStartIndex() );
                      				fc.setEndCharacter( ((CommonToken)functionName).getStopIndex() );
                      				ad.addInvoker(fc);
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:696:3: ( ( identifier ~ LEFT_PAREN )=>var= identifier )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:696:3: ( ( identifier ~ LEFT_PAREN )=>var= identifier )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:696:6: ( identifier ~ LEFT_PAREN )=>var= identifier
                    {
                    pushFollow(FOLLOW_identifier_in_from_source1712);
                    var=identifier();
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {
                      
                      			ad = new AccessorDescr(var.getText());	
                      			ad.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                      			ad.setStartCharacter( ((CommonToken)var).getStartIndex() );
                      			ad.setEndCharacter( ((CommonToken)var).getStopIndex() );
                      			ds = ad;
                      		    
                    }

                    }


                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:706:3: ( ( expression_chain[ad] )=> expression_chain[ad] )?
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==62) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1734);
                    expression_chain(ad);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:709:1: expression_chain[AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) ;
    public void expression_chain(AccessorDescr as) throws RecognitionException {   
        Token field = null;

        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:715:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:715:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:715:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:715:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )?
            {
            match(input,62,FOLLOW_62_in_expression_chain1759); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain1763);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:722:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt25=3;
            int LA25_0 = input.LA(1);
            if ( (LA25_0==LEFT_SQUARE) ) {
                alt25=1;
            }
            else if ( (LA25_0==LEFT_PAREN) ) {
                if ( (synpred40()) ) {
                    alt25=2;
                }
            }
            switch (alt25) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:723:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1794);
                    sqarg=square_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1827);
                    paarg=paren_chunk();
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:742:4: ( ( expression_chain[as] )=> expression_chain[as] )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( (LA26_0==62) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[as] )=> expression_chain[as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1847);
                    expression_chain(as);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:746:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk ',' ACTION text= paren_chunk ',' RESULT text= paren_chunk loc= ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:751:10: (loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk ',' ACTION text= paren_chunk ',' RESULT text= paren_chunk loc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:751:10: loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk ',' ACTION text= paren_chunk ',' RESULT text= paren_chunk loc= ')'
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
            match(input,64,FOLLOW_64_in_accumulate_statement1904); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourceColumn( (ColumnDescr)column );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement1913); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1917);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,64,FOLLOW_64_in_accumulate_statement1919); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement1928); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1932);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,64,FOLLOW_64_in_accumulate_statement1934); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement1943); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1947);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1951); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:775:1: collect_statement returns [CollectDescr d] : loc= COLLECT '(' column= lhs_column loc= ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:10: (loc= COLLECT '(' column= lhs_column loc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:10: loc= COLLECT '(' column= lhs_column loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement1994); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2004); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_collect_statement2008);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2012); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:792:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:798:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:798:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2046); if (failed) return d;
            match(input,65,FOLLOW_65_in_fact_binding2048); if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // handling incomplete parsing
               		        d = new ColumnDescr( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2061);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:813:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:818:5: ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* )
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
                    new NoViableAltException("813:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:818:5: ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2093); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2097);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2100); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:819:6: f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2111);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:824:4: ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);
                        if ( (LA27_0==OR||LA27_0==66) ) {
                            if ( (synpred43()) ) {
                                alt27=1;
                            }


                        }


                        switch (alt27) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:824:6: ( (OR|'||') fact )=> (OR|'||')f= fact
                    	    {
                    	    if ( input.LA(1)==OR||input.LA(1)==66 ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2124);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2141);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;


        
        		d=null;
        		ColumnDescr column = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:846:11: (id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:846:11: id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
               			column = new ColumnDescr( );
               			d = column; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2202);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        column.setObjectType( id );
               		        column.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2216); if (failed) return d;
            if ( backtracking==0 ) {
              
               				column.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        column.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:859:4: ( ( constraints[column] )=> constraints[column] )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( ((LA29_0>=PACKAGE && LA29_0<=ATTRIBUTES)||LA29_0==ENABLED||LA29_0==SALIENCE||(LA29_0>=DURATION && LA29_0<=LEFT_PAREN)||(LA29_0>=CONTAINS && LA29_0<=EXCLUDES)||LA29_0==NULL||(LA29_0>=AND && LA29_0<=THEN)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:859:6: ( constraints[column] )=> constraints[column]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2226);
                    constraints(column);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2239); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:871:1: constraints[ColumnDescr column] : ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* ;
    public void constraints(ColumnDescr column) throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:4: ( ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:4: ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )
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
                    new NoViableAltException("872:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:5: ( constraint[column] )=> constraint[column]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2260);
                    constraint(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:24: predicate[column]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2263);
                    predicate(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:873:3: ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( (LA32_0==64) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:873:5: ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] )
            	    {
            	    match(input,64,FOLLOW_64_in_constraints2271); if (failed) return ;
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:873:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )
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
            	            new NoViableAltException("873:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 31, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:873:10: ( constraint[column] )=> constraint[column]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2274);
            	            constraint(column);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:873:29: predicate[column]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2277);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:876:1: constraint[ColumnDescr column] : ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? ;
    public void constraint(ColumnDescr column) throws RecognitionException {   
        Token fb=null;
        Token con=null;
        Token f = null;

        RestrictionDescr rd = null;


        
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:3: ( ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:3: ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:3: ( ( ID ':' )=>fb= ID ':' )?
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( (LA33_0==ID) ) {
                int LA33_1 = input.LA(2);
                if ( (LA33_1==65) ) {
                    alt33=1;
                }
            }
            switch (alt33) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:5: ( ID ':' )=>fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2306); if (failed) return ;
                    match(input,65,FOLLOW_65_in_constraint2308); if (failed) return ;
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

            pushFollow(FOLLOW_identifier_in_constraint2329);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:3: ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            int alt35=3;
            int LA35_0 = input.LA(1);
            if ( ((LA35_0>=CONTAINS && LA35_0<=EXCLUDES)||(LA35_0>=70 && LA35_0<=75)) ) {
                alt35=1;
            }
            else if ( (LA35_0==69) ) {
                alt35=2;
            }
            switch (alt35) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:4: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:4: (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:6: rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2347);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:919:5: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);
                        if ( ((LA34_0>=67 && LA34_0<=68)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:6: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=67 && input.LA(1)<=68) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2369);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      
                    	      						if (con.getText().equals("&") ) {								
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      						} else {
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      						}							
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2388);
                    	    rd=constraint_expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	      
                    	      						fc.addRestriction(rd);
                    	      					
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:935:4: ( '->' predicate[column] )=> '->' predicate[column]
                    {
                    match(input,69,FOLLOW_69_in_constraint2416); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_constraint2418);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:939:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=CONTAINS && input.LA(1)<=EXCLUDES)||(input.LA(1)>=70 && input.LA(1)<=75) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2455);    throw mse;
            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:951:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            int alt36=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA36_1 = input.LA(2);
                if ( (LA36_1==62) ) {
                    alt36=2;
                }
                else if ( (LA36_1==EOF||LA36_1==RIGHT_PAREN||LA36_1==64||(LA36_1>=67 && LA36_1<=68)) ) {
                    alt36=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("951:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 36, 1, input);

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
                    new NoViableAltException("951:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:951:5: ( ID )=>bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2522); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:956:4: ( enum_constraint )=>lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2538);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:961:4: ( literal_constraint )=>lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2561);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:965:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2575);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:972:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
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
                    new NoViableAltException("976:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:6: ( STRING )=>t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2614); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:977:5: ( INT )=>t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2625); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:978:5: ( FLOAT )=>t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2638); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:979:5: ( BOOL )=>t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2649); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint2661); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:984:1: enum_constraint returns [String text] : id= ID ( ( '.' identifier )=> '.' ident= identifier )+ ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token id=null;
        Token ident = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2696); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:32: ( ( '.' identifier )=> '.' ident= identifier )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);
                if ( (LA38_0==62) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:34: ( '.' identifier )=> '.' ident= identifier
            	    {
            	    match(input,62,FOLLOW_62_in_enum_constraint2702); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint2706);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:1: predicate[ColumnDescr column] : text= paren_chunk ;
    public void predicate(ColumnDescr column) throws RecognitionException {   
        String text = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:995:3: (text= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:995:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate2731);
            text=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		        String body = text.substring(1, text.length()-1);
              			PredicateDescr d = new PredicateDescr( body );
              			column.addDescr( d );
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:1: paren_chunk returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2778); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1019:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )*
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);
                if ( ((LA39_0>=PACKAGE && LA39_0<=OR)||(LA39_0>=CONTAINS && LA39_0<=76)) ) {
                    alt39=1;
                }
                else if ( (LA39_0==LEFT_PAREN) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1020:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=OR)||(input.LA(1)>=CONTAINS && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2794);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1025:4: ( paren_chunk )=>chunk= paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2818);
            	    chunk=paren_chunk();
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2854); if (failed) return text;
            if ( backtracking==0 ) {
              
                                  buf.append( loc.getText() );
              		    text = buf.toString();
                              
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1044:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public String curly_chunk(BaseDescr descr) throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1050:3: (loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1050:3: loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk2905); if (failed) return text;
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1058:3: ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);
                if ( ((LA40_0>=PACKAGE && LA40_0<=NULL)||(LA40_0>=LEFT_SQUARE && LA40_0<=76)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_CURLY) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1059:4: (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk2921);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1064:4: ( curly_chunk[descr] )=>chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk2945);
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk2982); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1086:1: square_chunk returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE ;
    public String square_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1092:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1092:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3043); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1102:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);
                if ( ((LA41_0>=PACKAGE && LA41_0<=RIGHT_CURLY)||(LA41_0>=AND && LA41_0<=76)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_SQUARE) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1103:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3059);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1108:4: ( square_chunk )=>chunk= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3083);
            	    chunk=square_chunk();
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3119); if (failed) return text;
            if ( backtracking==0 ) {
              
                                  buf.append( loc.getText() );
              		    text = buf.toString();
                              
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1127:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1132:3: (c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1132:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3164);
            c=paren_chunk();
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1135:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:3: (left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:3: left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3191);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1142:3: ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( (LA42_0==OR||LA42_0==66) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1142:5: ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==OR||input.LA(1)==66 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3200);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3210);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1162:3: (left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1162:3: left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3246);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1163:3: ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( (LA43_0==AND||LA43_0==76) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1163:5: ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary
            	    {
            	    if ( input.LA(1)==AND||input.LA(1)==76 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3255);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3265);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1177:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1181:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1181:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1181:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )
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
                    new NoViableAltException("1181:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1181:6: ( lhs_exist )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3302);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1182:5: ( lhs_not )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3310);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:5: ( lhs_eval )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3318);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:5: ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary3326);
                    u=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:18: ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) ) )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);
                    if ( (LA45_0==FROM) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1185:13: ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary3342); if (failed) return d;
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1185:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) )
                            int alt44=3;
                            switch ( input.LA(1) ) {
                            case ACCUMULATE:
                                if ( (synpred71()) ) {
                                    alt44=1;
                                }
                                else if ( (true) ) {
                                    alt44=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1185:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) )", 44, 1, input);

                                    throw nvae;
                                }
                                break;
                            case COLLECT:
                                if ( (synpred72()) ) {
                                    alt44=2;
                                }
                                else if ( (true) ) {
                                    alt44=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1185:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) )", 44, 2, input);

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
                                    new NoViableAltException("1185:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (fm= from_statement ) )", 44, 0, input);

                                throw nvae;
                            }

                            switch (alt44) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1186:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1186:32: (ac= accumulate_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1186:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3370);
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
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1187:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1187:29: (cs= collect_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1187:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary3399);
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
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1188:14: (fm= from_statement )
                                    {
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1188:14: (fm= from_statement )
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1188:15: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary3421);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1191:5: ( lhs_forall )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary3460);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1192:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3468); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary3472);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3474); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3484);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1197:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:4: (loc= EXISTS ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:4: loc= EXISTS ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist3508); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:15: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1201:15: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:16: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3511); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3515);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3517); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:40: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3523);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			d = new ExistsDescr( (ColumnDescr) column ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1208:1: lhs_not returns [NotDescr d] : loc= NOT ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:4: (loc= NOT ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:4: loc= NOT ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not3553); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:12: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1212:12: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:13: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3556); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3560);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3563); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:38: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3569);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
              
              			d = new NotDescr( column ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: (loc= EVAL c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: loc= EVAL c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval3597); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3601);
            c=paren_chunk();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              		        String body = c.substring(1, c.length()-1);
              			checkTrailingSemicolon( body, offset(loc.getLine()) );
              			d = new EvalDescr( body ); 
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1231:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')' ;
    public ForallDescr lhs_forall() throws RecognitionException {   
        ForallDescr d = null;

        Token loc=null;
        BaseDescr base = null;

        BaseDescr column = null;


        
        		d = factory.createForall();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:4: (loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:4: loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall3629); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall3631); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_lhs_forall3635);
            base=lhs_column();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              		        // adding the base column
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1241:3: ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+
            int cnt50=0;
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( (LA50_0==ID||LA50_0==64) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1241:5: ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1241:5: ( ( ',' )=> ',' )?
            	    int alt49=2;
            	    int LA49_0 = input.LA(1);
            	    if ( (LA49_0==64) ) {
            	        alt49=1;
            	    }
            	    switch (alt49) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1241:6: ( ',' )=> ','
            	            {
            	            match(input,64,FOLLOW_64_in_lhs_forall3649); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_column_in_lhs_forall3655);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall3668); if (failed) return d;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* ;
    public String dotted_name(BaseDescr descr) throws RecognitionException {   
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1255:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1255:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3695); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1263:3: ( ( '.' identifier )=> '.' ident= identifier )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);
                if ( (LA51_0==62) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1263:5: ( '.' identifier )=> '.' ident= identifier
            	    {
            	    match(input,62,FOLLOW_62_in_dotted_name3707); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name3711);
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1271:3: ( ( '[' ']' )=> '[' loc= ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);
                if ( (LA52_0==LEFT_SQUARE) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1271:5: ( '[' ']' )=> '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name3733); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name3737); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1281:1: argument returns [String name] : id= identifier ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id = null;


        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:3: (id= identifier ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:3: id= identifier ( ( '[' ']' )=> '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument3776);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:40: ( ( '[' ']' )=> '[' ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);
                if ( (LA53_0==LEFT_SQUARE) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:42: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument3782); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument3784); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:1: rhs_chunk[RuleDescr rule] : start= THEN ( (~ END )=>~ END )* loc= END ;
    public void rhs_chunk(RuleDescr rule) throws RecognitionException {   
        Token start=null;
        Token loc=null;

        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:10: (start= THEN ( (~ END )=>~ END )* loc= END )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:10: start= THEN ( (~ END )=>~ END )* loc= END
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk3828); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:3: ( (~ END )=>~ END )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( ((LA54_0>=PACKAGE && LA54_0<=QUERY)||(LA54_0>=TEMPLATE && LA54_0<=76)) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1302:6: (~ END )=>~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk3840);    throw mse;
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
            match(input,END,FOLLOW_END_in_rhs_chunk3877); if (failed) return ;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1332:1: name returns [String s] : ( ( ID )=>tok= ID | str= STRING ) ;
    public String name() throws RecognitionException {   
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:2: ( ( ( ID )=>tok= ID | str= STRING ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:2: ( ( ID )=>tok= ID | str= STRING )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:2: ( ( ID )=>tok= ID | str= STRING )
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
                    new NoViableAltException("1334:2: ( ( ID )=>tok= ID | str= STRING )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1335:6: ( ID )=>tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name3921); if (failed) return s;
                    if ( backtracking==0 ) {
                      
                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1340:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name3940); if (failed) return s;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1347:1: identifier returns [Token tok] : ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) ;
    public Token identifier() throws RecognitionException {   
        Token tok = null;

        Token t=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1349:2: ( ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1349:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1349:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
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
                    new NoViableAltException("1349:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1349:10: ( ID )=>t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier3978); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:4: ( PACKAGE )=>t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier3991); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1351:4: ( FUNCTION )=>t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier3998); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1352:4: ( GLOBAL )=>t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier4005); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1353:4: ( IMPORT )=>t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier4012); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1354:4: ( RULE )=>t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier4021); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1355:4: ( QUERY )=>t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier4028); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1356:17: ( TEMPLATE )=>t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier4049); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1357:17: ( ATTRIBUTES )=>t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier4077); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1358:17: ( ENABLED )=>t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier4103); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1359:17: ( SALIENCE )=>t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier4132); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:17: ( DURATION )=>t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier4154); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1361:17: ( FROM )=>t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier4176); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1362:17: ( ACCUMULATE )=>t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier4205); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1363:17: ( INIT )=>t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier4227); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1364:17: ( ACTION )=>t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier4256); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:17: ( RESULT )=>t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier4285); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1366:17: ( COLLECT )=>t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier4314); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1367:17: ( OR )=>t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier4343); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1368:17: ( AND )=>t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier4372); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1369:17: ( CONTAINS )=>t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier4401); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1370:17: ( EXCLUDES )=>t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier4423); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1371:17: ( MATCHES )=>t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier4445); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1372:17: ( NULL )=>t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier4474); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1373:17: ( EXISTS )=>t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier4503); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:17: ( NOT )=>t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier4532); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1375:17: ( EVAL )=>t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier4561); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1376:17: ( FORALL )=>t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier4590); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1377:17: ( WHEN )=>t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier4628); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1378:17: ( THEN )=>t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier4660); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1379:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier4689); if (failed) return tok;

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
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:4: ( import_statement )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:4: import_statement
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
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:205:10: ( function_import_statement )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:205:10: function_import_statement
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
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: ( ( ( identifier LEFT_PAREN )=> identifier paren_chunk ) )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: ( ( identifier LEFT_PAREN )=> identifier paren_chunk )
        {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: ( ( identifier LEFT_PAREN )=> identifier paren_chunk )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:6: ( identifier LEFT_PAREN )=> identifier paren_chunk
        {
        pushFollow(FOLLOW_identifier_in_synpred361665);
        identifier();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_paren_chunk_in_synpred361669);
        paren_chunk();
        _fsp--;
        if (failed) return ;

        }


        }
    }
    // $ANTLR end synpred36

    // $ANTLR start synpred37
    public void synpred37_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:696:6: ( identifier ~ LEFT_PAREN )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:696:8: identifier ~ LEFT_PAREN
        {
        pushFollow(FOLLOW_identifier_in_synpred371701);
        identifier();
        _fsp--;
        if (failed) return ;
        if ( (input.LA(1)>=PACKAGE && input.LA(1)<=OR)||(input.LA(1)>=RIGHT_PAREN && input.LA(1)<=76) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred371703);    throw mse;
        }


        }
    }
    // $ANTLR end synpred37

    // $ANTLR start synpred40
    public void synpred40_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:6: ( LEFT_PAREN )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred401819); if (failed) return ;

        }
    }
    // $ANTLR end synpred40

    // $ANTLR start synpred43
    public void synpred43_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:824:6: ( (OR|'||') fact )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:824:6: (OR|'||') fact
        {
        if ( input.LA(1)==OR||input.LA(1)==66 ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred432124);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred432141);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred43

    // $ANTLR start synpred71
    public void synpred71_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1186:14: ( ACCUMULATE )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1186:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred713361); if (failed) return ;

        }
    }
    // $ANTLR end synpred71

    // $ANTLR start synpred72
    public void synpred72_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1187:14: ( COLLECT )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1187:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred723390); if (failed) return ;

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
    public boolean synpred37() {
        backtracking++;
        int start = input.mark();
        try {
            synpred37_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred43() {
        backtracking++;
        int start = input.mark();
        try {
            synpred43_fragment(); // can never throw exception
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
    public boolean synpred40() {
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


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    public static final String DFA4_eotS =
        "\u0390\uffff";
    public static final String DFA4_eofS =
        "\7\uffff\1\10\2\uffff\6\11\6\uffff\6\10\6\uffff\37\10\6\11\10\uffff"+
        "\1\11\12\uffff\37\11\2\uffff\1\10\46\uffff\37\11\22\uffff\1\11\1"+
        "\uffff\37\10\44\uffff\37\10\22\uffff\1\10\101\uffff\40\11\1\uffff"+
        "\2\10\6\uffff\1\10\37\11\100\uffff\40\10\1\11\1\uffff\1\11\6\uffff"+
        "\1\11\1\10\2\uffff\1\11\44\uffff\1\10\1\11\42\uffff\1\10\4\uffff"+
        "\1\11\76\uffff\1\11\141\uffff\1\10\137\uffff";
    public static final String DFA4_minS =
        "\1\5\1\4\5\uffff\1\4\2\uffff\1\4\1\5\1\4\2\5\2\4\1\37\1\4\2\17\3"+
        "\4\1\5\1\4\2\5\1\4\1\37\1\4\2\17\2\4\1\5\2\4\2\5\1\4\30\5\1\4\1"+
        "\5\1\4\2\5\2\4\1\37\1\4\2\17\11\4\1\37\2\4\2\17\1\4\1\5\1\4\1\5"+
        "\1\4\2\5\1\4\27\5\7\4\1\53\37\11\1\4\1\53\37\5\2\4\1\11\2\4\7\11"+
        "\1\4\5\11\1\5\42\4\1\53\37\11\1\4\1\53\37\5\2\4\1\11\1\4\2\11\1"+
        "\4\5\11\1\4\5\11\1\5\101\4\1\5\37\4\1\0\2\5\2\4\1\0\3\4\1\5\137"+
        "\4\1\5\37\4\1\5\1\0\1\5\2\4\1\0\3\4\2\5\1\4\1\53\1\5\41\4\1\53\1"+
        "\4\1\53\2\5\1\4\1\53\40\4\1\5\1\4\1\53\1\4\1\53\1\5\u0100\4";
    public static final String DFA4_maxS =
        "\1\13\1\62\5\uffff\1\77\2\uffff\6\77\1\62\1\41\1\62\2\41\1\62\6"+
        "\77\6\75\45\77\6\75\2\76\1\77\1\100\2\76\2\62\1\41\2\62\2\41\37"+
        "\77\2\76\1\77\1\100\2\76\1\62\1\53\37\75\1\62\1\53\37\75\1\62\6"+
        "\75\1\101\3\75\1\101\1\114\3\75\1\101\2\75\1\76\37\77\2\62\1\53"+
        "\37\75\1\62\1\53\37\75\1\62\6\75\1\101\3\75\1\101\1\114\1\101\5"+
        "\75\40\76\1\62\37\76\1\62\1\37\37\77\1\0\2\75\1\101\1\114\1\0\1"+
        "\114\2\101\1\75\37\77\37\76\1\62\37\76\1\62\1\37\37\77\1\75\1\0"+
        "\1\75\1\101\1\114\1\0\1\114\2\101\1\75\1\37\1\62\1\53\1\75\40\114"+
        "\1\62\1\53\1\62\1\53\2\37\1\62\1\53\40\114\1\75\1\62\1\53\1\62\1"+
        "\53\1\37\37\76\1\62\36\114\1\76\1\114\37\76\1\62\37\76\1\62\37\76"+
        "\1\62\1\76\37\114\37\76\1\62\37\76\1\62";
    public static final String DFA4_acceptS =
        "\2\uffff\1\3\1\4\1\5\1\6\1\7\1\uffff\1\1\1\2\u0386\uffff";
    public static final String DFA4_specialS =
        "\u01ab\uffff\1\1\4\uffff\1\2\u0084\uffff\1\3\3\uffff\1\0\u0156\uffff}>";
    public static final String[] DFA4_transition = {
        "\1\1\1\3\1\2\1\6\1\uffff\1\4\1\5",
        "\2\10\1\7\7\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3"+
        "\10\1\uffff\1\10\4\uffff\7\10",
        "",
        "",
        "",
        "",
        "",
        "\1\11\1\14\1\12\1\13\1\16\1\11\1\17\1\15\2\11\3\uffff\1\11\1\uffff"+
        "\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff"+
        "\3\10",
        "",
        "",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff"+
        "\3\11",
        "\4\11\1\uffff\2\11\23\uffff\1\10\35\uffff\3\11",
        "\1\10\1\27\1\26\1\30\1\33\1\10\1\31\1\32\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff"+
        "\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\10\17\uffff\1\10\35\uffff\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\10\17\uffff\1\10\35\uffff\3\11",
        "\1\10\1\36\1\34\1\35\1\40\1\10\1\41\1\37\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff"+
        "\3\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11",
        "\1\11\1\uffff\1\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11",
        "\1\11\17\uffff\1\11\1\uffff\1\10",
        "\1\11\17\uffff\1\11\1\uffff\1\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11",
        "\1\51\1\42\1\44\1\43\1\47\1\100\1\45\1\46\1\76\1\52\3\uffff\1\53"+
        "\1\uffff\1\54\5\uffff\1\55\1\57\1\60\1\61\1\62\1\63\1\50\1\64\2"+
        "\uffff\1\66\1\70\1\67\1\uffff\1\71\4\uffff\1\65\1\56\1\72\1\73\1"+
        "\74\1\75\1\77\12\uffff\3\10",
        "\1\11\1\103\1\101\1\102\1\105\1\11\1\106\1\104\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7"+
        "\11\12\uffff\3\10",
        "\4\10\1\uffff\2\10\23\uffff\1\11\35\uffff\3\10",
        "\1\11\1\111\1\107\1\110\1\113\1\11\1\114\1\112\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7"+
        "\11\12\uffff\3\10",
        "\4\10\1\uffff\2\10\3\uffff\1\11\17\uffff\1\11\35\uffff\3\10",
        "\4\10\1\uffff\2\10\3\uffff\1\11\17\uffff\1\11\35\uffff\3\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\115\1\11\2\uffff"+
        "\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\1\116\35\uffff\1\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\117\1\11\2\uffff"+
        "\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\1\11\17\uffff\1\120\35\uffff\1\10",
        "\1\11\17\uffff\1\121\35\uffff\1\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\122\1\11\2\uffff"+
        "\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10\12\uffff\3\10",
        "\4\10\1\uffff\2\10\23\uffff\1\10\1\uffff\1\11\33\uffff\3\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10\12\uffff\3\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10\12\uffff\3\10",
        "\4\10\1\uffff\2\10\3\uffff\1\10\17\uffff\1\10\1\uffff\1\11\33\uffff"+
        "\3\10",
        "\4\10\1\uffff\2\10\3\uffff\1\10\17\uffff\1\10\1\uffff\1\11\33\uffff"+
        "\3\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\11\11\1\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\3\10",
        "\1\133\1\136\1\134\1\135\1\140\1\170\1\141\1\137\1\166\1\142\3\uffff"+
        "\1\143\1\uffff\1\144\5\uffff\1\145\1\147\1\150\1\151\1\152\1\153"+
        "\1\132\1\154\2\uffff\1\156\1\160\1\157\1\uffff\1\161\4\uffff\1\155"+
        "\1\146\1\162\1\163\1\164\1\165\1\167\12\uffff\3\11",
        "\4\11\1\uffff\2\11\23\uffff\1\10\35\uffff\3\11",
        "\1\10\1\27\1\26\1\30\1\33\1\10\1\31\1\32\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff"+
        "\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\10\17\uffff\1\10\35\uffff\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\10\17\uffff\1\10\35\uffff\3\11",
        "\1\10\1\36\1\34\1\35\1\40\1\10\1\41\1\37\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff"+
        "\3\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\171\1\10\2\uffff"+
        "\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\172\35\uffff\1\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\173\1\10\2\uffff"+
        "\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\10\17\uffff\1\174\35\uffff\1\11",
        "\1\10\17\uffff\1\175\35\uffff\1\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\176\1\10\2\uffff"+
        "\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\1\11\1"+
        "\uffff\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1"+
        "\uffff\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13"+
        "\uffff\1\177",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\10\10\1\u00cc\1\u00c8\1\11\1\uffff\1\11\1\u00c9\1\uffff\1\u00ca"+
        "\1\uffff\4\11\1\u00cb\7\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10"+
        "\1\uffff\6\10\1\u00cd\13\uffff\1\10\1\uffff\1\11",
        "\5\10\1\u00d3\4\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\u00d1"+
        "\1\10\1\11\1\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff\2\10\1"+
        "\u00ce\1\u00cf\1\u00d0\1\u00d2\1\10\13\uffff\1\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\u00d4\1\10\2\uffff"+
        "\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\10",
        "\1\u00d6\1\u00d9\1\u00d7\1\u00d8\1\u00db\1\u00f3\1\u00dc\1\u00da"+
        "\1\u00f1\1\u00dd\3\uffff\1\u00de\1\uffff\1\u00df\5\uffff\1\u00e0"+
        "\1\u00e2\1\u00e3\1\u00e4\1\u00e5\1\u00e6\1\u00d5\1\u00e7\2\uffff"+
        "\1\u00e9\1\u00eb\1\u00ea\1\uffff\1\u00ec\4\uffff\1\u00e8\1\u00e1"+
        "\1\u00ed\1\u00ee\1\u00ef\1\u00f0\1\u00f2",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10",
        "\1\10\1\uffff\1\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\4\uffff\7\10",
        "\1\10\17\uffff\1\10\1\uffff\1\11",
        "\1\10\17\uffff\1\10\1\uffff\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\11\10\1\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11\12\uffff\3\11",
        "\4\11\1\uffff\2\11\23\uffff\1\11\1\uffff\1\10\33\uffff\3\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11\12\uffff\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\11\17\uffff\1\11\1\uffff\1\10\33\uffff"+
        "\3\11",
        "\4\11\1\uffff\2\11\3\uffff\1\11\17\uffff\1\11\1\uffff\1\10\33\uffff"+
        "\3\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\4\uffff\7\11\12\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\3\11",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\1\10\1"+
        "\uffff\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1"+
        "\uffff\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13"+
        "\uffff\1\u00f5",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\10\11\1\u0142\1\u013e\1\10\1\uffff\1\10\1\u013f\1\uffff\1\u0140"+
        "\1\uffff\4\10\1\u0141\7\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1\11"+
        "\1\uffff\6\11\1\u0143\13\uffff\1\11\1\uffff\1\10",
        "\5\11\1\u0149\4\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\u0144"+
        "\1\11\1\10\1\uffff\3\11\1\uffff\1\11\2\uffff\1\11\1\uffff\2\11\1"+
        "\u0145\1\u0146\1\u0147\1\u0148\1\11\13\uffff\1\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\u014a\1\11\2\uffff"+
        "\3\11\1\uffff\1\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\11",
        "\1\u014c\1\u014f\1\u014d\1\u014e\1\u0151\1\u0169\1\u0152\1\u0150"+
        "\1\u0167\1\u0153\3\uffff\1\u0154\1\uffff\1\u0155\5\uffff\1\u0156"+
        "\1\u0158\1\u0159\1\u015a\1\u015b\1\u015c\1\u014b\1\u015d\2\uffff"+
        "\1\u015f\1\u0161\1\u0160\1\uffff\1\u0162\4\uffff\1\u015e\1\u0157"+
        "\1\u0163\1\u0164\1\u0165\1\u0166\1\u0168",
        "\1\u016a",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\u016c\1\u016f\1\u016d\1\u016e\1\u0171\1\u0189\1\u0172\1\u0170"+
        "\1\u0187\1\u0173\3\uffff\1\u0174\1\uffff\1\u0175\5\uffff\1\u0176"+
        "\1\u0178\1\u0179\1\u017a\1\u017b\1\u017c\1\u016b\1\u017d\2\uffff"+
        "\1\u017f\1\u0181\1\u0180\1\uffff\1\u0182\4\uffff\1\u017e\1\u0177"+
        "\1\u0183\1\u0184\1\u0185\1\u0186\1\u0188",
        "\1\u018a",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u018b",
        "\1\u018d\1\u0190\1\u018e\1\u018f\1\u0192\1\u01aa\1\u0193\1\u0191"+
        "\1\u01a8\1\u0194\3\uffff\1\u0195\1\uffff\1\u0196\5\uffff\1\u0197"+
        "\1\u0199\1\u019a\1\u019b\1\u019c\1\u019d\1\u018c\1\u019e\2\uffff"+
        "\1\u01a0\1\u01a2\1\u01a1\1\uffff\1\u01a3\4\uffff\1\u019f\1\u0198"+
        "\1\u01a4\1\u01a5\1\u01a6\1\u01a7\1\u01a9",
        "\5\11\1\u01ab\4\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\117"+
        "\1\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\1\10\25\uffff\1\116\35\uffff\1\10",
        "\5\11\1\u01ac\4\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\115"+
        "\1\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\5\11\1\u01ad\4\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\122"+
        "\1\11\2\uffff\3\11\1\uffff\1\11\4\uffff\7\11\12\uffff\1\10",
        "\1\10\5\uffff\1\11\17\uffff\1\120\35\uffff\1\10",
        "\1\10\5\uffff\1\11\17\uffff\1\121\35\uffff\1\10",
        "\1\10\25\uffff\1\10\35\uffff\1\10\3\uffff\1\11",
        "\1\10\10\uffff\1\11\14\uffff\1\10\35\uffff\1\10",
        "\1\10\12\uffff\1\11\12\uffff\1\10\35\uffff\1\10",
        "\1\10\12\uffff\1\11\12\uffff\1\10\35\uffff\1\10",
        "\1\10\25\uffff\1\u01ae\1\uffff\1\11\14\uffff\5\11\12\uffff\1\10"+
        "\3\uffff\1\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\1\10\25\uffff\1\u01b2\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\u01b3\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\1\10\25\uffff\1\10\1\uffff\1\11\10\uffff\1\11\22\uffff\1\10\1\11"+
        "\2\uffff\1\11",
        "\1\10\25\uffff\1\10\1\uffff\1\11\33\uffff\1\10",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\10",
        "\5\11\1\u01b4\4\11\3\uffff\1\11\1\uffff\1\11\5\uffff\6\11\1\u014a"+
        "\1\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1\11\1\uffff\7\11\12\uffff"+
        "\1\10\1\11",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\11\1\124\1\126\1\125\1\131\1\11\1\127\1\130\2\11\3\uffff\1\11"+
        "\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1\11\2\uffff\1"+
        "\11\1\uffff\7\11\12\uffff\1\10\1\123\1\10",
        "\1\u01b6\1\u01b9\1\u01b7\1\u01b8\1\u01bb\1\u01d3\1\u01bc\1\u01ba"+
        "\1\u01d1\1\u01bd\3\uffff\1\u01be\1\uffff\1\u01bf\5\uffff\1\u01c0"+
        "\1\u01c2\1\u01c3\1\u01c4\1\u01c5\1\u01c6\1\u01b5\1\u01c7\2\uffff"+
        "\1\u01c9\1\u01cb\1\u01ca\1\uffff\1\u01cc\4\uffff\1\u01c8\1\u01c1"+
        "\1\u01cd\1\u01ce\1\u01cf\1\u01d0\1\u01d2",
        "\1\u01d5\1\u01d8\1\u01d6\1\u01d7\1\u01da\1\u01f2\1\u01db\1\u01d9"+
        "\1\u01f0\1\u01dc\3\uffff\1\u01dd\1\uffff\1\u01de\5\uffff\1\u01df"+
        "\1\u01e1\1\u01e2\1\u01e3\1\u01e4\1\u01e5\1\u01d4\1\u01e6\2\uffff"+
        "\1\u01e8\1\u01ea\1\u01e9\1\uffff\1\u01eb\4\uffff\1\u01e7\1\u01e0"+
        "\1\u01ec\1\u01ed\1\u01ee\1\u01ef\1\u01f1",
        "\1\u01f3",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\u01f5\1\u01f8\1\u01f6\1\u01f7\1\u01fa\1\u0212\1\u01fb\1\u01f9"+
        "\1\u0210\1\u01fc\3\uffff\1\u01fd\1\uffff\1\u01fe\5\uffff\1\u01ff"+
        "\1\u0201\1\u0202\1\u0203\1\u0204\1\u0205\1\u01f4\1\u0206\2\uffff"+
        "\1\u0208\1\u020a\1\u0209\1\uffff\1\u020b\4\uffff\1\u0207\1\u0200"+
        "\1\u020c\1\u020d\1\u020e\1\u020f\1\u0211",
        "\1\u0213",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0214",
        "\1\u0216\1\u0219\1\u0217\1\u0218\1\u021b\1\u0233\1\u021c\1\u021a"+
        "\1\u0231\1\u021d\3\uffff\1\u021e\1\uffff\1\u021f\5\uffff\1\u0220"+
        "\1\u0222\1\u0223\1\u0224\1\u0225\1\u0226\1\u0215\1\u0227\2\uffff"+
        "\1\u0229\1\u022b\1\u022a\1\uffff\1\u022c\4\uffff\1\u0228\1\u0221"+
        "\1\u022d\1\u022e\1\u022f\1\u0230\1\u0232",
        "\5\10\1\u0234\4\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\171"+
        "\1\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\11\25\uffff\1\172\35\uffff\1\11",
        "\5\10\1\u0235\4\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\173"+
        "\1\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\11\5\uffff\1\10\17\uffff\1\174\35\uffff\1\11",
        "\1\11\5\uffff\1\10\17\uffff\1\175\35\uffff\1\11",
        "\5\10\1\u0236\4\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\176"+
        "\1\10\2\uffff\3\10\1\uffff\1\10\4\uffff\7\10\12\uffff\1\11",
        "\1\11\25\uffff\1\11\35\uffff\1\11\3\uffff\1\10",
        "\1\11\10\uffff\1\10\14\uffff\1\11\35\uffff\1\11",
        "\1\11\12\uffff\1\10\12\uffff\1\11\35\uffff\1\11",
        "\1\11\12\uffff\1\10\12\uffff\1\11\35\uffff\1\11",
        "\1\11\25\uffff\1\u0237\1\uffff\1\10\14\uffff\5\10\12\uffff\1\11"+
        "\3\uffff\1\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\1\11\25\uffff\1\11\1\uffff\1\10\10\uffff\1\10\22\uffff\1\11\1\10"+
        "\2\uffff\1\10",
        "\1\11\25\uffff\1\u023b\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\u023c\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\1\11\25\uffff\1\11\1\uffff\1\10\33\uffff\1\11",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\11",
        "\5\10\1\u023d\4\10\3\uffff\1\10\1\uffff\1\10\5\uffff\6\10\1\u00d4"+
        "\1\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff\7\10\12\uffff"+
        "\1\11\1\10",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e\13\uffff"+
        "\1\177",
        "\1\u0082\1\u0085\1\u0083\1\u0084\1\u0087\1\u009f\1\u0088\1\u0086"+
        "\1\u009d\1\u0089\3\uffff\1\u008a\1\uffff\1\u008b\5\uffff\1\u008c"+
        "\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092\1\u0081\1\u0093\2\uffff"+
        "\1\u0095\1\u0097\1\u0096\1\uffff\1\u0098\2\uffff\1\u0080\1\uffff"+
        "\1\u0094\1\u008d\1\u0099\1\u009a\1\u009b\1\u009c\1\u009e",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf\13\uffff"+
        "\1\u00a0",
        "\1\u00a3\1\u00a6\1\u00a4\1\u00a5\1\u00a8\1\u00c0\1\u00a9\1\u00a7"+
        "\1\u00be\1\u00aa\3\uffff\1\u00ab\1\uffff\1\u00ac\5\uffff\1\u00ad"+
        "\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00a2\1\u00b4\2\uffff"+
        "\1\u00b6\1\u00b8\1\u00b7\1\uffff\1\u00b9\2\uffff\1\u00a1\1\uffff"+
        "\1\u00b5\1\u00ae\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00bf",
        "\4\11\1\10\2\11\23\uffff\1\10",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\12\uffff\1\11\1\u00c1\1\11",
        "\1\uffff",
        "\4\10\1\uffff\2\10\25\uffff\1\11\33\uffff\1\10",
        "\4\10\1\uffff\2\10\23\uffff\1\11\35\uffff\1\u023e",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f\2\uffff"+
        "\1\11",
        "\5\11\1\u01b0\25\11\1\u01b1\55\11",
        "\1\uffff",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262\2\uffff"+
        "\1\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264\2\uffff"+
        "\1\11",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\u0266",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\10\1\22\1\20\1\21\1\24\1\10\1\25\1\23\2\10\3\uffff\1\10\1\uffff"+
        "\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1\10\2\uffff\1\10\1\uffff"+
        "\7\10\12\uffff\1\11\1\u00f4\1\11",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114\13\uffff"+
        "\1\u00f5",
        "\1\u00f8\1\u00fb\1\u00f9\1\u00fa\1\u00fd\1\u0115\1\u00fe\1\u00fc"+
        "\1\u0113\1\u00ff\3\uffff\1\u0100\1\uffff\1\u0101\5\uffff\1\u0102"+
        "\1\u0104\1\u0105\1\u0106\1\u0107\1\u0108\1\u00f7\1\u0109\2\uffff"+
        "\1\u010b\1\u010d\1\u010c\1\uffff\1\u010e\2\uffff\1\u00f6\1\uffff"+
        "\1\u010a\1\u0103\1\u010f\1\u0110\1\u0111\1\u0112\1\u0114",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135\13\uffff"+
        "\1\u0116",
        "\1\u0119\1\u011c\1\u011a\1\u011b\1\u011e\1\u0136\1\u011f\1\u011d"+
        "\1\u0134\1\u0120\3\uffff\1\u0121\1\uffff\1\u0122\5\uffff\1\u0123"+
        "\1\u0125\1\u0126\1\u0127\1\u0128\1\u0129\1\u0118\1\u012a\2\uffff"+
        "\1\u012c\1\u012e\1\u012d\1\uffff\1\u012f\2\uffff\1\u0117\1\uffff"+
        "\1\u012b\1\u0124\1\u0130\1\u0131\1\u0132\1\u0133\1\u0135",
        "\4\10\1\11\2\10\23\uffff\1\11",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\12\uffff\1\10\1\u0137\1\10",
        "\4\11\1\uffff\2\11\25\uffff\1\10\33\uffff\1\11",
        "\1\uffff",
        "\4\11\1\uffff\2\11\23\uffff\1\10\35\uffff\1\u0267",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268\2\uffff"+
        "\1\10",
        "\5\10\1\u0239\25\10\1\u023a\55\10",
        "\1\uffff",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b\2\uffff"+
        "\1\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d\2\uffff"+
        "\1\10",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\u028f",
        "\4\10\1\uffff\2\10\23\uffff\1\11",
        "\1\u0291\1\u0294\1\u0292\1\u0293\1\u0296\1\u02ae\1\u0297\1\u0295"+
        "\1\u02ac\1\u0298\3\uffff\1\u0299\1\uffff\1\u029a\5\uffff\1\u029b"+
        "\1\u029d\1\u029e\1\u029f\1\u02a0\1\u02a1\1\u0290\1\u02a2\2\uffff"+
        "\1\u02a4\1\u02a6\1\u02a5\1\uffff\1\u02a7\4\uffff\1\u02a3\1\u029c"+
        "\1\u02a8\1\u02a9\1\u02aa\1\u02ab\1\u02ad",
        "\1\u02af",
        "\4\11\1\10\2\11\23\uffff\1\10\35\uffff\1\10",
        "\1\u02b1\1\u02b4\1\u02b2\1\u02b3\1\u02b6\1\u02ce\1\u02b7\1\u02b5"+
        "\1\u02cc\1\u02b8\3\11\1\u02b9\1\11\1\u02ba\5\11\1\u02bb\1\u02bd"+
        "\1\u02be\1\u02bf\1\u02c0\1\u02c1\1\u02b0\1\u02c2\2\11\1\u02c4\1"+
        "\u02c6\1\u02c5\1\11\1\u02c7\4\11\1\u02c3\1\u02bc\1\u02c8\1\u02c9"+
        "\1\u02ca\1\u02cb\1\u02cd\32\11",
        "\47\11\1\u02cf\41\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\5\11\1\u01b0\25\11\1\u01b1\35\11\1\u01af\17\11",
        "\1\u02d1\1\u02d4\1\u02d2\1\u02d3\1\u02d6\1\u02ee\1\u02d7\1\u02d5"+
        "\1\u02ec\1\u02d8\3\uffff\1\u02d9\1\uffff\1\u02da\5\uffff\1\u02db"+
        "\1\u02dd\1\u02de\1\u02df\1\u02e0\1\u02e1\1\u02d0\1\u02e2\2\uffff"+
        "\1\u02e4\1\u02e6\1\u02e5\1\uffff\1\u02e7\4\uffff\1\u02e3\1\u02dc"+
        "\1\u02e8\1\u02e9\1\u02ea\1\u02eb\1\u02ed",
        "\1\u02ef",
        "\1\u02f1\1\u02f4\1\u02f2\1\u02f3\1\u02f6\1\u030e\1\u02f7\1\u02f5"+
        "\1\u030c\1\u02f8\3\uffff\1\u02f9\1\uffff\1\u02fa\5\uffff\1\u02fb"+
        "\1\u02fd\1\u02fe\1\u02ff\1\u0300\1\u0301\1\u02f0\1\u0302\2\uffff"+
        "\1\u0304\1\u0306\1\u0305\1\uffff\1\u0307\4\uffff\1\u0303\1\u02fc"+
        "\1\u0308\1\u0309\1\u030a\1\u030b\1\u030d",
        "\1\u030f",
        "\4\10\1\11\2\10\23\uffff\1\11",
        "\4\11\1\uffff\2\11\23\uffff\1\10",
        "\1\u0311\1\u0314\1\u0312\1\u0313\1\u0316\1\u032e\1\u0317\1\u0315"+
        "\1\u032c\1\u0318\3\uffff\1\u0319\1\uffff\1\u031a\5\uffff\1\u031b"+
        "\1\u031d\1\u031e\1\u031f\1\u0320\1\u0321\1\u0310\1\u0322\2\uffff"+
        "\1\u0324\1\u0326\1\u0325\1\uffff\1\u0327\4\uffff\1\u0323\1\u031c"+
        "\1\u0328\1\u0329\1\u032a\1\u032b\1\u032d",
        "\1\u032f",
        "\1\u0332\1\u0335\1\u0333\1\u0334\1\u0337\1\u0330\1\u0338\1\u0336"+
        "\1\u034d\1\u0339\3\10\1\u033a\1\10\1\u033b\5\10\1\u033c\1\u033e"+
        "\1\u033f\1\u0340\1\u0341\1\u0342\1\u0331\1\u0343\2\10\1\u0345\1"+
        "\u0347\1\u0346\1\10\1\u0348\4\10\1\u0344\1\u033d\1\u0349\1\u034a"+
        "\1\u034b\1\u034c\1\u034e\32\10",
        "\47\10\1\u034f\41\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\5\10\1\u0239\25\10\1\u023a\35\10\1\u0238\17\10",
        "\4\10\1\11\2\10\23\uffff\1\11\35\uffff\1\11",
        "\1\u0351\1\u0354\1\u0352\1\u0353\1\u0356\1\u036e\1\u0357\1\u0355"+
        "\1\u036c\1\u0358\3\uffff\1\u0359\1\uffff\1\u035a\5\uffff\1\u035b"+
        "\1\u035d\1\u035e\1\u035f\1\u0360\1\u0361\1\u0350\1\u0362\2\uffff"+
        "\1\u0364\1\u0366\1\u0365\1\uffff\1\u0367\4\uffff\1\u0363\1\u035c"+
        "\1\u0368\1\u0369\1\u036a\1\u036b\1\u036d",
        "\1\u036f",
        "\1\u0371\1\u0374\1\u0372\1\u0373\1\u0376\1\u038e\1\u0377\1\u0375"+
        "\1\u038c\1\u0378\3\uffff\1\u0379\1\uffff\1\u037a\5\uffff\1\u037b"+
        "\1\u037d\1\u037e\1\u037f\1\u0380\1\u0381\1\u0370\1\u0382\2\uffff"+
        "\1\u0384\1\u0386\1\u0385\1\uffff\1\u0387\4\uffff\1\u0383\1\u037c"+
        "\1\u0388\1\u0389\1\u038a\1\u038b\1\u038d",
        "\1\u038f",
        "\4\11\1\10\2\11\23\uffff\1\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10\13\uffff\1\u023f",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0240\1\uffff\7\10",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\13\11\1\u0242\16\11",
        "\1\10\1\u00c2\1\u00c4\1\u00c3\1\u00c7\1\10\1\u00c5\1\u00c6\2\10"+
        "\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\2\uffff\3\10\1\uffff\1"+
        "\10\2\uffff\1\10\1\uffff\7\10\13\uffff\1\10",
        "\1\u0245\1\u0248\1\u0246\1\u0247\1\u024a\1\u0241\1\u024b\1\u0249"+
        "\1\u0260\1\u024c\3\11\1\u024d\1\11\1\u024e\5\11\1\u024f\1\u0251"+
        "\1\u0252\1\u0253\1\u0254\1\u0255\1\u0244\1\u0256\2\11\1\u0258\1"+
        "\u025a\1\u0259\1\11\1\u025b\2\11\1\u0243\1\11\1\u0257\1\u0250\1"+
        "\u025c\1\u025d\1\u025e\1\u025f\1\u0261\32\11",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10\13\uffff\1\u0262",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0263\1\uffff\7\10",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10\13\uffff\1\u0264",
        "\12\10\3\uffff\1\10\1\uffff\1\10\5\uffff\10\10\1\11\1\uffff\3\10"+
        "\1\uffff\1\10\2\uffff\1\u0265\1\uffff\7\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11\13\uffff\1\u0268",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u0269\1\uffff\7\11",
        "\1\11\1\u013a\1\u0138\1\u0139\1\u013c\1\11\1\u013d\1\u013b\2\11"+
        "\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\2\uffff\3\11\1\uffff\1"+
        "\11\2\uffff\1\11\1\uffff\7\11\13\uffff\1\11",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\13\10\1\u026a\16\10",
        "\1\u026d\1\u0270\1\u026e\1\u026f\1\u0272\1\u028a\1\u0273\1\u0271"+
        "\1\u0288\1\u0274\3\10\1\u0275\1\10\1\u0276\5\10\1\u0277\1\u0279"+
        "\1\u027a\1\u027b\1\u027c\1\u027d\1\u026c\1\u027e\2\10\1\u0280\1"+
        "\u0282\1\u0281\1\10\1\u0283\2\10\1\u026b\1\10\1\u027f\1\u0278\1"+
        "\u0284\1\u0285\1\u0286\1\u0287\1\u0289\32\10",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11\13\uffff\1\u028b",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028c\1\uffff\7\11",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11\13\uffff\1\u028d",
        "\12\11\3\uffff\1\11\1\uffff\1\11\5\uffff\10\11\1\10\1\uffff\3\11"+
        "\1\uffff\1\11\2\uffff\1\u028e\1\uffff\7\11"
    };

    class DFA4 extends DFA {
        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA.unpackEncodedString(DFA4_eotS);
            this.eof = DFA.unpackEncodedString(DFA4_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
            this.accept = DFA.unpackEncodedString(DFA4_acceptS);
            this.special = DFA.unpackEncodedString(DFA4_specialS);
            int numStates = DFA4_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA4_transition[i]);
            }
        }
        public String getDescription() {
            return "204:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )";
        }
        public int specialStateTransition(int s) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        input.rewind();
                        s = -1;
                        if ( (synpred4()) ) {s = 8;}

                        else if ( (synpred5()) ) {s = 9;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        input.rewind();
                        s = -1;
                        if ( (synpred4()) ) {s = 8;}

                        else if ( (synpred5()) ) {s = 9;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        input.rewind();
                        s = -1;
                        if ( (synpred4()) ) {s = 8;}

                        else if ( (synpred5()) ) {s = 9;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        input.rewind();
                        s = -1;
                        if ( (synpred4()) ) {s = 8;}

                        else if ( (synpred5()) ) {s = 9;}

                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    public static final String DFA8_eotS =
        "\6\uffff";
    public static final String DFA8_eofS =
        "\6\uffff";
    public static final String DFA8_minS =
        "\2\4\1\uffff\1\53\1\uffff\1\4";
    public static final String DFA8_maxS =
        "\1\62\1\100\1\uffff\1\53\1\uffff\1\100";
    public static final String DFA8_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\12\2\3\uffff\1\2\1\uffff\1\2\5\uffff\6\2\1\1\1\2\2\uffff\3\2\1"+
        "\uffff\1\2\4\uffff\7\2",
        "\12\4\3\uffff\1\4\1\uffff\1\4\5\uffff\10\4\1\uffff\1\2\3\4\1\uffff"+
        "\1\4\2\uffff\1\3\1\uffff\7\4\13\uffff\1\4\1\uffff\1\2",
        "",
        "\1\5",
        "",
        "\12\4\3\uffff\1\4\1\uffff\1\4\5\uffff\10\4\1\uffff\1\2\3\4\1\uffff"+
        "\1\4\2\uffff\1\3\1\uffff\7\4\15\uffff\1\2"
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
            return "322:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }
    public static final String DFA9_eotS =
        "\6\uffff";
    public static final String DFA9_eofS =
        "\6\uffff";
    public static final String DFA9_minS =
        "\2\4\2\uffff\1\53\1\4";
    public static final String DFA9_maxS =
        "\1\62\1\100\2\uffff\1\53\1\100";
    public static final String DFA9_acceptS =
        "\2\uffff\1\2\1\1\2\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\12\2\3\uffff\1\2\1\uffff\1\2\5\uffff\6\2\1\1\1\2\2\uffff\3\2\1"+
        "\uffff\1\2\4\uffff\7\2",
        "\12\3\3\uffff\1\3\1\uffff\1\3\5\uffff\10\3\1\uffff\1\2\3\3\1\uffff"+
        "\1\3\2\uffff\1\4\1\uffff\7\3\13\uffff\1\3\1\uffff\1\2",
        "",
        "",
        "\1\5",
        "\12\3\3\uffff\1\3\1\uffff\1\3\5\uffff\10\3\1\uffff\1\2\3\3\1\uffff"+
        "\1\3\2\uffff\1\4\1\uffff\7\3\15\uffff\1\2"
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
            return "326:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }
 

    public static final BitSet FOLLOW_61_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
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
    public static final BitSet FOLLOW_PACKAGE_in_package_statement202 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement206 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement241 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement264 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement293 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement295 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement318 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name349 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_62_in_import_name361 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_import_name365 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_63_in_import_name389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global425 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_dotted_name_in_global436 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_global448 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function477 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function482 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_function489 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function498 = new BitSet(new long[]{0x0007F0BDFE0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function508 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_argument_in_function515 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_function529 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function534 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_argument_in_function541 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function565 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query603 = new BitSet(new long[]{0x0000000080008000L});
    public static final BitSet FOLLOW_name_in_query607 = new BitSet(new long[]{0x0003C00280000200L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query620 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_END_in_query637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template667 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template671 = new BitSet(new long[]{0x2000000080000000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template673 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_template_slot_in_template688 = new BitSet(new long[]{0x0000000080000200L});
    public static final BitSet FOLLOW_END_in_template705 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot753 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot771 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule806 = new BitSet(new long[]{0x0000000080008000L});
    public static final BitSet FOLLOW_name_in_rule810 = new BitSet(new long[]{0x0004000003EB7000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule_attributes_in_rule819 = new BitSet(new long[]{0x0004000000001000L});
    public static final BitSet FOLLOW_WHEN_in_rule828 = new BitSet(new long[]{0x0007C00280000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule830 = new BitSet(new long[]{0x0007C00280000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule848 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes890 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule_attributes892 = new BitSet(new long[]{0x0000000003EB4002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_rule_attributes901 = new BitSet(new long[]{0x0000000003EB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes906 = new BitSet(new long[]{0x0000000003EB4002L,0x0000000000000001L});
    public static final BitSet FOLLOW_salience_in_rule_attribute947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1081 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1118 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1157 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1206 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_salience1210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1276 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1357 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1406 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1442 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1481 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_duration1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1512 = new BitSet(new long[]{0x0003C00280000002L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1665 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1669 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1712 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_expression_chain1759 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain1763 = new BitSet(new long[]{0x4000040200000002L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1794 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1827 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement1888 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1898 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1902 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_accumulate_statement1904 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement1913 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_accumulate_statement1919 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement1928 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_accumulate_statement1934 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement1943 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1947 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement1994 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2004 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement2008 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_fact_binding2048 = new BitSet(new long[]{0x0000000280000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2093 = new BitSet(new long[]{0x0000000280000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2097 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2111 = new BitSet(new long[]{0x0000000100000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_fact_expression2124 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_fact_in_fact_expression2141 = new BitSet(new long[]{0x0000000100000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_dotted_name_in_fact2202 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2216 = new BitSet(new long[]{0x0007F0BFFE0A3FF0L});
    public static final BitSet FOLLOW_constraints_in_fact2226 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2260 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_predicate_in_constraints2263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_constraints2271 = new BitSet(new long[]{0x0007F0BBFE0A3FF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2274 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_predicate_in_constraints2277 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_ID_in_constraint2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_constraint2308 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_constraint2329 = new BitSet(new long[]{0x0000003800000002L,0x0000000000000FE0L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2347 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000018L});
    public static final BitSet FOLLOW_set_in_constraint2369 = new BitSet(new long[]{0x0000003800000000L,0x0000000000000FC0L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2388 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000018L});
    public static final BitSet FOLLOW_69_in_constraint2416 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_predicate_in_constraint2418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2455 = new BitSet(new long[]{0x000000C280148000L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint2661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2696 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_enum_constraint2702 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint2706 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2778 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2794 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2818 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2905 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk2921 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2945 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk2982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3043 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3059 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3083 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3191 = new BitSet(new long[]{0x0000000100000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_lhs_or3200 = new BitSet(new long[]{0x0003C00280000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3210 = new BitSet(new long[]{0x0000000100000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3246 = new BitSet(new long[]{0x0000100000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_set_in_lhs_and3255 = new BitSet(new long[]{0x0003C00280000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3265 = new BitSet(new long[]{0x0000100000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3302 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3310 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3318 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3326 = new BitSet(new long[]{0x2000200000000002L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary3342 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3370 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3399 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3421 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3460 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3468 = new BitSet(new long[]{0x0003C00280000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary3472 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3474 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist3508 = new BitSet(new long[]{0x0000000280000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3511 = new BitSet(new long[]{0x0003C00280000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3515 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not3553 = new BitSet(new long[]{0x0000000280000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3556 = new BitSet(new long[]{0x0003C00280000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3560 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval3597 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall3629 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall3631 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3635 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_lhs_forall3649 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3655 = new BitSet(new long[]{0x0000000480000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3695 = new BitSet(new long[]{0x4000040000000002L});
    public static final BitSet FOLLOW_62_in_dotted_name3707 = new BitSet(new long[]{0x0007F0B9FE0A3FF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name3711 = new BitSet(new long[]{0x4000040000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name3733 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name3737 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_identifier_in_argument3776 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument3782 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument3784 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk3828 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk3840 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk3877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name3921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name3940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier3978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier3991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier3998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier4012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier4021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier4028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier4077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier4103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier4132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier4154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier4176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier4205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier4227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier4256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier4285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier4314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier4343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier4372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier4401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier4423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier4445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier4474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier4503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier4532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier4561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier4590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier4628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier4660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier4689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred4114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred5126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_synpred361665 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred361669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_synpred371701 = new BitSet(new long[]{0xFFFFFFFDFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_synpred371703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred401819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred432124 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_fact_in_synpred432141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred713361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred723390 = new BitSet(new long[]{0x0000000000000002L});

}