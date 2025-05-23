package com.mahsa.user_service.repository;


import com.mahsa.user_service.entity.User; // Import your User entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Optional, but good practice
import java.util.Optional;

@Repository // Marks this interface as a Spring Data repository (optional but good practice)
//User: The entity type this repository will manage. 
//Long: The data type of the primary key (id) of the User entity.
public interface UserRepository extends JpaRepository<User, Long> {



    // We can add custom query methods here later if needed, e.g.:
    // Optional provides a more elegant way to handle situations where a value might be absent (i.e., no user found with that username).
    // It helps avoid NullPointerExceptions.
    // If a user is found, the Optional will contain the User object.
    // If no user is found, the Optional will be "empty."
    
    //findByUsername: This is the magic of Spring Data JPA.
    //By following a specific naming convention (findBy<PropertyName>),
    //Spring Data JPA automatically understands what you want to do and generates the necessary SQL query (SELECT * FROM app_users WHERE username = ?) for you at runtime. 
    //You don't need to write the implementation!
    Optional<User>  findByUsername(String username);
    }



