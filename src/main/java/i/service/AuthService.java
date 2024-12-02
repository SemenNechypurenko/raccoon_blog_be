package i.service;

import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.dto.UserCreateResponseDto;
import i.model.User;
import i.repository.UserRepository;
import i.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public TokenDto token(AuthenticationRequestDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());
        final User currentUser = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.isEmailVerified()) {
            throw new RuntimeException("Email is not verified");
        }

        UserCreateResponseDto userCreateResponseDto
                = modelMapper.map(currentUser, UserCreateResponseDto.class);
        String token = jwtUtils.generateToken(userDetails);

        return new TokenDto(userCreateResponseDto, token);
    }
}
