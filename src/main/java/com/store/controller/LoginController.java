package com.store.controller;

import com.store.dao.*;
import com.store.entity.Customer;
import com.store.entity.Installation;
import com.store.entity.SessionManage;
import com.store.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private SystemUserRepository userRepository;

    @Autowired
    private SessionManageRepository sessionManageRepository;

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping({"/login"})
    public String login(Model model) {
        //redirect to login page
        model.addAttribute("user", new SystemUser());
        return "login";
    }

    @GetMapping({"/index","/"})
    public String index(Model model, HttpSession session) throws Exception {
        Long uid = (Long)session.getAttribute("uid");
        //check session and redirect to login page
        if (uid == null){
            model.addAttribute("user", new SystemUser());
            return "login";
        }
        SystemUser user = userRepository.findById(uid).orElseThrow(()->new Exception("user not found"));
        long inprogressCnt = 0;
        long completeCnt = 0;
        if (user.getPrivilege().equals("1")){
            //manager user
            Installation installation = new Installation();
            installation.setStatus("1");
            completeCnt = installationRepository.count(Example.of(installation));
            installation.setStatus("0");
            inprogressCnt = installationRepository.count(Example.of(installation));
        }else{
            //employee user
            Installation installation = new Installation();
            installation.setUser(new SystemUser(uid));
            installation.setStatus("1");
            completeCnt = installationRepository.count(Example.of(installation));
            installation.setStatus("0");
            inprogressCnt = installationRepository.count(Example.of(installation));
        }

        //push notice to user that the customer reward point great than 100
        List<Customer> rewardPointGT100Customers = customerRepository.findRewardPointGT100Customer();
        //installation statistic
        long totalInstallationCnt = installationRepository.count();
        Installation installation = new Installation();
        installation.setUser(new SystemUser(uid));
        long myInstallationCnt = installationRepository.count(Example.of(installation));
        //sales statistic
        Float totalSalesAmount = salesOrderRepository.findTotalSalesAmount();
        Float totalCostAmount = salesOrderRepository.findTotalCostAmount();
        Float totalInstallationSalesAmount = installationRepository.findTotalSalesAmount();
        Float totalInstallationCostAmount = installationRepository.findTotalCostAmount();
        List<Object[]> amountList = salesOrderRepository.findSalesAmountByDate();
        //stock statistic
        List<Object[]> productCategoryList = productRepository.findStockCntGroupByCategory();

        model.addAttribute("user", user);
        model.addAttribute("rewardPointGT100Customers", rewardPointGT100Customers);
        model.addAttribute("inprogressCnt", inprogressCnt);
        model.addAttribute("completeCnt", completeCnt);
        model.addAttribute("totalInstallationCnt", totalInstallationCnt);
        model.addAttribute("inprogressRate", totalInstallationCnt==0?0:String.format("%.1f", inprogressCnt*100.0d/totalInstallationCnt));
        model.addAttribute("completeRate", totalInstallationCnt==0?0:String.format("%.1f", completeCnt*100.0d/totalInstallationCnt));
        model.addAttribute("myInstallationRate", totalInstallationCnt==0?0:String.format("%.1f", myInstallationCnt*100.0d/totalInstallationCnt));
        model.addAttribute("myInstallationCnt", myInstallationCnt);
        model.addAttribute("totalSalesAmount", totalSalesAmount);
        model.addAttribute("totalCostAmount", totalCostAmount);
        model.addAttribute("totalInstallationSalesAmount", totalInstallationSalesAmount);
        model.addAttribute("totalInstallationCostAmount", totalInstallationCostAmount);
        model.addAttribute("productCategoryList", productCategoryList);
        model.addAttribute("amountList", amountList);
        model.addAttribute("netProfit", (totalSalesAmount==null||totalCostAmount==null)?0:totalSalesAmount-totalCostAmount);
        model.addAttribute("installationProfit", (totalInstallationSalesAmount==null||totalInstallationCostAmount==null)?0:totalInstallationSalesAmount-totalInstallationCostAmount);
        return "index";
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String login(Model model, SystemUser user, HttpSession session) throws Exception {
        SystemUser u = userRepository.findByUsernameAndPasswordAndPrivilege(user.getUsername(), user.getPassword(), user.getPrivilege());
        if (u == null){
            //login fail redirect to 403 forbidden
            throw new Exception("permission denied");
        }
        //login success create session
        session.setAttribute("uid", u.getId());
        SessionManage sm = new SessionManage("1", u.getId());
        sessionManageRepository.save(sm);
        model.addAttribute("user", u);
        return index(model, session);
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String logout(Model model, HttpSession session) throws Exception {
        //logout remove session
        Long uid = (Long)session.getAttribute("uid");
        SessionManage sm = new SessionManage("0", uid);
        sessionManageRepository.save(sm);
        session.removeAttribute("uid");
        model.addAttribute("user", new SystemUser());
        return "login";
    }


}
