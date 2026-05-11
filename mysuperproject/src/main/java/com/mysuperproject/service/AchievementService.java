package com.mysuperproject.service;

import com.mysuperproject.dao.AchievementDao;
import com.mysuperproject.entity.Achievement;
import java.util.List;
import java.util.Optional;

public class AchievementService {
    private final AchievementDao achievementDao = new AchievementDao();

    public Achievement createAchievement(String name, String requirementDesc) {
        if (name == null
                || name.isEmpty()
                || requirementDesc == null
                || requirementDesc.isEmpty()) {
            throw new IllegalArgumentException("Дані досягнення не можуть бути порожніми");
        }
        Achievement achievement = new Achievement();
        achievement.setName(name);
        achievement.setRequirementDesc(requirementDesc);
        return achievementDao.save(achievement);
    }

    public Optional<Achievement> getAchievementById(int id) {
        return achievementDao.findById(id);
    }

    public List<Achievement> getAllAchievements() {
        return achievementDao.findAll();
    }

    public void updateAchievement(Achievement achievement) {
        if (achievement == null || achievement.getId() == null) {
            throw new IllegalArgumentException("Некоректні дані досягнення для оновлення");
        }
        achievementDao.update(achievement);
    }

    public void deleteAchievement(int id) {
        achievementDao.delete(id);
    }
}
