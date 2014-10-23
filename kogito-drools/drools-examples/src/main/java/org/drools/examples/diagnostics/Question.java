package org.drools.examples.diagnostics;

public class Question {
    private String    id;
    private String text;

    public Question(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public void setBooleanAnswer(String booleanAnswer) {
//        if ( booleanAnswer.trim().equals("y") ) {
//            this.booleanAnswer = true;
//        } else if ( booleanAnswer.trim().equals("y") ) {
//            this.booleanAnswer = false;
//        } else {
//            throw new RuntimeException("String number be 'y' or 'c' it was " + booleanAnswer );
//        }
//    }

    @Override
    public String toString() {
        return "Question{" +
               "id=" + id +
               ", text='" + text + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Question question = (Question) o;

        if (!id.equals(question.id)) { return false; }
        if (!text.equals(question.text)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
