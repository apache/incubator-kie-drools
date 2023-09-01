/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.doc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class DroolsDocsComponentFactory {
    private static final String INDENT             = "    ";

    public static void createToC(PDDocument doc, int pageNumber, DrlPackageParser packageData) throws IOException {
        final PDPage page = new PDPage(PDRectangle.A4);
        float yPosition = page.getMediaBox().getHeight() - 50;  // Start position for the table

        doc.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {

            StringBuilder sb = new StringBuilder("Table of Contents\n");
            int count = 0;
            for (DrlRuleParser rule : packageData.getRules()) {
                count++;
                sb.append(count).append(". ").append(rule.getName().replaceAll("\"","")).append("\n");
            }

            drawText(contentStream, page, yPosition, 15, sb.toString());
            addFooter(contentStream, page, pageNumber, packageData.getName());
        }
    }

    public static void createRulePage(PDDocument doc, int pageNumber, String packageName, DrlRuleParser drlData) throws IOException {
        final PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
            createHeader(contentStream, page, packageName);
            float yPosition = createRuleHeader(contentStream, page, drlData.getName().replaceAll("\"",""));

            // Extends
            int extendIndex = drlData.getName().lastIndexOf( "extends" );
            if ( extendIndex > 0 ) {
                yPosition = drawText(contentStream, page, yPosition + 20, 12, "Extends: " + drlData.getName().substring( "extends".length() + extendIndex ).replaceAll("\"", ""));
            }

            if (!drlData.getDescription().trim().isEmpty()) {
                yPosition = createTable(contentStream, page, yPosition, Map.of("Description", List.of(drlData.getDescription())));
            } else {
                yPosition = yPosition + 40;
            }

            final LinkedHashMap<String, Collection<String>> content = new LinkedHashMap<>();

            content.put("Attributes", dashIfEmpty(prefix(INDENT, drlData.getHeader())));
            content.put(INDENT + "WHEN", dashIfEmpty(prefix(INDENT + INDENT, drlData.getLhs())));
            content.put(INDENT + "THEN", dashIfEmpty(prefix(INDENT + INDENT, drlData.getRhs())));

            yPosition = createTable(contentStream, page, yPosition - 40, content);
            createTable(contentStream, page, yPosition - 40, Map.of("Metadata", dashIfEmpty(drlData.getMetadata())));

            createOtherItems(contentStream, page, yPosition, drlData.getOtherInformation());

            addFooter(contentStream, page, pageNumber, packageName);
        }
    }

    private static Collection<String> prefix(String prefix, Collection<String> input){
        final Collection<String> result = new ArrayList<>(input.size());
        for (String s : input) {
            result.add(prefix + s.replaceAll("\t", "    "));
        }
        return result;
    }

    private static Collection<String> dashIfEmpty(Collection<String> input){
        return input.isEmpty() ? List.of("-") : input;
    }

    private static float createRuleHeader(PDPageContentStream contentStream, PDPage page, String string) throws IOException {
        float yPosition = page.getMediaBox().getHeight() - 50;
        return drawText(contentStream, page, yPosition, PDType1Font.TIMES_BOLD, 20, "Rule " + string);
    }

    public static void createFirstPage(PDDocument doc, String date, DrlPackageParser packageData) throws IOException {
        final PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
            float lastContentY = writeCenteredTitleAndDate(contentStream, page, packageData.getName().toUpperCase(), date);

            lastContentY = drawText(contentStream, page, lastContentY, 10, "\n\n\n\n\n" + packageData.getDescription());

            lastContentY = createTable(contentStream, page, lastContentY, Map.of("Metadata", packageData.getMetadata().isEmpty() ? List.of("-") : packageData.getMetadata()));
            lastContentY = createTable(contentStream, page, lastContentY - 20, Map.of("Globals", packageData.getGlobals()));

            createOtherItems(contentStream, page, lastContentY, packageData.getOtherInformation());

            addFooter(contentStream, page, 1, packageData.getName());
        }
    }

    private static float createOtherItems(PDPageContentStream contentStream, PDPage page, float lastContentY, Map<String, List<String>> otherInformation) throws IOException {
        for (Map.Entry<String, List<String>> other : otherInformation.entrySet()) {
            lastContentY = createTable(contentStream, page, lastContentY - 20, Map.of(other.getKey(), other.getValue()));
        }
        return lastContentY;
    }

    private static void addFooter(PDPageContentStream contentStream, PDPage page, int pageNumber, String footerText) throws IOException {
        contentStream.moveTo(50, 50); // Starting point of line
        contentStream.lineTo(page.getMediaBox().getWidth() - 50, 50); // Ending point of line
        contentStream.stroke();

        String text = footerText + " - " + pageNumber;
        int fontSize = 8;
        float width = PDType1Font.TIMES_ROMAN.getStringWidth(text) / 1000 * fontSize;
        float pageWidth = page.getMediaBox().getWidth();
        float xPosition = pageWidth - width - 50;  // subtract 50 (or whatever your right margin is)

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, fontSize);
        contentStream.newLineAtOffset(xPosition, 40);  // 50 (or whatever your bottom margin is)
        contentStream.showText(text);
        contentStream.endText();
    }

    private static float createTable(PDPageContentStream contentStream, PDPage page, float yPosition, Map<String, Collection<String>> icontent) throws IOException {
        float margin = 50;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float tableRowHeight = 20f;
        float colWidth = tableWidth;
        float contentY = 0;

        float tableTopY = yPosition;  // Save initial yPosition as the top of the table
        float tableBottomY = 0;  // Placeholder for bottom of the table

        for (Entry<String, Collection<String>> entry : icontent.entrySet()) {
            String header = entry.getKey();
            Collection<String> content = entry.getValue();

            // Draw header row
            float headerY = drawRow(contentStream, page, header, yPosition, tableWidth, tableRowHeight, colWidth, true);
            drawLine(contentStream, margin, headerY, margin + tableWidth, headerY);  // Header bottom border
            contentY = headerY;

            // Add content
            for (String rowContent : content) {
                contentY = drawRow(contentStream, page, rowContent, contentY, tableWidth, tableRowHeight, colWidth, false);
                drawLine(contentStream, margin, contentY, margin + tableWidth, contentY);  // Row bottom border
            }
            yPosition = contentY;
        }

        tableBottomY = yPosition;  // Save final yPosition as the bottom of the table

        // Draw table borders
        drawLine(contentStream, margin, tableTopY, margin + tableWidth, tableTopY);  // Top border
        drawLine(contentStream, margin, tableBottomY, margin + tableWidth, tableBottomY);  // Bottom border
        drawLine(contentStream, margin, tableTopY, margin, tableBottomY);  // Left border
        drawLine(contentStream, margin + colWidth, tableTopY, margin + colWidth, tableBottomY);  // Right border

        return contentY;
    }

    private static void drawLine(PDPageContentStream contentStream, float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }


    private static float drawRow(PDPageContentStream contentStream, PDPage page, String text, float yPosition, float tableWidth, float rowHeight, float colWidth, boolean header) throws IOException {
        float margin = 50;
        float textx = margin + 5;
        String[] lines = text.split("\n");
        float currentY = yPosition;

        // Draw background for header
        if (header) {
            PDColor headerBgColor = new PDColor(new float[]{0.8f, 0.8f, 1.0f}, PDDeviceRGB.INSTANCE);
            contentStream.setNonStrokingColor(headerBgColor);
            contentStream.addRect(margin, currentY - rowHeight * lines.length, tableWidth, rowHeight * lines.length);
            contentStream.fill();
            contentStream.setNonStrokingColor(new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE));
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        } else {
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
        }

        // Add row content
        for (String line : lines) {
            contentStream.beginText();
            contentStream.newLineAtOffset(textx, currentY - 15);
            contentStream.showText(line);
            contentStream.endText();
            currentY -= rowHeight;
        }

        return currentY;
    }

    private static float drawText(PDPageContentStream contentStream, PDPage page, float yPosition, int fontSize, String text) throws IOException {
        return drawText(contentStream, page, yPosition, PDType1Font.TIMES_ROMAN, fontSize, text);
    }

    private static float drawText(PDPageContentStream contentStream, PDPage page, float yPosition, PDFont font, int fontSize, String text) throws IOException {
        float margin = 50;
        float textx = margin + 5;
        float currentY = yPosition;
        float height = fontSize + 5f;

        contentStream.setFont(font, fontSize);
        for (String line : text.split("\n")) {
            contentStream.beginText();
            contentStream.newLineAtOffset(textx, currentY - (fontSize + 5f));
            contentStream.showText(line);
            contentStream.endText();
            currentY -= height;
        }

        return currentY - height;
    }

    private static float writeCenteredTitleAndDate(PDPageContentStream contentStream, PDPage page, String title, String currentDate) throws IOException {
        float yStart = (page.getMediaBox().getHeight() / 3) * 2;
        float xStart = page.getMediaBox().getWidth() / 2;
        float titleFontSize = 24;
        float dateFontSize = 10;

        // Center title
        float titleWidth = PDType1Font.TIMES_BOLD.getStringWidth(title) / 1000 * titleFontSize;
        float titleX = xStart - titleWidth / 2;
        float titleY = yStart;

        contentStream.setFont(PDType1Font.TIMES_BOLD, titleFontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(titleX, titleY);
        contentStream.showText(title);
        contentStream.endText();

        // Center date
        String textContent = "Documentation created: " + currentDate;
        float dateWidth = PDType1Font.TIMES_ROMAN.getStringWidth(textContent) / 1000 * dateFontSize;
        float dateX = xStart - dateWidth / 2;
        float dateY = yStart - titleFontSize + 5;

        contentStream.setFont(PDType1Font.TIMES_ROMAN, dateFontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(dateX, dateY);
        contentStream.showText(textContent);
        contentStream.endText();

        return dateY;
    }

    private static void createHeader(PDPageContentStream contentStream, PDPage page, String header) throws IOException {
        float margin = 40;
        float headerFontSize = 10;
        float xPosition = margin;
        float yPosition = page.getMediaBox().getHeight() - margin;

        contentStream.setFont(PDType1Font.TIMES_BOLD, headerFontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition, yPosition);
        contentStream.showText(header);
        contentStream.endText();
    }
}
