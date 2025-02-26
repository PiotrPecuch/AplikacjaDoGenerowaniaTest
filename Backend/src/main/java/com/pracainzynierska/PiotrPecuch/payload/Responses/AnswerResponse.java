package com.pracainzynierska.PiotrPecuch.payload.Responses;

import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.QuestionFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnswerResponse {
    private List<Answer> answers;
    private byte[] questionFile;
    private String questionFileType;
}
