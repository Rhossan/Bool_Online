package com.bool.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Refath Hossan on 5/9/17.
 */
public class NotificationDatastore {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();

    public void loadTestData(){
        List<Notification> testNotification = new ArrayList<>();

        for(int i=0;i<10; i++){
            testNotification.add(new Notification(
                    "mdadurian@example.com",
                    "nelson@example.com;reef@example.com",
                    "Mike's Circuit "+i
            ));
        }

        for(int i=0;i<10; i++){
            testNotification.add(new Notification(
                    "nelson@example.com",
                    "mdadurian@example.com;reef@example.com",
                    "Nelson's Circuit "+i
            ));
        }
        for(int i=0;i<10; i++){
            testNotification.add(new Notification(
                    "reef@example.com",
                    "mdadurian@example.com;kenny@example.com",
                    "Reef's Circuit "+i
            ));
        }
        for(int i=0;i<10; i++){
            testNotification.add(new Notification(
                    "kenny@example.com",
                    "",
                    "Kenny's Circuit "+i
            ));
        }

        for (Notification tc:testNotification) {
            pushData(tc);
        }
    }

    public void pushData(Notification notification){

        Key notificationKey = KeyFactory.createKey("Notification", notification.getName());
        Entity toPush = new Entity("Notification", notificationKey);

        toPush.setProperty("owner", notification.getOwner());
        toPush.setProperty("shared", notification.getShared());
        toPush.setProperty("name", notification.getName());

        datastore.put(toPush);
    }

    public List<Entity> loadSharedNotification(String sharedWith){

        List<Entity> toLoad;
        List<Entity> sharedNotifications = new ArrayList<>();

        Query query = new Query("Notification");
        query.addFilter("shared", Query.FilterOperator.NOT_EQUAL, "");
        toLoad = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

        for (Entity td:toLoad){

            String shared = (String)td.getProperty("shared");
            String[] splitShared = shared.split(";");

            for (String user: splitShared){
                if (user.equals(sharedWith)){
                    sharedNotifications.add(td);

                    break;
                }
            }
        }

        return sharedNotifications;

    }

    public void deleteNotification(Entity notification){
        Key notificationKey = notification.getKey();
        datastore.delete(notificationKey);
    }

    public Entity queryNotificationName(String name, String owner){
        Query query = new Query("Notification");
        query.addFilter("name", Query.FilterOperator.EQUAL, name);
        query.addFilter("owner", Query.FilterOperator.EQUAL, owner);
        PreparedQuery pq = datastore.prepare(query);

        Entity notificationToDelete = pq.asSingleEntity();

        return notificationToDelete;
    }




}
