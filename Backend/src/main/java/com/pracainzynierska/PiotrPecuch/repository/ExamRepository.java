package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@EnableJpaRepositories
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Modifying
    @Query("DELETE FROM Exam e WHERE e.user = ?1")
    void deleteExamsByUser(User user);

    Optional<Exam> findByExamNameAndUser (String name, User user);
    List<Exam> findAllByUserOrderByCreationDateDescCreationTimeDesc(User user);

    List<Exam> findAllByUser (User user);

    @Modifying
    @Query(value = "delete from answer where question_id in (select question_id from question where exam_id =?1 )",nativeQuery = true)
    void deleteAnswerByExamId(Long examId);

    @Modifying
    @Query(value = "DELETE FROM question WHERE exam_id = ?1", nativeQuery = true)
    void deleteQuestionsByExamId(Long examId);

    @Modifying
    @Query(value = "DELETE FROM exam WHERE exam_id = ?1", nativeQuery = true)
    void deleteExamById(Long examId);

    Boolean existsExamByExamNameAndUser (String name, User user);

    Exam findExamByExamNameAndUser(String examName, User user);


}
