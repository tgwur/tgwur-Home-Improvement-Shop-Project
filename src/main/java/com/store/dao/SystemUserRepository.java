package com.store.dao;

import com.store.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    @Query("select u from SystemUser u where u.username = ?1 and u.password = ?2 and u.privilege = ?3")
    SystemUser findByUsernameAndPasswordAndPrivilege(String username, String password, String privilege);

    @Query("select privilege from SystemUser where id = ?1")
    String findPrivilegeById(Long id);

    @Query("select u from SystemUser u where u.username = ?1")
    SystemUser findByUsername(String username);

}
