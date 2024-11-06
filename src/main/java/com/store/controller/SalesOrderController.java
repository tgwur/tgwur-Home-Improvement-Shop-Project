package com.store.controller;

import com.store.dao.*;
import com.store.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;

@Controller
@RequestMapping("/order")
public class SalesOrderController {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SystemUserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping({"", "/"})
    public String list(Model model, HttpSession session, @RequestParam(name = "page", required = false, defaultValue = "0") String page) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10);
        Page<SalesOrder> pageList = salesOrderRepository.findAll(pageable);
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "order";
    }

    @GetMapping({"/delete"})
    public String delete(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        salesOrderRepository.deleteById(Long.parseLong(id));
        return list(model, session, "0");
    }

    @GetMapping({"/detail"})
    public String detail(Model model, HttpSession session, @RequestParam(name = "id") String id, @RequestParam(name = "item", defaultValue = "1") String item) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        SalesOrder order;
        if (id.equals("add")){
            order = new SalesOrder();
            order.setCreatedBy(uid);
            order.setCreateTime(new Date());
        }else {
            order = salesOrderRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("SalesOrder not found"));
        }
        List<Product> products = productRepository.findAllByOrderByCategoryAsc();
        Map<String, List<Map<String, Object>>> productsMap = new HashMap<>();
        List<Map<String, Object>> list;
        for (Product p:products){
            if (!productsMap.containsKey(p.getCategory())) {
                list = new ArrayList<>();
            }else {
                list = productsMap.get(p.getCategory());
            }
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", p.getId());
            productMap.put("name", p.getName());
            list.add(productMap);
            productsMap.put(p.getCategory(), list);
        }
        List<Customer> customerList = customerRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("productsMap", productsMap);
        model.addAttribute("customerList", customerList);
        model.addAttribute("item", Integer.parseInt(item)<=0?1:Integer.parseInt(item));
        return "order_detail";
    }

    @PostMapping({"/save"})
    @Transactional
    public String save(Model model, HttpSession session, @ModelAttribute SalesOrder order, @RequestParam String[] pids, @RequestParam String[] quantities) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        //save order
        order.setUpdatedBy(uid);
        order.setUpdateTime(new Date());
        order = salesOrderRepository.save(order);
        float totalPrice = 0;
        for (int i=0;i<pids.length;i++){
            //create item list
            OrderProduct orderProduct = new OrderProduct();
            Product product = productRepository.findById(Long.parseLong(pids[i])).orElseThrow(()->new Exception("product not found"));
            //check inventory
            Long inventory = product.getStockCnt();
            if (inventory-Long.parseLong(quantities[i])<0){
                throw new RuntimeException(product.getName() + " not enough stock! only " + inventory + " but input " + quantities[i]);
            }
            orderProduct.setProduct(product);
            orderProduct.setOrder(order);
            orderProduct.setQuantity(Integer.parseInt(quantities[i]));
            orderProduct.setCreatedBy(uid);
            orderProduct.setCreateTime(new Date());
            orderProductRepository.save(orderProduct);
            //caculate total price
            totalPrice += product.getPrice()*Integer.parseInt(quantities[i]);
            //update inventory
            product.setStockCnt(inventory-Long.parseLong(quantities[i]));
            productRepository.save(product);
        }
        //increase customer award point
        Customer customer = customerRepository.findById(order.getCustomer().getId()).orElseThrow(()->new Exception("customer not found"));
        Float awardPoint = totalPrice/100;
        customer.setRewardPoint(customer.getRewardPoint() + awardPoint);
        customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();
        entityManager.close();
        return list(model, session, "0");
    }


}
