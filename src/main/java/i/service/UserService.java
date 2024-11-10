package i.service;

import i.dto.RoleDto;
import i.dto.UserCreateRequestDto;
import i.dto.UserCreateResponseDto;
import i.model.Role;
import i.model.User;
import i.repository.RoleRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public UserCreateResponseDto save(UserCreateRequestDto userCreateRequestDto) {
        User user = convertToEntity(userCreateRequestDto);
        user = userRepository.save(user);
        return convertFromEntity(user);
    }

    /**
     * Converts the UserCreateRequestDto entity to User.
     */
    public User convertToEntity(UserCreateRequestDto userCreateRequestDto) {
        // Use ModelMapper for basic mapping
        User user = modelMapper.map(userCreateRequestDto, User.class);

        // Set roles by getting them from RoleRepository
        Set<Role> roles = getRolesFromDto(userCreateRequestDto.getRoles());
        user.setRoles(roles);
        return user;
    }


    /**
     * Converts the User entity to UserCreateResponseDto.
     */
    public UserCreateResponseDto convertFromEntity(User user) {

        // Use ModelMapper for basic mapping
        UserCreateResponseDto responseDto = modelMapper.map(user, UserCreateResponseDto.class);

        // Set the roles for the response
        Set<RoleDto> roleDtos = user.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        responseDto.setRoles(roleDtos);

        return responseDto;
    }

    /**
     * Method for getting roles from DTO
     */
    private Set<Role> getRolesFromDto(Set<RoleDto> roleDtos) {
        Set<Role> roles = new HashSet<>();
        if (roleDtos.isEmpty()) {
            roles.add(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found")));
        } else {
            roleDtos.forEach(roleDto ->
                    roleRepository.findByName(roleDto.getName())
                            .ifPresent(roles::add));
        }
        return roles;
    }


}
