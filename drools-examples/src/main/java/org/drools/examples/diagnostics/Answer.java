package org.drools.examples.diagnostics;

public class Answer {
    private Question question;
    private String answer;

    public Answer(Question question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Answer{" +
               "question=" + question +
               ", answer='" + answer + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Answer answer1 = (Answer) o;

        if (!answer.equals(answer1.answer)) { return false; }
        if (!question.equals(answer1.question)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = question.hashCode();
        result = 31 * result + answer.hashCode();
        return result;
    }
}
