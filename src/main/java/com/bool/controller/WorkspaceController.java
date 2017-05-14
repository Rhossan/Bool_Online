package com.bool.controller;


import com.bool.data.Circuit;
import com.bool.data.Datastore;
import com.bool.data.Notification;
import com.bool.data.NotificationDatastore;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;

/**
 * Created by Nelson on 4/19/2017.
 */

@Controller
public class WorkspaceController {

    Datastore datastore = new Datastore();
    /*Added*/NotificationDatastore notification_data = new NotificationDatastore();

    @RequestMapping("/workspace")
    public ModelAndView workspace(){
        ModelAndView mv = new ModelAndView("pages/workspace");

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();

        if(userService.isUserLoggedIn()){
            mv.addObject("currEmail", currUser.getEmail());
            System.out.println(currUser.getEmail());
        }

        return mv;
    }

    @RequestMapping(value = "/workspace/submit", method = RequestMethod.GET)
    @ResponseBody
    public String getSubmission(
            @RequestParam(required = false, value ="owner") String owner,
            @RequestParam(required = false, value ="shared") String shared,
            @RequestParam(required = false, value ="name") String name,
            @RequestParam(required = false, value ="circuitContent") String circuitContent,
            @RequestParam(required = false, value ="quizletConstraints") String quizletConstraints,
            @RequestParam(required = false, value ="tags") String tags
    ){

        Circuit circuitFile = new Circuit(owner, shared, name, circuitContent, quizletConstraints, tags);
        /*Added*/Notification NotificationFile = new Notification(owner,shared,name);

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();

        if(currUser != null) {
            circuitFile.setOwner(currUser.getEmail());
            NotificationFile.setOwner(currUser.getEmail());
            datastore.pushData(circuitFile);
            notification_data.pushData(NotificationFile);
            return "Successfully submitted circuitFile to datastore!";
        }
        else {
            return "User not signed in!";
        }
    }

    @RequestMapping("/workspace/toggleLoginLogout")
    public ModelAndView workspaceLogin(){

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();

        ModelAndView mv;

        if (userService.isUserLoggedIn()) { //signed in
            mv = new ModelAndView("redirect:" + userService.createLogoutURL("/workspace"));
        } else { //not signed in
            mv = new ModelAndView("redirect:" + userService.createLoginURL("/workspace"));
        }

        return mv;
    }

}
