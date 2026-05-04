package com.mysuperproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {
    private Integer id;
    private String name;
    private String requirementDesc;
}
