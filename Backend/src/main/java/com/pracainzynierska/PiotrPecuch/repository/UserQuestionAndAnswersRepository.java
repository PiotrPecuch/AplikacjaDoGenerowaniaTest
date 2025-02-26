package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.UserQuestionsAndAnswers;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface UserQuestionAndAnswersRepository extends JpaRepository<UserQuestionsAndAnswers, Long> {
    List<UserQuestionsAndAnswers> findByQuestion(Question question);
    void deleteByExamStudentsInformations(ExamStudentsInformations examStudentsInformations);

    List<UserQuestionsAndAnswers> findUserQuestionsAndAnswersByExamStudentsInformations(ExamStudentsInformations examStudentsInformations);

    List<UserQuestionsAndAnswers> findUserQuestionsAndAnswersByExamStudentsInformationsId(Long id);

    UserQuestionsAndAnswers findByQuestionAndExamStudentsInformations(Question question, ExamStudentsInformations examStudentsInformations);

    Long countByExamStudentsInformations(ExamStudentsInformations examStudentsInformations);



    @Query(value = """
            select * from user_questions_and_asnwers uqaa where
            exam_students_informations_id = ?1
            and answer_id_list IS null
            and answer_text IS null
            LIMIT 1""", nativeQuery = true)
    Optional<UserQuestionsAndAnswers> getFirstQuestionBySSOLoginAndSSOPassword(Long esiId);

    UserQuestionsAndAnswers getUserQuestionsAndAnswersByExamStudentsInformationsIdAndQuestion(Long examStudentsInformations, Question question);

    Optional<UserQuestionsAndAnswers> findByExamStudentsInformationsIdAndQuestion(Long examStudentsInformations, Question question);
}
