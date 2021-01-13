

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

    public abstract StringBuffer format(Object obj,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos);

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