package com.bool.controller; /**
 * Created by Nelson on 4/10/2017.
 */

import com.google.appengine.api.datastore.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String splash() {
        return "pages/splash_screen";
    }

    @RequestMapping("/login")
    public ModelAndView workspaceLogin(){

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();

        ModelAndView mv;

        if (userService.isUserLoggedIn()) { //signed in
            mv = new ModelAndView("redirect:/workspace");
        } else { //not signed in
            mv = new ModelAndView("redirect:" + userService.createLoginURL("/workspace"));
        }

        return mv;
    }


}
