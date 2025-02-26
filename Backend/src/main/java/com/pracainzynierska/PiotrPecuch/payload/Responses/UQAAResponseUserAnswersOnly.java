package com.pracainzynierska.PiotrPecuch.payload.Responses;

import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UQAAResponseUserAnswersOnly {
    Question question;
    Double points;
    Double percentageOfPoints;
    List<Answer> userAnswers;
}
