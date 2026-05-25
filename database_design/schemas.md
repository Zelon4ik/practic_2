# Схеми Бази Даних для Smart-Тренажера Правопису

## 1. Концептуальна схема (Нотація Пітера Чена)

У цій нотації сутності зображені прямокутниками, їх атрибути — овалами, а зв'язки — ромбами. 
Тут ми маємо зв'язки «Багато-до-Багатьох» (M:M), які на концептуальному етапі показані напряму. Атрибути, що виникають внаслідок взаємодії (наприклад, результат тренування), прив'язані до самого ромбу зв'язку.

```mermaid
flowchart TD
    %% Стилізація
    classDef entity fill:#bbf,stroke:#333,stroke-width:2px;
    classDef attribute fill:#fff,stroke:#333,stroke-width:1px,rx:20,ry:20;
    classDef relationship fill:#ff9,stroke:#333,stroke-width:2px,shape:diamond;

    %% Сутності
    U[Користувач / Студент]:::entity
    G[Практичний Тренажер]:::entity
    S[Розділ Правопису]:::entity
    A[Досягнення]:::entity
    Q[Завдання з правопису]:::entity

    %% Атрибути Користувача
    U_id([ID]):::attribute --- U
    U_n([username]):::attribute --- U
    U_e([email]):::attribute --- U

    %% Атрибути Тренажера
    G_id([ID]):::attribute --- G
    G_t([title]):::attribute --- G
    G_m([max_score]):::attribute --- G

    %% Атрибути Завдання
    Q_id([ID]):::attribute --- Q
    Q_text([question_text]):::attribute --- Q
    Q_ans([correct_answer]):::attribute --- Q

    %% Атрибути Розділу Правопису
    S_id([ID]):::attribute --- S
    S_n([name]):::attribute --- S

    %% Атрибути Досягнення
    A_id([ID]):::attribute --- A
    A_n([name]):::attribute --- A

    %% Зв'язки
    S_G{Містить}:::relationship
    S ---|1| S_G
    S_G ---|N| G

    G_Q{Має}:::relationship
    G ---|1| G_Q
    G_Q ---|N| Q

    U_G{Проходить <br> M:M}:::relationship
    U ---|M| U_G
    U_G ---|N| G
    
    %% Атрибути зв'язку "Проходить" (сесії тренувань)
    Attr1([score]):::attribute --- U_G
    Attr2([mistakes_count]):::attribute --- U_G
    Attr3([completed_at]):::attribute --- U_G

    U_A{Отримує <br> M:M}:::relationship
    U ---|M| U_A
    U_A ---|N| A
    
    %% Атрибут зв'язку "Отримує"
    Attr4([earned_at]):::attribute --- U_A
```

## 2. Логічна схема (Нотація Crow's Foot / Вороняча лапка)

На етапі логічного проектування зв'язки «Багато-до-Багатьох» (M:M) розбиваються на два зв'язки «Один-до-Багатьох» (1:M) за допомогою **асоціативних таблиць** (перетинів). Тут `GAME_SESSIONS` грає роль таблиці сесій тренувань (зберігає статистику проходження конкретного тренажера конкретним юзером), а `USER_ACHIEVEMENTS` — отримані досягнення.

```mermaid
erDiagram
    USERS ||--o{ GAME_SESSIONS : "проходить тренування (1:M)"
    USERS ||--o{ USER_ACHIEVEMENTS : "отримує досягнення (1:M)"
    GAMES ||--o{ GAME_SESSIONS : "має сесії (1:M)"
    GAMES ||--o{ QUESTIONS : "має завдання (1:M)"
    SUBJECTS ||--o{ GAMES : "містить тренажери (1:M)"
    ACHIEVEMENTS ||--o{ USER_ACHIEVEMENTS : "присвоюється студенту (1:M)"

    USERS {
        int id PK
        varchar username
        varchar email
        varchar password
        timestamp created_at
    }
    SUBJECTS {
        int id PK
        varchar name "Назва розділу правопису"
        text description "Детальний опис правил"
    }
    GAMES {
        int id PK
        int subject_id FK "Посилання на розділ правопису"
        varchar title "Назва практичної вправи"
        int max_score "Максимальний бал"
    }
    QUESTIONS {
        int id PK
        int game_id FK "Посилання на практичну вправу"
        varchar question_text "Слово з пропуском для вставки"
        varchar correct_answer "Правильний варіант у форматі ПовнеСлово|Символ"
    }
    GAME_SESSIONS {
        int id PK
        int user_id FK "Посилання на користувача"
        int game_id FK "Посилання на практичну вправу"
        int score "Отримані бали"
        int mistakes_count "Кількість зроблених помилок"
        timestamp completed_at "Дата проходження тренування"
    }
    ACHIEVEMENTS {
        int id PK
        varchar name "Назва досягнення"
        varchar requirement_desc "Умова для отримання досягнення"
    }
    USER_ACHIEVEMENTS {
        int user_id PK,FK
        int achievement_id PK,FK
        timestamp earned_at "Дата отримання досягнення"
    }
```
