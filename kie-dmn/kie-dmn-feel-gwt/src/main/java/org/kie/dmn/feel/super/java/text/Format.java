/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.text;

import java.io.Serializable;

public abstract class Format implements Serializable,
                                        Cloneable {

    private static final long serialVersionUID = -299282585814624189L;

    protected Format() {
    }

    public final String format(Object obj) {
        return "";
    }

    public abstract StringBuffer format(Object obj, StringBuffer toAppendTo, Object pos);

    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return null;
    }

    public abstract Object parseObject(String source, ParsePosition pos);

    public Object parseObject(String source) throws ParseException {
        return null;
    }

    public Object clone() {
        return null;
    }

    AttributedCharacterIterator createAttributedCharacterIterator(String s) {
        return null;
    }

    AttributedCharacterIterator createAttributedCharacterIterator(
            AttributedCharacterIterator[] iterators) {
        return null;
    }

    AttributedCharacterIterator createAttributedCharacterIterator(
            String string, AttributedCharacterIterator.Attribute key,
            Object value) {
        return null;
    }

    AttributedCharacterIterator createAttributedCharacterIterator(
            AttributedCharacterIterator iterator,
            AttributedCharacterIterator.Attribute key, Object value) {
        return null;
    }

    public static class Field extends AttributedCharacterIterator.Attribute {

        private static final long serialVersionUID = 276966692217360283L;

        protected Field(String name) {
            super(null);
        }
    }

    interface FieldDelegate {

        public void formatted(Format.Field attr, Object value, int start,
                              int end, StringBuffer buffer);

        public void formatted(int fieldID, Format.Field attr, Object value,
                              int start, int end, StringBuffer buffer);
    }
}