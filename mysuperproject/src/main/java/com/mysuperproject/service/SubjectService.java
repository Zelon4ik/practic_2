package com.mysuperproject.service;

import com.mysuperproject.dao.SubjectDao;
import com.mysuperproject.entity.Subject;
import java.util.List;
import java.util.Optional;

public class SubjectService {
    private final SubjectDao subjectDao = new SubjectDao();

    public Subject createSubject(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Назва предмету не може бути порожньою");
        }
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        return subjectDao.save(subject);
    }

    public Optional<Subject> getSubjectById(int id) {
        return subjectDao.findById(id);
    }

    public List<Subject> getAllSubjects() {
        return subjectDao.findAll();
    }

    public void updateSubject(Subject subject) {
        if (subject == null || subject.getId() == null) {
            throw new IllegalArgumentException("Некоректні дані предмету для оновлення");
        }
        subjectDao.update(subject);
    }

    public void deleteSubject(int id) {
        subjectDao.delete(id);
    }
}
