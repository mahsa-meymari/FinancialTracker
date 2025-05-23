package com.mahsa.user_service.entity; // Ensure this is correct


//import JPA annotations
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//@Entity: Marks a class as a JPA entity (mapped to a database table).
@Entity
@Table(name = "users") // Specifies the table name(optional)
public class User {
   //@Id – Specifies the primary key of the entity.
   @Id
   //@GeneratedValue: Defines how the primary key is generated
   @GeneratedValue(strategy = GenerationType.IDENTITY) // Database generates the ID
   private Long id;

   //@Column – Maps a class field to a table column;
   @Column(nullable = false, unique = true)
   private String username;
   @Column(nullable = false)
   private String password;
  
   // JPA requires a no-arg constructor
   public User() {}

   // constructor
   public User(String username, String password) {
       this.username = username;
       this.password = password;
   }

   //getter and setters
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }

   public String getUsername() { return username; }
   public void setUsername(String username) { this.username = username; }

   public String getPassword() { return password; }
   public void setPassword(String password) { this.password = password; }

}







