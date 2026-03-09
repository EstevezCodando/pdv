package com.pdv.pontovenda.service;

import com.pdv.pontovenda.entity.Produto;
import com.pdv.pontovenda.exception.RecursoNaoEncontradoException;
import com.pdv.pontovenda.exception.RegraDeNegocioException;
import com.pdv.pontovenda.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsavel pelas regras de negocio relacionadas a Produto.
 * Valida duplicidade de codigo de barras e garante consistencia nas operacoes CRUD.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long contarTodos() {
        return produtoRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return produtoRepository.countByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    @Transactional
    public Produto salvar(Produto produto) {
        validarCodigoBarrasUnico(produto);
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto existente = buscarPorId(id);

        validarCodigoBarrasUnicoParaAtualizacao(produtoAtualizado.getCodigoBarras(), id);
        aplicarAlteracoes(produtoAtualizado, existente);

        return produtoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }

    private void aplicarAlteracoes(Produto origem, Produto destino) {
        destino.setNome(origem.getNome());
        destino.setDescricao(origem.getDescricao());
        destino.setPreco(origem.getPreco());
        destino.setQuantidadeEstoque(origem.getQuantidadeEstoque());
        destino.setCodigoBarras(origem.getCodigoBarras());
        destino.setAtivo(origem.getAtivo());
    }

    private void validarCodigoBarrasUnico(Produto produto) {
        String codigoBarras = produto.getCodigoBarras();
        if (codigoBarras != null && !codigoBarras.isBlank() && produtoRepository.existsByCodigoBarras(codigoBarras)) {
            throw new RegraDeNegocioException(
                    "Ja existe um produto com o codigo de barras: " + codigoBarras);
        }
    }

    private void validarCodigoBarrasUnicoParaAtualizacao(String codigoBarras, Long idAtual) {
        if (codigoBarras != null && !codigoBarras.isBlank()
                && produtoRepository.existsByCodigoBarrasAndIdNot(codigoBarras, idAtual)) {
            throw new RegraDeNegocioException(
                    "Ja existe um produto com o codigo de barras: " + codigoBarras);
        }
    }
}
