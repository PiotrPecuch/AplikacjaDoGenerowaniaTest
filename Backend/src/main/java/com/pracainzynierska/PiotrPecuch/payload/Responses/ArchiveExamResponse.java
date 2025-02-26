package com.pracainzynierska.PiotrPecuch.payload.Responses;

import com.pracainzynierska.PiotrPecuch.models.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveExamResponse {
    LocalDateTime startDate;
    LocalDateTime endDate;
    Double sumOfPoints;
    Long indexNumber;
    String firstName;
    String lastName;
    List<UserQuestionAndAnswersResponse> uqaaList;
    List<Question> questionList;
}
