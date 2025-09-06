//package com.pagam.service;
//
//import com.pagam.entity.Role;
//import com.pagam.repository.RoleRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class RoleService {
//
//    private final RoleRepository roleRepository;
//
//    public RoleService(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//
//    // Ajouter ou modifier un rôle
//    public Role saveRole(Role role) {
//        return roleRepository.save(role);
//    }
//
//    // Récupérer tous les rôles
//    public List<Role> getAllRoles() {
//        return roleRepository.findAll();
//    }
//
//    // Récupérer un rôle par ID
//    public Role getRoleById(Long id) {
//        return roleRepository.findById(id).orElse(null);
//    }
//
//    // Supprimer un rôle
//    public void deleteRole(Long id) {
//        roleRepository.deleteById(id);
//    }
//}
