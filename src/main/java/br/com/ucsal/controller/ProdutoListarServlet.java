package br.com.ucsal.controller;

import java.io.IOException;
import java.util.List;

import br.com.ucsal.annotations.Rota;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.PersistenciaFactory;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.ucsal.annotations.Inject;

@Rota(value = "/listarProdutos")
public class ProdutoListarServlet implements Command {
    private Integer memoriaValue = 1;
    private PersistenciaFactory persistenciaFactory = new PersistenciaFactory();

    @Inject
    private ProdutoService produtoService;

    // Construtor
    public ProdutoListarServlet() {
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
        try {
            List<Produto> produtos = produtoService.listarProdutos();
            if (produtos == null) {
                produtos = List.of(); // Evitar null-pointer
            }

            System.out.println("Debug - Produtos encontrados: " + produtos.size());
            if (!produtos.isEmpty()) {
                System.out.printf("Debug - Primeiro produto ID: %d, Último produto ID: %d%n",
                        produtos.get(0).getId(), produtos.get(produtos.size() - 1).getId());
            }

            request.setAttribute("produtos", produtos);
            System.out.println(produtos);
            response.setContentType("text/html;charset=UTF-8");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/produtolista.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao processar a requisição. Detalhes: " + e.getMessage());
        }
    }
}
