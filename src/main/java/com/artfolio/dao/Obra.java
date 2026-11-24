
package com.artfolio;

import java.time.LocalDateTime;

public class Obra {

    private int id;
    private int artistaId;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String archivoUrl;
    private String imagenPortada;
    private LocalDateTime fechaPublicacion;
    private boolean publicada;

    public Obra() {
    }

    public Obra(int id, int artistaId, String titulo,
            String descripcion, String categoria,
            String archivoUrl, String imagenPortada,
            LocalDateTime fechaPublicacion,
            boolean publicada) {

        this.id = id;
        this.artistaId = artistaId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.archivoUrl = archivoUrl;
        this.imagenPortada = imagenPortada;
        this.fechaPublicacion = fechaPublicacion;
        this.publicada = publicada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(int artistaId) {
        this.artistaId = artistaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public String getImagenPortada() {
        return imagenPortada;
    }

    public void setImagenPortada(String imagenPortada) {
        this.imagenPortada = imagenPortada;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(
            LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public boolean isPublicada() {
        return publicada;
    }

    public void setPublicada(boolean publicada) {
        this.publicada = publicada;
    }
}
