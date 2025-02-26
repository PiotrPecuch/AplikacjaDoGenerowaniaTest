package com.pracainzynierska.PiotrPecuch.payload.Requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class UserAnswerRequest {
    private List<Object> answers;
    String textAnswer;


}
