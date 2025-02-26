package com.pracainzynierska.PiotrPecuch.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer_file")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String filename;

    private String contentType;

    @Lob
    @Column(columnDefinition="MEDIUMBLOB",updatable = false) // do 16MB
    private byte[]  data;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "answer_id")
    Answer answer;


    public AnswerFile(String filename, String contentType, byte[] data, Answer answer) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.answer = answer;
    }
}
