package com.liam.javareactmysql.services;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.liam.javareactmysql.models.LoginUser;
import com.liam.javareactmysql.models.User;
import com.liam.javareactmysql.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	// Get All
	public List<User> getAllUsers() {
		return userRepo.findAll();

	}
	
	// Get One
	public User getUser(Long id) {
		User user = userRepo.findById(id).orElse(null);
		return user;
	}
	
	
	// Check Email
	public ResponseEntity<User> simpleEmailCheck(User userEmail) {
		if(userRepo.findByEmail(userEmail.getEmail()).isPresent()) {
	          return new ResponseEntity<User>(userEmail, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	
	
	// Hash Password & Save
	public User simpleCreateUser(User newUser) {

        String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
        newUser.setPassword(hashed);

		return userRepo.save(newUser);
	}
	
	
    
	// Login
    	public User login(LoginUser newLogin, BindingResult result) {
        	if(result.hasErrors()) {
            		return null;
        	}
        	Optional<User> potentialUser = userRepo.findByEmail(newLogin.getEmail());
        	if(!potentialUser.isPresent()) {
			result.rejectValue("email", "Unique", "Unknown email!");
			return null;
        	}
        	User user = potentialUser.get();
        	if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
			result.rejectValue("password", "Matches", "Invalid Password!");
        	}
        	if(result.hasErrors()) {
			return null;
        	}else {
            		return user;
        	}
    	}
    
	// Update
	public User updateOne(User user) {
		return userRepo.save(user);
	}
	
	// Delete
	public void deleteOne(Long id) {
		userRepo.deleteById(id);
	}
	
	//	// Create One (with email verification) - OOO
//	public User createUser(User newUser, BindingResult result) {
//		if(userRepo.findByEmail(newUser.getEmail()).isPresent()) {
//          result.rejectValue("email", "Unique", "This email is already in use!");
//		}
//		if(result.hasErrors()) {
//          return null;
//      } else {
//          String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
//          newUser.setPassword(hashed);
//          return userRepo.save(newUser);
//      }
//	}

}
