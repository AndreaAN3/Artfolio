
package com.artfolio.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.annotation.WebFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = {
    "/admin/*",
    "/api/privado/*"
})
public class AutenticacionFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        // ======================================
        // EVITAR CACHE DE CONTENIDO PRIVADO
        // ======================================

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader("Expires", 0);

        // ======================================
        // IDENTIFICAR TIPO DE SOLICITUD
        // ======================================

        String ruta = request.getRequestURI()
                .substring(
                    request.getContextPath().length()
                );

        boolean esApi =
                ruta.equals("/api/privado")
                || ruta.startsWith("/api/privado/");

        // ======================================
        // VERIFICAR SESION
        // ======================================

        HttpSession session =
                request.getSession(false);

        boolean autenticado = false;

        if (session != null) {

            Object artistaId =
                    session.getAttribute("artistaId");

            if (artistaId instanceof Integer id) {

                autenticado = id > 0;
            }
        }

        // ======================================
        // BLOQUEAR ACCESO NO AUTORIZADO
        // ======================================

        if (!autenticado) {

            if (esApi) {

                enviarError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Debes iniciar sesión."
                );

            } else {

                response.sendRedirect(
                    request.getContextPath()
                    + "/login.html"
                );
            }

            return;
        }

        // ======================================
        // GENERAR TOKEN CSRF
        // ======================================

        String csrfToken;

        synchronized (session) {

            csrfToken = (String)
                    session.getAttribute("csrfToken");

            if (csrfToken == null) {

                csrfToken =
                        UUID.randomUUID().toString();

                session.setAttribute(
                    "csrfToken",
                    csrfToken
                );
            }
        }

        // ======================================
        // PROTEGER OPERACIONES DE ESCRITURA
        // ======================================

        String metodo = request.getMethod();

        boolean lectura =
                "GET".equals(metodo)
                || "HEAD".equals(metodo)
                || "OPTIONS".equals(metodo);

        if (esApi && !lectura) {

            String tokenRecibido =
                    request.getHeader("X-CSRF-Token");

            if (!csrfToken.equals(tokenRecibido)) {

                enviarError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solicitud no autorizada."
                );

                return;
            }
        }

        // ======================================
        // PERMITIR ACCESO
        // ======================================

        chain.doFilter(request, response);
    }

    private void enviarError(
            HttpServletResponse response,
            int estado,
            String mensaje)
            throws IOException {

        response.setStatus(estado);

        response.setContentType(
            "application/json;charset=UTF-8"
        );

        response.getWriter().write(
            "{\"error\":\"" + mensaje + "\"}"
        );
    }
}
