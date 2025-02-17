package qna.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qna.domain.*;
import qna.exception.CannotDeleteException;
import qna.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private DeleteHistoryService deleteHistoryService;

    public QnaService(QuestionRepository questionRepository, AnswerRepository answerRepository, DeleteHistoryService deleteHistoryService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.deleteHistoryService = deleteHistoryService;
    }

    @Transactional(readOnly = true)
    public Question findQuestionById(Long id) {
        return questionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteQuestion(User loginUser, Long questionId) throws CannotDeleteException {
        Question question = findQuestionById(questionId);
        Answers answerList = new Answers(answerRepository.findByQuestionIdAndDeletedFalse(questionId));

        validateDeletePermission(loginUser, question, answerList);
        deleteAll(question, answerList);

        List<DeleteHistory> deleteHistories = createDeleteHistories(question, answerList);
        deleteHistoryService.saveAll(deleteHistories);
    }

    private void hasDeleteAnswerPermission(User loginUser, Answers answerList) {
        for (Answer answer : answerList.getAnswers()) {
            if (!answer.isOwner(loginUser)) {
                throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
            }
        }
    }

    private void hasDeleteQuestionPermission(User loginUser, Question question) {
        if (!question.isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
    }

    private void validateDeletePermission(User loginUser, Question question, Answers answerList) {
        hasDeleteAnswerPermission(loginUser, answerList);
        hasDeleteQuestionPermission(loginUser, question);
    }

    private void deleteAll(Question question, Answers answerList) {
        answerList.delete();
        question.delete();
    }

    private List<DeleteHistory> createDeleteHistories(Question question, Answers answerList) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();

        deleteHistories.addAll(
                answerList.getAnswers().stream()
                        .map(answer -> new DeleteHistory(
                                ContentType.ANSWER,
                                answer.getId(),
                                answer.getWriter(),
                                LocalDateTime.now()))
                        .toList()
        );

        deleteHistories.add(new DeleteHistory(
                ContentType.QUESTION,
                question.getId(),
                question.getWriter(),
                LocalDateTime.now()
        ));

        return deleteHistories;
    }
}
