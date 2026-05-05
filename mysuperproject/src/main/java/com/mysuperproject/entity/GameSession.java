package com.mysuperproject.entity;

import com.mysuperproject.annotation.Column;
import com.mysuperproject.annotation.Id;
import com.mysuperproject.annotation.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "game_sessions")
public class GameSession {
    @Id(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "mistakes_count")
    private Integer mistakesCount;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
