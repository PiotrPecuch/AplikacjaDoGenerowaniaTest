package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.Question;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface AnswerRepository extends JpaRepository<Answer,Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into question( answer_content, answer_photo, is_correct, question_id) values (?1,null, ?2,?3)",nativeQuery = true)
    void saveAnswersToQuestion(String answerContent, Boolean isCorrect, Long questionId);

    void deleteAllByQuestion(Question question);

    List<Answer> findAnswerByQuestion(Question question);
    List<Answer> findAllByAnswerIdIn(List<Long> ids);

    @Query(value = "select answer_id from answer where question_id = ?1 AND correct = true",nativeQuery = true)
    List<Long> getPositiveAnswers(Long questionId);

}
