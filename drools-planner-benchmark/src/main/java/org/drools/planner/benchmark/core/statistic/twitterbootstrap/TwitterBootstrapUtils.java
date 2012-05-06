/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.benchmark.core.statistic.twitterbootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class TwitterBootstrapUtils {

    private static final String RESOURCE_NAMESPACE = "/org/drools/planner/benchmark/core/statistic/";

    public static void copyResourcesTo(File statisticDirectory) {
        copyResource(statisticDirectory, "twitterbootstrap/css/bootstrap-responsive.css");
        copyResource(statisticDirectory, "twitterbootstrap/css/bootstrap-responsive.min.css");
        copyResource(statisticDirectory, "twitterbootstrap/css/bootstrap.css");
        copyResource(statisticDirectory, "twitterbootstrap/css/bootstrap.min.css");
        copyResource(statisticDirectory, "twitterbootstrap/img/glyphicons-halflings-white.png");
        copyResource(statisticDirectory, "twitterbootstrap/img/glyphicons-halflings.png");
        copyResource(statisticDirectory, "twitterbootstrap/js/bootstrap.js");
        copyResource(statisticDirectory, "twitterbootstrap/js/bootstrap.min.js");
        copyResource(statisticDirectory, "twitterbootstrap/js/jquery.js");
        copyResource(statisticDirectory, "twitterbootstrap/js/jquery.min.js");
    }

    private static void copyResource(File statisticDirectory, String twitterBootstrapResource) {
        File outputFile = new File(statisticDirectory, twitterBootstrapResource);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = TwitterBootstrapUtils.class.getResourceAsStream(RESOURCE_NAMESPACE + twitterBootstrapResource);
            if (in == null) {
                throw new IllegalStateException("The twitterBootstrapResource (" + twitterBootstrapResource
                        + ") does not exist.");
            }
            outputFile.getParentFile().mkdirs();
            out = new FileOutputStream(outputFile);
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy twitterBootstrapResource (" + twitterBootstrapResource
                    + ") to outputFile (" + outputFile + ").", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private TwitterBootstrapUtils() {
    }

}
