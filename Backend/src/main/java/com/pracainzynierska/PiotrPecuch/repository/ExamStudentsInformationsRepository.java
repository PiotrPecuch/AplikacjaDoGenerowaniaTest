package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.GeneratedExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamStudentsInformationsRepository extends JpaRepository<ExamStudentsInformations, Integer> {
    List<ExamStudentsInformations> findByGeneratedExam(GeneratedExam exam);


    ExamStudentsInformations findBySsoLoginAndSsoPasswordAndGeneratedExam(String ssoLogin, String ssoPasswordA, GeneratedExam generatedExam);
    Optional<ExamStudentsInformations> findBySsoLoginAndSsoPassword(String ssoLogin, String ssoPasswordB);
    Boolean existsBySsoLoginAndSsoPassword(String ssoLogin, String ssoPassword);
}
