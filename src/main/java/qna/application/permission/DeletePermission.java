package qna.application.permission;

import qna.domain.Answer;
import qna.domain.Answers;
import qna.domain.Question;
import qna.domain.User;
import qna.exception.CannotDeleteException;

public class DeletePermission {

    private static void hasDeleteAnswerPermission(User loginUser, Answers answerList) {
        for (Answer answer : answerList.getAnswers()) {
            if (!answer.isOwner(loginUser)) {
                throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
            }
        }
    }

    private static void hasDeleteQuestionPermission(User loginUser, Question question) {
        if (!question.isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
    }

    public static void validateDeletePermission(User loginUser, Question question, Answers answerList) {
        hasDeleteAnswerPermission(loginUser, answerList);
        hasDeleteQuestionPermission(loginUser, question);
    }
}
