package com.pracainzynierska.PiotrPecuch.payload.Responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminAllUsersResponse {
    String userEmail;
}
