package com.pracainzynierska.PiotrPecuch.payload.Responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminAllInformationResponse {
    String databaseSize;
    Long userCount;
    Long questionCount;
    Long examCount;
}
