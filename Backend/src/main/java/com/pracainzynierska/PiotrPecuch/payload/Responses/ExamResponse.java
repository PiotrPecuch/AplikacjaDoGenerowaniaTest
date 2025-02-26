package com.pracainzynierska.PiotrPecuch.payload.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
    Long examId;
    String examName;
    Time creationTime;
    LocalDate creationDate;
    Time modificationTime;
    LocalDate modificationDate;
    Integer numberOfQuestions;

}
