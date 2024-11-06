package com.store.dao;

import com.store.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.rewardPoint >= 100")
    List<Customer> findRewardPointGT100Customer();

}
