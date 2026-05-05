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
@Table(name = "subjects")
public class Subject {
    @Id(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
