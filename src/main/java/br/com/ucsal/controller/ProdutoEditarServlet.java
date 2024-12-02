package br.com.ucsal.controller;

import java.io.IOException;

import br.com.ucsal.annotations.Inject;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.HSQLProdutoRepository;
import br.com.ucsal.persistencia.PersistenciaFactory;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.ucsal.annotations.Rota;

@Rota("/editarProduto")
public class ProdutoEditarServlet implements Command {
    private static final long serialVersionUID = 1L;
    private Integer memoriaValue = 1;
    private PersistenciaFactory persistenciaFactory = new PersistenciaFactory();

    @Inject
    private ProdutoService produtoService;

    // Construtor
    public ProdutoEditarServlet() {
        // Seleciona o tipo de repositório antes de injetar a dependência
        if (memoriaValue == 1) {
            DependencyInjector.inject(this, PersistenciaFactory.MEMORIA);
        } else {
            DependencyInjector.inject(this, PersistenciaFactory.HSQL);
        }

        if (this.produtoService == null) {
            throw new IllegalStateException("ProdutoService não foi injetado corretamente.");
        }
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String method = request.getMethod();

            if ("GET".equalsIgnoreCase(method)) {
                Integer id = Integer.parseInt(request.getParameter("id"));
                System.out.println(id);
                Produto produto = produtoService.obterProdutoPorId(id);

                System.out.println("Produto encontrado!");
                System.out.println(produto);

                if (produto == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado");
                    return;
                }

                request.setAttribute("produto", produto);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/produtoformulario.jsp");

                if (dispatcher == null) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Dispatcher não encontrado");
                    return;
                }

                dispatcher.forward(request, response);
            } else if ("POST".equalsIgnoreCase(method)) {
                // Atualizar produto
                Integer id = Integer.parseInt(request.getParameter("id"));
                String nome = request.getParameter("nome");
                double preco = Double.parseDouble(request.getParameter("preco"));

                Produto produto = new Produto(id, nome, preco);
                produtoService.atualizarProduto(produto);

                System.out.println("Produto atualizado!");
                System.out.println(produto);

                response.sendRedirect("listarProdutos");
            } else {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método não suportado");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato inválido para ID ou preço");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar a requisição: " + e.getMessage());
        }
    }
}

