package org.drools.template.parser;

/**
 * <a href="stevearoonie@gmail.com">Steven Williams</a>
 * Generate the rules for a decision table
 */
public interface Generator {

    void generate(String templateName, Row row);

    String getDrl();

}
