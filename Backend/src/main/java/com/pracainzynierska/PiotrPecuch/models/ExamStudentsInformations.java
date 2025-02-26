package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "exam_students_informations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamStudentsInformations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String ssoLogin;

    String ssoPassword;

    Long indexNumber;

    String studentName;
    String studentLastName;

    Boolean started;

    Boolean cheated;

    Boolean finished;

    Double pointsFromExam;

    Double percentageOfPoints;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "generated_exam",referencedColumnName = "id")
    GeneratedExam generatedExam;

    @OneToMany(mappedBy = "examStudentsInformations", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<UserQuestionsAndAnswers> userQuestionsAndAnswers;


}
