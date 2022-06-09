package org.optaplanner.benchmark.impl.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class WebsiteResourceUtils {

    private static final String RESOURCE_NAMESPACE = "/org/optaplanner/benchmark/impl/report/";

    public static void copyResourcesTo(File benchmarkReportDirectory) {
        // Twitter Bootstrap
        copyResource(benchmarkReportDirectory, "twitterbootstrap/css/bootstrap-responsive.css");
        // copyResource(benchmarkReportDirectory, "twitterbootstrap/css/bootstrap-responsive.min.css");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/css/bootstrap.css");
        // copyResource(benchmarkReportDirectory, "twitterbootstrap/css/bootstrap.min.css");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/css/prettify.css");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/img/glyphicons-halflings-white.png");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/img/glyphicons-halflings.png");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/js/bootstrap.js");
        // copyResource(benchmarkReportDirectory, "twitterbootstrap/js/bootstrap.min.js");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/js/jquery.js");
        // copyResource(benchmarkReportDirectory, "twitterbootstrap/js/jquery.min.js");
        copyResource(benchmarkReportDirectory, "twitterbootstrap/js/prettify.js");
        // Website resources
        copyResource(benchmarkReportDirectory, "website/css/benchmarkReport.css");
        copyResource(benchmarkReportDirectory, "website/img/optaPlannerLogo.png");
    }

    private static void copyResource(File benchmarkReportDirectory, String websiteResource) {
        File outputFile = new File(benchmarkReportDirectory, websiteResource);
        outputFile.getParentFile().mkdirs();
        try (InputStream in = WebsiteResourceUtils.class.getResourceAsStream(RESOURCE_NAMESPACE + websiteResource)) {
            if (in == null) {
                throw new IllegalStateException("The websiteResource (" + websiteResource
                        + ") does not exist.");
            }
            Files.copy(in, outputFile.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy websiteResource (" + websiteResource
                    + ") to outputFile (" + outputFile + ").", e);
        }
    }

    private WebsiteResourceUtils() {
    }

}
