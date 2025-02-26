package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_questions_and_asnwers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserQuestionsAndAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long userQuestionsAndAnswersId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    String answerIdList;


    @Column(length = 3000)
    String answerText;


    Double points;

    @JsonBackReference
    @ManyToOne
    ExamStudentsInformations examStudentsInformations;
}
