/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.optaplanner.persistence.xstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.impl.SingleBenchmarkState;

public class XStreamResumeIO {

    private XStream xStream;

    public XStreamResumeIO() {
        this.xStream = new XStream();
    }

    public SingleBenchmarkState read(File inputFile) {
        SingleBenchmarkState singleBenchmarkState = null;
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
            singleBenchmarkState = (SingleBenchmarkState) xStream.fromXML(reader);
        } catch (XStreamException e) {
            throw new IllegalArgumentException("Problem reading inputFile: " + inputFile, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading inputFile: " + inputFile, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return singleBenchmarkState;
    }

    public void write(SingleBenchmarkState singleBenchmarkState, File outputFile) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");
            xStream.toXML(singleBenchmarkState, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing outputFile: " + outputFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
