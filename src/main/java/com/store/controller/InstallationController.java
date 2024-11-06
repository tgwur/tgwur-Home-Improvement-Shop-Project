package com.store.controller;

import com.store.dao.CustomerRepository;
import com.store.dao.InstallationRepository;
import com.store.dao.SystemUserRepository;
import com.store.entity.Customer;
import com.store.entity.Installation;
import com.store.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/installation")
public class InstallationController {

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private SystemUserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping({"", "/"})
    public String list(Model model, HttpSession session, @RequestParam(name = "page", required = false, defaultValue = "0") String page) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        //check session
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        //page query installation
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10);
        Page<Installation> pageList;
        if (user.getPrivilege().equals("1")){
            //manager user get all installation
            pageList = installationRepository.findAll(pageable);
        }else{
            //employee user get owner installation
            Installation installation = new Installation();
            installation.setUser(new SystemUser(uid));
            pageList = installationRepository.findAll(Example.of(installation), pageable);
        }
        model.addAttribute("user", user);
        model.addAttribute("pageList", pageList);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("totalCnt", pageList.getTotalElements());
        model.addAttribute("page", pageList.getNumber());
        return "installation";
    }

    @GetMapping({"/delete"})
    public String delete(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        //check session
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        Installation installation = installationRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("installation not found"));
        //employee user can only delete their owner installation
        if (user.getPrivilege().equals("2") && uid != installation.getUser().getId()){
            throw new Exception("permission denied");
        }
        installationRepository.deleteById(Long.parseLong(id));
        return list(model, session, "0");
    }

    @GetMapping({"/detail"})
    public String detail(Model model, HttpSession session, @RequestParam(name = "id") String id) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        //check session
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        Installation installation;
        if (id.equals("add")){
            //create installation
            installation = new Installation();
            installation.setCreatedBy(uid);
            installation.setCreateTime(new Date());
            if (user.getPrivilege().equals("2")){
                //employee user set current uid
                installation.setUser(new SystemUser(uid));
            }
        }else {
            //update installation
            installation = installationRepository.findById(Long.parseLong(id)).orElseThrow(()->new Exception("installation not found"));
        }
        if (user.getPrivilege().equals("2") && uid != installation.getUser().getId()){
            //employee user can't update the installation not assign to them
            throw new Exception("permission denied");
        }
        List<Customer> customerList = customerRepository.findAll();
        List<SystemUser> userList = userRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("installation", installation);
        model.addAttribute("customerList", customerList);
        model.addAttribute("userList", userList);
        return "installation_detail";
    }

    @PostMapping({"/save"})
    public String save(Model model, HttpSession session, @ModelAttribute Installation installation) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        if (uid == null){
            //check session
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date fromTime = sdf.parse(installation.getFromTime());
        Date toTime = sdf.parse(installation.getToTime());
        if (toTime.before(fromTime)){
            throw new Exception("toTime must great than fromTime");
        }
        Long installId;
        if (installation.getId() == null){
            installId = 0L;
        }else {
            installId = installation.getId();
        }
        List<Installation> conflictInstallations = installationRepository.findConflictTime(installation.getDate(), installation.getFromTime(), installation.getToTime(), installId, installation.getUser().getId());
        if (conflictInstallations.size()>0){
            throw new Exception("The employee work time conflict, please change the time, another work time: " + installation.getDate() + " " + installation.getFromTime() + "~" + installation.getToTime());
        }
        installation.setUpdatedBy(uid);
        installation.setUpdateTime(new Date());
        installationRepository.save(installation);
        return list(model, session, "0");
    }


}
