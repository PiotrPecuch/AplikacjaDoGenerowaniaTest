package com.pracainzynierska.PiotrPecuch.payload.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndExamResponse {
    Double maxPointsFromExam;
    Double points;
    Double pointsPercentage;
    Double grade;
    String examDescription;
}
