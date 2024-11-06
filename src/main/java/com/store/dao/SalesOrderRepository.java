package com.store.dao;

import com.store.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    @Query(nativeQuery = true, value = "select sum(op.quantity*p.price) totalSalesAmount from Product p join Order_Product op on p.id = op.product_id")
    Float findTotalSalesAmount();

    @Query(nativeQuery = true, value = "select sum(op.quantity*p.cost) totalCostAmount from Product p join Order_Product op on p.id = op.product_id")
    Float findTotalCostAmount();

    @Query(nativeQuery = true, value = "select to_char(op.create_time,'yyyy-mm-dd') order_date, sum(op.quantity*p.cost) totalCostAmount, sum(op.quantity*p.price) totalSalesAmount from Product p join Order_Product op on p.id = op.product_id group by to_char(op.create_time,'yyyy-mm-dd') order by to_char(op.create_time,'yyyy-mm-dd')")
    List<Object[]> findSalesAmountByDate();
}
