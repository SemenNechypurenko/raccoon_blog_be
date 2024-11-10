package i.service;

import i.dto.RoleDto;
import i.model.Role;
import i.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public RoleDto save (RoleDto dto) {

        Role role = repository.findByName(dto.getName())
                .orElseGet(() -> repository.save(new Role(dto.getName())));

        return new RoleDto(role.getId(), role.getName());
    }

}
