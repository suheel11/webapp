package com.neu.edu.user.controller;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/v1/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public Object addUser(@RequestBody User user){
        //System.out.println("entered first ");
        try {
            String regex = "^[\\w-\\.+]*[\\w-\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            String password = "^([a-zA-Z0-9@*#]{8,15})$";
            if ((user.getEmail() != null && !user.getEmail().matches(regex)) || (!user.getPassword().matches(password))) {
                return ResponseEntity.badRequest();
            }
            return service.saveUser(user);
        }
        catch (Exception e){
            System.out.println("inside catch");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request",e);
        }
    }

//    @PostMapping("/addUsers")
//    public List<User> addUsers(@RequestBody List<User> users){
//        return service.saveUsers(users);
//    }
    //@GetMapping("/users")
    //public List<User> findAllUsers(){
      //  return service.getUsers();
    //}
    @GetMapping("/self")
    public User findUserById(@AuthenticationPrincipal User user){

        return service.getUserById(user.getUserId());
    }
    /*public User findUserById(@AuthenticationPrincipal User user){
        User u=service.getUserById(user.getUserId());
        System.out.println("u"+u.getPassword());
        System.out.println("user"+user.getPassword());
        if(u.getPassword().equals(user.getPassword()))
        return u;
        else
            return null;
    }*/
   // @GetMapping("/user/{name}")
    //public User findUserByName(@PathVariable String name){
      //  return service.getUserByName(name);
    //}
    @PutMapping("/self")
    public Object updateUser(@AuthenticationPrincipal User user1, @RequestBody User user){
        //System.out.println("user1"+user1.getUserId());
        //System.out.println("useremail"+user.getEmail());
        //System.out.println("user"+user.getUserId());
        try {
            if(user.getEmail()!=null||user.getAccountCreated()!=null||user.getAccountUpdated()!=null){
                throw new Exception();
            }
            return service.updateUser(user1.getUserId(), user);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request",e);
        }
    }
//    @DeleteMapping("/delete/{id}")
//    public String deleteUser(@PathVariable int id){
//        return service.deleteUserById(id);
//    }
}
