//package com.pagam.controller;
//
//import com.pagam.entity.Role;
//import com.pagam.entity.Utilisateur;
//import com.pagam.service.RoleService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequestMapping("/roles")
//public class RoleController {
//
//    private final RoleService roleService;
//
//    public RoleController(RoleService roleService) {
//        this.roleService = roleService;
//    }
//
//    @GetMapping
//    public String listeRoles(Model model) {
//        model.addAttribute("roles", roleService.getAllRoles());
//        return "roles/liste-roles";
//    }
//
//    @GetMapping("/ajouter")
//    public String formAjouterUtilisateur(Model model) {
//        model.addAttribute("utilisateur", new Utilisateur());
//        model.addAttribute("roles", Role.values()); // toutes les valeurs de l'enum
//        return "utilisateurs/ajouter-utilisateur";
//    }
//
//
//
//    @PostMapping("/save")
//    public String saveRole(@ModelAttribute Role role) {
//        roleService.saveRole(role);
//        return "redirect:/roles";
//    }
//
//    @GetMapping("/modifier/{id}")
//    public String modifierRole(@PathVariable Long id, Model model) {
//        model.addAttribute("role", roleService.getRoleById(id));
//        return "roles/modifier-role";
//    }
//
//    @GetMapping("/supprimer/{id}")
//    public String supprimerRole(@PathVariable Long id) {
//        roleService.deleteRole(id);
//        return "redirect:/roles";
//    }
//}
