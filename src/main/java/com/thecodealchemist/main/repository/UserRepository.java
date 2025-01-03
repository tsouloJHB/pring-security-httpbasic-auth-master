package com.thecodealchemist.main.repository;

import com.thecodealchemist.main.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByCellNumber(String cellNumber);
    User findByAccountNumber(Long accountNumber);
}
