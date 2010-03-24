package org.drools.verifier.report.html;

import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
class UrlFactory {

    public static final String THIS_FOLDER                 = ".";
    public static final String PREVIOUS_FOLDER             = "..";

    public static final String SOURCE_FOLDER               = "report";
    public static final String OBJECT_TYPE_FOLDER          = "objectTypes";
    public static final String FIELD_FOLDER                = "fields";
    public static final String RULE_FOLDER                 = "rules";
    public static final String PACKAGE_FOLDER              = "packages";
    public static final String CSS_FOLDER                  = "css";

    public static final String CSS_BASIC                   = "basic.css";

    public static final String IMAGES_FOLDER               = "images";

    public static final String HTML_FILE_INDEX             = "index.htm";
    public static final String HTML_FILE_PACKAGES          = "packages.htm";
    public static final String HTML_FILE_VERIFIER_MESSAGES = "verifierMessages.htm";

    /**
     * Finds a link to object if one exists.
     * 
     * @param o
     *            Object that might have a page that can be linked.
     * @return Link to objects page or the toString() text if no link could not
     *         be created.
     */
    public static String getUrl(Object o) {
        if ( o instanceof VerifierRule ) {
            VerifierRule rule = (VerifierRule) o;
            return getRuleUrl( UrlFactory.RULE_FOLDER,
                               rule.getPath(),
                               rule.getName() );
        }

        return o.toString();
    }

    static String getRuleUrl(String sourceFolder,
                             String ruleId,
                             String ruleName) {
        return "<a href=\"" + sourceFolder + "/" + RULE_FOLDER + "/" + ruleId + ".htm\">" + ruleName + "</a>";
    }

}
