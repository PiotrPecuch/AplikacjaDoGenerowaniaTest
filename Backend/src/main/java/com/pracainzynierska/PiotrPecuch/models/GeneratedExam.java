package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "generated_exam")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "exam")
    private Exam exam;

    String examDescription;


    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer soultionTime;

    @JsonBackReference
    @OneToMany(mappedBy = "generatedExam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamStudentsInformations> examStudentsInformations;

}
