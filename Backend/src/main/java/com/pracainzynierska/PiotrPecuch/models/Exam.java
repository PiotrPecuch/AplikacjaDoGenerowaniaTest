package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exam")
@Getter
@Setter
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonBackReference
    Long examId;

    String examName;

    LocalDate creationDate;
    Time creationTime;

    LocalDate modificationDate;
    Time modificationTime;

    @OneToMany(mappedBy = "exam",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Question> questionList;

    @JsonBackReference
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedExam> generatedExams;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="user",referencedColumnName = "id")
    private User user;

}
