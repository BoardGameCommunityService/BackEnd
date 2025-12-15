package com.boardmate.repository;

import com.boardmate.domain.notification.NotificationSetting;
import com.boardmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUser(User user);

    Optional<NotificationSetting> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
