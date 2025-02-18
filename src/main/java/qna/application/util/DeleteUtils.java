package qna.application.util;

import qna.application.permission.DeletePermission;
import qna.domain.Answers;
import qna.domain.DeleteHistory;
import qna.domain.Question;
import qna.domain.User;

import java.util.ArrayList;
import java.util.List;

public class DeleteUtils {

    public static List<DeleteHistory> deleteQuestion(User deleteUser, Question question, Answers answerList) {
        DeletePermission.validateDeletePermission(deleteUser, question, answerList);
        deleteAll(question, answerList);
        return createDeleteQuestionHistories(deleteUser, question, answerList);
    }

    private static void deleteAll(Question question, Answers answerList) {
        answerList.delete();
        question.delete();
    }

    private static List<DeleteHistory> createDeleteQuestionHistories(User deleteUser, Question question, Answers answerList) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.addAll(answerList.createDeleteHistories(deleteUser));
        deleteHistories.add(question.createDeleteHistory(deleteUser));

        return deleteHistories;
    }
}
