package com.pracainzynierska.PiotrPecuch.payload.Requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class AnswerDTO {
    private String answerContent;
    private MultipartFile attachment;
    private Boolean correct;
}
