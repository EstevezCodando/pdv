package com.pdv.pontovenda.controller;

import com.pdv.pontovenda.entity.Usuario;
import com.pdv.pontovenda.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API para operacoes CRUD de Usuario.
 * Endpoints expostos sob /api/usuarios para integracao e testes automatizados.
 * Aplica fail early: valida entradas antes de processar.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        // Fail early: rejeita IDs invalidos antes de acessar o banco
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.salvar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        Usuario atualizado = usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
