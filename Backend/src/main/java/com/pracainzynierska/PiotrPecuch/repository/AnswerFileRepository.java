package com.pracainzynierska.PiotrPecuch.repository;


import com.pracainzynierska.PiotrPecuch.models.AnswerFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface AnswerFileRepository extends JpaRepository<AnswerFile, Long> {
}
