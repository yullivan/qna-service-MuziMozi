package qna.domain;

import jakarta.persistence.Embeddable;
import qna.exception.CannotDeleteException;

import java.util.List;

@Embeddable
public class Answers {

    private final List<Answer> answers;

    public Answers (List<Answer> answerList) {
        this.answers = answerList;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<DeleteHistory> delete() {
        return this.answers.stream().map(answer -> answer.delete()).toList();
    }

}
