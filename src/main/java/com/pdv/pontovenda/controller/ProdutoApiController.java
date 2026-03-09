package com.pdv.pontovenda.controller;

import com.pdv.pontovenda.entity.Produto;
import com.pdv.pontovenda.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API para operacoes CRUD de Produto.
 * Endpoints expostos sob /api/produtos para integracao e testes automatizados.
 * Aplica fail early: valida entradas antes de processar.
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoApiController {

    private final ProdutoService produtoService;

    public ProdutoApiController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        Produto salvo = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        Produto atualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um numero positivo");
        }
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
