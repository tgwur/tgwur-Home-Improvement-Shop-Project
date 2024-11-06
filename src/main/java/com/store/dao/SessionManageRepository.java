package com.store.dao;

import com.store.entity.SessionManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SessionManageRepository extends JpaRepository<SessionManage, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update SessionManage set status = '0' where createdBy = ?1")
    void updateSessionStatus0ByUserId(Long userId);

    @Query("select sm from SessionManage sm where sm.status = '1' and createdBy = ?1")
    SessionManage findSessionStatus1ByUserId(Long userId);

}
