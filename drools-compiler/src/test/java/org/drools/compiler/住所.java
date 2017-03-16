package org.drools.compiler;

/**
 * non-ASCII class name. "Address" in Japanese
 */
public class 住所 {

    private String 郵便番号; // "zipCode" in Japanese

    public 住所() {
    }

    public 住所(String 郵便番号) {
        this.郵便番号 = 郵便番号;
    }

    public String get郵便番号() {
        return 郵便番号;
    }

    public void set郵便番号(String 郵便番号) {
        this.郵便番号 = 郵便番号;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((郵便番号 == null) ? 0 : 郵便番号.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        住所 other = (住所) obj;
        if (郵便番号 == null) {
            if (other.郵便番号 != null)
                return false;
        } else if (!郵便番号.equals(other.郵便番号))
            return false;
        return true;
    }
}
