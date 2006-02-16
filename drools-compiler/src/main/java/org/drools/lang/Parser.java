package org.drools.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.rule.GlobalDeclaration;
import org.drools.rule.DuplicateRuleNameException;
import org.drools.rule.Rule;
import org.drools.rule.RuleConstructionException;

/**
 * The one, the only DRL parser.
 * Full of regex goodness. If you are not totally comfortable with regex,
 * then please either look elsewhere, or put on your safety pants.
 * 
 * @master Bob McWirther
 * @apprentice Michael Neale
 */
public class Parser {
	
    
    //----------------------------------------
    // General parser recogniser patterns
    //----------------------------------------
    static Pattern PACKAGE_DECL = Pattern.compile( "\\s*package\\s*([^;]+);?\\s*" );
	static Pattern IMPORT_STATEMENT = Pattern.compile( "\\s*import\\s*([^;]+);?\\s*" );
    static Pattern APP_DATA_STATEMENT = Pattern.compile( "\\s*application-data\\s*([^;]+)\\s([^;]+);?\\s*" );    
	static Pattern RULE_DECL = Pattern.compile( "\\s*rule\\s*(.*[^\\s]+)\\s*" );
    static Pattern FUNCTION_DECL = Pattern.compile("\\s*functions?\\s*");
    static Pattern EXPANDER_STATEMENT = Pattern.compile("\\s*use\\s*expander\\s([^;]+);?\\s*");
	static Pattern FACT_BINDING = Pattern.compile( "\\s*(\\w+)\\s*=>\\s*(.*)" );
	static Pattern FIELD_BINDING = Pattern.compile( "\\s*(\\w+):(\\w+)\\s*" );
	static Pattern SALIENCE_STATEMENT = Pattern.compile("\\s*salience\\s*([^;]+)\\s*");
    static Pattern NOLOOP_STATEMENT = Pattern.compile("\\s*no-loop\\s*([^;]+)\\s*");
    
    
    //----------------------------------------
    // Consequence handling patterns
    //----------------------------------------
    static String KNOWLEDGE_HELPER_PFX = ""; //could also be: "drools\\." for "classic" mode.
    static Pattern MODIFY = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "modify\\s*\\(([^\\)]+)\\)(.*)");
    static Pattern ASSERT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "assert\\s*\\(([^\\)]+)\\)(.*)");
    static Pattern RETRACT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "retract\\s*\\(([^\\)]+)\\)(.*)");

    
    //---------------------------------------
    // Constants
    //---------------------------------------
    static String COMMENT_1 = "#";
    static String COMMENT_2 = "//";
    
	private BufferedReader reader;
	private String packageDeclaration;
	private List imports;
    private Expander expander;
    private Expander consequenceExpander;
	private List rules;
    private Map globalDeclarations;
    private String functions;
    private int lineNumber;


    /**
     * @param reader A reader to UTF-8 encoded (preferably) DRL source.
     */
    public Parser(Reader reader) {
		this.reader  = new BufferedReader( reader );
		this.imports = new ArrayList();
		this.rules   = new ArrayList();
        this.expander = null;
        this.globalDeclarations = new HashMap();
        this.consequenceExpander = new ConsequenceExpander();
        this.lineNumber = 0;
	}
	
	public String getPackageDeclaration() {
		return packageDeclaration;
	}
	
	public List getImports() {
		return imports;
	}
	
	public List getRules() {
		return rules;
	}
    
    public String getFunctions() {
        return functions;
    }
    
    Expander getExpander() {
        return expander;
    }
	
	public void parse() throws IOException, RuleConstructionException {
        try {
    		prolog();
    		rules();
        } catch (Exception e) { //for misc exceptions. Stil want the line number.
            if (e instanceof ParseException) {
                throw (ParseException) e;
            } else {
                throw new ParseException("Parse error", this.lineNumber, e);
            }
        }
	}
	
	protected void prolog() throws IOException {
		packageDeclaration();
		importStatements();
        applicationDataStatements();
        expanderStatement();
        functions();
        //BOB: do we want to allow flexible ordering of functions, etc? 
        //I think they should be at the end actually...
	}
	
	protected void packageDeclaration() throws IOException {
		String line = laDiscard();
		
		if ( line == null ) { return; }
		
		Matcher matcher = PACKAGE_DECL.matcher( line );
		
		if ( matcher.matches() ) {
			consumeDiscard();
			packageDeclaration = matcher.group( 1 );
		}
	}
	
	protected void importStatements() throws IOException {
		while ( importStatement() ) {
			// just do it again
		}
	}
    
    protected void applicationDataStatements() throws IOException {
        while ( applicationDataStatement() ) {
            //nike
        }
    }
    
    protected boolean applicationDataStatement() throws IOException {
        String line = laDiscard();
        
        if ( line == null ) { return false; }
        
        Matcher matcher = APP_DATA_STATEMENT.matcher( line );
        
        if ( matcher.matches() ) {
            consumeDiscard();
            
            globalDeclarations.put( matcher.group( 2 ).trim(), matcher.group( 1 ).trim() );
            return true;
        }
        
        return false;
    }
    
	protected boolean importStatement() throws IOException {
		String line = laDiscard();
		
		if ( line == null ) { return false; }
		
		Matcher matcher = IMPORT_STATEMENT.matcher( line );
		
		if ( matcher.matches() ) {
			consumeDiscard();
			imports.add( matcher.group( 1 ) );
			return true;
		}
		
		return false;
	}
    
    protected boolean expanderStatement() throws IOException {
        String line = laDiscard();
        
        if ( line == null ) { return false; }
        
        Matcher matcher = EXPANDER_STATEMENT.matcher( line );
        
        if ( matcher.matches() ) {
            consumeDiscard();
            String expanderName = matcher.group( 1 );
            expander = ExpanderContext.getInstance().getExpander(expanderName);
            return true;
        }
        
        return false;
    }    
	
	protected void functions() throws IOException {
        String line = laDiscard();
        
        if ( line == null ) { return; }
        
        Matcher matcher = FUNCTION_DECL.matcher( line );
        
        if ( matcher.matches() ) {
            consumeDiscard();
            //here we could put the semantic type declaration
        } else {
            return; //no functions, chopper... here, theres no functions.
        }
        
        StringBuffer buffer = new StringBuffer();
        
        while ( true ) {
            line = laDiscard();
            if ( line == null ) {
                throw new ParseException( "end of functions expected", lineNumber );
            }
            String trimLine = line.trim();
            if ( trimLine.equals( "end" ) ) {
                break;
            }
            consumeDiscard();
            line = maybeExpand( trimLine );
            buffer.append( line + "\n" );
        }
        
        if ( ! line.trim().equals( "end" ) ) {
            throw new ParseException( "[end] of functions expected. But instead got: [" + line.trim() + "]", lineNumber);
        }
        
        this.functions = buffer.toString();
        
        consumeDiscard();
   }
    
	
	protected void rules() throws IOException, RuleConstructionException {
		while ( rule() ) {
			// do it again!
		}
	}
	
	protected boolean rule() throws IOException, RuleConstructionException {
		String line = laDiscard();
		
		if ( line == null ) { return false; }
		
		Matcher matcher = RULE_DECL.matcher( line );
		
		if ( matcher.matches() ) {
			consumeDiscard();
			String ruleName = matcher.group(1);
			
			for ( Iterator ruleIter = rules.iterator() ; ruleIter.hasNext() ; ) {
				Rule rule = (Rule) ruleIter.next();
				if ( rule.getName().equals( ruleName ) ) {
					throw new DuplicateRuleNameException( null, rule, null );
				}
			}
            Rule rule = new Rule( ruleName );
			rules.add( rule );
            
            ruleAttributes(rule);
            ruleConditions();
            ruleConsequence();
            
		} 
		
		line = laDiscard();
		
		if ( ! line.trim().equals( "end" ) ) {
			throw new ParseException( "end of rule expected. But instead got: [" + line.trim() + "]", lineNumber);
		}
		
		consumeDiscard();
		
		return true;
	}
	
    /** For rule attributes like salience, etc */
    protected void ruleAttributes(Rule rule) throws IOException {
        String line = laDiscard();
        
        if ( line == null ) { return; }
        
        while ( ruleAttribute(rule) ) {
            
        }
    }
    
    protected boolean ruleAttribute(Rule rule) throws IOException {
        String line = laDiscard();
        if ( line == null ) {
            return false;
        }
        
        line = line.trim();
        
        if ( line.equals( "when" ) || line.equals( "then" ) ) {
            return false;
        }        
        
        line = consume();
        
        Matcher matcher = SALIENCE_STATEMENT.matcher(line);
        if (matcher.matches()) {
            rule.setSalience(Integer.parseInt(matcher.group(1)));
            return true;
        }
        matcher = NOLOOP_STATEMENT.matcher(line);
        if (matcher.matches()) {
            rule.setNoLoop(Boolean.getBoolean(matcher.group(1)));
            return true;
        }
        
        
        return true;
    }
    
	protected void ruleConditions() throws IOException {
		String line = laDiscard();
		
		if ( line == null ) { return; }
		
		if ( line.trim().equals( "when" ) ) {
			consumeDiscard();
		} else {
			return;
		}
		
		while ( ruleCondition() ) {
			// do it again!
		}
	}
	
	protected boolean ruleCondition() throws IOException {
		String line = laDiscard();
		
		
		if ( line == null ) {
			return false;
		}
		
		line = line.trim();
		
		if ( line.equals( "then" ) || line.equals( "end" ) ) {
			return false;
		}
		
		Matcher matcher = FACT_BINDING.matcher( line.trim() );
		
		String name = null;
		String pattern = null;
		
		consumeDiscard();
		
		if ( matcher.matches() ) {
			name    = matcher.group( 1 );
			pattern = matcher.group( 2 );
		} else {
			pattern = line;
		}
		
        
		pattern = maybeExpand( pattern );
		
		pattern( pattern );
		
		return true;
	}

    /**
     * Only want to expand if we are using an expander, and if the pattern isn't escaped.
     */
    protected String maybeExpand(final String pattern) {
        if ( expanding() ) {
            if (pattern.startsWith( ">" )) {
                return pattern.substring(1);
            } else {
                return expand( pattern );
            }
		}
        return pattern;
    }
	
	protected String expand(String pattern) {
   		return expander.expand(pattern, this);
	}

    protected boolean expanding() {
        return this.expander != null;
    }
	
	protected void pattern(String pattern) {
		int leftParen = pattern.indexOf( "(" );
		
		if ( leftParen < 0 ) {
			throw new ParseException( "invalid pattern: " + pattern, lineNumber );
		}
		
		int rightParen = pattern.lastIndexOf( ")" );
		
		if ( rightParen < 0 ) {
			throw new ParseException( "invalid pattern: " + pattern, lineNumber );
		}
		
		String guts = pattern.substring( leftParen+1, rightParen ).trim();
		
		// TODO: Michael, should be also expand the guts? Nope. Don't really think so.
		
		StringTokenizer tokens = new StringTokenizer( guts, "," );
		
		while ( tokens.hasMoreTokens() ) {
			constraint( tokens.nextToken().trim() );
			//System.err.println( "constraint [" + tokens.nextToken().trim() + "]" );
		}
	}
	
	protected void constraint(String constraint) {
		Matcher matcher = FIELD_BINDING.matcher( constraint );
		
		if ( matcher.matches() ) {
			String bindTo = matcher.group( 1 );
			String field = matcher.group( 2 );
			System.err.println( "bind [" + bindTo + "] to field [" + field + "]" );
		} else {
			// TODO: Michael, want to jack in here also? Nope, not really...
			System.err.println( "further work required for [" + constraint + "]" );
		}
	}
	
	protected void ruleConsequence() throws IOException {
		String line = laDiscard();
		
		if ( line.trim().equals( "then" ) ) {
			consumeDiscard();
			
			StringBuffer consequence = new StringBuffer();
			
			while ( true ) {
				line = laDiscard();
				if ( line == null ) {
					throw new ParseException( "end expected", lineNumber );
				}
                String trimLine = line.trim();
				if ( trimLine.equals( "end" ) ) {
					break;
				}
				consumeDiscard();
                
                //we expand with special consequence expander first (for knowledge helper).
                line = maybeExpand( 
                                    consequenceExpander.expand(trimLine, this) 
                                   );
				consequence.append( line + "\n" );
			}
			System.err.println( "begin consequence");
			System.err.println( consequence );
			System.err.println( "end consequence");
		}
	}
	
	protected String la() throws IOException {
		reader.mark( 1024 );
		String line = reader.readLine();
		reader.reset();
		return line;
	}
	
	protected String laDiscard() throws IOException {
		reader.mark( 1024 );
		String line = null;
		
		while ( line == null ) {
			line = reader.readLine();
			
			if ( line == null ) {
				reader.reset();
				return null;
			}
			
			String trimLine = line.trim(); 
			
			if ( trimLine.length() == 0 || trimLine.startsWith( COMMENT_1 ) || trimLine.startsWith( COMMENT_2 ) ) {
				line = null;
			}
		}
		
		reader.reset();
		return line;
	}
	
	protected String consume() throws IOException {
        lineNumber++;
		return reader.readLine();
	}
	
	protected String consumeDiscard() throws IOException {
		String line = null;
		
		while ( line == null ) {
			line = reader.readLine();
			
			if ( line == null ) {
				reader.reset();
				return null;
			}
			lineNumber++;
            
			String trimLine = line.trim();
			
			if ( trimLine.length() == 0 || trimLine.startsWith( COMMENT_1 ) || trimLine.startsWith( COMMENT_2 ) ) {
				line = null;
			}
		}
		
		return line;
	}

    /**
     * @return A Map<<String>identifier, <String>Class> view of the app data
     */
    public Map getGlobalDeclarations() {
        return this.globalDeclarations;
    }

}
