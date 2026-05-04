package com.mysuperproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    private Integer id;
    private Integer subjectId;
    private String title;
    private Integer maxScore;
}
