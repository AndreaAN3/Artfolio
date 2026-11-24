
package com.artfolio.servlet;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // 1. Evitar caché de la respuesta

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

        // 2. Obtener sesión existente
        // No crear una nueva sesión

        HttpSession session =
                request.getSession(false);

        // 3. Invalidar sesión del artista

        if (session != null) {
            session.invalidate();
        }

        // 4. Redirigir a la página de login

        response.sendRedirect(
                request.getContextPath()
                + "/login.html"
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // El cierre de sesión requiere POST

        response.setHeader(
                "Allow",
                "POST"
        );

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Utiliza POST para cerrar sesión."
        );
    }
}
