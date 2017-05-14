package com.bool.controller;

/**
 * Created by Nelson on 4/16/2017.
 */

import com.bool.data.Circuit;
import com.bool.data.Datastore;
import com.bool.data.NotificationDatastore;
import com.bool.search.Search;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.repackaged.com.google.appengine.api.search.SearchServicePb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller

public class ProfileController {

    Datastore datastore = new Datastore();
    /*added*/NotificationDatastore notification_data = new NotificationDatastore();
    @RequestMapping("/loadtestdata")
    public String pcloadTestData() {
        datastore.loadTestData();
        notification_data.loadTestData();
        return "pages/profile";
    }

    //load entire string passed from form in profile.jsp
    //psas over initial url and append the string
    //parse string and query database
    @RequestMapping(value = "profile/submitSearch", method = RequestMethod.GET)
    @ModelAttribute("searchParams")
    public ModelAndView submitSearch(Model model, HttpServletRequest request){

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();


        Search searchParams = new Search(request.getParameter("searchParams"));
        model.addAttribute("searchParams", searchParams);

        ModelAndView mv = new ModelAndView("pages/profile");
        mv.addObject("searchParams", request.getParameter("searchParams"));

        System.out.println(searchParams.getQuery());
        List<Entity> searchResults = searchParams.parseQuery(searchParams.getQuery());
        List<String> circuitNames = new ArrayList<>();
        List<String> circuitOwners = new ArrayList<>();

        List<String> canDelete = new ArrayList<>();
        List<String> canShare = new ArrayList<>();

        for (Entity searchResult: searchResults){
            circuitNames.add((String)searchResult.getProperty("name"));
            circuitOwners.add((String)searchResult.getProperty("owner"));
            if(currUser.getEmail().equals(searchResult.getProperty("owner"))){
                canShare.add("true");
                canDelete.add("true");
            }else{
                canShare.add("false");
                canDelete.add("false");

            }
        }
        //added
        List<Entity> notifications = notification_data.loadSharedNotification(currUser.getEmail());
        List<String> notification_Names = new ArrayList<>();
        List<String> notification_Owners = new ArrayList<>();

        for (Entity notification: notifications){
            notification_Names.add((String) notification.getProperty("name"));
            notification_Owners.add((String) notification.getProperty("owner"));


        }
        mv.addObject("notification_Names",notification_Names);
        mv.addObject("notification_Owners",notification_Owners);
        //added

        mv.addObject("circuitNames", circuitNames);
        mv.addObject("circuitOwners", circuitOwners);
        mv.addObject("currUser", currUser);
        mv.addObject("canDelete", canDelete);
        mv.addObject("canShare", canShare);


        return mv;
    }

    @RequestMapping(value = "profile/loadCircuitFromNotification", method = RequestMethod.GET)
    @ModelAttribute("searchParams")
    public ModelAndView loadCircuitFromNotification(Model model, HttpServletRequest request){

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();


        Search searchParams = new Search(request.getParameter("searchParams"));
        model.addAttribute("searchParams", searchParams);

        ModelAndView mv = new ModelAndView("pages/profile");
        mv.addObject("searchParams", request.getParameter("searchParams"));

        System.out.println(searchParams.getQuery());
        List<Entity> searchResults = searchParams.parseQuery(searchParams.getQuery());
        List<String> circuitNames = new ArrayList<>();
        List<String> circuitOwners = new ArrayList<>();



        for (Entity searchResult: searchResults){
            circuitNames.add((String)searchResult.getProperty("name"));
            circuitOwners.add((String)searchResult.getProperty("owner"));

        }




        StringBuilder circuitBuild = new StringBuilder();
        for(String a: circuitNames){
            circuitBuild.append(a);
            circuitBuild.append(" ");
        }
        String circuitName = circuitBuild.toString();
        circuitName = circuitName.substring(0, circuitName.length() - (" ").length());
        System.out.println(circuitName);

        StringBuilder ownerBuild = new StringBuilder();
        for(String a: circuitOwners){
            ownerBuild.append(a);
            ownerBuild.append(" ");
        }
        String ownerName = ownerBuild.toString();
        ownerName = ownerName.substring(0, ownerName.length() - (" ").length());
        System.out.println(ownerName);


        deleteNotification(circuitName, ownerName);


        List<Entity> notifications = notification_data.loadSharedNotification(currUser.getEmail());
        List<String> notification_Names = new ArrayList<>();
        List<String> notification_Owners = new ArrayList<>();

        for (Entity notification: notifications){
            notification_Names.add((String) notification.getProperty("name"));
            notification_Owners.add((String) notification.getProperty("owner"));


        }

        mv.addObject("notification_Names",notification_Names);
        mv.addObject("notification_Owners",notification_Owners);

        mv.addObject("circuitNames", circuitNames);
        mv.addObject("circuitOwners", circuitOwners);
        mv.addObject("currUser", currUser);


        return mv;
    }

    public void deleteNotification (String NotificationName, String NotificationOwner){

        Entity circuitToDelete = notification_data.queryNotificationName(NotificationName,NotificationOwner);
        notification_data.deleteNotification(circuitToDelete);

    }

    @RequestMapping("/profile/notifications")
    public ModelAndView profileNotifications() {

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();

        ModelAndView mv = new ModelAndView("pages/profile");
        List<Entity> toDisplay = notification_data.loadSharedNotification(currUser.getEmail());
        List<String> notification_Names = new ArrayList<>();
        List<String> notification_Owners = new ArrayList<>();

        for (Entity td : toDisplay) {
            notification_Names.add((String) td.getProperty("name"));
            notification_Owners.add((String) td.getProperty("owner"));
        }

        mv.addObject("notification_Names", notification_Names);
        mv.addObject("notification_Owners", notification_Owners);

        return mv;

    }

    @RequestMapping(value = "profile/share", method = RequestMethod.GET)
    @ResponseBody
    public String shareCircuit(@RequestParam(required = true, value ="circuitName") String circuitName,
                             @RequestParam(required = true, value = "circuitOwner") String circuitOwner
                             ){

        Entity currCircuit = datastore.queryCircuitName(circuitName, circuitOwner);

        String currShared = (String)currCircuit.getProperty("shared");
        String currName = (String)currCircuit.getProperty("name");
        String currOwner = (String)currCircuit.getProperty("owner");
        String currTags = (String)currCircuit.getProperty("tags");


        return "{" +  "\"pCircuitName\":" + "\"" + currName + "\"" + "," + "\"pCircuitOwner\":" + "\"" + currOwner + "\"" +  "," + "\"pCircuitShared\":" + "\"" + currShared + "\"" +
                "," + "\"pCircuitTags\":" + "\"" + currTags + "\"" + "}";


    }

    @RequestMapping(value = "profile/submitEdit", method = RequestMethod.GET)
    @ResponseBody
    public String confirmEdit(@RequestParam(required = true, value = "circuitName") String circuitName,
                              @RequestParam(required = true, value = "circuitOwner") String circuitOwner,
                              @RequestParam(required = true, value = "circuitTags") String circuitTags,
                              @RequestParam(required = true, value = "circuitShared") String circuitShared){

        System.out.println("name: " + circuitName + " shared with: " + circuitShared);
        datastore.updateShared(circuitName, circuitOwner, circuitShared);
        return "SUCCESS";

    }



    @RequestMapping(value = "profile/delete", method = RequestMethod.GET)
    @ResponseBody
    public String deleteCircuit (@RequestParam(required = true, value ="circuitName") String circuitName,
                                 @RequestParam(required = true, value = "circuitOwner") String circuitOwner,
                                 @RequestParam(required = false, value = "currRow") String currRow){

        Entity circuitToDelete = datastore.queryCircuitName(circuitName, circuitOwner);
        datastore.deleteCircuit(circuitToDelete);

        return currRow;

    }



    @RequestMapping("/profile")
    public ModelAndView profileLogin() {

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();


        System.out.println("currUser " + currUser);

        if (userService.isUserLoggedIn()) { //signed in
            ModelAndView mv = new ModelAndView("pages/profile");
            List<Entity> toDisplay = datastore.loadAllCircuits(currUser.getEmail());
            /*added*/List<Entity> notifications = notification_data.loadSharedNotification(currUser.getEmail());

            List<String> circuitNames = new ArrayList<>();
            List<String> circuitOwners = new ArrayList<>();
            List<String> canDelete = new ArrayList<>();

            List<String> notification_Names = new ArrayList<>();
            List<String> notification_Owners = new ArrayList<>();

            for (Entity td : toDisplay) {

                circuitNames.add((String) td.getProperty("name"));
                circuitOwners.add((String) td.getProperty("owner"));
                if(currUser.getEmail().equals(td.getProperty("owner"))){
                    canDelete.add("true");
                }else{
                    canDelete.add("false");
                }
            }

            for (Entity notification : notifications){
                notification_Names.add((String) notification.getProperty("name"));
                notification_Owners.add((String) notification.getProperty("owner"));
            }


            mv.addObject("circuitNames", circuitNames);
            mv.addObject("circuitOwners", circuitOwners);
            mv.addObject("currUser", currUser);
            mv.addObject("canDelete", canDelete);
            mv.addObject("notification_Names", notification_Names);
            mv.addObject("notification_Owners", notification_Owners);


            return mv;
        } else { //not signed in
            return new ModelAndView("redirect:" + userService.createLoginURL("/profile"));
        }
    }





}
