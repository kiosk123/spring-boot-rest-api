package com.study.user.service; 

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.study.user.controller.v1.dto.UserDto;
import com.study.user.controller.v1.dto.UserRequestDto;
import com.study.user.domain.User;
import com.study.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<UserDto> findOneUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn());
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    public List<UserDto> findAll() {
        return userRepository
                    .findAll()
                    .stream()
                    .map(user -> new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn()))
                    .collect(Collectors.toList());
    }

    @Transactional
    public void removeUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) { 
            userRepository.delete(findUser.get());
        }
    }

    @Transactional
    public Optional<UserDto> updateUser(UserRequestDto userRequestDto) {
        Optional<User> findUser = userRepository.findById(userRequestDto.getId());
        if(findUser.isPresent()) {
            User user = findUser.get();
            user.setName(userRequestDto.getName());
            user.setPassword(userRequestDto.getPassword());
            user.setSsn(userRequestDto.getSsn());
            
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn());
            return Optional.of(userDto);
        } 
        return Optional.empty();
    }
}
