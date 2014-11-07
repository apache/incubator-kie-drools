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

        public XpathPart(String field, boolean iterate, List<String> constraints) {
            this.field = field;
            this.iterate = iterate;
            this.constraints = constraints;
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
    }

    public static XpathAnalysis analyze(String xpath) {
        if (xpath.charAt(0) != '/') return null;

        boolean iterate = true;
        List<XpathPart> parts = new ArrayList<XpathPart>();

        List<String> constraints = new ArrayList<String>();
        int lastStart = 1;
        int nestedParam = 0;
        int nestedSquare = 0;
        boolean isQuoted = false;
        String field = null;

        for (int i = 1; i < xpath.length(); i++) {
            switch (xpath.charAt(i)) {
                case '/':
                case '.':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 0) {
                        if (field == null) {
                            field = xpath.substring(lastStart, i).trim();
                        }
                        parts.add(new XpathPart(field, iterate, constraints));

                        iterate = xpath.charAt(i) == '/';
                        constraints = new ArrayList<String>();
                        lastStart = i + 1;
                        field = null;
                    }
                    break;
                case '(':
                case '{':
                    if (!isQuoted) nestedParam++;
                    break;
                case ')':
                case '}':
                    if (!isQuoted) nestedParam--;
                    break;
                case '[':
                    if (!isQuoted && nestedParam == 0) {
                        if (nestedSquare == 0) {
                            field = xpath.substring(lastStart, i).trim();
                            lastStart = i+1;
                        }
                        nestedSquare++;
                    }
                    break;
                case ']':
                    if (!isQuoted && nestedParam == 0) {
                        nestedSquare--;
                        if (nestedSquare == 0) {
                            constraints.add(xpath.substring(lastStart, i).trim());
                        }
                    }
                    break;
                case ',':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 1) {
                        constraints.add(xpath.substring(lastStart, i).trim());
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
        parts.add(new XpathPart(field, iterate, constraints));

        return new XpathAnalysis(parts);
    }
}
