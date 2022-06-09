package org.optaplanner.examples.common.persistence;

import java.awt.Color;

import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class XSSFColorUtil {

    private static final IndexedColorMap INDEXED_COLOR_MAP = new DefaultIndexedColorMap();

    public static XSSFColor getXSSFColor(Color awtColor) {
        byte[] rgb = new byte[] {
                intToByte(awtColor.getRed()),
                intToByte(awtColor.getGreen()),
                intToByte(awtColor.getBlue())
        };
        return new XSSFColor(rgb, INDEXED_COLOR_MAP);
    }

    private static byte intToByte(int integer) {
        return ((Integer) integer).byteValue();
    }

    private XSSFColorUtil() {
        // No external instances.
    }

}
