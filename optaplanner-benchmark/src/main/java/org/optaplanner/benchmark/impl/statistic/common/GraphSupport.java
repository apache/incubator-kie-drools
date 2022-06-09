package org.optaplanner.benchmark.impl.statistic.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;

public class GraphSupport {

    public static void writeChartToImageFile(JFreeChart chart, File chartFile) {
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        try (OutputStream out = new FileOutputStream(chartFile)) {
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed writing chartFile (" + chartFile + ").", e);
        }
    }

    private GraphSupport() {
    }

}
