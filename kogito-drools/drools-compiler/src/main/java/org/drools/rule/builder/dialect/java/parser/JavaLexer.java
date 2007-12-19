// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g 2007-12-19 16:46:02

	package org.drools.rule.builder.dialect.java.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaLexer extends Lexer {
    public static final int T29=29;
    public static final int HexDigit=12;
    public static final int T70=70;
    public static final int T74=74;
    public static final int T85=85;
    public static final int T102=102;
    public static final int Letter=19;
    public static final int T114=114;
    public static final int T103=103;
    public static final int T32=32;
    public static final int T81=81;
    public static final int T41=41;
    public static final int T24=24;
    public static final int FloatTypeSuffix=15;
    public static final int T113=113;
    public static final int T62=62;
    public static final int T109=109;
    public static final int IntegerTypeSuffix=13;
    public static final int T68=68;
    public static final int T73=73;
    public static final int T84=84;
    public static final int T33=33;
    public static final int Identifier=4;
    public static final int T78=78;
    public static final int WS=21;
    public static final int T42=42;
    public static final int T96=96;
    public static final int T71=71;
    public static final int LINE_COMMENT=23;
    public static final int T72=72;
    public static final int T94=94;
    public static final int T76=76;
    public static final int UnicodeEscape=17;
    public static final int HexLiteral=9;
    public static final int T75=75;
    public static final int T89=89;
    public static final int DecimalLiteral=11;
    public static final int T67=67;
    public static final int T31=31;
    public static final int T60=60;
    public static final int T82=82;
    public static final int T100=100;
    public static final int T49=49;
    public static final int T30=30;
    public static final int T79=79;
    public static final int OctalLiteral=10;
    public static final int T36=36;
    public static final int T58=58;
    public static final int T93=93;
    public static final int T35=35;
    public static final int T107=107;
    public static final int T83=83;
    public static final int T61=61;
    public static final int T45=45;
    public static final int T34=34;
    public static final int T101=101;
    public static final int T64=64;
    public static final int T25=25;
    public static final int T91=91;
    public static final int T105=105;
    public static final int T37=37;
    public static final int T86=86;
    public static final int EscapeSequence=16;
    public static final int T26=26;
    public static final int T51=51;
    public static final int T111=111;
    public static final int T46=46;
    public static final int T77=77;
    public static final int T38=38;
    public static final int FloatingPointLiteral=6;
    public static final int T106=106;
    public static final int T112=112;
    public static final int T69=69;
    public static final int T39=39;
    public static final int ENUM=5;
    public static final int T44=44;
    public static final int T55=55;
    public static final int Exponent=14;
    public static final int T95=95;
    public static final int T50=50;
    public static final int T110=110;
    public static final int T108=108;
    public static final int CharacterLiteral=7;
    public static final int T92=92;
    public static final int T43=43;
    public static final int T28=28;
    public static final int T40=40;
    public static final int T66=66;
    public static final int COMMENT=22;
    public static final int T88=88;
    public static final int StringLiteral=8;
    public static final int T63=63;
    public static final int T57=57;
    public static final int T65=65;
    public static final int T98=98;
    public static final int T56=56;
    public static final int T87=87;
    public static final int T80=80;
    public static final int JavaIDDigit=20;
    public static final int T59=59;
    public static final int T97=97;
    public static final int T48=48;
    public static final int T54=54;
    public static final int EOF=-1;
    public static final int T104=104;
    public static final int T47=47;
    public static final int Tokens=115;
    public static final int T53=53;
    public static final int OctalEscape=18;
    public static final int T99=99;
    public static final int T27=27;
    public static final int T52=52;
    public static final int T90=90;

    	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    	protected boolean enumIsKeyword = true;

    public JavaLexer() {;} 
    public JavaLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g"; }

    // $ANTLR start T24
    public final void mT24() throws RecognitionException {
        try {
            int _type = T24;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:10:5: ( 'package' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:10:7: 'package'
            {
            match("package"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T24

    // $ANTLR start T25
    public final void mT25() throws RecognitionException {
        try {
            int _type = T25;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:11:5: ( ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:11:7: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T25

    // $ANTLR start T26
    public final void mT26() throws RecognitionException {
        try {
            int _type = T26;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:12:5: ( 'import' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:12:7: 'import'
            {
            match("import"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T26

    // $ANTLR start T27
    public final void mT27() throws RecognitionException {
        try {
            int _type = T27;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:13:5: ( 'static' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:13:7: 'static'
            {
            match("static"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T27

    // $ANTLR start T28
    public final void mT28() throws RecognitionException {
        try {
            int _type = T28;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:14:5: ( '.' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:14:7: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T28

    // $ANTLR start T29
    public final void mT29() throws RecognitionException {
        try {
            int _type = T29;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:15:5: ( '*' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:15:7: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T29

    // $ANTLR start T30
    public final void mT30() throws RecognitionException {
        try {
            int _type = T30;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:16:5: ( 'class' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:16:7: 'class'
            {
            match("class"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T30

    // $ANTLR start T31
    public final void mT31() throws RecognitionException {
        try {
            int _type = T31;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:17:5: ( 'extends' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:17:7: 'extends'
            {
            match("extends"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T31

    // $ANTLR start T32
    public final void mT32() throws RecognitionException {
        try {
            int _type = T32;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:18:5: ( 'implements' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:18:7: 'implements'
            {
            match("implements"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T32

    // $ANTLR start T33
    public final void mT33() throws RecognitionException {
        try {
            int _type = T33;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:19:5: ( '<' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:19:7: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T33

    // $ANTLR start T34
    public final void mT34() throws RecognitionException {
        try {
            int _type = T34;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:20:5: ( ',' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:20:7: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T34

    // $ANTLR start T35
    public final void mT35() throws RecognitionException {
        try {
            int _type = T35;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:21:5: ( '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:21:7: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T35

    // $ANTLR start T36
    public final void mT36() throws RecognitionException {
        try {
            int _type = T36;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:22:5: ( '&' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:22:7: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T36

    // $ANTLR start T37
    public final void mT37() throws RecognitionException {
        try {
            int _type = T37;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:23:5: ( '{' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:23:7: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T37

    // $ANTLR start T38
    public final void mT38() throws RecognitionException {
        try {
            int _type = T38;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:24:5: ( '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:24:7: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T38

    // $ANTLR start T39
    public final void mT39() throws RecognitionException {
        try {
            int _type = T39;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:25:5: ( 'interface' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:25:7: 'interface'
            {
            match("interface"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T39

    // $ANTLR start T40
    public final void mT40() throws RecognitionException {
        try {
            int _type = T40;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:26:5: ( 'void' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:26:7: 'void'
            {
            match("void"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T40

    // $ANTLR start T41
    public final void mT41() throws RecognitionException {
        try {
            int _type = T41;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:27:5: ( '[' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:27:7: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T41

    // $ANTLR start T42
    public final void mT42() throws RecognitionException {
        try {
            int _type = T42;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:28:5: ( ']' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:28:7: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T42

    // $ANTLR start T43
    public final void mT43() throws RecognitionException {
        try {
            int _type = T43;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:29:5: ( 'throws' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:29:7: 'throws'
            {
            match("throws"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T43

    // $ANTLR start T44
    public final void mT44() throws RecognitionException {
        try {
            int _type = T44;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:30:5: ( '=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:30:7: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T44

    // $ANTLR start T45
    public final void mT45() throws RecognitionException {
        try {
            int _type = T45;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:31:5: ( 'public' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:31:7: 'public'
            {
            match("public"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T45

    // $ANTLR start T46
    public final void mT46() throws RecognitionException {
        try {
            int _type = T46;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:32:5: ( 'protected' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:32:7: 'protected'
            {
            match("protected"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T46

    // $ANTLR start T47
    public final void mT47() throws RecognitionException {
        try {
            int _type = T47;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:33:5: ( 'private' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:33:7: 'private'
            {
            match("private"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T47

    // $ANTLR start T48
    public final void mT48() throws RecognitionException {
        try {
            int _type = T48;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:34:5: ( 'abstract' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:34:7: 'abstract'
            {
            match("abstract"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T48

    // $ANTLR start T49
    public final void mT49() throws RecognitionException {
        try {
            int _type = T49;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:35:5: ( 'final' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:35:7: 'final'
            {
            match("final"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T49

    // $ANTLR start T50
    public final void mT50() throws RecognitionException {
        try {
            int _type = T50;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:36:5: ( 'native' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:36:7: 'native'
            {
            match("native"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T50

    // $ANTLR start T51
    public final void mT51() throws RecognitionException {
        try {
            int _type = T51;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:37:5: ( 'synchronized' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:37:7: 'synchronized'
            {
            match("synchronized"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T51

    // $ANTLR start T52
    public final void mT52() throws RecognitionException {
        try {
            int _type = T52;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:38:5: ( 'transient' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:38:7: 'transient'
            {
            match("transient"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T52

    // $ANTLR start T53
    public final void mT53() throws RecognitionException {
        try {
            int _type = T53;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:39:5: ( 'volatile' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:39:7: 'volatile'
            {
            match("volatile"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T53

    // $ANTLR start T54
    public final void mT54() throws RecognitionException {
        try {
            int _type = T54;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:40:5: ( 'strictfp' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:40:7: 'strictfp'
            {
            match("strictfp"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T54

    // $ANTLR start T55
    public final void mT55() throws RecognitionException {
        try {
            int _type = T55;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:41:5: ( 'boolean' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:41:7: 'boolean'
            {
            match("boolean"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T55

    // $ANTLR start T56
    public final void mT56() throws RecognitionException {
        try {
            int _type = T56;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:42:5: ( 'char' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:42:7: 'char'
            {
            match("char"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T56

    // $ANTLR start T57
    public final void mT57() throws RecognitionException {
        try {
            int _type = T57;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:43:5: ( 'byte' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:43:7: 'byte'
            {
            match("byte"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T57

    // $ANTLR start T58
    public final void mT58() throws RecognitionException {
        try {
            int _type = T58;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:44:5: ( 'short' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:44:7: 'short'
            {
            match("short"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T58

    // $ANTLR start T59
    public final void mT59() throws RecognitionException {
        try {
            int _type = T59;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:45:5: ( 'int' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:45:7: 'int'
            {
            match("int"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T59

    // $ANTLR start T60
    public final void mT60() throws RecognitionException {
        try {
            int _type = T60;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:46:5: ( 'long' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:46:7: 'long'
            {
            match("long"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T60

    // $ANTLR start T61
    public final void mT61() throws RecognitionException {
        try {
            int _type = T61;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:47:5: ( 'float' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:47:7: 'float'
            {
            match("float"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T61

    // $ANTLR start T62
    public final void mT62() throws RecognitionException {
        try {
            int _type = T62;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:48:5: ( 'double' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:48:7: 'double'
            {
            match("double"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T62

    // $ANTLR start T63
    public final void mT63() throws RecognitionException {
        try {
            int _type = T63;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:49:5: ( '?' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:49:7: '?'
            {
            match('?'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T63

    // $ANTLR start T64
    public final void mT64() throws RecognitionException {
        try {
            int _type = T64;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:50:5: ( 'super' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:50:7: 'super'
            {
            match("super"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T64

    // $ANTLR start T65
    public final void mT65() throws RecognitionException {
        try {
            int _type = T65;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:51:5: ( '(' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:51:7: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T65

    // $ANTLR start T66
    public final void mT66() throws RecognitionException {
        try {
            int _type = T66;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:52:5: ( ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:52:7: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T66

    // $ANTLR start T67
    public final void mT67() throws RecognitionException {
        try {
            int _type = T67;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:53:5: ( '...' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:53:7: '...'
            {
            match("..."); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T67

    // $ANTLR start T68
    public final void mT68() throws RecognitionException {
        try {
            int _type = T68;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:54:5: ( 'null' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:54:7: 'null'
            {
            match("null"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T68

    // $ANTLR start T69
    public final void mT69() throws RecognitionException {
        try {
            int _type = T69;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:55:5: ( 'true' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:55:7: 'true'
            {
            match("true"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T69

    // $ANTLR start T70
    public final void mT70() throws RecognitionException {
        try {
            int _type = T70;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:56:5: ( 'false' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:56:7: 'false'
            {
            match("false"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T70

    // $ANTLR start T71
    public final void mT71() throws RecognitionException {
        try {
            int _type = T71;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:57:5: ( '@' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:57:7: '@'
            {
            match('@'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T71

    // $ANTLR start T72
    public final void mT72() throws RecognitionException {
        try {
            int _type = T72;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:58:5: ( 'default' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:58:7: 'default'
            {
            match("default"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T72

    // $ANTLR start T73
    public final void mT73() throws RecognitionException {
        try {
            int _type = T73;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:59:5: ( 'assert' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:59:7: 'assert'
            {
            match("assert"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T73

    // $ANTLR start T74
    public final void mT74() throws RecognitionException {
        try {
            int _type = T74;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:60:5: ( ':' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:60:7: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T74

    // $ANTLR start T75
    public final void mT75() throws RecognitionException {
        try {
            int _type = T75;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:61:5: ( 'if' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:61:7: 'if'
            {
            match("if"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T75

    // $ANTLR start T76
    public final void mT76() throws RecognitionException {
        try {
            int _type = T76;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:62:5: ( 'else' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:62:7: 'else'
            {
            match("else"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T76

    // $ANTLR start T77
    public final void mT77() throws RecognitionException {
        try {
            int _type = T77;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:63:5: ( 'for' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:63:7: 'for'
            {
            match("for"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T77

    // $ANTLR start T78
    public final void mT78() throws RecognitionException {
        try {
            int _type = T78;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:64:5: ( 'while' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:64:7: 'while'
            {
            match("while"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T78

    // $ANTLR start T79
    public final void mT79() throws RecognitionException {
        try {
            int _type = T79;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:65:5: ( 'do' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:65:7: 'do'
            {
            match("do"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T79

    // $ANTLR start T80
    public final void mT80() throws RecognitionException {
        try {
            int _type = T80;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:66:5: ( 'try' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:66:7: 'try'
            {
            match("try"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T80

    // $ANTLR start T81
    public final void mT81() throws RecognitionException {
        try {
            int _type = T81;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:67:5: ( 'finally' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:67:7: 'finally'
            {
            match("finally"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T81

    // $ANTLR start T82
    public final void mT82() throws RecognitionException {
        try {
            int _type = T82;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:68:5: ( 'switch' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:68:7: 'switch'
            {
            match("switch"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T82

    // $ANTLR start T83
    public final void mT83() throws RecognitionException {
        try {
            int _type = T83;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:69:5: ( 'return' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:69:7: 'return'
            {
            match("return"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T83

    // $ANTLR start T84
    public final void mT84() throws RecognitionException {
        try {
            int _type = T84;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:70:5: ( 'throw' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:70:7: 'throw'
            {
            match("throw"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T84

    // $ANTLR start T85
    public final void mT85() throws RecognitionException {
        try {
            int _type = T85;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:71:5: ( 'break' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:71:7: 'break'
            {
            match("break"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T85

    // $ANTLR start T86
    public final void mT86() throws RecognitionException {
        try {
            int _type = T86;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:72:5: ( 'continue' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:72:7: 'continue'
            {
            match("continue"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T86

    // $ANTLR start T87
    public final void mT87() throws RecognitionException {
        try {
            int _type = T87;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:73:5: ( 'modify' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:73:7: 'modify'
            {
            match("modify"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T87

    // $ANTLR start T88
    public final void mT88() throws RecognitionException {
        try {
            int _type = T88;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:74:5: ( 'catch' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:74:7: 'catch'
            {
            match("catch"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T88

    // $ANTLR start T89
    public final void mT89() throws RecognitionException {
        try {
            int _type = T89;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:75:5: ( 'case' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:75:7: 'case'
            {
            match("case"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T89

    // $ANTLR start T90
    public final void mT90() throws RecognitionException {
        try {
            int _type = T90;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:76:5: ( '+=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:76:7: '+='
            {
            match("+="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T90

    // $ANTLR start T91
    public final void mT91() throws RecognitionException {
        try {
            int _type = T91;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:77:5: ( '-=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:77:7: '-='
            {
            match("-="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T91

    // $ANTLR start T92
    public final void mT92() throws RecognitionException {
        try {
            int _type = T92;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:78:5: ( '*=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:78:7: '*='
            {
            match("*="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T92

    // $ANTLR start T93
    public final void mT93() throws RecognitionException {
        try {
            int _type = T93;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:79:5: ( '/=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:79:7: '/='
            {
            match("/="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T93

    // $ANTLR start T94
    public final void mT94() throws RecognitionException {
        try {
            int _type = T94;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:80:5: ( '&=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:80:7: '&='
            {
            match("&="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T94

    // $ANTLR start T95
    public final void mT95() throws RecognitionException {
        try {
            int _type = T95;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:81:5: ( '|=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:81:7: '|='
            {
            match("|="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T95

    // $ANTLR start T96
    public final void mT96() throws RecognitionException {
        try {
            int _type = T96;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:82:5: ( '^=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:82:7: '^='
            {
            match("^="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T96

    // $ANTLR start T97
    public final void mT97() throws RecognitionException {
        try {
            int _type = T97;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:83:5: ( '%=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:83:7: '%='
            {
            match("%="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T97

    // $ANTLR start T98
    public final void mT98() throws RecognitionException {
        try {
            int _type = T98;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:84:5: ( '||' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:84:7: '||'
            {
            match("||"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T98

    // $ANTLR start T99
    public final void mT99() throws RecognitionException {
        try {
            int _type = T99;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:85:5: ( '&&' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:85:7: '&&'
            {
            match("&&"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T99

    // $ANTLR start T100
    public final void mT100() throws RecognitionException {
        try {
            int _type = T100;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:86:6: ( '|' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:86:8: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T100

    // $ANTLR start T101
    public final void mT101() throws RecognitionException {
        try {
            int _type = T101;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:87:6: ( '^' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:87:8: '^'
            {
            match('^'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T101

    // $ANTLR start T102
    public final void mT102() throws RecognitionException {
        try {
            int _type = T102;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:88:6: ( '==' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:88:8: '=='
            {
            match("=="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T102

    // $ANTLR start T103
    public final void mT103() throws RecognitionException {
        try {
            int _type = T103;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:89:6: ( '!=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:89:8: '!='
            {
            match("!="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T103

    // $ANTLR start T104
    public final void mT104() throws RecognitionException {
        try {
            int _type = T104;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:90:6: ( 'instanceof' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:90:8: 'instanceof'
            {
            match("instanceof"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T104

    // $ANTLR start T105
    public final void mT105() throws RecognitionException {
        try {
            int _type = T105;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:91:6: ( '+' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:91:8: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T105

    // $ANTLR start T106
    public final void mT106() throws RecognitionException {
        try {
            int _type = T106;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:92:6: ( '-' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:92:8: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T106

    // $ANTLR start T107
    public final void mT107() throws RecognitionException {
        try {
            int _type = T107;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:93:6: ( '/' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:93:8: '/'
            {
            match('/'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T107

    // $ANTLR start T108
    public final void mT108() throws RecognitionException {
        try {
            int _type = T108;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:94:6: ( '%' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:94:8: '%'
            {
            match('%'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T108

    // $ANTLR start T109
    public final void mT109() throws RecognitionException {
        try {
            int _type = T109;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:95:6: ( '++' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:95:8: '++'
            {
            match("++"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T109

    // $ANTLR start T110
    public final void mT110() throws RecognitionException {
        try {
            int _type = T110;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:96:6: ( '--' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:96:8: '--'
            {
            match("--"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T110

    // $ANTLR start T111
    public final void mT111() throws RecognitionException {
        try {
            int _type = T111;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:97:6: ( '~' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:97:8: '~'
            {
            match('~'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T111

    // $ANTLR start T112
    public final void mT112() throws RecognitionException {
        try {
            int _type = T112;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:98:6: ( '!' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:98:8: '!'
            {
            match('!'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T112

    // $ANTLR start T113
    public final void mT113() throws RecognitionException {
        try {
            int _type = T113;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:99:6: ( 'this' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:99:8: 'this'
            {
            match("this"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T113

    // $ANTLR start T114
    public final void mT114() throws RecognitionException {
        try {
            int _type = T114;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:100:6: ( 'new' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:100:8: 'new'
            {
            match("new"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T114

    // $ANTLR start HexLiteral
    public final void mHexLiteral() throws RecognitionException {
        try {
            int _type = HexLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:12: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:14: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:28: ( HexDigit )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:28: HexDigit
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:38: ( IntegerTypeSuffix )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='L'||LA2_0=='l') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:960:38: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HexLiteral

    // $ANTLR start DecimalLiteral
    public final void mDecimalLiteral() throws RecognitionException {
        try {
            int _type = DecimalLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:16: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:18: ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:18: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    new NoViableAltException("962:18: ( '0' | '1' .. '9' ( '0' .. '9' )* )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:19: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:25: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:34: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:34: '0' .. '9'
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:45: ( IntegerTypeSuffix )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='L'||LA5_0=='l') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:962:45: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DecimalLiteral

    // $ANTLR start OctalLiteral
    public final void mOctalLiteral() throws RecognitionException {
        try {
            int _type = OctalLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:14: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:16: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:20: ( '0' .. '7' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:21: '0' .. '7'
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:32: ( IntegerTypeSuffix )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='L'||LA7_0=='l') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:964:32: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OctalLiteral

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:967:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:967:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start IntegerTypeSuffix
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:970:19: ( ( 'l' | 'L' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:970:21: ( 'l' | 'L' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end IntegerTypeSuffix

    // $ANTLR start FloatingPointLiteral
    public final void mFloatingPointLiteral() throws RecognitionException {
        try {
            int _type = FloatingPointLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix )
            int alt19=4;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:9: ( '0' .. '9' )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:10: '0' .. '9'
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:37: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:37: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:47: ( FloatTypeSuffix )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='D'||LA11_0=='F'||LA11_0=='d'||LA11_0=='f') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:973:47: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:9: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); 
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:13: ( '0' .. '9' )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:14: '0' .. '9'
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

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:25: ( Exponent )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='E'||LA13_0=='e') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:25: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:35: ( FloatTypeSuffix )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0=='D'||LA14_0=='F'||LA14_0=='d'||LA14_0=='f') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:974:35: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:975:9: ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:975:9: ( '0' .. '9' )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:975:10: '0' .. '9'
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:975:30: ( FloatTypeSuffix )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='D'||LA16_0=='F'||LA16_0=='d'||LA16_0=='f') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:975:30: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:976:9: ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:976:9: ( '0' .. '9' )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:976:10: '0' .. '9'
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

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:976:21: ( Exponent )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='E'||LA18_0=='e') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:976:21: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    mFloatTypeSuffix(); 

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FloatingPointLiteral

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:980:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:980:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:980:22: ( '+' | '-' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='+'||LA20_0=='-') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:980:33: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:980:34: '0' .. '9'
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
    // $ANTLR end Exponent

    // $ANTLR start FloatTypeSuffix
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:983:17: ( ( 'f' | 'F' | 'd' | 'D' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:983:19: ( 'f' | 'F' | 'd' | 'D' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end FloatTypeSuffix

    // $ANTLR start CharacterLiteral
    public final void mCharacterLiteral() throws RecognitionException {
        try {
            int _type = CharacterLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:986:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:986:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:986:14: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='\\') ) {
                alt22=1;
            }
            else if ( ((LA22_0>='\u0000' && LA22_0<='&')||(LA22_0>='(' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFE')) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("986:14: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:986:16: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:986:33: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            match('\''); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CharacterLiteral

    // $ANTLR start StringLiteral
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:990:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:990:8: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:990:12: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\\') ) {
                    alt23=1;
                }
                else if ( ((LA23_0>='\u0000' && LA23_0<='!')||(LA23_0>='#' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='\uFFFE')) ) {
                    alt23=2;
                }


                switch (alt23) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:990:14: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:990:31: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end StringLiteral

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:995:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'u':
                    {
                    alt24=2;
                    }
                    break;
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
                        new NoViableAltException("993:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 24, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("993:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:995:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:996:9: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:997:9: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public final void mOctalEscape() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt25=3;
            int LA25_0 = input.LA(1);

            if ( (LA25_0=='\\') ) {
                int LA25_1 = input.LA(2);

                if ( ((LA25_1>='0' && LA25_1<='3')) ) {
                    int LA25_2 = input.LA(3);

                    if ( ((LA25_2>='0' && LA25_2<='7')) ) {
                        int LA25_5 = input.LA(4);

                        if ( ((LA25_5>='0' && LA25_5<='7')) ) {
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
                        new NoViableAltException("1000:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1000:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:14: ( '0' .. '3' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:36: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1002:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1003:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1003:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1003:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1003:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1003:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1004:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1004:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1004:15: '0' .. '7'
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
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1009:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1009:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
    // $ANTLR end UnicodeEscape

    // $ANTLR start ENUM
    public final void mENUM() throws RecognitionException {
        try {
            int _type = ENUM;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1012:5: ( 'enum' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1012:7: 'enum'
            {
            match("enum"); 

            if ( !enumIsKeyword ) _type=Identifier;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ENUM

    // $ANTLR start Identifier
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1016:5: ( Letter ( Letter | JavaIDDigit )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1016:9: Letter ( Letter | JavaIDDigit )*
            {
            mLetter(); 
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1016:16: ( Letter | JavaIDDigit )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='$'||(LA26_0>='0' && LA26_0<='9')||(LA26_0>='A' && LA26_0<='Z')||LA26_0=='_'||(LA26_0>='a' && LA26_0<='z')||(LA26_0>='\u00C0' && LA26_0<='\u00D6')||(LA26_0>='\u00D8' && LA26_0<='\u00F6')||(LA26_0>='\u00F8' && LA26_0<='\u1FFF')||(LA26_0>='\u3040' && LA26_0<='\u318F')||(LA26_0>='\u3300' && LA26_0<='\u337F')||(LA26_0>='\u3400' && LA26_0<='\u3D2D')||(LA26_0>='\u4E00' && LA26_0<='\u9FFF')||(LA26_0>='\uF900' && LA26_0<='\uFAFF')) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end Identifier

    // $ANTLR start Letter
    public final void mLetter() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1024:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end Letter

    // $ANTLR start JavaIDDigit
    public final void mJavaIDDigit() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1041:5: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u06F0' && input.LA(1)<='\u06F9')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09EF')||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end JavaIDDigit

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1058:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1058:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1062:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1062:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1062:14: ( options {greedy=false; } : . )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='*') ) {
                    int LA27_1 = input.LA(2);

                    if ( (LA27_1=='/') ) {
                        alt27=2;
                    }
                    else if ( ((LA27_1>='\u0000' && LA27_1<='.')||(LA27_1>='0' && LA27_1<='\uFFFE')) ) {
                        alt27=1;
                    }


                }
                else if ( ((LA27_0>='\u0000' && LA27_0<=')')||(LA27_0>='+' && LA27_0<='\uFFFE')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1062:42: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            match("*/"); 

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:12: (~ ( '\\n' | '\\r' ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>='\u0000' && LA28_0<='\t')||(LA28_0>='\u000B' && LA28_0<='\f')||(LA28_0>='\u000E' && LA28_0<='\uFFFE')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:26: ( '\\r' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='\r') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1066:26: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    public void mTokens() throws RecognitionException {
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:8: ( T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | ENUM | Identifier | WS | COMMENT | LINE_COMMENT )
        int alt30=102;
        alt30 = dfa30.predict(input);
        switch (alt30) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:10: T24
                {
                mT24(); 

                }
                break;
            case 2 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:14: T25
                {
                mT25(); 

                }
                break;
            case 3 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:18: T26
                {
                mT26(); 

                }
                break;
            case 4 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:22: T27
                {
                mT27(); 

                }
                break;
            case 5 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:26: T28
                {
                mT28(); 

                }
                break;
            case 6 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:30: T29
                {
                mT29(); 

                }
                break;
            case 7 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:34: T30
                {
                mT30(); 

                }
                break;
            case 8 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:38: T31
                {
                mT31(); 

                }
                break;
            case 9 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:42: T32
                {
                mT32(); 

                }
                break;
            case 10 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:46: T33
                {
                mT33(); 

                }
                break;
            case 11 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:50: T34
                {
                mT34(); 

                }
                break;
            case 12 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:54: T35
                {
                mT35(); 

                }
                break;
            case 13 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:58: T36
                {
                mT36(); 

                }
                break;
            case 14 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:62: T37
                {
                mT37(); 

                }
                break;
            case 15 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:66: T38
                {
                mT38(); 

                }
                break;
            case 16 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:70: T39
                {
                mT39(); 

                }
                break;
            case 17 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:74: T40
                {
                mT40(); 

                }
                break;
            case 18 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:78: T41
                {
                mT41(); 

                }
                break;
            case 19 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:82: T42
                {
                mT42(); 

                }
                break;
            case 20 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:86: T43
                {
                mT43(); 

                }
                break;
            case 21 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:90: T44
                {
                mT44(); 

                }
                break;
            case 22 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:94: T45
                {
                mT45(); 

                }
                break;
            case 23 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:98: T46
                {
                mT46(); 

                }
                break;
            case 24 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:102: T47
                {
                mT47(); 

                }
                break;
            case 25 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:106: T48
                {
                mT48(); 

                }
                break;
            case 26 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:110: T49
                {
                mT49(); 

                }
                break;
            case 27 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:114: T50
                {
                mT50(); 

                }
                break;
            case 28 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:118: T51
                {
                mT51(); 

                }
                break;
            case 29 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:122: T52
                {
                mT52(); 

                }
                break;
            case 30 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:126: T53
                {
                mT53(); 

                }
                break;
            case 31 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:130: T54
                {
                mT54(); 

                }
                break;
            case 32 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:134: T55
                {
                mT55(); 

                }
                break;
            case 33 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:138: T56
                {
                mT56(); 

                }
                break;
            case 34 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:142: T57
                {
                mT57(); 

                }
                break;
            case 35 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:146: T58
                {
                mT58(); 

                }
                break;
            case 36 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:150: T59
                {
                mT59(); 

                }
                break;
            case 37 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:154: T60
                {
                mT60(); 

                }
                break;
            case 38 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:158: T61
                {
                mT61(); 

                }
                break;
            case 39 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:162: T62
                {
                mT62(); 

                }
                break;
            case 40 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:166: T63
                {
                mT63(); 

                }
                break;
            case 41 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:170: T64
                {
                mT64(); 

                }
                break;
            case 42 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:174: T65
                {
                mT65(); 

                }
                break;
            case 43 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:178: T66
                {
                mT66(); 

                }
                break;
            case 44 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:182: T67
                {
                mT67(); 

                }
                break;
            case 45 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:186: T68
                {
                mT68(); 

                }
                break;
            case 46 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:190: T69
                {
                mT69(); 

                }
                break;
            case 47 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:194: T70
                {
                mT70(); 

                }
                break;
            case 48 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:198: T71
                {
                mT71(); 

                }
                break;
            case 49 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:202: T72
                {
                mT72(); 

                }
                break;
            case 50 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:206: T73
                {
                mT73(); 

                }
                break;
            case 51 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:210: T74
                {
                mT74(); 

                }
                break;
            case 52 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:214: T75
                {
                mT75(); 

                }
                break;
            case 53 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:218: T76
                {
                mT76(); 

                }
                break;
            case 54 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:222: T77
                {
                mT77(); 

                }
                break;
            case 55 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:226: T78
                {
                mT78(); 

                }
                break;
            case 56 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:230: T79
                {
                mT79(); 

                }
                break;
            case 57 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:234: T80
                {
                mT80(); 

                }
                break;
            case 58 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:238: T81
                {
                mT81(); 

                }
                break;
            case 59 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:242: T82
                {
                mT82(); 

                }
                break;
            case 60 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:246: T83
                {
                mT83(); 

                }
                break;
            case 61 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:250: T84
                {
                mT84(); 

                }
                break;
            case 62 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:254: T85
                {
                mT85(); 

                }
                break;
            case 63 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:258: T86
                {
                mT86(); 

                }
                break;
            case 64 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:262: T87
                {
                mT87(); 

                }
                break;
            case 65 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:266: T88
                {
                mT88(); 

                }
                break;
            case 66 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:270: T89
                {
                mT89(); 

                }
                break;
            case 67 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:274: T90
                {
                mT90(); 

                }
                break;
            case 68 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:278: T91
                {
                mT91(); 

                }
                break;
            case 69 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:282: T92
                {
                mT92(); 

                }
                break;
            case 70 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:286: T93
                {
                mT93(); 

                }
                break;
            case 71 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:290: T94
                {
                mT94(); 

                }
                break;
            case 72 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:294: T95
                {
                mT95(); 

                }
                break;
            case 73 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:298: T96
                {
                mT96(); 

                }
                break;
            case 74 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:302: T97
                {
                mT97(); 

                }
                break;
            case 75 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:306: T98
                {
                mT98(); 

                }
                break;
            case 76 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:310: T99
                {
                mT99(); 

                }
                break;
            case 77 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:314: T100
                {
                mT100(); 

                }
                break;
            case 78 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:319: T101
                {
                mT101(); 

                }
                break;
            case 79 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:324: T102
                {
                mT102(); 

                }
                break;
            case 80 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:329: T103
                {
                mT103(); 

                }
                break;
            case 81 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:334: T104
                {
                mT104(); 

                }
                break;
            case 82 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:339: T105
                {
                mT105(); 

                }
                break;
            case 83 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:344: T106
                {
                mT106(); 

                }
                break;
            case 84 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:349: T107
                {
                mT107(); 

                }
                break;
            case 85 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:354: T108
                {
                mT108(); 

                }
                break;
            case 86 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:359: T109
                {
                mT109(); 

                }
                break;
            case 87 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:364: T110
                {
                mT110(); 

                }
                break;
            case 88 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:369: T111
                {
                mT111(); 

                }
                break;
            case 89 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:374: T112
                {
                mT112(); 

                }
                break;
            case 90 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:379: T113
                {
                mT113(); 

                }
                break;
            case 91 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:384: T114
                {
                mT114(); 

                }
                break;
            case 92 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:389: HexLiteral
                {
                mHexLiteral(); 

                }
                break;
            case 93 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:400: DecimalLiteral
                {
                mDecimalLiteral(); 

                }
                break;
            case 94 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:415: OctalLiteral
                {
                mOctalLiteral(); 

                }
                break;
            case 95 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:428: FloatingPointLiteral
                {
                mFloatingPointLiteral(); 

                }
                break;
            case 96 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:449: CharacterLiteral
                {
                mCharacterLiteral(); 

                }
                break;
            case 97 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:466: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 98 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:480: ENUM
                {
                mENUM(); 

                }
                break;
            case 99 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:485: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 100 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:496: WS
                {
                mWS(); 

                }
                break;
            case 101 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:499: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 102 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:1:507: LINE_COMMENT
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
        "\2\56\1\uffff\1\53\2\uffff\2\60\2\uffff";
    static final String DFA19_maxS =
        "\1\71\1\146\1\uffff\1\71\2\uffff\1\71\1\146\2\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\1\uffff\1\4\1\1\2\uffff\2\3";
    static final String DFA19_specialS =
        "\12\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\5\1\uffff\12\1\12\uffff\1\4\1\3\1\4\35\uffff\1\4\1\3\1\4",
            "",
            "\1\6\1\uffff\1\6\2\uffff\12\7",
            "",
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
            return "972:1: FloatingPointLiteral : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ ( Exponent )? FloatTypeSuffix );";
        }
    }
    static final String DFA30_eotS =
        "\1\uffff\1\56\1\uffff\2\56\1\74\1\77\2\56\3\uffff\1\111\2\uffff"+
        "\1\56\2\uffff\1\56\1\116\6\56\5\uffff\3\56\1\143\1\146\1\152\1\155"+
        "\1\157\1\161\1\163\1\uffff\2\165\4\uffff\3\56\1\174\7\56\5\uffff"+
        "\7\56\3\uffff\3\56\2\uffff\16\56\1\u00a4\3\56\25\uffff\1\u00a8\1"+
        "\165\4\56\1\uffff\1\u00ae\23\56\1\u00c3\10\56\1\u00cc\1\56\1\u00ce"+
        "\7\56\1\uffff\3\56\1\uffff\5\56\1\uffff\11\56\1\u00e7\1\56\1\u00e9"+
        "\2\56\1\u00ec\1\u00ed\1\56\1\u00ef\1\56\1\u00f1\1\uffff\1\56\1\u00f3"+
        "\6\56\1\uffff\1\u00fa\1\uffff\3\56\1\u00fe\1\u00ff\20\56\1\u0110"+
        "\1\56\1\u0112\1\uffff\1\u0113\1\uffff\1\u0114\1\56\2\uffff\1\56"+
        "\1\uffff\1\56\1\uffff\1\56\1\uffff\1\u011a\2\56\1\u011d\1\u011f"+
        "\1\u0120\1\uffff\2\56\1\u0123\2\uffff\2\56\1\u0126\4\56\1\u012b"+
        "\3\56\1\u012f\2\56\1\u0132\1\u0133\1\uffff\1\56\3\uffff\4\56\1\u0139"+
        "\1\uffff\1\u013a\1\56\1\uffff\1\56\2\uffff\1\u013d\1\56\1\uffff"+
        "\1\56\1\u0140\1\uffff\1\u0141\1\u0142\1\u0143\1\56\1\uffff\1\u0145"+
        "\2\56\1\uffff\2\56\2\uffff\2\56\1\u014c\2\56\2\uffff\1\56\1\u0150"+
        "\1\uffff\1\u0151\1\u0152\4\uffff\1\56\1\uffff\3\56\1\u0157\1\56"+
        "\1\u0159\1\uffff\1\u015a\1\56\1\u015c\3\uffff\1\u015d\1\u015e\2"+
        "\56\1\uffff\1\56\2\uffff\1\u0162\3\uffff\1\u0163\1\u0164\1\56\3"+
        "\uffff\1\56\1\u0167\1\uffff";
    static final String DFA30_eofS =
        "\u0168\uffff";
    static final String DFA30_minS =
        "\1\11\1\141\1\uffff\1\146\1\150\1\56\1\75\1\141\1\154\3\uffff\1"+
        "\46\2\uffff\1\157\2\uffff\1\150\1\75\1\142\2\141\2\157\1\145\5\uffff"+
        "\1\150\1\145\1\157\1\53\1\55\1\52\4\75\1\uffff\2\56\4\uffff\1\151"+
        "\1\142\1\143\1\44\1\163\1\160\1\141\1\151\1\157\1\156\1\160\5\uffff"+
        "\1\141\1\163\1\141\1\156\1\163\1\165\1\164\3\uffff\1\151\1\141\1"+
        "\151\2\uffff\2\163\1\154\1\156\1\157\1\162\1\154\1\167\1\164\1\157"+
        "\1\145\1\164\1\156\1\146\1\44\1\151\1\164\1\144\25\uffff\2\56\1"+
        "\166\1\164\1\154\1\153\1\uffff\1\44\1\164\1\154\1\151\2\164\1\162"+
        "\1\143\1\145\1\162\1\143\1\145\1\163\1\164\1\145\1\155\1\145\1\144"+
        "\1\141\1\145\1\44\1\156\1\163\1\157\1\145\1\164\1\163\2\141\1\44"+
        "\1\154\1\44\1\151\1\154\1\141\1\145\1\147\1\141\1\142\1\uffff\1"+
        "\154\1\165\1\151\1\uffff\1\141\1\145\1\151\1\141\1\162\1\uffff\1"+
        "\141\1\162\1\145\1\143\1\151\1\143\1\164\1\150\1\162\1\44\1\150"+
        "\1\44\1\163\1\151\2\44\1\156\1\44\1\164\1\44\1\uffff\1\163\1\44"+
        "\1\167\2\162\1\145\1\154\1\164\1\uffff\1\44\1\uffff\1\166\1\145"+
        "\1\153\2\44\1\165\1\154\1\145\1\162\1\146\1\164\2\143\1\147\1\146"+
        "\1\156\1\164\1\155\1\164\1\143\1\150\1\44\1\162\1\44\1\uffff\1\44"+
        "\1\uffff\1\44\1\156\2\uffff\1\144\1\uffff\1\151\1\uffff\1\151\1"+
        "\uffff\1\44\1\164\1\141\3\44\1\uffff\1\145\1\141\1\44\2\uffff\1"+
        "\154\1\145\1\44\1\156\1\171\1\145\1\164\1\44\1\145\1\141\1\143\1"+
        "\44\1\145\1\146\2\44\1\uffff\1\157\3\uffff\1\165\1\163\1\154\1\145"+
        "\1\44\1\uffff\1\44\1\143\1\uffff\1\171\2\uffff\1\44\1\156\1\uffff"+
        "\1\164\1\44\1\uffff\3\44\1\145\1\uffff\1\44\1\143\1\145\1\uffff"+
        "\1\156\1\160\2\uffff\1\156\1\145\1\44\1\145\1\156\2\uffff\1\164"+
        "\1\44\1\uffff\2\44\4\uffff\1\144\1\uffff\1\145\1\157\1\164\1\44"+
        "\1\151\1\44\1\uffff\1\44\1\164\1\44\3\uffff\2\44\1\146\1\163\1\uffff"+
        "\1\172\2\uffff\1\44\3\uffff\2\44\1\145\3\uffff\1\144\1\44\1\uffff";
    static final String DFA30_maxS =
        "\1\ufaff\1\165\1\uffff\1\156\1\171\1\71\1\75\1\157\1\170\3\uffff"+
        "\1\75\2\uffff\1\157\2\uffff\1\162\1\75\1\163\1\157\1\165\1\171\2"+
        "\157\5\uffff\1\150\1\145\1\157\3\75\1\174\3\75\1\uffff\1\170\1\146"+
        "\4\uffff\1\157\1\142\1\143\1\ufaff\1\164\1\160\1\162\1\151\1\157"+
        "\1\156\1\160\5\uffff\1\141\1\164\1\141\1\156\1\163\1\165\1\164\3"+
        "\uffff\1\154\1\171\1\162\2\uffff\2\163\1\154\1\156\1\157\1\162\1"+
        "\154\1\167\1\164\1\157\1\145\1\164\1\156\1\146\1\ufaff\1\151\1\164"+
        "\1\144\25\uffff\2\146\1\166\1\164\1\154\1\153\1\uffff\1\ufaff\1"+
        "\164\1\157\1\151\2\164\1\162\1\143\1\145\1\162\1\143\1\145\1\163"+
        "\1\164\1\145\1\155\1\145\1\144\1\141\1\145\1\ufaff\1\156\1\163\1"+
        "\157\1\145\1\164\1\163\2\141\1\ufaff\1\154\1\ufaff\1\151\1\154\1"+
        "\141\1\145\1\147\1\141\1\142\1\uffff\1\154\1\165\1\151\1\uffff\1"+
        "\141\1\145\1\151\1\141\1\162\1\uffff\1\141\1\162\1\145\1\143\1\151"+
        "\1\143\1\164\1\150\1\162\1\ufaff\1\150\1\ufaff\1\163\1\151\2\ufaff"+
        "\1\156\1\ufaff\1\164\1\ufaff\1\uffff\1\163\1\ufaff\1\167\2\162\1"+
        "\145\1\154\1\164\1\uffff\1\ufaff\1\uffff\1\166\1\145\1\153\2\ufaff"+
        "\1\165\1\154\1\145\1\162\1\146\1\164\2\143\1\147\1\146\1\156\1\164"+
        "\1\155\1\164\1\143\1\150\1\ufaff\1\162\1\ufaff\1\uffff\1\ufaff\1"+
        "\uffff\1\ufaff\1\156\2\uffff\1\144\1\uffff\1\151\1\uffff\1\151\1"+
        "\uffff\1\ufaff\1\164\1\141\3\ufaff\1\uffff\1\145\1\141\1\ufaff\2"+
        "\uffff\1\154\1\145\1\ufaff\1\156\1\171\1\145\1\164\1\ufaff\1\145"+
        "\1\141\1\143\1\ufaff\1\145\1\146\2\ufaff\1\uffff\1\157\3\uffff\1"+
        "\165\1\163\1\154\1\145\1\ufaff\1\uffff\1\ufaff\1\143\1\uffff\1\171"+
        "\2\uffff\1\ufaff\1\156\1\uffff\1\164\1\ufaff\1\uffff\3\ufaff\1\145"+
        "\1\uffff\1\ufaff\1\143\1\145\1\uffff\1\156\1\160\2\uffff\1\156\1"+
        "\145\1\ufaff\1\145\1\156\2\uffff\1\164\1\ufaff\1\uffff\2\ufaff\4"+
        "\uffff\1\144\1\uffff\1\145\1\157\1\164\1\ufaff\1\151\1\ufaff\1\uffff"+
        "\1\ufaff\1\164\1\ufaff\3\uffff\2\ufaff\1\146\1\163\1\uffff\1\172"+
        "\2\uffff\1\ufaff\3\uffff\2\ufaff\1\145\3\uffff\1\144\1\ufaff\1\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\6\uffff\1\12\1\13\1\14\1\uffff\1\16\1\17\1\uffff\1"+
        "\22\1\23\10\uffff\1\50\1\52\1\53\1\60\1\63\12\uffff\1\130\2\uffff"+
        "\1\140\1\141\1\143\1\144\13\uffff\1\54\1\5\1\137\1\105\1\6\7\uffff"+
        "\1\114\1\107\1\15\3\uffff\1\117\1\25\22\uffff\1\103\1\126\1\122"+
        "\1\104\1\127\1\123\1\106\1\146\1\145\1\124\1\110\1\113\1\115\1\111"+
        "\1\116\1\112\1\125\1\120\1\131\1\134\1\135\6\uffff\1\64\47\uffff"+
        "\1\70\3\uffff\1\136\5\uffff\1\44\24\uffff\1\71\10\uffff\1\66\1\uffff"+
        "\1\133\30\uffff\1\41\1\uffff\1\102\2\uffff\1\65\1\142\1\uffff\1"+
        "\21\1\uffff\1\56\1\uffff\1\132\6\uffff\1\55\3\uffff\1\42\1\45\20"+
        "\uffff\1\43\1\uffff\1\51\1\101\1\7\5\uffff\1\75\2\uffff\1\57\1\uffff"+
        "\1\32\1\46\2\uffff\1\76\2\uffff\1\67\4\uffff\1\26\3\uffff\1\3\2"+
        "\uffff\1\4\1\73\5\uffff\1\24\1\62\2\uffff\1\33\2\uffff\1\47\1\74"+
        "\1\100\1\30\1\uffff\1\1\6\uffff\1\10\3\uffff\1\72\1\40\1\61\4\uffff"+
        "\1\37\1\uffff\1\77\1\36\1\uffff\1\31\1\27\1\20\3\uffff\1\35\1\121"+
        "\1\11\2\uffff\1\34";
    static final String DFA30_specialS =
        "\u0168\uffff}>";
    static final String[] DFA30_transitionS = {
            "\2\57\1\uffff\2\57\22\uffff\1\57\1\50\1\55\1\uffff\1\56\1\47"+
            "\1\14\1\54\1\33\1\34\1\6\1\42\1\12\1\43\1\5\1\44\1\52\11\53"+
            "\1\36\1\2\1\11\1\23\1\13\1\32\1\35\32\56\1\20\1\uffff\1\21\1"+
            "\46\1\56\1\uffff\1\24\1\27\1\7\1\31\1\10\1\25\2\56\1\3\2\56"+
            "\1\30\1\41\1\26\1\56\1\1\1\56\1\40\1\4\1\22\1\56\1\17\1\37\3"+
            "\56\1\15\1\45\1\16\1\51\101\uffff\27\56\1\uffff\37\56\1\uffff"+
            "\u1f08\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff"+
            "\u092e\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\62\20\uffff\1\60\2\uffff\1\61",
            "",
            "\1\63\6\uffff\1\65\1\64",
            "\1\70\13\uffff\1\66\1\72\1\uffff\1\67\1\uffff\1\71",
            "\1\73\1\uffff\12\75",
            "\1\76",
            "\1\101\6\uffff\1\100\3\uffff\1\102\2\uffff\1\103",
            "\1\104\1\uffff\1\105\11\uffff\1\106",
            "",
            "",
            "",
            "\1\107\26\uffff\1\110",
            "",
            "",
            "\1\112",
            "",
            "",
            "\1\114\11\uffff\1\113",
            "\1\115",
            "\1\120\20\uffff\1\117",
            "\1\121\7\uffff\1\122\2\uffff\1\123\2\uffff\1\124",
            "\1\127\3\uffff\1\126\17\uffff\1\125",
            "\1\130\2\uffff\1\131\6\uffff\1\132",
            "\1\133",
            "\1\134\11\uffff\1\135",
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
            "\1\151\4\uffff\1\150\15\uffff\1\147",
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
            "\1\170\5\uffff\1\171",
            "\1\172",
            "\1\173",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\176\1\175",
            "\1\177",
            "\1\u0081\20\uffff\1\u0080",
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
            "\1\u0088\1\u0087",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "",
            "",
            "",
            "\1\u008e\2\uffff\1\u008f",
            "\1\u0092\23\uffff\1\u0090\3\uffff\1\u0091",
            "\1\u0093\10\uffff\1\u0094",
            "",
            "",
            "\1\u0095",
            "\1\u0096",
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
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\24\56"+
            "\1\u00a3\5\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
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
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\4\56"+
            "\1\u00ad\25\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u00af",
            "\1\u00b1\2\uffff\1\u00b0",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\1\u00cb",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00cd",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00cf",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "",
            "\1\u00d9",
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
            "\1\u00e5",
            "\1\u00e6",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00e8",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00ea",
            "\1\u00eb",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00ee",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00f0",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u00f2",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00f4",
            "\1\u00f5",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u00fb",
            "\1\u00fc",
            "\1\u00fd",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0111",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0115",
            "",
            "",
            "\1\u0116",
            "",
            "\1\u0117",
            "",
            "\1\u0118",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\22\56"+
            "\1\u0119\7\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u011b",
            "\1\u011c",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\13\56"+
            "\1\u011e\16\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0121",
            "\1\u0122",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\u0124",
            "\1\u0125",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0127",
            "\1\u0128",
            "\1\u0129",
            "\1\u012a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u012c",
            "\1\u012d",
            "\1\u012e",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0130",
            "\1\u0131",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0134",
            "",
            "",
            "",
            "\1\u0135",
            "\1\u0136",
            "\1\u0137",
            "\1\u0138",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u013b",
            "",
            "\1\u013c",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u013e",
            "",
            "\1\u013f",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0144",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0146",
            "\1\u0147",
            "",
            "\1\u0148",
            "\1\u0149",
            "",
            "",
            "\1\u014a",
            "\1\u014b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u014d",
            "\1\u014e",
            "",
            "",
            "\1\u014f",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "",
            "",
            "\1\u0153",
            "",
            "\1\u0154",
            "\1\u0155",
            "\1\u0156",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0158",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u015b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u015f",
            "\1\u0160",
            "",
            "\1\u0161",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0165",
            "",
            "",
            "",
            "\1\u0166",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
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
            return "1:1: Tokens : ( T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | ENUM | Identifier | WS | COMMENT | LINE_COMMENT );";
        }
    }
 

}