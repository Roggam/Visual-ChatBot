package com.zipcode.java.project.visual.chatbot.repository;

import com.zipcode.java.project.visual.chatbot.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long>{
    Role findByRole(String role);
    
}
