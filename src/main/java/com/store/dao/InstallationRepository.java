package com.store.dao;

import com.store.entity.Installation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstallationRepository extends JpaRepository<Installation, Long> {

    @Query(nativeQuery = true, value = "select sum(price) totalSalesAmount from Installation")
    Float findTotalSalesAmount();

    @Query(nativeQuery = true, value = "select sum(cost) totalCostAmount from Installation")
    Float findTotalCostAmount();

    @Query(nativeQuery = true, value = "select * from Installation where date = ?1 and to_timestamp(?3, 'HH24:MI') > to_timestamp(from_time, 'HH24:MI') and to_timestamp(?2, 'HH24:MI') < to_timestamp(to_time, 'HH24:MI') and id != ?4 and user_id = ?5")
    List<Installation> findConflictTime(String Date, String startTime, String endTime, Long id, Long employeeId);

}
