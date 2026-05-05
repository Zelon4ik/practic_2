package com.mysuperproject.dao;

import com.mysuperproject.entity.Subject;

public class SubjectDao extends GenericDao<Subject, Integer> {
    public SubjectDao() {
        super(Subject.class);
    }
}
