package com.pracainzynierska.PiotrPecuch.payload.Responses;


import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.AnswerFile;
import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.QuestionFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAndAnswerResponse {
    Question question;
    List<Answer> answer;
}
