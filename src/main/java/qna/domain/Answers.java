package qna.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {
    }

    public Answers (List<Answer> answerList) {
        this.answers = answerList;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void delete() {
        if (answers == null || answers.isEmpty()) return;
        for (Answer answer : answers) {
            answer.delete();
        }
    }

    public List<DeleteHistory> createDeleteHistories(User deleteUser) {
        return this.answers.stream()
                .map(answer -> answer.createDeleteHistory(deleteUser))
                .toList();
    }
}
