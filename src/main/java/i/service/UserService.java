package i.service;

import i.dto.UserCreateRequestDto;
import i.model.Role;
import i.model.User;
import i.repository.RoleRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserCreateRequestDto save(UserCreateRequestDto userCreateRequestDto) {

        return null;
    }

//    private User toEntityFromCreateRequestDto(UserCreateRequestDto userCreateRequestDto) {
//        User user = new User();
//        user.setName(userCreateRequestDto.getName());
//        user.setEmail(userCreateRequestDto.getEmail());
//        user.setPassword(userCreateRequestDto.getPassword());
//        Set<Role> roles = userCreateRequestDto.getRoles().stream()
//                .map(roleDto -> {
//                    Optional<Role> role = roleRepository.findRoleByName(roleDto.getName());
//                    if (role.isEmpty()) {
//                        log.warn("Can not add role {}", roleDto.getName());
//                    }
//                    return role.orElse(null);
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//        if (roles.isEmpty()) {
//            roles.add(roleRepository.findRoleByName("ROLE_USER").get());
//        }
//        user.setRoles(roles);
//        return user;
//    }
//
//    public static UserCreateRequestDto fromEntityToCreateResponseDto(User user) {
//        return new UserCreateRequestDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
//                user.getRoles().stream().map(RoleService::fromEntity).collect(Collectors.toSet()));
//    }

}
