package com.bool.search;

import com.bool.data.Datastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.ArrayList;
import java.util.List;

/*Parse query, split into separate strings
* filter datastore, send list back to profile controller
*
*
* Search just filters the dataastore based on the parsed query*/
public class Search{

    private String query;

    private List<String> names = new ArrayList<String>();
    private List<String> tags = new ArrayList<String>();
    private List<String> owners = new ArrayList<String>();
    private List<String> sharedWith = new ArrayList<String>();

    boolean searchMultipleOwners = false;

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Datastore data = new Datastore();

    public Search(){}

    public Search(String query){
        this.query = query;
    }

    public List<Entity> parseQuery(String query){
        /*Default search: just circuit name
          <name> #<tag> owner:<owner> shared:<shared>
        * owner:john owner:mike
        * Make 4 lists that hold each one of the fields (names, tags, ownres, shared)
        * tokenize on space, if there is something unrecognizable then just filter on name
        * "Nelson's Circuit" use contains filter to search for any name containing Nelson's and Circuit
        * */

        List<Entity> defaultFilter = getDefaultCircuits();
        if (query.length() == 0){
            return defaultFilter;
        }
        String[] splitQuery = query.split(" ");



        for (String term: splitQuery){

            if (term.charAt(0) == ('#')){
                tags.add(term);
                System.out.println("Found tag: " + term.substring(1));
            }
            else if (term.contains("owner:")){
                owners.add(term.substring(6));
                System.out.println("Found owner: " + term.substring(6));
            }
            else if (term.contains("shared:")){
                sharedWith.add(term.substring(7));
                System.out.println("Found shared: " + term.substring(7));
            }
            else{
                names.add(term);
                System.out.println("Found name: " + term);
            }

        }

        List<Entity> searchResults = new ArrayList<>();

        if (owners.size() > 1){
            for (String owner: owners){
                List<Entity> ownerSearch = multipleOwnersSearch(owner, defaultFilter, tags, sharedWith, names);
                searchResults.addAll(ownerSearch);
            }
        }else{
            searchResults = getSearchResults(defaultFilter, tags, sharedWith, owners, names);
        }




        return searchResults;



    }

    public List<Entity> multipleOwnersSearch(String owner, List<Entity> defaultFilter, List<String> tags, List<String> sharedWith, List<String> names){
        List<Entity> searchResult = new ArrayList<>();

        for (Entity circuit: defaultFilter){
            String currTags = (String)circuit.getProperty("tags");
            String currShared = (String)circuit.getProperty("shared");
            String currOwner = (String)circuit.getProperty("owner");
            String currName = (String)circuit.getProperty("name");

            if (owner.equals(currOwner)){
                /*Search for circuits by different owners*/
                if (tags.isEmpty() && sharedWith.isEmpty() && names.isEmpty()){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }

                /*Search for circuit by owner and tag*/
                else if (!tags.isEmpty() && sharedWith.isEmpty() && names.isEmpty()){
                    if (checkProperty(currTags, tags)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }

                /*Search by owner and shared*/
                else if (tags.isEmpty() && !sharedWith.isEmpty() && names.isEmpty()){
                    if (checkProperty(currShared, sharedWith)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }

                /*Search by owner and circuit name*/
                else if (tags.isEmpty() && sharedWith.isEmpty() && !names.isEmpty()){
                    if (checkProperty(currName, names)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }

                /*Search by owner, tags, and name*/
                else if (!tags.isEmpty() && sharedWith.isEmpty() && !names.isEmpty()){
                    if (checkProperty(currTags, tags) && checkProperty(currName, names)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }
                /*Search by owner, tag, and shared*/
                else if (!tags.isEmpty() && !sharedWith.isEmpty() && names.isEmpty()){
                    if (checkProperty(currTags, tags) && checkProperty(currShared, sharedWith)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }

                /*Search by owner, shared, and name*/
                else if (tags.isEmpty() && !sharedWith.isEmpty() && !names.isEmpty()){
                    if (checkProperty(currShared, sharedWith) && checkProperty(currName, names)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }

                /*Search by all 4*/
                else if (!tags.isEmpty() && !sharedWith.isEmpty() && !names.isEmpty()){
                    if (checkProperty(currTags, tags) && checkProperty(currShared, sharedWith) && checkProperty(currName, names)){
                        if (!searchResult.contains(circuit))
                            searchResult.add(circuit);
                    }
                }
            }



        }


        return searchResult;

    }

    public List<Entity> getSearchResults(List<Entity> defaultFilter, List<String> tags, List<String> sharedWith, List<String> owners, List<String> names){

        List<Entity> searchResult = new ArrayList<>();
        for (Entity circuit: defaultFilter){
            String currTags = (String)circuit.getProperty("tags");
            String currShared = (String)circuit.getProperty("shared");
            String currOwner = (String)circuit.getProperty("owner");
            String currName = (String)circuit.getProperty("name");


            /*Search by tag only*/
            if (!tags.isEmpty() && sharedWith.isEmpty() && owners.isEmpty() && names.isEmpty()){

                if (checkProperty(currTags, tags)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }
            /*Search by tag and shared with*/
            else if (!tags.isEmpty() && !sharedWith.isEmpty() && owners.isEmpty() && names.isEmpty()){

                if (checkProperty(currTags, tags) && checkProperty(currShared, sharedWith)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by tag and shared with and owners*/
            else if (!tags.isEmpty() && !sharedWith.isEmpty() && !owners.isEmpty() && names.isEmpty()){

                if (checkProperty(currTags, tags) && checkProperty(currShared, sharedWith) && checkProperty(currOwner, owners)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by tag and shared with and owners and name*/
            else if (!tags.isEmpty() && !sharedWith.isEmpty() && !owners.isEmpty() && !names.isEmpty()){


                if (checkProperty(currTags, tags) && checkProperty(currShared, sharedWith) && checkProperty(currOwner, owners) && checkProperty(currName, names)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by owner*/
            else if (tags.isEmpty() && sharedWith.isEmpty() && !owners.isEmpty() && names.isEmpty()){

                if (checkProperty(currOwner, owners)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }

            }

            /*Search by name*/
            else if (tags.isEmpty() && sharedWith.isEmpty() && owners.isEmpty() && !names.isEmpty()){

                if (checkProperty(currName, names)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }

            }

            /*Search by tag and name*/
            else if (!tags.isEmpty() && sharedWith.isEmpty() && owners.isEmpty() && !names.isEmpty()){

                if (checkProperty(currName, names) && checkProperty(currTags, tags)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by shared*/
            else if (tags.isEmpty() && !sharedWith.isEmpty() && owners.isEmpty() && names.isEmpty()){

                if (checkProperty(currShared, sharedWith)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by owner and tag*/
            else if (!tags.isEmpty() && sharedWith.isEmpty() && !owners.isEmpty() && names.isEmpty()){
                if (checkProperty(currTags, tags) && checkProperty(currOwner, owners)){
                    if (!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }

            /*Search by owner and name*/
            else if (tags.isEmpty() && sharedWith.isEmpty() && !owners.isEmpty() && !names.isEmpty()){
                if (checkProperty(currOwner, owners) && checkProperty(currName, names)){
                    if(!searchResult.contains(circuit))
                        searchResult.add(circuit);
                }
            }




        }

        return searchResult;

    }

    public List<Entity> getDefaultCircuits(){

        UserService userService = UserServiceFactory.getUserService();
        User currUser = userService.getCurrentUser();


        /*Define default filter:
            owner: currentUser && (shared: currUser || tag.contains(#public))
        */

        /*Get all circuits belonging to the current user*/
        List<Entity> ownedCircuits = data.loadYourCircuits(currUser.getEmail());

        /*Get all circuits that have been shared with the current user*/
        List<Entity> sharedCircuits = data.loadSharedCircuits(currUser.getEmail());

        /*Get all public circuits*/
        List<Entity> publicTags = data.loadPublicCircuits();

        List<Entity> defaultFilter = new ArrayList<>();
        defaultFilter.addAll(ownedCircuits);

            /*Check for duplicates*/
        for (Entity circuit: publicTags){
            if (!defaultFilter.contains(circuit))
                defaultFilter.add(circuit);
        }

            /*Check for duplicates*/
        for (Entity circuit: sharedCircuits){
            if (!defaultFilter.contains(circuit))
                defaultFilter.add(circuit);
        }


        return defaultFilter;

    }


    public boolean checkProperty(String currProperty, List<String> searchTerms){
        int fullMatch = 1;

        /*Check to see if the searched property is contained within the current Circuit
        * searchTerms = ["#public", "#test", "#sdfs"]
        * currProperty= "#public;#test;#sdfs"               */


        for(String term: searchTerms){

            if (!currProperty.contains(term)){
                return false;
            }
        }

        return true;

    }


    public String getQuery(){
        return query;
    }
    public void setQuery(String query){
        this.query = query;
    }

}