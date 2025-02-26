package com.pracainzynierska.PiotrPecuch.repository;

import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.QuestionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface QuestionFileRepository extends JpaRepository<QuestionFile, Long> {
    QuestionFile findByQuestion(Question question);
}
