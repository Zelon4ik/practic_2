# Додаток 3. UML діаграма класів

У цій діаграмі відображена реальна об'єктно-орієнтована архітектура нашого програмного комплексу, що зв'язує графічні вікна JavaFX (Views), сервісний шар бізнес-логіки (`GameService`) та класи доступу до бази даних (DAOs).

```mermaid
classDiagram
    class App {
        -Stage primaryStage
        +start(Stage stage) void
        +showLoginView() void
        +showAdminView() void
        +showStudentDashboard(User user) void
        +stop() void
        +main(String[] args) void
    }

    class GameService {
        -UserDao userDao
        -SubjectDao subjectDao
        -GameDao gameDao
        -QuestionDao questionDao
        -AchievementDao achievementDao
        -ScoreRecordDao scoreRecordDao
        -UserMistakeDao userMistakeDao
        +authenticate(String username, String password) User
        +registerStudent(String username, String password) User
        +getAllSubjects() List~Subject~
        +getGamesBySubject(int subjectId) List~Game~
        +getQuestionsByGame(int gameId) List~Question~
        +submitGameResults(User user, int gameId, int score, int correct, int total) void
        +getLeaderboard() List~ScoreRecord~
        +getMistakes(int userId) List~UserMistake~
        +addMistake(int userId, String text, String ans) void
        +removeMistake(int id) void
        +getAchievements(int userId) List~Achievement~
        +checkAndAwardAchievements(User user) void
    }

    class UserDao {
        +findByUsername(String username) User
        +save(User user) void
        +getAllStudents() List~User~
    }

    class UserMistakeDao {
        +findByUserId(int userId) List~UserMistake~
        +save(UserMistake mistake) void
        +delete(int id) void
    }

    class ScoreRecordDao {
        +save(ScoreRecord record) void
        +getLeaderboard() List~ScoreRecord~
        +getPlayCount(int userId) int
    }

    class StudentDashboardView {
        -User currentUser
        -GameService gameService
        +getView() Parent
    }

    class GamePlayView {
        -User currentUser
        -Game currentGame
        -GameService gameService
        +getView() Parent
    }

    class LoginView {
        -GameService gameService
        +getView() Parent
    }

    class AdminView {
        -GameService gameService
        +getView() Parent
    }

    App --> LoginView : shows
    App --> StudentDashboardView : shows
    App --> AdminView : shows
    StudentDashboardView --> GamePlayView : opens
    LoginView --> GameService : uses
    StudentDashboardView --> GameService : uses
    GamePlayView --> GameService : uses
    AdminView --> GameService : uses
    GameService --> UserDao : uses
    GameService --> UserMistakeDao : uses
    GameService --> ScoreRecordDao : uses
```
