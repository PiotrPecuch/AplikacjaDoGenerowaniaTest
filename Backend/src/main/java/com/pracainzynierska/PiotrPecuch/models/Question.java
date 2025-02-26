package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "question")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long questionId;

    @Column(length = 3000)
    String questionContent;

    @Max(50)
    Integer points;

    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Answer> answerList;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuestionFile questionFile;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<UserQuestionsAndAnswers> userQuestionsAndAnswers;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "exam_id")
    private Exam exam;

    public Question(String questionContent, int points, List<Answer> answers, QuestionFile questionFile, Exam exam) {
        this.questionContent = questionContent;
        this.points = points;
        this.answerList = answers;
        this.exam = exam;
        this.questionFile = questionFile;
    }
}
