package com.alakey.telegram_auth_test_task.repository;


import com.alakey.telegram_auth_test_task.model.UserTelegram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserTelegram, Long> {
}
