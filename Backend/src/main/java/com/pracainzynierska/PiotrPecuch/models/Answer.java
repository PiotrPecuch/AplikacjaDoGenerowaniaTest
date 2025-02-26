package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "answer")
@Table(name = "answer")
@Getter
@Setter
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long answerId;

    @Column(length = 1024)
    String answerContent;

    @Column(updatable = false)
    Boolean correct;

    @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL)
    private AnswerFile answerFiles;



    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="question_id")
    private Question question;

    public Answer(String answerContent, Boolean correct, Question question) {
        this.answerContent = answerContent;
        this.correct = correct;
        this.question = question;
    }

}
