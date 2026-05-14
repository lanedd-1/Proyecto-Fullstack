package com.semestral.gestion_usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.model.Usuario;
import com.semestral.gestion_usuarios.repository.RolRepository;
import com.semestral.gestion_usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor

public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRep;
    private final RolRepository rolRep;
    @Override
    public void run(String... args){
        if(usuarioRep.count()>0){
            log.info("Tabla Productos con datos precargados, se omite la carga inicial");
            return;
        }

        Rol admin = new Rol();
        admin.setNombreRol("ADMIN");
        admin = rolRep.save(admin); // Cambiado: Guardamos y reasignamos

        Rol user = new Rol();
        user.setNombreRol("USER"); // Corregido: Antes decía admin.setNombreRol
        user = rolRep.save(user); // Cambiado: Guardamos y reasignamos

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setNombreU("Administrador");
        usuarioAdmin.setRutU("11111111-1");
        usuarioAdmin.setCorreoU("admin@JoyeriaEter.com");
        usuarioAdmin.setClaveU("1");
        usuarioAdmin.setRol(admin);
        usuarioRep.save(usuarioAdmin);

        Usuario usuario1 = new Usuario();
        usuario1.setNombreU("Usuario ejemplo");
        usuario1.setRutU("22222222-2");
        usuario1.setCorreoU("user@example.com");
        usuario1.setClaveU("1");
        usuario1.setRol(user);
        usuarioRep.save(usuario1);
    }
}
