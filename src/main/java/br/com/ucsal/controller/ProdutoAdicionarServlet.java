package br.com.ucsal.controller;

import java.io.IOException;
import br.com.ucsal.annotations.Inject;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.ucsal.annotations.Rota;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.PersistenciaFactory;

@Rota(value= "/adicionarProduto")
public class ProdutoAdicionarServlet implements Command {
    private static final long serialVersionUID = 1L; //Sinceramente não sei para o que serve.
    private Integer memoriaValue = 1;
    private PersistenciaFactory persistenciaFactory = new PersistenciaFactory();

    @Inject
    private ProdutoService produtoService;

    public ProdutoAdicionarServlet() {
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
            String nomeProduto = request.getParameter("nome");
            String precoProdutoStr = request.getParameter("preco");

            // Aqui valido se os dados são válidos
            if (nomeProduto == null || nomeProduto.isEmpty() || precoProdutoStr == null || precoProdutoStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome e preço são obrigatórios");
                return;
            }

            // Converto o preço para Double
            double precoProduto = 0.0;
            try {
                precoProduto = Double.parseDouble(precoProdutoStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Preço inválido");
                return;
            }

            // Criar o objeto Produto
            Produto produto = new Produto();
            produto.setNome(nomeProduto);
            produto.setPreco(precoProduto);

            // Usar o ProdutoService para adicionar o produto ao repositório
            produtoService.adicionarProduto(produto);
            System.out.println(produto);
            System.out.println("Produto adicionado com sucesso");

            response.setContentType("text/html;charset=UTF-8");//Tentativa para azer o JSP funcionar. (Parece que não deu certo).
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/produtoformulario.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            System.err.println("Erro no processamento:");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar: " + e.getMessage());
        }
    }

}


