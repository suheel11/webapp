package com.neu.edu.user.controller;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.service.UserService;
import com.timgroup.statsd.NonBlockingStatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value="/v1/user")
public class UserController {

    @Autowired
    private UserService service;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final NonBlockingStatsDClient client = new NonBlockingStatsDClient("my.prefix", "localhost", 8125);
    @PostMapping
    public Object addUser(@RequestBody User user){
        //System.out.println("entered first ");
        logger.info("Method - Add user");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/user");
        try {
            String regex = "^[\\w-\\.+]*[\\w-\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

            if ((user.getEmail() != null && !user.getEmail().matches(regex)) || (!service.validatePassword(user.getPassword()))) {
                logger.warn("Please enter valid email ad passwords");
                return new ResponseEntity<>("Please enter valid Credentials", HttpStatus.BAD_REQUEST);
            }
            logger.info("Added user successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/user",duration);
            return service.saveUser(user);

        }
        catch (Exception e){
            System.out.println("inside catch");
            logger.error("Bad Request");
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
    @GetMapping("/{id}")
    public Object findUserByIdNoAuth(@PathVariable String id){
        try{
            logger.info("Method - Find user by id");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/{id}");
            User user = service.getUserById(id);
            System.out.println("user"+user);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/{id}",duration);
            if (user == null)
                throw new Exception();
            else
                return user;
        }
        catch (Exception e){
            System.out.println("Exception");
            logger.error("Not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found",e);
        }
    }
    @GetMapping("/self")
    public Object findUserById(@AuthenticationPrincipal User user){
        logger.info("Method - Find user by id");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/self");
        User u=service.getUserById(user.getUserId());
        try {
            if(u==null){
                logger.error("User Not Found");
                return new ResponseEntity<>("Not Found",HttpStatus.NOT_FOUND);
            }
            if (!u.getPassword().equalsIgnoreCase(user.getPassword())) {
                logger.error("Invalid password");
                return new ResponseEntity<>("Invalid Credentials",HttpStatus.FORBIDDEN);
            }
        }catch(Exception ex){
            logger.error("Invalid credentials");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Credentials",ex);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        client.recordExecutionTime("/self",duration);
        return u;
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
            logger.info("Method - Update user");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/self");
            if(user.getEmail()!=null||user.getAccountCreated()!=null||user.getAccountUpdated()!=null){
                logger.error("Invalid email");
                throw new Exception();
            }
            service.updateUser(user1.getUserId(), user);
            logger.info("User updated successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/self",duration);
            return new ResponseEntity<>("No Content",HttpStatus.NO_CONTENT);
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
