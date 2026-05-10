package com.mifichafavorita.gestionusuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mifichafavorita.gestionusuarios.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
    Optional<Users> findByEmail(String email);
}