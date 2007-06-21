/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.lang.DRLLexer;
import org.drools.lang.DRLParser;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.FromDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author fernandomeyer
 */

public class ExpressionHandler extends BaseAbstractHandler
    implements
    Handler {

    ExpressionHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( FromHandler.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( BaseDescr.class );

            this.allowNesting = true;
        }
    }

    public Class generateNodeFor() {
        return BaseDescr.class;
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        return new BaseDescr();
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {

        final Configuration config = this.xmlPackageReader.endConfiguration();
        final BaseDescr baseDescr = (BaseDescr) this.xmlPackageReader.getCurrent();

        final String expression = config.getText();

        if ( expression == null || expression.trim().equals( "" ) ) {
            throw new SAXParseException( "<" + localName + "> must have some content",
                                         this.xmlPackageReader.getLocator() );
        }

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();

        final FromDescr fromSource = (FromDescr) parent;
        final CharStream charStream = new ANTLRStringStream( expression.trim() );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        try {
            final DeclarativeInvokerDescr declarativeInvoker = parser.from_source( fromSource );
            fromSource.setDataSource( declarativeInvoker );

        } catch ( final RecognitionException e ) {
            throw new SAXParseException( "<" + localName + "> must have a valid expression content ",
                                         this.xmlPackageReader.getLocator() );
        }

        return null;
    }

}
