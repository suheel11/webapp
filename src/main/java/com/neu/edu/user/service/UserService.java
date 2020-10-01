package com.neu.edu.user.service;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.*;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public User saveUser(User user){
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        String date = String.valueOf(java.time.LocalDateTime.now());
        user.setAccountUpdated(date);
        if(user.getAccountCreated()==null)
            user.setAccountCreated(date);
        return repository.save(user);
    }

    public List<User> saveUsers(List<User> users){
        return repository.saveAll(users);
    }

    public List<User> getUsers(){
        return repository.findAll();
    }

    public User getUserById(String id){
        return repository.findById(id).orElse(null);
    }

    //public User getUserByName(String name){
      //  return repository.findByName(name);
    //}

//    public String deleteUserById(int id){
//        repository.deleteById(id);
//        return "User removed"+id;
//    }

    public Object updateUser(String id,User user){

        if(user!=null){
            User existingUser = repository.findById(id).orElse(null);
            existingUser.setName(user.getName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPassword(bcryptEncoder.encode(user.getPassword()));
            String updateDate = String.valueOf(java.time.LocalDateTime.now());
            existingUser.setAccountUpdated(updateDate);
            return repository.save(existingUser);
        }
        return null;
    }
    public boolean validatePassword(String pass){
        System.out.println("validatePassword");
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern p=Pattern.compile(regex);
        if (pass == null) {
            return false;
        }
        Matcher m = p.matcher(pass);
        boolean res=m.matches();
        System.out.println(res);
        return  res;
        //System.out.println("false or tru"+pass.matches(password));
        /*if(pass.matches(password)) {
            return true;
        }
        else {
            System.out.println("entered else"+pass);
            return false;
        }*/
    }

}
