package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Question;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface QuestionRepository extends JpaRepository<Question,Long> {

    List<Question> findAllByExam (Exam exam);

    void deleteByExam (Exam exam);
    @Modifying
    @Transactional
    @Query(value = "insert into question(question_content, exam_id) values (?1, ?2)",nativeQuery = true)
    Optional<Question> saveQuestionToExam(String questionContent, Long examId);

    Optional<Question> findByExamAndQuestionContent(Exam exam, String questionContent);
    Optional<Question> findByExamAndQuestionContentAndQuestionId(Exam exam, String questionContent, Long questionId);

    Integer countByExam(Exam exam);




}
