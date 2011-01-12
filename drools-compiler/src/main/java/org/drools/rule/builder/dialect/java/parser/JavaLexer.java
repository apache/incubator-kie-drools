// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/semantics/java/parser/Java.g 2011-01-11 15:22:34

	package org.drools.rule.builder.dialect.java.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__59=59;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__73=73;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__84=84;
    public static final int T__85=85;
    public static final int T__86=86;
    public static final int T__87=87;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__90=90;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__99=99;
    public static final int T__100=100;
    public static final int T__101=101;
    public static final int T__102=102;
    public static final int T__103=103;
    public static final int T__104=104;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int T__107=107;
    public static final int T__108=108;
    public static final int T__109=109;
    public static final int T__110=110;
    public static final int T__111=111;
    public static final int T__112=112;
    public static final int T__113=113;
    public static final int T__114=114;
    public static final int T__115=115;
    public static final int T__116=116;
    public static final int T__117=117;
    public static final int Identifier=4;
    public static final int ENUM=5;
    public static final int FloatingPointLiteral=6;
    public static final int CharacterLiteral=7;
    public static final int StringLiteral=8;
    public static final int HexLiteral=9;
    public static final int OctalLiteral=10;
    public static final int DecimalLiteral=11;
    public static final int HexDigit=12;
    public static final int IntegerTypeSuffix=13;
    public static final int Exponent=14;
    public static final int FloatTypeSuffix=15;
    public static final int EscapeSequence=16;
    public static final int UnicodeEscape=17;
    public static final int OctalEscape=18;
    public static final int Letter=19;
    public static final int JavaIDDigit=20;
    public static final int WS=21;
    public static final int COMMENT=22;
    public static final int LINE_COMMENT=23;

    	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    	protected boolean enumIsKeyword = true;


    // delegates
    // delegators

    public JavaLexer() {;} 
    public JavaLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public JavaLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "src/main/resources/org/drools/semantics/java/parser/Java.g"; }

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:11:7: ( 'package' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:11:9: 'package'
            {
            match("package"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:12:7: ( ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:12:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:13:7: ( 'import' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:13:9: 'import'
            {
            match("import"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:14:7: ( 'static' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:14:9: 'static'
            {
            match("static"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:15:7: ( '.' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:15:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:16:7: ( '*' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:16:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:17:7: ( 'class' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:17:9: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:18:7: ( 'extends' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:18:9: 'extends'
            {
            match("extends"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:19:7: ( 'implements' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:19:9: 'implements'
            {
            match("implements"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:20:7: ( '<' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:20:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:21:7: ( ',' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:21:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:22:7: ( '>' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:22:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:23:7: ( '&' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:23:9: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:24:7: ( '{' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:24:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:25:7: ( '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:25:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:26:7: ( 'interface' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:26:9: 'interface'
            {
            match("interface"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:27:7: ( 'void' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:27:9: 'void'
            {
            match("void"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:28:7: ( '[' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:28:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:29:7: ( ']' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:29:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:30:7: ( 'throws' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:30:9: 'throws'
            {
            match("throws"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:31:7: ( '=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:31:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:32:7: ( 'public' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:32:9: 'public'
            {
            match("public"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:33:7: ( 'protected' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:33:9: 'protected'
            {
            match("protected"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:34:7: ( 'private' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:34:9: 'private'
            {
            match("private"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:35:7: ( 'abstract' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:35:9: 'abstract'
            {
            match("abstract"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:36:7: ( 'final' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:36:9: 'final'
            {
            match("final"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:37:7: ( 'native' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:37:9: 'native'
            {
            match("native"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:38:7: ( 'synchronized' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:38:9: 'synchronized'
            {
            match("synchronized"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:39:7: ( 'transient' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:39:9: 'transient'
            {
            match("transient"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:40:7: ( 'volatile' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:40:9: 'volatile'
            {
            match("volatile"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__53"

    // $ANTLR start "T__54"
    public final void mT__54() throws RecognitionException {
        try {
            int _type = T__54;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:41:7: ( 'strictfp' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:41:9: 'strictfp'
            {
            match("strictfp"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__54"

    // $ANTLR start "T__55"
    public final void mT__55() throws RecognitionException {
        try {
            int _type = T__55;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:42:7: ( 'boolean' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:42:9: 'boolean'
            {
            match("boolean"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__55"

    // $ANTLR start "T__56"
    public final void mT__56() throws RecognitionException {
        try {
            int _type = T__56;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:43:7: ( 'char' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:43:9: 'char'
            {
            match("char"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__56"

    // $ANTLR start "T__57"
    public final void mT__57() throws RecognitionException {
        try {
            int _type = T__57;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:44:7: ( 'byte' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:44:9: 'byte'
            {
            match("byte"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__57"

    // $ANTLR start "T__58"
    public final void mT__58() throws RecognitionException {
        try {
            int _type = T__58;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:45:7: ( 'short' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:45:9: 'short'
            {
            match("short"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__58"

    // $ANTLR start "T__59"
    public final void mT__59() throws RecognitionException {
        try {
            int _type = T__59;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:46:7: ( 'int' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:46:9: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__59"

    // $ANTLR start "T__60"
    public final void mT__60() throws RecognitionException {
        try {
            int _type = T__60;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:47:7: ( 'long' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:47:9: 'long'
            {
            match("long"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__60"

    // $ANTLR start "T__61"
    public final void mT__61() throws RecognitionException {
        try {
            int _type = T__61;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:48:7: ( 'float' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:48:9: 'float'
            {
            match("float"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__61"

    // $ANTLR start "T__62"
    public final void mT__62() throws RecognitionException {
        try {
            int _type = T__62;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:49:7: ( 'double' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:49:9: 'double'
            {
            match("double"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__62"

    // $ANTLR start "T__63"
    public final void mT__63() throws RecognitionException {
        try {
            int _type = T__63;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:50:7: ( '?' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:50:9: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__63"

    // $ANTLR start "T__64"
    public final void mT__64() throws RecognitionException {
        try {
            int _type = T__64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:51:7: ( 'super' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:51:9: 'super'
            {
            match("super"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__64"

    // $ANTLR start "T__65"
    public final void mT__65() throws RecognitionException {
        try {
            int _type = T__65;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:52:7: ( '(' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:52:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__65"

    // $ANTLR start "T__66"
    public final void mT__66() throws RecognitionException {
        try {
            int _type = T__66;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:53:7: ( ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:53:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__66"

    // $ANTLR start "T__67"
    public final void mT__67() throws RecognitionException {
        try {
            int _type = T__67;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:54:7: ( '...' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:54:9: '...'
            {
            match("..."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__67"

    // $ANTLR start "T__68"
    public final void mT__68() throws RecognitionException {
        try {
            int _type = T__68;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:55:7: ( 'null' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:55:9: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__68"

    // $ANTLR start "T__69"
    public final void mT__69() throws RecognitionException {
        try {
            int _type = T__69;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:56:7: ( 'true' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:56:9: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__69"

    // $ANTLR start "T__70"
    public final void mT__70() throws RecognitionException {
        try {
            int _type = T__70;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:57:7: ( 'false' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:57:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__70"

    // $ANTLR start "T__71"
    public final void mT__71() throws RecognitionException {
        try {
            int _type = T__71;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:58:7: ( '@' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:58:9: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__71"

    // $ANTLR start "T__72"
    public final void mT__72() throws RecognitionException {
        try {
            int _type = T__72;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:59:7: ( 'default' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:59:9: 'default'
            {
            match("default"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__72"

    // $ANTLR start "T__73"
    public final void mT__73() throws RecognitionException {
        try {
            int _type = T__73;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:60:7: ( 'assert' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:60:9: 'assert'
            {
            match("assert"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__73"

    // $ANTLR start "T__74"
    public final void mT__74() throws RecognitionException {
        try {
            int _type = T__74;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:61:7: ( ':' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:61:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__74"

    // $ANTLR start "T__75"
    public final void mT__75() throws RecognitionException {
        try {
            int _type = T__75;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:62:7: ( 'if' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:62:9: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__75"

    // $ANTLR start "T__76"
    public final void mT__76() throws RecognitionException {
        try {
            int _type = T__76;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:63:7: ( 'else' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:63:9: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__76"

    // $ANTLR start "T__77"
    public final void mT__77() throws RecognitionException {
        try {
            int _type = T__77;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:64:7: ( 'for' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:64:9: 'for'
            {
            match("for"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__77"

    // $ANTLR start "T__78"
    public final void mT__78() throws RecognitionException {
        try {
            int _type = T__78;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:65:7: ( 'while' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:65:9: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__78"

    // $ANTLR start "T__79"
    public final void mT__79() throws RecognitionException {
        try {
            int _type = T__79;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:66:7: ( 'do' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:66:9: 'do'
            {
            match("do"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__79"

    // $ANTLR start "T__80"
    public final void mT__80() throws RecognitionException {
        try {
            int _type = T__80;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:67:7: ( 'try' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:67:9: 'try'
            {
            match("try"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__80"

    // $ANTLR start "T__81"
    public final void mT__81() throws RecognitionException {
        try {
            int _type = T__81;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:68:7: ( 'finally' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:68:9: 'finally'
            {
            match("finally"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__81"

    // $ANTLR start "T__82"
    public final void mT__82() throws RecognitionException {
        try {
            int _type = T__82;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:69:7: ( 'switch' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:69:9: 'switch'
            {
            match("switch"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__82"

    // $ANTLR start "T__83"
    public final void mT__83() throws RecognitionException {
        try {
            int _type = T__83;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:70:7: ( 'return' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:70:9: 'return'
            {
            match("return"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__83"

    // $ANTLR start "T__84"
    public final void mT__84() throws RecognitionException {
        try {
            int _type = T__84;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:71:7: ( 'throw' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:71:9: 'throw'
            {
            match("throw"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__84"

    // $ANTLR start "T__85"
    public final void mT__85() throws RecognitionException {
        try {
            int _type = T__85;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:72:7: ( 'break' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:72:9: 'break'
            {
            match("break"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__85"

    // $ANTLR start "T__86"
    public final void mT__86() throws RecognitionException {
        try {
            int _type = T__86;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:73:7: ( 'continue' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:73:9: 'continue'
            {
            match("continue"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__86"

    // $ANTLR start "T__87"
    public final void mT__87() throws RecognitionException {
        try {
            int _type = T__87;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:74:7: ( 'modify' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:74:9: 'modify'
            {
            match("modify"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__87"

    // $ANTLR start "T__88"
    public final void mT__88() throws RecognitionException {
        try {
            int _type = T__88;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:75:7: ( 'exitPoints' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:75:9: 'exitPoints'
            {
            match("exitPoints"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__88"

    // $ANTLR start "T__89"
    public final void mT__89() throws RecognitionException {
        try {
            int _type = T__89;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:76:7: ( 'entryPoints' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:76:9: 'entryPoints'
            {
            match("entryPoints"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__89"

    // $ANTLR start "T__90"
    public final void mT__90() throws RecognitionException {
        try {
            int _type = T__90;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:77:7: ( 'channels' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:77:9: 'channels'
            {
            match("channels"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__90"

    // $ANTLR start "T__91"
    public final void mT__91() throws RecognitionException {
        try {
            int _type = T__91;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:78:7: ( 'catch' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:78:9: 'catch'
            {
            match("catch"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__91"

    // $ANTLR start "T__92"
    public final void mT__92() throws RecognitionException {
        try {
            int _type = T__92;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:79:7: ( 'case' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:79:9: 'case'
            {
            match("case"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__92"

    // $ANTLR start "T__93"
    public final void mT__93() throws RecognitionException {
        try {
            int _type = T__93;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:80:7: ( '+=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:80:9: '+='
            {
            match("+="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__93"

    // $ANTLR start "T__94"
    public final void mT__94() throws RecognitionException {
        try {
            int _type = T__94;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:81:7: ( '-=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:81:9: '-='
            {
            match("-="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__94"

    // $ANTLR start "T__95"
    public final void mT__95() throws RecognitionException {
        try {
            int _type = T__95;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:82:7: ( '*=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:82:9: '*='
            {
            match("*="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__95"

    // $ANTLR start "T__96"
    public final void mT__96() throws RecognitionException {
        try {
            int _type = T__96;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:83:7: ( '/=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:83:9: '/='
            {
            match("/="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__96"

    // $ANTLR start "T__97"
    public final void mT__97() throws RecognitionException {
        try {
            int _type = T__97;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:84:7: ( '&=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:84:9: '&='
            {
            match("&="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__97"

    // $ANTLR start "T__98"
    public final void mT__98() throws RecognitionException {
        try {
            int _type = T__98;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:85:7: ( '|=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:85:9: '|='
            {
            match("|="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__98"

    // $ANTLR start "T__99"
    public final void mT__99() throws RecognitionException {
        try {
            int _type = T__99;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:86:7: ( '^=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:86:9: '^='
            {
            match("^="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__99"

    // $ANTLR start "T__100"
    public final void mT__100() throws RecognitionException {
        try {
            int _type = T__100;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:87:8: ( '%=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:87:10: '%='
            {
            match("%="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__100"

    // $ANTLR start "T__101"
    public final void mT__101() throws RecognitionException {
        try {
            int _type = T__101;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:88:8: ( '||' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:88:10: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__101"

    // $ANTLR start "T__102"
    public final void mT__102() throws RecognitionException {
        try {
            int _type = T__102;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:89:8: ( '&&' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:89:10: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__102"

    // $ANTLR start "T__103"
    public final void mT__103() throws RecognitionException {
        try {
            int _type = T__103;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:90:8: ( '|' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:90:10: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__103"

    // $ANTLR start "T__104"
    public final void mT__104() throws RecognitionException {
        try {
            int _type = T__104;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:91:8: ( '^' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:91:10: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__104"

    // $ANTLR start "T__105"
    public final void mT__105() throws RecognitionException {
        try {
            int _type = T__105;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:92:8: ( '==' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:92:10: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__105"

    // $ANTLR start "T__106"
    public final void mT__106() throws RecognitionException {
        try {
            int _type = T__106;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:93:8: ( '!=' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:93:10: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__106"

    // $ANTLR start "T__107"
    public final void mT__107() throws RecognitionException {
        try {
            int _type = T__107;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:94:8: ( 'instanceof' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:94:10: 'instanceof'
            {
            match("instanceof"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__107"

    // $ANTLR start "T__108"
    public final void mT__108() throws RecognitionException {
        try {
            int _type = T__108;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:95:8: ( '+' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:95:10: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__108"

    // $ANTLR start "T__109"
    public final void mT__109() throws RecognitionException {
        try {
            int _type = T__109;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:96:8: ( '-' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:96:10: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__109"

    // $ANTLR start "T__110"
    public final void mT__110() throws RecognitionException {
        try {
            int _type = T__110;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:97:8: ( '/' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:97:10: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__110"

    // $ANTLR start "T__111"
    public final void mT__111() throws RecognitionException {
        try {
            int _type = T__111;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:98:8: ( '%' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:98:10: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__111"

    // $ANTLR start "T__112"
    public final void mT__112() throws RecognitionException {
        try {
            int _type = T__112;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:99:8: ( '++' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:99:10: '++'
            {
            match("++"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__112"

    // $ANTLR start "T__113"
    public final void mT__113() throws RecognitionException {
        try {
            int _type = T__113;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:100:8: ( '--' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:100:10: '--'
            {
            match("--"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__113"

    // $ANTLR start "T__114"
    public final void mT__114() throws RecognitionException {
        try {
            int _type = T__114;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:101:8: ( '~' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:101:10: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__114"

    // $ANTLR start "T__115"
    public final void mT__115() throws RecognitionException {
        try {
            int _type = T__115;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:102:8: ( '!' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:102:10: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__115"

    // $ANTLR start "T__116"
    public final void mT__116() throws RecognitionException {
        try {
            int _type = T__116;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:103:8: ( 'this' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:103:10: 'this'
            {
            match("this"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__116"

    // $ANTLR start "T__117"
    public final void mT__117() throws RecognitionException {
        try {
            int _type = T__117;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:104:8: ( 'new' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:104:10: 'new'
            {
            match("new"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__117"

    // $ANTLR start "HexLiteral"
    public final void mHexLiteral() throws RecognitionException {
        try {
            int _type = HexLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:993:12: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:993:14: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // src/main/resources/org/drools/semantics/java/parser/Java.g:993:28: ( HexDigit )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='F')||(LA1_0>='a' && LA1_0<='f')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:993:28: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:993:38: ( IntegerTypeSuffix )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='L'||LA2_0=='l') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:993:38: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HexLiteral"

    // $ANTLR start "DecimalLiteral"
    public final void mDecimalLiteral() throws RecognitionException {
        try {
            int _type = DecimalLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:995:16: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:995:18: ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )?
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:995:18: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='0') ) {
                alt4=1;
            }
            else if ( ((LA4_0>='1' && LA4_0<='9')) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:995:19: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:995:25: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:995:34: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:995:34: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:995:45: ( IntegerTypeSuffix )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='L'||LA5_0=='l') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:995:45: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DecimalLiteral"

    // $ANTLR start "OctalLiteral"
    public final void mOctalLiteral() throws RecognitionException {
        try {
            int _type = OctalLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:14: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:16: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:20: ( '0' .. '7' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>='0' && LA6_0<='7')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:997:21: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:32: ( IntegerTypeSuffix )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='L'||LA7_0=='l') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:997:32: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OctalLiteral"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1000:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1000:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "IntegerTypeSuffix"
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1003:19: ( ( 'l' | 'L' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1003:21: ( 'l' | 'L' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IntegerTypeSuffix"

    // $ANTLR start "FloatingPointLiteral"
    public final void mFloatingPointLiteral() throws RecognitionException {
        try {
            int _type = FloatingPointLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix )
            int alt19=4;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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

                    match('.'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:37: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:37: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:47: ( FloatTypeSuffix )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='D'||LA11_0=='F'||LA11_0=='d'||LA11_0=='f') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1006:47: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:9: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:13: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:25: ( Exponent )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='E'||LA13_0=='e') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:25: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:35: ( FloatTypeSuffix )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0=='D'||LA14_0=='F'||LA14_0=='d'||LA14_0=='f') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1007:35: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:9: ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )?
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:9: ( '0' .. '9' )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);

                    mExponent(); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:30: ( FloatTypeSuffix )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='D'||LA16_0=='F'||LA16_0=='d'||LA16_0=='f') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:30: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:9: ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:9: ( '0' .. '9' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt17 >= 1 ) break loop17;
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:21: ( Exponent )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='E'||LA18_0=='e') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:21: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    mFloatTypeSuffix(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FloatingPointLiteral"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1013:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1013:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // src/main/resources/org/drools/semantics/java/parser/Java.g:1013:22: ( '+' | '-' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='+'||LA20_0=='-') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:1013:33: ( '0' .. '9' )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1013:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "FloatTypeSuffix"
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1016:17: ( ( 'f' | 'F' | 'd' | 'D' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1016:19: ( 'f' | 'F' | 'd' | 'D' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "FloatTypeSuffix"

    // $ANTLR start "CharacterLiteral"
    public final void mCharacterLiteral() throws RecognitionException {
        try {
            int _type = CharacterLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1019:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1019:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1019:14: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='\\') ) {
                alt22=1;
            }
            else if ( ((LA22_0>='\u0000' && LA22_0<='&')||(LA22_0>='(' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFF')) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1019:16: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1019:33: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CharacterLiteral"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1023:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1023:8: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1023:12: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\\') ) {
                    alt23=1;
                }
                else if ( ((LA23_0>='\u0000' && LA23_0<='!')||(LA23_0>='#' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='\uFFFF')) ) {
                    alt23=2;
                }


                switch (alt23) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1023:14: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1023:31: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1028:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt24=1;
                    }
                    break;
                case 'u':
                    {
                    alt24=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt24=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1028:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1029:9: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1030:9: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "OctalEscape"
    public final void mOctalEscape() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt25=3;
            int LA25_0 = input.LA(1);

            if ( (LA25_0=='\\') ) {
                int LA25_1 = input.LA(2);

                if ( ((LA25_1>='0' && LA25_1<='3')) ) {
                    int LA25_2 = input.LA(3);

                    if ( ((LA25_2>='0' && LA25_2<='7')) ) {
                        int LA25_4 = input.LA(4);

                        if ( ((LA25_4>='0' && LA25_4<='7')) ) {
                            alt25=1;
                        }
                        else {
                            alt25=2;}
                    }
                    else {
                        alt25=3;}
                }
                else if ( ((LA25_1>='4' && LA25_1<='7')) ) {
                    int LA25_3 = input.LA(3);

                    if ( ((LA25_3>='0' && LA25_3<='7')) ) {
                        alt25=2;
                    }
                    else {
                        alt25=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:14: ( '0' .. '3' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:36: ( '0' .. '7' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1035:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1036:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1036:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1036:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1036:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1036:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1037:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1037:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1037:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OctalEscape"

    // $ANTLR start "UnicodeEscape"
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1042:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1042:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); 
            match('u'); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UnicodeEscape"

    // $ANTLR start "ENUM"
    public final void mENUM() throws RecognitionException {
        try {
            int _type = ENUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1045:5: ( 'enum' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1045:7: 'enum'
            {
            match("enum"); 

            if ( !enumIsKeyword ) _type=Identifier;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENUM"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1049:5: ( Letter ( Letter | JavaIDDigit )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1049:9: Letter ( Letter | JavaIDDigit )*
            {
            mLetter(); 
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1049:16: ( Letter | JavaIDDigit )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='$'||(LA26_0>='0' && LA26_0<='9')||(LA26_0>='A' && LA26_0<='Z')||LA26_0=='_'||(LA26_0>='a' && LA26_0<='z')||(LA26_0>='\u00C0' && LA26_0<='\u00D6')||(LA26_0>='\u00D8' && LA26_0<='\u00F6')||(LA26_0>='\u00F8' && LA26_0<='\u1FFF')||(LA26_0>='\u3040' && LA26_0<='\u318F')||(LA26_0>='\u3300' && LA26_0<='\u337F')||(LA26_0>='\u3400' && LA26_0<='\u3D2D')||(LA26_0>='\u4E00' && LA26_0<='\u9FFF')||(LA26_0>='\uF900' && LA26_0<='\uFAFF')||(LA26_0>='\uFF10' && LA26_0<='\uFF19')) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF')||(input.LA(1)>='\uFF10' && input.LA(1)<='\uFF19') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Identifier"

    // $ANTLR start "Letter"
    public final void mLetter() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1057:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "Letter"

    // $ANTLR start "JavaIDDigit"
    public final void mJavaIDDigit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1074:5: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' | '\\uff10' .. '\\uff19' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u06F0' && input.LA(1)<='\u06F9')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09EF')||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049')||(input.LA(1)>='\uFF10' && input.LA(1)<='\uFF19') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "JavaIDDigit"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1092:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1092:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1096:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1096:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // src/main/resources/org/drools/semantics/java/parser/Java.g:1096:14: ( options {greedy=false; } : . )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='*') ) {
                    int LA27_1 = input.LA(2);

                    if ( (LA27_1=='/') ) {
                        alt27=2;
                    }
                    else if ( ((LA27_1>='\u0000' && LA27_1<='.')||(LA27_1>='0' && LA27_1<='\uFFFF')) ) {
                        alt27=1;
                    }


                }
                else if ( ((LA27_0>='\u0000' && LA27_0<=')')||(LA27_0>='+' && LA27_0<='\uFFFF')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1096:42: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            match("*/"); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:12: (~ ( '\\n' | '\\r' ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>='\u0000' && LA28_0<='\t')||(LA28_0>='\u000B' && LA28_0<='\f')||(LA28_0>='\u000E' && LA28_0<='\uFFFF')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:26: ( '\\r' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='\r') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:26: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    public void mTokens() throws RecognitionException {
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1:8: ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | T__85 | T__86 | T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | T__93 | T__94 | T__95 | T__96 | T__97 | T__98 | T__99 | T__100 | T__101 | T__102 | T__103 | T__104 | T__105 | T__106 | T__107 | T__108 | T__109 | T__110 | T__111 | T__112 | T__113 | T__114 | T__115 | T__116 | T__117 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | ENUM | Identifier | WS | COMMENT | LINE_COMMENT )
        int alt30=105;
        alt30 = dfa30.predict(input);
        switch (alt30) {
            case 1 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:10: T__24
                {
                mT__24(); 

                }
                break;
            case 2 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:16: T__25
                {
                mT__25(); 

                }
                break;
            case 3 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:22: T__26
                {
                mT__26(); 

                }
                break;
            case 4 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:28: T__27
                {
                mT__27(); 

                }
                break;
            case 5 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:34: T__28
                {
                mT__28(); 

                }
                break;
            case 6 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:40: T__29
                {
                mT__29(); 

                }
                break;
            case 7 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:46: T__30
                {
                mT__30(); 

                }
                break;
            case 8 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:52: T__31
                {
                mT__31(); 

                }
                break;
            case 9 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:58: T__32
                {
                mT__32(); 

                }
                break;
            case 10 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:64: T__33
                {
                mT__33(); 

                }
                break;
            case 11 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:70: T__34
                {
                mT__34(); 

                }
                break;
            case 12 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:76: T__35
                {
                mT__35(); 

                }
                break;
            case 13 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:82: T__36
                {
                mT__36(); 

                }
                break;
            case 14 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:88: T__37
                {
                mT__37(); 

                }
                break;
            case 15 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:94: T__38
                {
                mT__38(); 

                }
                break;
            case 16 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:100: T__39
                {
                mT__39(); 

                }
                break;
            case 17 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:106: T__40
                {
                mT__40(); 

                }
                break;
            case 18 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:112: T__41
                {
                mT__41(); 

                }
                break;
            case 19 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:118: T__42
                {
                mT__42(); 

                }
                break;
            case 20 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:124: T__43
                {
                mT__43(); 

                }
                break;
            case 21 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:130: T__44
                {
                mT__44(); 

                }
                break;
            case 22 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:136: T__45
                {
                mT__45(); 

                }
                break;
            case 23 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:142: T__46
                {
                mT__46(); 

                }
                break;
            case 24 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:148: T__47
                {
                mT__47(); 

                }
                break;
            case 25 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:154: T__48
                {
                mT__48(); 

                }
                break;
            case 26 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:160: T__49
                {
                mT__49(); 

                }
                break;
            case 27 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:166: T__50
                {
                mT__50(); 

                }
                break;
            case 28 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:172: T__51
                {
                mT__51(); 

                }
                break;
            case 29 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:178: T__52
                {
                mT__52(); 

                }
                break;
            case 30 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:184: T__53
                {
                mT__53(); 

                }
                break;
            case 31 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:190: T__54
                {
                mT__54(); 

                }
                break;
            case 32 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:196: T__55
                {
                mT__55(); 

                }
                break;
            case 33 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:202: T__56
                {
                mT__56(); 

                }
                break;
            case 34 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:208: T__57
                {
                mT__57(); 

                }
                break;
            case 35 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:214: T__58
                {
                mT__58(); 

                }
                break;
            case 36 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:220: T__59
                {
                mT__59(); 

                }
                break;
            case 37 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:226: T__60
                {
                mT__60(); 

                }
                break;
            case 38 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:232: T__61
                {
                mT__61(); 

                }
                break;
            case 39 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:238: T__62
                {
                mT__62(); 

                }
                break;
            case 40 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:244: T__63
                {
                mT__63(); 

                }
                break;
            case 41 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:250: T__64
                {
                mT__64(); 

                }
                break;
            case 42 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:256: T__65
                {
                mT__65(); 

                }
                break;
            case 43 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:262: T__66
                {
                mT__66(); 

                }
                break;
            case 44 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:268: T__67
                {
                mT__67(); 

                }
                break;
            case 45 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:274: T__68
                {
                mT__68(); 

                }
                break;
            case 46 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:280: T__69
                {
                mT__69(); 

                }
                break;
            case 47 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:286: T__70
                {
                mT__70(); 

                }
                break;
            case 48 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:292: T__71
                {
                mT__71(); 

                }
                break;
            case 49 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:298: T__72
                {
                mT__72(); 

                }
                break;
            case 50 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:304: T__73
                {
                mT__73(); 

                }
                break;
            case 51 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:310: T__74
                {
                mT__74(); 

                }
                break;
            case 52 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:316: T__75
                {
                mT__75(); 

                }
                break;
            case 53 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:322: T__76
                {
                mT__76(); 

                }
                break;
            case 54 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:328: T__77
                {
                mT__77(); 

                }
                break;
            case 55 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:334: T__78
                {
                mT__78(); 

                }
                break;
            case 56 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:340: T__79
                {
                mT__79(); 

                }
                break;
            case 57 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:346: T__80
                {
                mT__80(); 

                }
                break;
            case 58 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:352: T__81
                {
                mT__81(); 

                }
                break;
            case 59 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:358: T__82
                {
                mT__82(); 

                }
                break;
            case 60 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:364: T__83
                {
                mT__83(); 

                }
                break;
            case 61 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:370: T__84
                {
                mT__84(); 

                }
                break;
            case 62 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:376: T__85
                {
                mT__85(); 

                }
                break;
            case 63 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:382: T__86
                {
                mT__86(); 

                }
                break;
            case 64 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:388: T__87
                {
                mT__87(); 

                }
                break;
            case 65 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:394: T__88
                {
                mT__88(); 

                }
                break;
            case 66 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:400: T__89
                {
                mT__89(); 

                }
                break;
            case 67 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:406: T__90
                {
                mT__90(); 

                }
                break;
            case 68 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:412: T__91
                {
                mT__91(); 

                }
                break;
            case 69 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:418: T__92
                {
                mT__92(); 

                }
                break;
            case 70 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:424: T__93
                {
                mT__93(); 

                }
                break;
            case 71 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:430: T__94
                {
                mT__94(); 

                }
                break;
            case 72 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:436: T__95
                {
                mT__95(); 

                }
                break;
            case 73 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:442: T__96
                {
                mT__96(); 

                }
                break;
            case 74 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:448: T__97
                {
                mT__97(); 

                }
                break;
            case 75 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:454: T__98
                {
                mT__98(); 

                }
                break;
            case 76 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:460: T__99
                {
                mT__99(); 

                }
                break;
            case 77 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:466: T__100
                {
                mT__100(); 

                }
                break;
            case 78 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:473: T__101
                {
                mT__101(); 

                }
                break;
            case 79 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:480: T__102
                {
                mT__102(); 

                }
                break;
            case 80 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:487: T__103
                {
                mT__103(); 

                }
                break;
            case 81 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:494: T__104
                {
                mT__104(); 

                }
                break;
            case 82 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:501: T__105
                {
                mT__105(); 

                }
                break;
            case 83 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:508: T__106
                {
                mT__106(); 

                }
                break;
            case 84 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:515: T__107
                {
                mT__107(); 

                }
                break;
            case 85 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:522: T__108
                {
                mT__108(); 

                }
                break;
            case 86 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:529: T__109
                {
                mT__109(); 

                }
                break;
            case 87 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:536: T__110
                {
                mT__110(); 

                }
                break;
            case 88 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:543: T__111
                {
                mT__111(); 

                }
                break;
            case 89 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:550: T__112
                {
                mT__112(); 

                }
                break;
            case 90 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:557: T__113
                {
                mT__113(); 

                }
                break;
            case 91 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:564: T__114
                {
                mT__114(); 

                }
                break;
            case 92 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:571: T__115
                {
                mT__115(); 

                }
                break;
            case 93 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:578: T__116
                {
                mT__116(); 

                }
                break;
            case 94 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:585: T__117
                {
                mT__117(); 

                }
                break;
            case 95 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:592: HexLiteral
                {
                mHexLiteral(); 

                }
                break;
            case 96 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:603: DecimalLiteral
                {
                mDecimalLiteral(); 

                }
                break;
            case 97 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:618: OctalLiteral
                {
                mOctalLiteral(); 

                }
                break;
            case 98 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:631: FloatingPointLiteral
                {
                mFloatingPointLiteral(); 

                }
                break;
            case 99 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:652: CharacterLiteral
                {
                mCharacterLiteral(); 

                }
                break;
            case 100 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:669: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 101 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:683: ENUM
                {
                mENUM(); 

                }
                break;
            case 102 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:688: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 103 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:699: WS
                {
                mWS(); 

                }
                break;
            case 104 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:702: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 105 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:1:710: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;

        }

    }


    protected DFA19 dfa19 = new DFA19(this);
    protected DFA30 dfa30 = new DFA30(this);
    static final String DFA19_eotS =
        "\7\uffff\1\10\2\uffff";
    static final String DFA19_eofS =
        "\12\uffff";
    static final String DFA19_minS =
        "\2\56\2\uffff\1\53\1\uffff\2\60\2\uffff";
    static final String DFA19_maxS =
        "\1\71\1\146\2\uffff\1\71\1\uffff\1\71\1\146\2\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\1\1\1\uffff\1\4\2\uffff\2\3";
    static final String DFA19_specialS =
        "\12\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\12\uffff\1\5\1\4\1\5\35\uffff\1\5\1\4\1\5",
            "",
            "",
            "\1\6\1\uffff\1\6\2\uffff\12\7",
            "",
            "\12\7",
            "\12\7\12\uffff\1\11\1\uffff\1\11\35\uffff\1\11\1\uffff\1\11",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "1005:1: FloatingPointLiteral : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix );";
        }
    }
    static final String DFA30_eotS =
        "\1\uffff\1\56\1\uffff\2\56\1\74\1\77\2\56\3\uffff\1\111\2\uffff"+
        "\1\56\2\uffff\1\56\1\116\6\56\5\uffff\3\56\1\143\1\146\1\152\1\155"+
        "\1\157\1\161\1\163\1\uffff\2\165\4\uffff\5\56\1\177\5\56\5\uffff"+
        "\7\56\3\uffff\3\56\2\uffff\15\56\1\u00a5\4\56\25\uffff\1\u00aa\1"+
        "\165\5\56\1\u00b2\1\56\1\uffff\26\56\1\u00cb\5\56\1\u00d1\2\56\1"+
        "\u00d4\5\56\1\uffff\4\56\1\uffff\7\56\1\uffff\10\56\1\u00ed\3\56"+
        "\1\u00f1\2\56\1\u00f4\1\56\1\u00f6\1\u00f7\2\56\1\u00fa\1\56\1\u00fc"+
        "\1\uffff\5\56\1\uffff\1\56\1\u0103\1\uffff\1\56\1\u0105\1\56\1\u0107"+
        "\20\56\1\u0118\1\u0119\1\56\1\u011b\1\uffff\2\56\1\u011e\1\uffff"+
        "\2\56\1\uffff\1\56\2\uffff\1\56\1\u0124\1\uffff\1\56\1\uffff\2\56"+
        "\1\u0129\1\u012a\1\u012b\1\56\1\uffff\1\56\1\uffff\1\u012e\1\uffff"+
        "\2\56\1\u0131\3\56\1\u0135\2\56\1\u0138\3\56\1\u013c\2\56\2\uffff"+
        "\1\u013f\1\uffff\2\56\1\uffff\4\56\1\u0146\1\uffff\2\56\1\u0149"+
        "\1\56\3\uffff\1\u014b\1\56\1\uffff\1\u014d\1\56\1\uffff\1\u014f"+
        "\1\u0150\1\u0151\1\uffff\1\56\1\u0153\1\uffff\3\56\1\uffff\2\56"+
        "\1\uffff\2\56\1\u015b\3\56\1\uffff\2\56\1\uffff\1\u0161\1\uffff"+
        "\1\u0162\1\uffff\1\u0163\3\uffff\1\56\1\uffff\3\56\1\u0168\1\56"+
        "\1\u016a\1\u016b\1\uffff\2\56\1\u016e\1\56\1\u0170\3\uffff\1\u0171"+
        "\1\56\1\u0173\1\56\1\uffff\1\56\2\uffff\2\56\1\uffff\1\u0178\2\uffff"+
        "\1\u0179\1\uffff\1\u017a\1\56\1\u017c\1\56\3\uffff\1\56\1\uffff"+
        "\1\u017f\1\u0180\2\uffff";
    static final String DFA30_eofS =
        "\u0181\uffff";
    static final String DFA30_minS =
        "\1\11\1\141\1\uffff\1\146\1\150\1\56\1\75\1\141\1\154\3\uffff\1"+
        "\46\2\uffff\1\157\2\uffff\1\150\1\75\1\142\2\141\2\157\1\145\5\uffff"+
        "\1\150\1\145\1\157\1\53\1\55\1\52\4\75\1\uffff\2\56\4\uffff\1\143"+
        "\1\142\1\151\1\160\1\163\1\44\1\141\1\156\1\157\1\160\1\151\5\uffff"+
        "\2\141\1\156\1\163\1\151\1\163\1\164\3\uffff\2\151\1\141\2\uffff"+
        "\2\163\1\156\1\157\1\154\1\162\1\164\1\154\1\167\1\157\1\164\1\145"+
        "\1\156\1\44\1\146\1\151\1\164\1\144\25\uffff\2\56\1\153\1\154\1"+
        "\164\1\166\1\154\1\44\1\164\1\uffff\1\164\1\151\1\143\1\162\1\145"+
        "\1\164\1\163\1\156\1\164\1\143\2\145\1\164\1\145\1\162\1\155\1\144"+
        "\1\141\1\157\1\163\1\156\1\145\1\44\1\164\1\145\2\141\1\163\1\44"+
        "\1\151\1\154\1\44\1\154\1\145\1\141\1\147\1\142\1\uffff\1\141\1"+
        "\154\1\165\1\151\1\uffff\1\141\1\151\1\145\1\141\1\162\1\145\1\162"+
        "\1\uffff\1\141\1\151\1\143\1\150\1\164\1\162\1\143\1\163\1\44\1"+
        "\156\1\151\1\150\1\44\1\156\1\120\1\44\1\171\2\44\1\164\1\167\1"+
        "\44\1\163\1\44\1\uffff\2\162\1\154\1\164\1\145\1\uffff\1\166\1\44"+
        "\1\uffff\1\145\1\44\1\153\1\44\1\154\1\165\1\145\1\162\1\146\1\147"+
        "\2\143\2\164\1\155\1\146\1\156\1\143\1\164\1\162\2\44\1\150\1\44"+
        "\1\uffff\1\145\1\156\1\44\1\uffff\1\144\1\157\1\uffff\1\120\2\uffff"+
        "\1\151\1\44\1\uffff\1\151\1\uffff\1\141\1\164\3\44\1\145\1\uffff"+
        "\1\141\1\uffff\1\44\1\uffff\1\145\1\154\1\44\1\156\1\171\1\145\1"+
        "\44\1\164\1\145\1\44\1\145\1\141\1\143\1\44\1\146\1\157\2\uffff"+
        "\1\44\1\uffff\1\154\1\165\1\uffff\1\163\1\151\1\157\1\154\1\44\1"+
        "\uffff\1\145\1\143\1\44\1\171\3\uffff\1\44\1\156\1\uffff\1\44\1"+
        "\164\1\uffff\3\44\1\uffff\1\145\1\44\1\uffff\1\156\1\143\1\145\1"+
        "\uffff\1\160\1\156\1\uffff\1\163\1\145\1\44\1\156\1\151\1\145\1"+
        "\uffff\1\156\1\164\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\3\uffff"+
        "\1\144\1\uffff\1\164\1\145\1\157\1\44\1\151\2\44\1\uffff\1\164\1"+
        "\156\1\44\1\164\1\44\3\uffff\1\44\1\163\1\44\1\146\1\uffff\1\172"+
        "\2\uffff\1\163\1\164\1\uffff\1\44\2\uffff\1\44\1\uffff\1\44\1\145"+
        "\1\44\1\163\3\uffff\1\144\1\uffff\2\44\2\uffff";
    static final String DFA30_maxS =
        "\1\ufaff\1\165\1\uffff\1\156\1\171\1\71\1\75\1\157\1\170\3\uffff"+
        "\1\75\2\uffff\1\157\2\uffff\1\162\1\75\1\163\1\157\1\165\1\171\2"+
        "\157\5\uffff\1\150\1\145\1\157\3\75\1\174\3\75\1\uffff\1\170\1\146"+
        "\4\uffff\1\143\1\142\1\157\1\160\1\164\1\uff19\1\162\1\156\1\157"+
        "\1\160\1\151\5\uffff\2\141\1\156\2\164\1\163\1\165\3\uffff\1\154"+
        "\1\162\1\171\2\uffff\2\163\1\156\1\157\1\154\1\162\1\164\1\154\1"+
        "\167\1\157\1\164\1\145\1\156\1\uff19\1\146\1\151\1\164\1\144\25"+
        "\uffff\2\146\1\153\1\154\1\164\1\166\1\157\1\uff19\1\164\1\uffff"+
        "\1\164\1\151\1\143\1\162\1\145\1\164\1\163\1\162\1\164\1\143\2\145"+
        "\1\164\1\145\1\162\1\155\1\144\1\141\1\157\1\163\1\156\1\145\1\uff19"+
        "\1\164\1\145\2\141\1\163\1\uff19\1\151\1\154\1\uff19\1\154\1\145"+
        "\1\141\1\147\1\142\1\uffff\1\141\1\154\1\165\1\151\1\uffff\1\141"+
        "\1\151\1\145\1\141\1\162\1\145\1\162\1\uffff\1\141\1\151\1\143\1"+
        "\150\1\164\1\162\1\143\1\163\1\uff19\1\156\1\151\1\150\1\uff19\1"+
        "\156\1\120\1\uff19\1\171\2\uff19\1\164\1\167\1\uff19\1\163\1\uff19"+
        "\1\uffff\2\162\1\154\1\164\1\145\1\uffff\1\166\1\uff19\1\uffff\1"+
        "\145\1\uff19\1\153\1\uff19\1\154\1\165\1\145\1\162\1\146\1\147\2"+
        "\143\2\164\1\155\1\146\1\156\1\143\1\164\1\162\2\uff19\1\150\1\uff19"+
        "\1\uffff\1\145\1\156\1\uff19\1\uffff\1\144\1\157\1\uffff\1\120\2"+
        "\uffff\1\151\1\uff19\1\uffff\1\151\1\uffff\1\141\1\164\3\uff19\1"+
        "\145\1\uffff\1\141\1\uffff\1\uff19\1\uffff\1\145\1\154\1\uff19\1"+
        "\156\1\171\1\145\1\uff19\1\164\1\145\1\uff19\1\145\1\141\1\143\1"+
        "\uff19\1\146\1\157\2\uffff\1\uff19\1\uffff\1\154\1\165\1\uffff\1"+
        "\163\1\151\1\157\1\154\1\uff19\1\uffff\1\145\1\143\1\uff19\1\171"+
        "\3\uffff\1\uff19\1\156\1\uffff\1\uff19\1\164\1\uffff\3\uff19\1\uffff"+
        "\1\145\1\uff19\1\uffff\1\156\1\143\1\145\1\uffff\1\160\1\156\1\uffff"+
        "\1\163\1\145\1\uff19\1\156\1\151\1\145\1\uffff\1\156\1\164\1\uffff"+
        "\1\uff19\1\uffff\1\uff19\1\uffff\1\uff19\3\uffff\1\144\1\uffff\1"+
        "\164\1\145\1\157\1\uff19\1\151\2\uff19\1\uffff\1\164\1\156\1\uff19"+
        "\1\164\1\uff19\3\uffff\1\uff19\1\163\1\uff19\1\146\1\uffff\1\172"+
        "\2\uffff\1\163\1\164\1\uffff\1\uff19\2\uffff\1\uff19\1\uffff\1\uff19"+
        "\1\145\1\uff19\1\163\3\uffff\1\144\1\uffff\2\uff19\2\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\6\uffff\1\12\1\13\1\14\1\uffff\1\16\1\17\1\uffff\1"+
        "\22\1\23\10\uffff\1\50\1\52\1\53\1\60\1\63\12\uffff\1\133\2\uffff"+
        "\1\143\1\144\1\146\1\147\13\uffff\1\54\1\5\1\142\1\110\1\6\7\uffff"+
        "\1\112\1\117\1\15\3\uffff\1\122\1\25\22\uffff\1\106\1\131\1\125"+
        "\1\107\1\132\1\126\1\111\1\150\1\151\1\127\1\113\1\116\1\120\1\114"+
        "\1\121\1\115\1\130\1\123\1\134\1\137\1\140\11\uffff\1\64\45\uffff"+
        "\1\70\4\uffff\1\141\7\uffff\1\44\30\uffff\1\71\5\uffff\1\66\2\uffff"+
        "\1\136\30\uffff\1\41\3\uffff\1\105\2\uffff\1\65\1\uffff\1\145\1"+
        "\21\2\uffff\1\135\1\uffff\1\56\6\uffff\1\55\1\uffff\1\42\1\uffff"+
        "\1\45\20\uffff\1\43\1\51\1\uffff\1\7\2\uffff\1\104\5\uffff\1\75"+
        "\4\uffff\1\32\1\46\1\57\2\uffff\1\76\2\uffff\1\67\3\uffff\1\26\2"+
        "\uffff\1\3\3\uffff\1\4\2\uffff\1\73\6\uffff\1\24\2\uffff\1\62\1"+
        "\uffff\1\33\1\uffff\1\47\1\uffff\1\74\1\100\1\1\1\uffff\1\30\7\uffff"+
        "\1\10\5\uffff\1\72\1\40\1\61\4\uffff\1\37\1\uffff\1\103\1\77\2\uffff"+
        "\1\36\1\uffff\1\31\1\27\1\uffff\1\20\4\uffff\1\35\1\11\1\124\1\uffff"+
        "\1\101\2\uffff\1\102\1\34";
    static final String DFA30_specialS =
        "\u0181\uffff}>";
    static final String[] DFA30_transitionS = {
            "\2\57\1\uffff\2\57\22\uffff\1\57\1\50\1\55\1\uffff\1\56\1\47"+
            "\1\14\1\54\1\33\1\34\1\6\1\42\1\12\1\43\1\5\1\44\1\52\11\53"+
            "\1\36\1\2\1\11\1\23\1\13\1\32\1\35\32\56\1\20\1\uffff\1\21\1"+
            "\46\1\56\1\uffff\1\24\1\27\1\7\1\31\1\10\1\25\2\56\1\3\2\56"+
            "\1\30\1\41\1\26\1\56\1\1\1\56\1\40\1\4\1\22\1\56\1\17\1\37\3"+
            "\56\1\15\1\45\1\16\1\51\101\uffff\27\56\1\uffff\37\56\1\uffff"+
            "\u1f08\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff"+
            "\u092e\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\60\20\uffff\1\62\2\uffff\1\61",
            "",
            "\1\65\6\uffff\1\63\1\64",
            "\1\70\13\uffff\1\66\1\71\1\uffff\1\72\1\uffff\1\67",
            "\1\73\1\uffff\12\75",
            "\1\76",
            "\1\103\6\uffff\1\101\3\uffff\1\100\2\uffff\1\102",
            "\1\105\1\uffff\1\106\11\uffff\1\104",
            "",
            "",
            "",
            "\1\110\26\uffff\1\107",
            "",
            "",
            "\1\112",
            "",
            "",
            "\1\113\11\uffff\1\114",
            "\1\115",
            "\1\117\20\uffff\1\120",
            "\1\123\7\uffff\1\121\2\uffff\1\122\2\uffff\1\124",
            "\1\125\3\uffff\1\127\17\uffff\1\126",
            "\1\130\2\uffff\1\132\6\uffff\1\131",
            "\1\133",
            "\1\135\11\uffff\1\134",
            "",
            "",
            "",
            "",
            "",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\142\21\uffff\1\141",
            "\1\145\17\uffff\1\144",
            "\1\150\4\uffff\1\151\15\uffff\1\147",
            "\1\153\76\uffff\1\154",
            "\1\156",
            "\1\160",
            "\1\162",
            "",
            "\1\75\1\uffff\10\166\2\75\12\uffff\3\75\21\uffff\1\164\13\uffff"+
            "\3\75\21\uffff\1\164",
            "\1\75\1\uffff\12\167\12\uffff\3\75\35\uffff\3\75",
            "",
            "",
            "",
            "",
            "\1\170",
            "\1\171",
            "\1\173\5\uffff\1\172",
            "\1\174",
            "\1\176\1\175",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0080\20\uffff\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\u0084",
            "\1\u0085",
            "",
            "",
            "",
            "",
            "",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u008a\1\u0089",
            "\1\u008c\12\uffff\1\u008b",
            "\1\u008d",
            "\1\u008e\1\u008f",
            "",
            "",
            "",
            "\1\u0090\2\uffff\1\u0091",
            "\1\u0093\10\uffff\1\u0092",
            "\1\u0094\23\uffff\1\u0095\3\uffff\1\u0096",
            "",
            "",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\24"+
            "\56\1\u00a4\5\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08"+
            "\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12"+
            "\56",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\75\1\uffff\10\166\2\75\12\uffff\3\75\35\uffff\3\75",
            "\1\75\1\uffff\12\167\12\uffff\3\75\35\uffff\3\75",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00b0\2\uffff\1\u00af",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\4\56"+
            "\1\u00b1\25\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12"+
            "\56",
            "\1\u00b3",
            "",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bc\3\uffff\1\u00bb",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00cc",
            "\1\u00cd",
            "\1\u00ce",
            "\1\u00cf",
            "\1\u00d0",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00d2",
            "\1\u00d3",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "",
            "\1\u00da",
            "\1\u00db",
            "\1\u00dc",
            "\1\u00dd",
            "",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00ee",
            "\1\u00ef",
            "\1\u00f0",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00f2",
            "\1\u00f3",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00f5",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00f8",
            "\1\u00f9",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u00fb",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "",
            "\1\u0102",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u0104",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0106",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "\1\u0112",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "\1\u0117",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u011a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u011c",
            "\1\u011d",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u011f",
            "\1\u0120",
            "",
            "\1\u0121",
            "",
            "",
            "\1\u0122",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\22"+
            "\56\1\u0123\7\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08"+
            "\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12"+
            "\56",
            "",
            "\1\u0125",
            "",
            "\1\u0126",
            "\1\u0127",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\13"+
            "\56\1\u0128\16\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08"+
            "\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12"+
            "\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u012c",
            "",
            "\1\u012d",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u012f",
            "\1\u0130",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0132",
            "\1\u0133",
            "\1\u0134",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0136",
            "\1\u0137",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0139",
            "\1\u013a",
            "\1\u013b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u013d",
            "\1\u013e",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u0140",
            "\1\u0141",
            "",
            "\1\u0142",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u0147",
            "\1\u0148",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u014a",
            "",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u014c",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u014e",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u0152",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u0154",
            "\1\u0155",
            "\1\u0156",
            "",
            "\1\u0157",
            "\1\u0158",
            "",
            "\1\u0159",
            "\1\u015a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u015c",
            "\1\u015d",
            "\1\u015e",
            "",
            "\1\u015f",
            "\1\u0160",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "",
            "",
            "\1\u0164",
            "",
            "\1\u0165",
            "\1\u0166",
            "\1\u0167",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0169",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\u016c",
            "\1\u016d",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u016f",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0172",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u0174",
            "",
            "\1\u0175",
            "",
            "",
            "\1\u0176",
            "\1\u0177",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u017b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\u017d",
            "",
            "",
            "",
            "\1\u017e",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56\u0410\uffff\12\56",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | T__85 | T__86 | T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | T__93 | T__94 | T__95 | T__96 | T__97 | T__98 | T__99 | T__100 | T__101 | T__102 | T__103 | T__104 | T__105 | T__106 | T__107 | T__108 | T__109 | T__110 | T__111 | T__112 | T__113 | T__114 | T__115 | T__116 | T__117 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | ENUM | Identifier | WS | COMMENT | LINE_COMMENT );";
        }
    }
 

}