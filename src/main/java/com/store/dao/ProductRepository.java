package com.store.dao;

import com.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByCategoryAsc();

    @Query(nativeQuery = true, value = "select category, sum(stock_cnt) stockCnt from Product group by category")
    List<Object[]> findStockCntGroupByCategory();

}
