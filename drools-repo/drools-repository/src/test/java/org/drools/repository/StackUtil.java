/**
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

package org.drools.repository;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class StackUtil {

    /**
     * Return the name of the routine that called getCurrentMethodName
     *
     * @author Johan Känngård, http://dev.kanngard.net
     * (found on the net in 2000, donŽt remember where...)
     */
    public static String getCurrentMethodName() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        (new Throwable()).printStackTrace(pw);
        pw.flush();
        String stackTrace = baos.toString();
        pw.close();

        StringTokenizer tok = new StringTokenizer(stackTrace, "\n");
        String l = tok.nextToken(); // 'java.lang.Throwable'
        l = tok.nextToken(); // 'at ...getCurrentMethodName'
        l = tok.nextToken(); // 'at ...<caller to getCurrentRoutine>'
        // Parse line 3
        tok = new StringTokenizer(l.trim(), " <(");
        String t = tok.nextToken(); // 'at'
        t = tok.nextToken(); // '...<caller to getCurrentRoutine>'
        return t;
    }    
    
}
