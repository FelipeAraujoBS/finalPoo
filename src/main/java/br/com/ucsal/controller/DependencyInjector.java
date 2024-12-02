package br.com.ucsal.controller;

import br.com.ucsal.annotations.Inject;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.PersistenciaFactory;
import br.com.ucsal.persistencia.ProdutoRepository;
import br.com.ucsal.service.ProdutoService;

import java.lang.reflect.Field;

public class DependencyInjector {
    // Método para injetar dependências dinamicamente
    public static void inject(Object target, int repositoryType) {
        Class<?> clazz = target.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);

                try {
                    // Verifica se o campo é do tipo ProdutoService
                    if (field.getType() == ProdutoService.class) {
                        System.out.println("Injetando dependência: " + field.getName());

                        // Usa a PersistenciaFactory para escolher o repositório
                        ProdutoRepository<?, ?> repository = PersistenciaFactory.getProdutoRepository(repositoryType);

                        // Cria o ProdutoService com o repositório selecionado
                        ProdutoService service = new ProdutoService((ProdutoRepository<Produto, Integer>) repository);
                        field.set(target, service);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Erro ao injetar dependência", e);
                }
            }
        }
    }
}

