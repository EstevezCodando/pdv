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

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long contarTodos() {
        return usuarioRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return usuarioRepository.countByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        validarEmailUnico(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario existente = buscarPorId(id);

        validarEmailUnicoParaAtualizacao(usuarioAtualizado.getEmail(), id);
        aplicarAlteracoes(usuarioAtualizado, existente);

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    private void aplicarAlteracoes(Usuario origem, Usuario destino) {
        destino.setNome(origem.getNome());
        destino.setEmail(origem.getEmail());
        destino.setSenha(origem.getSenha());
        destino.setPerfil(origem.getPerfil());
        destino.setAtivo(origem.getAtivo());
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com o e-mail: " + email);
        }
    }

    private void validarEmailUnicoParaAtualizacao(String email, Long idAtual) {
        if (usuarioRepository.existsByEmailAndIdNot(email, idAtual)) {
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com o e-mail: " + email);
        }
    }
}
