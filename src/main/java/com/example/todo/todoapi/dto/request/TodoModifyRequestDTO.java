package com.example.todo.todoapi.dto.request;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TodoModifyRequestDTO {


    @NotBlank
    private String id;

    private boolean done;


}
