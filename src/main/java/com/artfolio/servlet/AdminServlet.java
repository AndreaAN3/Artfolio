
package com.artfolio.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    private static final String ADMIN_VIEWS =
            "/WEB-INF/views/admin/";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Evitar caché en páginas privadas

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        // 2. Obtener ruta solicitada

        String ruta =
                request.getPathInfo();

        // 3. Redirigir al dashboard si no hay subruta

        if (ruta == null || "/".equals(ruta)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/admin/dashboard"
            );

            return;
        }

        // 4. Seleccionar vista administrativa

        String archivo;

        switch (ruta) {

            case "/dashboard":
                archivo = "dashboard.html";
                break;

            case "/editor":
                archivo = "editor.html";
                break;

            case "/mis-obras":
                archivo = "mis-obras.html";
                break;

            case "/mis-enlaces":
                archivo = "mis-enlaces.html";
                break;

            default:

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Página administrativa no encontrada."
                );

                return;
        }

        // 5. Construir ruta interna de la vista

        String vista =
                ADMIN_VIEWS + archivo;

        // 6. Obtener dispatcher

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(vista);

        if (dispatcher == null) {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo cargar la página administrativa."
            );

            return;
        }

        // 7. Cargar la vista privada

        dispatcher.forward(
                request,
                response
        );
    }
}
