package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Shared query methods go here
    java.util.Optional<User> findByEmail(String email);
}