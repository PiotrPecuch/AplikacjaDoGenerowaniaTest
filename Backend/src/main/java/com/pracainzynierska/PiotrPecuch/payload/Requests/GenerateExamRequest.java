package com.pracainzynierska.PiotrPecuch.payload.Requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Getter
@Setter
public class GenerateExamRequest {
    private String examName;
    private MultipartFile studentList;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private Integer solutionTime;
    private Integer numberOfQuestions;

}
