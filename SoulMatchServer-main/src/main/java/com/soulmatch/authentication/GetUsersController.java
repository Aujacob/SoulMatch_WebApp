package com.soulmatch.authentication;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.soulmatch.Utils.UserUtils;
import com.soulmatch.firebase.FirebaseController;
import com.soulmatch.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class GetUsersController {

    @Autowired
    private FirebaseController controller;

    @RequestMapping(
            value = "/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUsersByProfile(@RequestBody User user) {
        List<String> compatibleUsers = new ArrayList<>();
        try {
            for (QueryDocumentSnapshot userDocument : controller.getFirestore().collection("users").get().get().getDocuments()) {
                User user1 = UserUtils.convertToUser(userDocument);
                if (UserUtils.doUsersProfileMatch(user, user1)) {
                    compatibleUsers.add(user1.getId());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(compatibleUsers);
    }
}
