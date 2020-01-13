package com.appdeveloper.app.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloper.app.ws.io.entity.UserEntity;
import com.appdeveloper.app.ws.io.repositories.UserRepository;
import com.appdeveloper.app.ws.service.UserService;
import com.appdeveloper.app.ws.shared.Utils;
import com.appdeveloper.app.ws.shared.dto.UserDto;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCrptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto user) {
		// TODO Auto-generated method stub
		
		if (userRepository.findByEmail(user.getEmail()) != null ) throw new RuntimeException("Record already exist");
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		userEntity.setEncryptedPassword(bCrptPasswordEncoder.encode(user.getPassword()));
		
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		return returnValue;
	}
	
	@Override 
	public UserDto getUser(String email) throws UsernameNotFoundException{
		UserEntity entity = userRepository.findByEmail(email);
		
		if (entity == null) throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(entity, returnValue);
		return returnValue;
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if (userEntity == null) throw new UsernameNotFoundException(email);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

}