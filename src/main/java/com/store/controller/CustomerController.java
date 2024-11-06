package com.store.controller;

import com.store.dao.CustomerRepository;
import com.store.dao.SystemUserRepository;
import com.store.entity.Customer;
import com.store.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SystemUserRepository userRepository;

    @GetMapping({"", "/"})
    public String list(Model model, HttpSession session, @RequestParam(name = "page", required = false, defaultValue = "0") String page) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10);
        Page<Customer> pageList = customerRepository.findAll(pageable);
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "customer";
    }

    @GetMapping({"/delete"})
    public String delete(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        customerRepository.deleteById(Long.parseLong(id));
        return list(model, session, "0");
    }

    @GetMapping({"/detail"})
    public String detail(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        Customer customer;
        if (id.equals("add")){
            customer = new Customer();
            customer.setCreatedBy(uid);
            customer.setCreateTime(new Date());
        }else {
            customer = customerRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("customer not found"));
        }
        model.addAttribute("user", user);
        model.addAttribute("customer", customer);
        return "customer_detail";
    }

    @PostMapping({"/save"})
    public String save(Model model, HttpSession session, @ModelAttribute Customer customer) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        customer.setUpdatedBy(uid);
        customer.setUpdateTime(new Date());
        customerRepository.save(customer);
        return list(model, session, "0");
    }


}
