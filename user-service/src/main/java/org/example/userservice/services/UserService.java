package org.example.userservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.entities.User;
import org.example.userservice.exceptions.AlreadyRegisteredException;
import org.example.userservice.exceptions.UserNotFoundException;
import org.example.userservice.repositories.UserRepository;
import org.example.userservice.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    public boolean checkUserExistence(String login){
        return userRepository.findByLogin(login).size() == 1;
    }
    public String registerUser (String login, String password,String name){
        if(this.checkUserExistence(login)) throw new AlreadyRegisteredException();
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setName(name);
        user = userRepository.save(user);
        log.info(String.valueOf(user));
        return jwtUtil.generateToken(user);
    }
    public String loginUser (String login, String password){
        List<User> user_list = userRepository.findByLoginAndPassword(login,password);
        if(user_list.size() !=1) throw new UserNotFoundException();
        return jwtUtil.generateToken(user_list.getFirst());
    }
    @Transactional
    public void updatePassword (Long id, String password){
        userRepository.updatePassword(id,password);
    }
    @Transactional
    public void updateName (Long id, String password){
        userRepository.updateName(id,password);
    }
    @Transactional
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
