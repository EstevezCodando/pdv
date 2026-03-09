package com.pdv.pontovenda.service;

import com.pdv.pontovenda.entity.Usuario;
import com.pdv.pontovenda.exception.RecursoNaoEncontradoException;
import com.pdv.pontovenda.exception.RegraDeNegocioException;
import com.pdv.pontovenda.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsavel pelas regras de negocio relacionadas a Usuario.
 * Valida duplicidade de e-mail e garante consistencia nas operacoes CRUD.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lista todos os usuarios cadastrados.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca um usuario pelo ID.
     * @throws RecursoNaoEncontradoException se nao encontrado
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));
    }

    /**
     * Salva um novo usuario apos validar regras de negocio.
     * @throws RegraDeNegocioException se o e-mail ja estiver cadastrado
     */
    @Transactional
    public Usuario salvar(Usuario usuario) {
        validarEmailUnico(usuario);
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza um usuario existente.
     * @throws RecursoNaoEncontradoException se o usuario nao existir
     * @throws RegraDeNegocioException se o novo e-mail conflitar com outro usuario
     */
    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario existente = buscarPorId(id);

        validarEmailUnicoParaAtualizacao(usuarioAtualizado.getEmail(), id);

        existente.setNome(usuarioAtualizado.getNome());
        existente.setEmail(usuarioAtualizado.getEmail());
        existente.setSenha(usuarioAtualizado.getSenha());
        existente.setPerfil(usuarioAtualizado.getPerfil());
        existente.setAtivo(usuarioAtualizado.getAtivo());

        return usuarioRepository.save(existente);
    }

    /**
     * Remove um usuario pelo ID.
     * @throws RecursoNaoEncontradoException se o usuario nao existir
     */
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    /**
     * Valida que o e-mail nao esta cadastrado (para criacao).
     */
    private void validarEmailUnico(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com o e-mail: " + usuario.getEmail());
        }
    }

    /**
     * Valida que o e-mail nao pertence a outro usuario (para atualizacao).
     */
    private void validarEmailUnicoParaAtualizacao(String email, Long idAtual) {
        if (usuarioRepository.existsByEmailAndIdNot(email, idAtual)) {
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com o e-mail: " + email);
        }
    }
}
