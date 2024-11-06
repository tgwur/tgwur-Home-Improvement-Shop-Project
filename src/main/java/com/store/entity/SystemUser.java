package com.store.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username", unique = true, nullable = false)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "phone")
    String phone;

    @Column(name = "privilege")
    String privilege;

    @Column(name = "create_time")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @Column(name = "created_by")
    Long createdBy;

    @Column(name = "update_time")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    @Column(name = "updated_by")
    Long updatedBy;

    public SystemUser(){

    }

    public SystemUser(Long uid){
        this.id = uid;
    }

//    @Transient
//    @Autowired
//    private UserRepository userRepository;
//
//    @Transient
//    @Autowired
//    private SessionManageRepository sessionManageRepository;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<UserGrantedAuthority> list = new ArrayList<>();
//        UserGrantedAuthority ua = new UserGrantedAuthority();
//        ua.authority = userRepository.findPrivilegeById(id);
//        list.add(ua);
//        return list;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        SessionManage session = sessionManageRepository.findSessionStatus1ByUserId(id);
//        Date lastUpdateTime = session.getUpdateTime();
//        long time = new Date().getTime() - lastUpdateTime.getTime();
//        if (time*1000*60>30){
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
