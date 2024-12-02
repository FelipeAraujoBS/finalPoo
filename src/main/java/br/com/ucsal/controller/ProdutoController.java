package br.com.ucsal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.ucsal.annotations.Rota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/*")
public class ProdutoController extends HttpServlet {

    private Map<String, Command> commands = new HashMap<>();

    @Override
    public void init() {
        // Reflete sobre todas as classes que implementam Command
        Set<Class<?>> classes = Set.of(
                ProdutoListarServlet.class,
                ProdutoEditarServlet.class,
                ProdutoAdicionarServlet.class,
                ProdutoExcluirServlet.class
        );

        // Mapeia os comandos dinamicamente com base nas anotações
        for (Class<?> clazz : classes) {
            Rota rota = clazz.getAnnotation(Rota.class);
            if (rota != null) {
                try {
                    Command command = (Command) clazz.getDeclaredConstructor().newInstance();
                    commands.put(rota.value(), command);
                    System.out.println("Registered command: " + rota.value() + " -> " + clazz.getSimpleName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());

        // Normaliza o Path
        path = path.trim();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        System.out.println("Normalized Path: " + path);

        // Se for um JSP, deixa ser processado normalmente.
        if (path.startsWith("WEB-INF/") || path.endsWith(".jsp")) {
            chain(request, response);
            return;
        }

        // Verificar se há um comando correspondente à rota
        Command command = commands.get("/" + path);
        System.out.println("Matching Command: " + command);

        if (command == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comando não encontrado para rota: " + path);
        } else {
            command.execute(request, response);
        }
    }

    private void chain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Usa o servlet default para lidar com os recursos
        request.getServletContext()
                .getNamedDispatcher("default")
                .forward(request, response);
    }
}