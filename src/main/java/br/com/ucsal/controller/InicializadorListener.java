package br.com.ucsal.controller;

import br.com.ucsal.persistencia.MemoriaProdutoRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import br.com.ucsal.persistencia.HSQLProdutoRepository;

@WebListener
public class InicializadorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        HSQLProdutoRepository produtoRepository = HSQLProdutoRepository.getInstance();

        System.out.println("Repositório HSQL inicializado");

        MemoriaProdutoRepository repository = MemoriaProdutoRepository.getInstancia();
        System.out.println("Repositório em memória inicializado");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HSQLProdutoRepository.getInstance().shutdown();
        MemoriaProdutoRepository.getInstancia().shutdown();
    }
}