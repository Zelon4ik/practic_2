# Схеми Бази Даних для Підсистеми Моніторингу Навчального Застосунку

## 1. Концептуальна схема (Нотація Пітера Чена)

У цій нотації сутності зображені прямокутниками, їх атрибути — овалами, а зв'язки — ромбами. 
Тут ми маємо зв'язки «Багато-до-Багатьох» (M:M), які на концептуальному етапі показані напряму. Атрибути, що виникають внаслідок взаємодії (наприклад, результат гри), прив'язані до самого ромбу зв'язку.

```mermaid
flowchart TD
    %% Стилізація
    classDef entity fill:#bbf,stroke:#333,stroke-width:2px;
    classDef attribute fill:#fff,stroke:#333,stroke-width:1px,rx:20,ry:20;
    classDef relationship fill:#ff9,stroke:#333,stroke-width:2px,shape:diamond;

    %% Сутності
    U[Користувач]:::entity
    G[Гра_Активність]:::entity
    S[Предмет_Тема]:::entity
    A[Досягнення]:::entity
    Q[Завдання_Питання]:::entity

    %% Атрибути Користувача
    U_id([ID]):::attribute --- U
    U_n([username]):::attribute --- U
    U_e([email]):::attribute --- U

    %% Атрибути Гри
    G_id([ID]):::attribute --- G
    G_t([title]):::attribute --- G
    G_m([max_score]):::attribute --- G

    %% Атрибути Завдання
    Q_id([ID]):::attribute --- Q
    Q_text([question_text]):::attribute --- Q
    Q_ans([correct_answer]):::attribute --- Q

    %% Атрибути Предмету
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
    
    %% Атрибути зв'язку "Проходить" (те, що пізніше стане таблицею Session)
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

На етапі логічного проектування зв'язки «Багато-до-Багатьох» (M:M) розбиваються на два зв'язки «Один-до-Багатьох» (1:M) за допомогою **асоціативних таблиць** (перетинів). Саме тут з'являються `GAME_SESSIONS` (зберігає статистику проходження конкретної гри конкретним юзером) та `USER_ACHIEVEMENTS` (зберігає отримані досягнення).

```mermaid
erDiagram
    USERS ||--o{ GAME_SESSIONS : "проходить (1:M)"
    USERS ||--o{ USER_ACHIEVEMENTS : "отримує (1:M)"
    GAMES ||--o{ GAME_SESSIONS : "має записи (1:M)"
    GAMES ||--o{ QUESTIONS : "має завдання (1:M)"
    SUBJECTS ||--o{ GAMES : "містить (1:M)"
    ACHIEVEMENTS ||--o{ USER_ACHIEVEMENTS : "присвоюється (1:M)"

    USERS {
        int id PK
        varchar username
        varchar email
        timestamp created_at
    }
    SUBJECTS {
        int id PK
        varchar name
        text description
    }
    GAMES {
        int id PK
        int subject_id FK
        varchar title
        int max_score
    }
    QUESTIONS {
        int id PK
        int game_id FK
        varchar question_text
        varchar correct_answer
    }
    GAME_SESSIONS {
        int id PK
        int user_id FK
        int game_id FK
        int score
        int mistakes_count
        timestamp completed_at
    }
    ACHIEVEMENTS {
        int id PK
        varchar name
        varchar requirement_desc
    }
    USER_ACHIEVEMENTS {
        int user_id PK,FK
        int achievement_id PK,FK
        timestamp earned_at
    }
```
