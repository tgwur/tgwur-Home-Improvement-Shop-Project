package com.store.controller;

import com.store.dao.SystemUserRepository;
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
@RequestMapping("/user")
public class SystemUserController {

    @Autowired
    private SystemUserRepository systemUserRepository;

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
        if (!user.getPrivilege().equals("1")){
            throw new Exception("permission denied");
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10);
        Page<SystemUser> pageList = userRepository.findAll(pageable);
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "user";
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
        userRepository.deleteById(Long.parseLong(id));
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
        if (!user.getPrivilege().equals("1")){
            throw new Exception("permission denied");
        }
        SystemUser u;
        if (id.equals("add")){
            u = new SystemUser();
            u.setCreatedBy(uid);
            u.setCreateTime(new Date());
        }else {
            u = userRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("user not found"));
        }
        model.addAttribute("user", user);
        model.addAttribute("u", u);
        return "user_detail";
    }

    @PostMapping({"/save"})
    public String save(Model model, HttpSession session, @ModelAttribute SystemUser u) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        if (!user.getPrivilege().equals("1")){
            throw new Exception("permission denied");
        }
        u.setUpdatedBy(uid);
        u.setUpdateTime(new Date());
        userRepository.save(u);
        return list(model, session, "0");
    }

}
