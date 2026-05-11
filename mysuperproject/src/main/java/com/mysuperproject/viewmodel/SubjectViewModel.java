package com.mysuperproject.viewmodel;

import com.mysuperproject.entity.Subject;
import com.mysuperproject.service.SubjectService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class SubjectViewModel {
    private final SubjectService subjectService = new SubjectService();
    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();
    private final FilteredList<Subject> filteredSubjects;

    private final StringProperty searchQuery = new SimpleStringProperty("");

    public SubjectViewModel() {
        refresh();

        filteredSubjects = new FilteredList<>(subjects, p -> true);

        // Додаємо слухача до поля пошуку
        searchQuery.addListener(
                (observable, oldValue, newValue) -> {
                    filteredSubjects.setPredicate(
                            subject -> {
                                // Якщо поле пошуку порожнє, показуємо всі записи
                                if (newValue == null || newValue.isEmpty()) {
                                    return true;
                                }

                                String lowerCaseFilter = newValue.toLowerCase();

                                // Фільтруємо за назвою або описом
                                if (subject.getName() != null
                                        && subject.getName()
                                                .toLowerCase()
                                                .contains(lowerCaseFilter)) {
                                    return true;
                                } else if (subject.getDescription() != null
                                        && subject.getDescription()
                                                .toLowerCase()
                                                .contains(lowerCaseFilter)) {
                                    return true;
                                }
                                return false;
                            });
                });
    }

    public void refresh() {
        subjects.setAll(subjectService.getAllSubjects());
    }

    public FilteredList<Subject> getFilteredSubjects() {
        return filteredSubjects;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public void addSubject(String name, String description) {
        subjectService.createSubject(name, description);
        refresh();
    }

    public void updateSubject(Subject subject, String newName, String newDescription) {
        subject.setName(newName);
        subject.setDescription(newDescription);
        subjectService.updateSubject(subject);
        refresh();
    }

    public void deleteSubject(Subject subject) {
        subjectService.deleteSubject(subject.getId());
        refresh();
    }
}
