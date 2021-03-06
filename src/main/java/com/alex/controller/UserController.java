package com.alex.controller;

import com.alex.entity.User;
import com.alex.service.UserService;

import org.jboss.logging.Logger;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    public UserController() {
        System.out.println("UserController()");
    }

    @Autowired
    private UserService userService;

    @RequestMapping("createUser")
    public ModelAndView createUser(@ModelAttribute User user) {
        logger.info("Creating User. Data: "+ user);
        return new ModelAndView("userForm");
    }

    @RequestMapping("editUser")
    public ModelAndView editUser(@RequestParam int id, @ModelAttribute User user) {
        logger.info("Updating the User for the Id "+id);
        user = userService.getUser(id);
        return new ModelAndView("userForm", "userObject", user);
    }

    @RequestMapping("saveUser")
    public ModelAndView saveUser(@ModelAttribute User user) {
        logger.info("Saving the User. Data : "+ user);
        if(user.getId() == 0){ // if user id is 0 then creating the user other updating the user
            userService.createUser(user);
        } else {
            userService.updateUser(user);
        }
        return new ModelAndView("redirect:getAllUsers");
    }

    @RequestMapping("deleteUser")
    public ModelAndView deleteUser(@RequestParam int id) {
        logger.info("Deleting the User. Id : "+id);
        userService.deleteUser(id);
        return new ModelAndView("redirect:getAllUsers");
    }


    @RequestMapping(value = {"getAllUsers", "/", "/list"})
    public ModelAndView getAllUsers(@RequestParam(required = false) Integer page) {
        logger.info("Getting the all Users.");
        List<User> userList = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("userList", "userList", userList);
        PagedListHolder<User> pagedListHolder = new PagedListHolder<>(userList);
        pagedListHolder.setPageSize(5);
        modelAndView.addObject("maxPages", pagedListHolder.getPageCount());
        if(page==null || page < 1 || page > pagedListHolder.getPageCount())page=1;

        modelAndView.addObject("page", page);
        if(page == null || page < 1 || page > pagedListHolder.getPageCount()){
            pagedListHolder.setPage(0);
            modelAndView.addObject("userList", pagedListHolder.getPageList());
        }
        else if(page <= pagedListHolder.getPageCount()) {
            pagedListHolder.setPage(page-1);
            modelAndView.addObject("userList", pagedListHolder.getPageList());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/searchUser/{searchName}")
    public ModelAndView searchUser(@RequestParam("searchName") String searchName) {
        logger.info("Searching the User. User Names: "+searchName);
        List<User> userList = userService.getAllUsers(searchName);
        return new ModelAndView("userList", "userList", userList);
    }

    @RequestMapping("addDemoData")
    public ModelAndView addDemoData() {
        logger.info("Creating demo data.");
        userService.createDemoData();
        userService.createDemoData();
        return new ModelAndView("redirect:getAllUsers");
    }

}