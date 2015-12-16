/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.core.util.StringUtils;

public class ReaderInputStream extends InputStream {

    /** Source Reader */
    private Reader in;

    private String encoding = System.getProperty("file.encoding");

    private byte[] slack;

    private int begin;

    /**
     * Construct a <code>ReaderInputStream</code>
     * for the specified <code>Reader</code>.
     *
     * @param reader   <code>Reader</code>.  Must not be <code>null</code>.
     */
    public ReaderInputStream(Reader reader) {
        if ( reader == null ) {
            throw new IllegalArgumentException("reader must not be null");
        }
        in = reader;
        if ( reader instanceof InputStreamReader ) {
            // set the encoding if it's an InputStreamReader and that reader defines an encoding.
            if ( !StringUtils.isEmpty( ((InputStreamReader) reader).getEncoding() ) ) {
                this.encoding = ((InputStreamReader) reader).getEncoding();
            }
        }
    }

    /**
     * Construct a <code>ReaderInputStream</code>
     * for the specified <code>Reader</code>,
     * with the specified encoding.
     *
     * @param reader     non-null <code>Reader</code>.
     * @param encoding   non-null <code>String</code> encoding.
     */
    public ReaderInputStream(Reader reader, String encoding) {
        this(reader);
        if (encoding == null) {
            throw new IllegalArgumentException("encoding must not be null");
        } else {
            this.encoding = encoding;
        }
    }

    /**
     * Reads from the <code>Reader</code>, returning the same value.
     *
     * @return the value of the next character in the <code>Reader</code>.
     *
     * @exception IOException if the original <code>Reader</code> fails to be read
     */
    public synchronized int read() throws IOException {
        if (in == null) {
            throw new IOException("Stream Closed");
        }

        byte result;
        if (slack != null && begin < slack.length) {
            result = slack[begin];
            if (++begin == slack.length) {
                slack = null;
            }
        } else {
            byte[] buf = new byte[1];
            if (read(buf, 0, 1) <= 0) {
                return -1;
            } else {
                result = buf[0];
            }
        }
        return result & 0xFF;
    }

    /**
     * Reads from the <code>Reader</code> into a byte array
     *
     * @param b  the byte array to read into
     * @param off the offset in the byte array
     * @param len the length in the byte array to fill
     * @return the actual number read into the byte array, -1 at
     *         the end of the stream
     * @exception IOException if an error occurs
     */
    public synchronized int read(byte[] b, int off, int len)
        throws IOException {
        if (in == null) {
            throw new IOException("Stream Closed");
        }
        if (len == 0) {
            return 0;
        }
        while (slack == null) {
            char[] buf = new char[len]; // might read too much
            int n = in.read(buf);
            if (n == -1) {
                return -1;
            }
            if (n > 0) {
                slack = new String(buf, 0, n).getBytes(encoding);
                begin = 0;
            }
        }

        if (len > slack.length - begin) {
            len = slack.length - begin;
        }

        System.arraycopy(slack, begin, b, off, len);

        if ((begin += len) >= slack.length) {
            slack = null;
        }

        return len;
    }

    /**
     * Marks the read limit of the Reader.
     *
     * @param limit the maximum limit of bytes that can be read before the
     *              mark position becomes invalid
     */
    public synchronized void mark(final int limit) {
        try {
            in.mark(limit);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }


    /**
     * @return   the current number of bytes ready for reading
     * @exception IOException if an error occurs
     */
    public synchronized int available() throws IOException {
        if (in == null) {
            throw new IOException("Stream Closed");
        }
        if (slack != null) {
            return slack.length - begin;
        }
        if (in.ready()) {
            return 1;
        }
        return 0;
    }

    /**
     * @return false - mark is not supported
     */
    public boolean markSupported () {
        return false;   // would be imprecise
    }

    /**
     * Resets the Reader.
     *
     * @exception IOException if the Reader fails to be reset
     */
    public synchronized void reset() throws IOException {
        if (in == null) {
            throw new IOException("Stream Closed");
        }
        slack = null;
        in.reset();
    }

    /**
     * Closes the Reader.
     *
     * @exception IOException if the original Reader fails to be closed
     */
    public synchronized void close() throws IOException {
        if (in != null) {
            in.close();
            slack = null;
            in = null;
        }
    }
}

