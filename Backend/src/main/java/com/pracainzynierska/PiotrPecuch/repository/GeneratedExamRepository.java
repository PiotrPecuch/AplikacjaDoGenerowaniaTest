package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.GeneratedExam;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
@Transactional
public interface GeneratedExamRepository extends JpaRepository<GeneratedExam, Long> {

    @Query(value = "SELECT * FROM generated_exam ge INNER JOIN exam e ON ge.exam = e.exam_id " +
            "WHERE ge.end_date  > SYSDATE()\n" +
            "AND e.`user` = ?1 ORDER BY id DESC", nativeQuery = true)
    List<GeneratedExam> getStartedExamsByUserId(Long userId);


    @Query(value = "SELECT * FROM generated_exam ge INNER JOIN exam e ON ge.exam = e.exam_id " +
            "WHERE ge.end_date  < SYSDATE()\n" +
            "AND e.`user` = ?1", nativeQuery = true)
    List<GeneratedExam> getFinishedExamByUserId(Long userId);

    void deleteByExam(Exam exam);

    List<GeneratedExam> findByExam(Exam exam);
}
