/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

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
        try (InputStream in = WebsiteResourceUtils.class.getResourceAsStream(RESOURCE_NAMESPACE + websiteResource);
                OutputStream out = new FileOutputStream(outputFile)) {
            if (in == null) {
                throw new IllegalStateException("The websiteResource (" + websiteResource
                        + ") does not exist.");
            }
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy websiteResource (" + websiteResource
                    + ") to outputFile (" + outputFile + ").", e);
        }
    }

    private WebsiteResourceUtils() {
    }

}
