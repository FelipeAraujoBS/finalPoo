package br.com.ucsal.service;

import java.util.List;

import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.ProdutoRepository;

public class ProdutoService {
 private ProdutoRepository<Produto, Integer> produtoRepository;

 public ProdutoService(ProdutoRepository<Produto, Integer> produtoRepository) {
     this.produtoRepository = produtoRepository;
 }

    public void adicionarProduto(Produto produto) {
        produtoRepository.adicionar(produto);
    }

 public void removerProduto(Integer id) {
     produtoRepository.remover(id);
 }

 public Produto obterProdutoPorId(Integer id) {
     return produtoRepository.obterPorID(id);
 }
 
 public void atualizarProduto(Produto p) {
	produtoRepository.atualizar(p);
}

 public List<Produto> listarProdutos() {
     return produtoRepository.listar();
 }
}

