package org.example.backendproject.Auth.repository;

import org.example.backendproject.Auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

}
