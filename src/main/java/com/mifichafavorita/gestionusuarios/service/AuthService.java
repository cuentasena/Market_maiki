package com.mifichafavorita.gestionusuarios.service;

import com.mifichafavorita.gestionusuarios.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifichafavorita.gestionusuarios.dto.HttpGlobalResponse;
import com.mifichafavorita.gestionusuarios.dto.JwtDTO;
import com.mifichafavorita.gestionusuarios.dto.LoginRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.RegisterRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.RegisterResponseDTO;
import com.mifichafavorita.gestionusuarios.entity.Users;
import com.mifichafavorita.gestionusuarios.enums.RolEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    /**
     * Repositorio de usuarios
     */
    private final UserRepository userRepository;

    /**
     * Encriptación de contraseñas
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Servicio de jwt
     */
    private final JwtService jwtService;

    /**
     * Registro de usuario
     * 
     * @param request
     * @return RegisterResponseDTO
     */
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        RegisterResponseDTO response = new RegisterResponseDTO();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.setMessage("El correo ya está en uso");
            return response;
        }

        Long rolRegistro = request.getRol();
        if (rolRegistro == null) {
            rolRegistro = RolEnum.USUARIO.getId();
        }
        if (!RolEnum.CAJERO.coincide(rolRegistro) && !RolEnum.USUARIO.coincide(rolRegistro)) {
            response.setMessage("Rol no válido. Use 1 (CAJERO) o 2 (USUARIO)");
            return response;
        }

        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAge(request.getAge());
        user.setRolId(rolRegistro);
        userRepository.save(user);

        response.setMessage("Se ha registrado correctamente");
        return response;
    }

    /**
     * Inicio de sesión de usuario
     * 
     * @param request
     * @return HttpGlobalResponse<JwtDTO>
     */
    public HttpGlobalResponse<JwtDTO> login(LoginRequestDTO request) {
        HttpGlobalResponse<JwtDTO> response = new HttpGlobalResponse<>();
        Optional<Users> userFound = userRepository.findByEmail(request.getEmail());

        if (userFound.isEmpty()) {
            response.setMessage("Este usuario no se encuentra registrado");
            return response;
        }

        Users user = userFound.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Correo o contraseña son incorrectos");
            return response;
        }

        JwtDTO jwtDTO = new JwtDTO();
        String jwt = jwtService.generateToken(user.getId(), user.getRolId(), user.getEmail());
        jwtDTO.setJwt(jwt);
        response.setMessage("Inicio de sesión exitoso");
        response.setData(jwtDTO);
        return response;
    }

    /**
     * Refresco del JWT
     * 
     * @param token
     * @return JwtDTO
     * @throws Exception
     */
    public JwtDTO refreshToken(String token) throws Exception{
        JwtDTO response = new JwtDTO();
        String jwt = jwtService.refreshToken(token);
        response.setJwt(jwt);
        return response;
    }
}
