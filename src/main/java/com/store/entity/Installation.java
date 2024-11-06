package com.store.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
public class Installation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "price")
    Float price;

    @Column(name = "cost")
    Float cost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    SystemUser user;

    @Column(name = "date")
    String date;

    @Column(name = "status")
    String status;

    @Column(name = "from_time")
    String fromTime;

    @Column(name = "to_time")
    String toTime;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

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

}
