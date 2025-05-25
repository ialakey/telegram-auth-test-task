package com.alakey.telegram_auth_test_task.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTelegram {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String username;
}