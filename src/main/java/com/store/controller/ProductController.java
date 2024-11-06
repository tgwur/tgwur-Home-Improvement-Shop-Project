package com.store.controller;

import com.store.dao.ProductRepository;
import com.store.dao.SystemUserRepository;
import com.store.entity.Product;
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
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

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
        Page<Product> pageList = productRepository.findAll(pageable);
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "product";
    }

    @GetMapping({"/delete"})
    public String delete(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        if (!user.getPrivilege().equals("1")){
            throw new Exception("permission denied");
        }
        productRepository.deleteById(Long.parseLong(id));
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
        Product product;
        if (id.equals("add")){
            product = new Product();
            product.setCreatedBy(uid);
            product.setCreateTime(new Date());
        }else {
            product = productRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("Product not found"));
        }
        model.addAttribute("user", user);
        model.addAttribute("product", product);
        return "product_detail";
    }

    @PostMapping({"/save"})
    public String save(Model model, HttpSession session, @ModelAttribute Product product) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        if (!user.getPrivilege().equals("1")){
            throw new Exception("permission denied");
        }
        product.setUpdatedBy(uid);
        product.setUpdateTime(new Date());
        productRepository.save(product);
        return list(model, session, "0");
    }

}
