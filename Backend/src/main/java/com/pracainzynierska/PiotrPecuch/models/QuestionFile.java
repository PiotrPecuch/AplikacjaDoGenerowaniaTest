package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String filename;

    private String contentType;

    @Lob
    @Column(columnDefinition="MEDIUMBLOB",updatable = false) // do 16MB
    private byte[]  imageData;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "question_id")
    Question question;

    public QuestionFile(String filename, String contentType, byte[] imageData, Question question) {
        this.filename = filename;
        this.contentType = contentType;
        this.imageData = imageData;
        this.question = question;
    }
}
