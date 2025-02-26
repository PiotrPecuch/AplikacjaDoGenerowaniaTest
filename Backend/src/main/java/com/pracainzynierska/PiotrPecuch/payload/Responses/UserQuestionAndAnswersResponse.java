package com.pracainzynierska.PiotrPecuch.payload.Responses;


import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.odftoolkit.odfdom.type.Points;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestionAndAnswersResponse {
    Long id;
    Question question;
    Double points;
    Double percentageOfPoints;
    List<Answer> userAnswers;
    String userTextAnswer;
    List<Answer> correctAnswers;
}
