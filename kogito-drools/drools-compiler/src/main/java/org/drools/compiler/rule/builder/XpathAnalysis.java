/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XpathAnalysis implements Iterable<XpathAnalysis.XpathPart> {

    private final List<XpathPart> parts;

    public XpathAnalysis(List<XpathPart> parts) {
        this.parts = parts;
    }

    @Override
    public Iterator<XpathPart> iterator() {
        return parts.iterator();
    }

    public static class XpathPart {
        private final String field;
        private final boolean iterate;
        private final List<String> constraints;
        private final String inlineCast;
        private final int index;

        public XpathPart(String field, boolean iterate, List<String> constraints, String inlineCast, int index) {
            this.field = field;
            this.iterate = iterate;
            this.constraints = constraints;
            this.inlineCast = inlineCast;
            this.index = index;
        }

        public String getField() {
            return field;
        }

        public boolean isIterate() {
            return iterate;
        }

        public List<String> getConstraints() {
            return constraints;
        }

        public String getInlineCast() {
            return inlineCast;
        }

        public int getIndex() {
            return index;
        }

        public void addInlineCastConstraint(Class<?> clazz) {
            constraints.add(0, "this instanceof " + clazz.getCanonicalName());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder( field );
            if (index >= 0) {
                sb.append( "[" ).append( index ).append( "]" );
            }
            if (!constraints.isEmpty()) {
                sb.append( "{" );
                sb.append( constraints.get(0) );
                for (int i = 1; i < constraints.size(); i++) {
                    sb.append( ", " ).append( constraints.get( i ) );
                }
                sb.append( "}" );
            }
            return sb.toString();
        }
    }

    public static XpathAnalysis analyze(String xpath) {
        if (xpath.charAt(0) != '/') return null;

        boolean iterate = true;
        List<XpathPart> parts = new ArrayList<XpathPart>();

        List<String> constraints = new ArrayList<String>();
        String inlineCast = null;
        int index = -1;

        int lastStart = 1;
        int nestedParam = 0;
        int nestedCurly = 0;
        int nestedSquare = 0;
        boolean isQuoted = false;
        String field = null;

        for (int i = 1; i < xpath.length(); i++) {
            switch (xpath.charAt(i)) {
                case '/':
                case '.':
                    if (!isQuoted && nestedParam == 0 && nestedCurly == 0 && nestedSquare == 0) {
                        if (field == null) {
                            field = xpath.substring(lastStart, i).trim();
                        }
                        parts.add(new XpathPart(field, iterate, constraints, inlineCast, index));

                        iterate = xpath.charAt(i) == '/';
                        constraints = new ArrayList<String>();
                        inlineCast = null;
                        index = -1;
                        lastStart = i + 1;
                        field = null;
                    }
                    break;
                case '(':
                    if (!isQuoted) {
                        nestedParam++;
                    }
                    break;
                case ')':
                    if (!isQuoted) {
                        nestedParam--;
                        if (nestedParam < 0) {
                            return null;
                        }
                    }
                    break;
                case '[':
                    if (!isQuoted && nestedParam == 0 && nestedCurly == 0) {
                        if (nestedSquare == 0) {
                            if (field == null) {
                                field = xpath.substring( lastStart, i ).trim();
                            }
                            lastStart = i+1;
                        }
                        nestedSquare++;
                    }
                    break;
                case ']':
                    if (!isQuoted && nestedParam == 0 && nestedCurly == 0) {
                        nestedSquare--;
                        if (nestedSquare == 0) {
                            try {
                                index = Integer.parseInt( xpath.substring( lastStart, i ).trim() );
                            } catch (Exception e) {
                                return null;
                            }
                        } else if (nestedSquare < 0) {
                            return null;
                        }
                    }
                    break;
                case '{':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 0) {
                        if (nestedCurly == 0) {
                            if (field == null) {
                                field = xpath.substring( lastStart, i ).trim();
                            }
                            lastStart = i+1;
                        }
                        nestedCurly++;
                    }
                    break;
                case '}':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 0) {
                        nestedCurly--;
                        if (nestedCurly == 0) {
                            String constraint = xpath.substring(lastStart, i).trim();
                            if (constraint.startsWith("#")) {
                                inlineCast = constraint.substring(1);
                            } else {
                                constraints.add(constraint);
                            }
                        } else if (nestedCurly < 0) {
                            return null;
                        }
                    }
                    break;
                case ',':
                    if (!isQuoted && nestedParam == 0 && nestedCurly == 1) {
                        String constraint = xpath.substring(lastStart, i).trim();
                        if (constraint.startsWith("#")) {
                            inlineCast = constraint.substring(1);
                        } else {
                            constraints.add(constraint);
                        }
                        lastStart = i+1;
                    }
                    break;
                case '"':
                case '\'':
                    isQuoted = !isQuoted;
                    break;
            }
        }

        if (field == null) {
            field = xpath.substring(lastStart).trim();
        }
        parts.add(new XpathPart(field, iterate, constraints, inlineCast, index));

        return new XpathAnalysis(parts);
    }
}
