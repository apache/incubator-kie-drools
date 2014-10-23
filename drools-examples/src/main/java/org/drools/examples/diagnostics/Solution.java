package org.drools.examples.diagnostics;

public class Solution {
    private String text;

    public Solution(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Solution solution = (Solution) o;

        if (!text.equals(solution.text)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
