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

/**
 * Lógica de autenticación: alta de usuarios, login y refresco de token.
 * Las contraseñas se persisten solo con hash BCrypt.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    /** Acceso a persistencia de {@link com.mifichafavorita.gestionusuarios.entity.Users}. */
    private final UserRepository userRepository;

    /** Codificación y comparación segura de contraseñas. */
    private final PasswordEncoder passwordEncoder;

    /** Generación y refresco de JWT tras login exitoso. */
    private final JwtService jwtService;

    /**
     * Registra un usuario nuevo. Si no se envía rol, se asigna {@link RolEnum#USUARIO}.
     * Valida que {@code rol} sea solo 1 (CAJERO) o 2 (USUARIO).
     *
     * @param request datos del formulario de registro (validados en controlador con {@code @Valid})
     * @return mensaje de éxito o de error de negocio (correo duplicado, rol inválido)
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
     * Autentica por correo y contraseña; si coincide, devuelve JWT con {@code userId} y {@code rolId}.
     *
     * @param request correo y contraseña en texto plano (esta última se compara con el hash guardado)
     * @return envoltorio con mensaje y, si aplica, {@link JwtDTO} con el token
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
     * Solicita un nuevo JWT a partir de uno aún válido (delega en {@link JwtService#refreshToken(String)}).
     *
     * @param token JWT actual (sin prefijo {@code Bearer})
     * @return DTO con el nuevo token
     * @throws Exception si el token no puede refrescarse
     */
    public JwtDTO refreshToken(String token) throws Exception{
        JwtDTO response = new JwtDTO();
        String jwt = jwtService.refreshToken(token);
        response.setJwt(jwt);
        return response;
    }
}
