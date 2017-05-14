package com.bool.data;

/**
 * Created by Nelson on 4/16/2017.
 */

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.ArrayList;
import java.util.List;

public class Datastore {

        /*Default filter here*/

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        UserService userService = UserServiceFactory.getUserService();

        public void loadTestData(){
            List<Circuit> testCircuits = new ArrayList<>();

            for(int i=0;i<10; i++){
                testCircuits.add(new Circuit(
                        "mdadurian@example.com",
                        "nelson@example.com;reef@example.com",
                        "Mike's Circuit "+i,
                        "",
                        "",
                        "#public;#test;#sdfs"
                ));
            }

            for(int i=0;i<10; i++){
                testCircuits.add(new Circuit(
                        "nelson@example.com",
                        "mdadurian@example.com;reef@example.com",
                        "Nelson's Circuit "+i,
                        "",
                        "",
                        "#public;#test"
                ));
            }
            for(int i=0;i<10; i++){
                testCircuits.add(new Circuit(
                        "reef@example.com",
                        "mdadurian@example.com;kenny@example.com",
                        "Reef's Circuit "+i,
                        "",
                        "",
                        "#public"
                ));
            }
            for(int i=0;i<10; i++){
                testCircuits.add(new Circuit(
                        "kenny@example.com",
                        "",
                        "Kenny's Circuit "+i,
                        "",
                        "",
                        ""
                ));
            }

            for (Circuit tc:testCircuits) {
                pushData(tc);
            }
        }



        public void pushData(Circuit circuit){

            Key circuitKey = KeyFactory.createKey("Circuit", circuit.getName());
            Entity toPush = new Entity("Circuit", circuitKey);

            toPush.setProperty("owner", circuit.getOwner());
            toPush.setProperty("shared", circuit.getShared());
            toPush.setProperty("name", circuit.getName());
            toPush.setProperty("circuitContent", circuit.getCircuitContent());
            toPush.setProperty("quizletConstraints", circuit.getQuizletConstraints());
            toPush.setProperty("tags", circuit.getTags());

            datastore.put(toPush);
        }

        public void deleteCircuit(Entity circuit){
            Key circuitKey = circuit.getKey();
            datastore.delete(circuitKey);
        }

        public List<Entity> loadYourCircuits(String owner){

            List<Entity> toLoad;
            Query query = new Query("Circuit");
            query.addFilter("owner", Query.FilterOperator.EQUAL, owner);
            toLoad = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

            return toLoad;


        }

        public Entity queryCircuitName(String name, String owner){
            Query query = new Query("Circuit");
            query.addFilter("name", Query.FilterOperator.EQUAL, name);
            query.addFilter("owner", Query.FilterOperator.EQUAL, owner);
            PreparedQuery pq = datastore.prepare(query);

            Entity circuit = pq.asSingleEntity();

            return circuit;
        }

        public void updateShared(String circuitName, String circuitOwner, String circuitShared){
            Entity circuit = queryCircuitName(circuitName, circuitOwner);
            circuit.setProperty("shared", circuitShared);

            datastore.put(circuit);

            System.out.println(circuit.getProperty("shared"));
        }

        public List<Entity> loadPublicCircuits(){
            List<Entity> toLoad;
            List<Entity> publicCircuits = new ArrayList<>();

            Query query = new Query("Circuit");
            query.addFilter("tags", Query.FilterOperator.NOT_EQUAL, "");
            toLoad = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());


            for (Entity td:toLoad) {

                String tags = (String)td.getProperty("tags");
                String[] splitTags = tags.split(";");

                for (String tag: splitTags){
                    if (tag.equals("#public")){
                        publicCircuits.add(td);
                        break;
                    }

                }
            }

            return publicCircuits;

        }

        public List<Entity> loadSharedCircuits(String sharedWith){

            List<Entity> toLoad;
            List<Entity> sharedCircuits = new ArrayList<>();

            Query query = new Query("Circuit");
            query.addFilter("shared", Query.FilterOperator.NOT_EQUAL, "");
            toLoad = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

            for (Entity td:toLoad){

                String shared = (String)td.getProperty("shared");
                String[] splitShared = shared.split(";");

                for (String user: splitShared){
                    if (user.equals(sharedWith)){
                        sharedCircuits.add(td);

                        break;
                    }
                }
            }

            return sharedCircuits;

        }

        public List<Entity> loadAllCircuits(String owner){
            List<Entity> allCircuits = new ArrayList<>();
            List<Entity> sharedCircuits = loadSharedCircuits(owner);
            List<Entity> publicCircuits = loadPublicCircuits();
            List<Entity> yourCircuits = loadYourCircuits(owner);


            allCircuits.addAll(sharedCircuits);

            /*Check for duplicates*/
            for (Entity circuit: publicCircuits){
                if (!allCircuits.contains(circuit))
                    allCircuits.add(circuit);
            }

            /*Check for duplicates*/
            for (Entity circuit: yourCircuits){
                if (!allCircuits.contains(circuit))
                    allCircuits.add(circuit);
            }


            return allCircuits;



        }

}
