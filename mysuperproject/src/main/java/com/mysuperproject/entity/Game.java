package com.mysuperproject.entity;

import com.mysuperproject.annotation.Column;
import com.mysuperproject.annotation.Id;
import com.mysuperproject.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "games")
public class Game {
    @Id(name = "id")
    private Integer id;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "title")
    private String title;

    @Column(name = "max_score")
    private Integer maxScore;
}
