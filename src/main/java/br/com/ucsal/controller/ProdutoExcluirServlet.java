package br.com.ucsal.controller;

import java.io.IOException;

import br.com.ucsal.annotations.Inject;
import br.com.ucsal.annotations.Rota;
import br.com.ucsal.persistencia.HSQLProdutoRepository;
import br.com.ucsal.persistencia.PersistenciaFactory;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Rota(value= "/excluirProduto")
public class ProdutoExcluirServlet implements Command {
	private static final long serialVersionUID = 1L;
	private Integer memoriaValue = 1;
	private PersistenciaFactory persistenciaFactory = new PersistenciaFactory();

	@Inject
	private ProdutoService produtoService;

	// Construtor
	public ProdutoExcluirServlet() {
		// Seleciona o tipo de repositório antes de injetar a dependência
		if (memoriaValue == 1) {
			DependencyInjector.inject(this, PersistenciaFactory.MEMORIA); // Injeção com repositório de memória
		} else {
			DependencyInjector.inject(this, PersistenciaFactory.HSQL); // Injeção com repositório HSQL
		}

		if (this.produtoService == null) {
			throw new IllegalStateException("ProdutoService não foi injetado corretamente.");
		}
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer id = Integer.parseInt(request.getParameter("id"));
		produtoService.removerProduto(id);
		System.out.println("Produto excluido com sucesso!");

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/produtolista.jsp");
		dispatcher.forward(request, response);
	}
}
