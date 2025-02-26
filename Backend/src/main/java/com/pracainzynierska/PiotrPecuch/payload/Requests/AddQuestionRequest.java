package com.pracainzynierska.PiotrPecuch.payload.Requests;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Setter
@Getter
public class AddQuestionRequest {
    private Long examId;
    private String questionContent;
    private MultipartFile questionImage;
    private Integer questionPoints;
    private List<AnswerDTO> answerList;
}
