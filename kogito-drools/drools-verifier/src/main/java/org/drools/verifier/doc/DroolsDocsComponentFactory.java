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

package org.drools.verifier.doc;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;


import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class DroolsDocsComponentFactory {

    private static final int INDENTATION_LEFT = 20;

    private static final String INDENT             = "    ";

    private static final Font   CHAPTER_TITLE      = FontFactory.getFont( FontFactory.TIMES,
                                                                          20,
                                                                          Font.BOLD );
    private static final Font   PACKAGE_NAME       = FontFactory.getFont( FontFactory.TIMES,
                                                                          10,
                                                                          Font.BOLD );
    private static final Font   RULE_PACKAGE_TITLE = FontFactory.getFont( FontFactory.TIMES,
                                                                          24,
                                                                          Font.BOLD );
    private static final Font   CATEGORIES_TEXT    = FontFactory.getFont( FontFactory.TIMES,
                                                                          12 );
    private static final Font   BODY_TEXT          = FontFactory.getFont( FontFactory.TIMES,
                                                                          10 );
    static final Font           HEADER_FOOTER_TEXT = FontFactory.getFont( FontFactory.TIMES,
                                                                          8 );

    public static Table newDescription(String description) throws DocumentException {
        if ( description == null || "".equals( description ) ) {
            description = " - ";
        }

        Table table = newTable();

        Cell headerCell = newHeaderCell( "Description",
                                         CATEGORIES_TEXT );
        table.addCell( headerCell );

        table.addCell( newCell( description ) );

        return table;
    }

    private static Table newTable() throws BadElementException {
        Table table = new Table( 1 );

        table.setBorderWidthTop( 1 );
        table.setBorderWidthLeft( 1 );
        table.setBorderWidthRight( 1 );
        table.setBorderWidthBottom( 0 );
        table.setWidth( 100 );
        table.setPadding( 3 );
        table.setAlignment( Table.ALIGN_LEFT );

        return table;
    }

    public static void createOtherItems(Document document,
                                        Map<String, java.util.List<String>> other) throws DocumentException {

        for ( String key : other.keySet() ) {
            document.add( newTable( key,
                                    other.get( key ) ) );
        }
    }

    public static Table newRuleTable(DrlRuleParser drl) throws BadElementException,
                                                     DocumentException {
        Table table = newTable();

        Cell headerCell = newHeaderCell( "Attributes",
                                         CATEGORIES_TEXT );
        table.addCell( headerCell );

        for ( String s : drl.getHeader() ) {
            table.addCell( newCell( INDENT + s.trim() ) );
        }

        table.addCell( newHeaderCell( INDENT + "WHEN",
                                      BODY_TEXT ) );
        for ( String s : drl.getLhs() ) {
            table.addCell( newCell( INDENT + INDENT + s.trim() ) );
        }

        table.addCell( newHeaderCell( INDENT + "THEN",
                                      BODY_TEXT ) );
        for ( String s : drl.getRhs() ) {
            table.addCell( newCell( INDENT + INDENT + s.trim() ) );
        }
        // table.addCell( newEmptyWhenThenCell( "END" ) );

        return table;
    }

    public static Table newTable(final String topic,
                                 Collection<String> items) throws BadElementException,
                                                          DocumentException {
        Table table = newTable();

        Cell headerCell = newHeaderCell( topic,
                                         CATEGORIES_TEXT );
        table.addCell( headerCell );

        if ( items.isEmpty() ) {
            table.addCell( newCell( " - " ) );
        } else {
            for ( String s : items ) {
                table.addCell( newCell( s ) );
            }
        }

        return table;
    }

    public static List createContents(java.util.List<DrlRuleParser> rules) {
        List index = new List( true );

        for ( DrlRuleParser drlRuleData : rules ) {
            Chunk chunk = new Chunk( drlRuleData.getName() );
            // chunk.setLocalGoto( item.getName() );
            ListItem listItem = new ListItem( chunk );
            index.add( listItem );
        }

        return index;
    }

    private static Cell newHeaderCell(String text,
                                      Font font) throws BadElementException {
        Cell c = new Cell( new Phrase( text,
                                       font ) );
        c.setBackgroundColor( Color.decode( "#CCCCFF" ) );
        c.setLeading( 10 );
        c.setBorder( 1 );

        return c;
    }

    private static Cell newCell(String text) throws BadElementException {
        Cell c = new Cell( new Phrase( text,
                                       BODY_TEXT ) );
        c.setLeading( 10 );
        c.setBorder( 0 );
        c.setBorderWidthBottom( 1 );

        return c;
    }

    public static HeaderFooter createFooter(String packageName) {
        HeaderFooter footer = new HeaderFooter( new Phrase( packageName + "-",
                                                            HEADER_FOOTER_TEXT ),
                                                true );
        footer.setBorder( 1 );
        footer.setAlignment( Element.ALIGN_RIGHT );

        return footer;
    }
    
    private static String[] splitFirst(String source, String splitter) {
        Vector rv = new Vector();
        int last = 0;
        int next = 0;

        next = source.indexOf(splitter, last);
        if (next != -1)
        {
          rv.add(source.substring(last, next));
          last = next + splitter.length();
        }

        if (last < source.length())
        {
          rv.add(source.substring(last, source.length()));
        }

        return (String[]) rv.toArray(new String[rv.size()]);
      }


    public static void newRulePage(Document document,
                                   String packageName,
                                   DrlRuleParser drlData) throws DocumentException {

        document.add( new Paragraph( packageName,
                                     PACKAGE_NAME ) );
        document.add( new Paragraph( "Rule " + drlData.getName(),
                                     CHAPTER_TITLE ) );

        // Extends
        int index = drlData.getName().lastIndexOf( "extends" );
        if ( index > 0 ) {
            document.add( new Paragraph( "Extends:",
                                         BODY_TEXT ) );

            Paragraph ext = new Paragraph( drlData.getName().substring( "extends".length() + index ),
                                           BODY_TEXT );
            ext.setIndentationLeft(INDENTATION_LEFT);
            document.add( ext );
        }

        // if the data came from guvnor, this will be empty
        if(drlData.getDescription() != null && drlData.getDescription().trim().equals("")) {
            Iterator<String> iter = drlData.getMetadata().iterator();
            while(iter.hasNext()) {
            	String nextDesc = iter.next();
            	if(nextDesc.startsWith("Description")) {
            		String[] parts = splitFirst(nextDesc, ":");
            		// no description
            		if(parts.length == 1) {
            			// guvnor did not have it
            			document.add( newDescription( drlData.getDescription() ) );
            		} else {
            			document.add(newDescription(parts[1]));
            		}
            	}
            }

        } else {
            document.add( newDescription( drlData.getDescription() ) );
        }
        
        // DRL
        document.add( newRuleTable( drlData ) );

        // Meta data
        document.add( newTable( "Metadata",
                                drlData.getMetadata() ) );

        // Other
        createOtherItems( document,
                          drlData.getOtherInformation() );

        document.newPage();
    }

    public static void createFirstPage(Document document,
                                       String currentDate,
                                       DrlPackageParser packageData) throws DocumentException {
        Paragraph title = new Paragraph( "\n\n\n\n\n" + packageData.getName().toUpperCase(),
                                         RULE_PACKAGE_TITLE );
        title.setAlignment( Element.ALIGN_CENTER );
        document.add( title );

        Paragraph date = new Paragraph( "Documentation created: " + currentDate,
                                        BODY_TEXT );
        date.setAlignment( Element.ALIGN_CENTER );
        document.add( date );

        document.add( new Paragraph( "\n\n\n\n\n" + packageData.getDescription(),
                                     BODY_TEXT ) );
        document.add( newTable( "Metadata ",
                                packageData.getMetadata() ) );
        document.add( newTable( "Globals ",
                                packageData.getGlobals() ) );
        createOtherItems( document,
                          packageData.getOtherInformation() );
    }
}

class EndPage extends PdfPageEventHelper {
    private final String currentDate;

    public EndPage(String currentDate) {
        this.currentDate = currentDate;
    }

    public void onEndPage(PdfWriter writer,
                          Document document) {

        try {
            Image image = Image.getInstance( DroolsDocsBuilder.class.getResource( "guvnor-webapp.png" ) ); // TODO this image never existed
            image.setAlignment( Image.RIGHT );
            image.scaleAbsolute( 100,
                                 30 );
            Rectangle page = document.getPageSize();
            PdfPTable head = new PdfPTable( 2 );

            PdfPCell cell1 = new PdfPCell( image );
            cell1.setHorizontalAlignment( Element.ALIGN_LEFT );
            cell1.setBorder( 0 );

            head.addCell( cell1 );

            PdfPCell cell2 = new PdfPCell( new Phrase( currentDate,
                                                       DroolsDocsComponentFactory.HEADER_FOOTER_TEXT ) );
            cell2.setHorizontalAlignment( Element.ALIGN_RIGHT );
            cell2.setBorder( 0 );

            head.addCell( cell2 );

            head.setTotalWidth( page.getWidth() - document.leftMargin() - document.rightMargin() );
            head.writeSelectedRows( 0,
                                    -1,
                                    document.leftMargin(),
                                    page.getHeight() - document.topMargin() + head.getTotalHeight(),
                                    writer.getDirectContent() );

        } catch ( Exception e ) {
            throw new ExceptionConverter( e );
        }
    }
}
