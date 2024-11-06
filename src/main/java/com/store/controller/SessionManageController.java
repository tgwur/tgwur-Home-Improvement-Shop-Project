package com.store.controller;

import com.store.dao.SessionManageRepository;
import com.store.dao.SystemUserRepository;
import com.store.entity.SessionManage;
import com.store.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/session")
public class SessionManageController {

    @Autowired
    private SessionManageRepository sessionManageRepository;

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
        Page<SessionManage> pageList = sessionManageRepository.findAll(pageable);
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "session";
    }


}
