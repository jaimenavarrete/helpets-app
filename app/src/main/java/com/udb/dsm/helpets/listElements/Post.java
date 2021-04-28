package com.udb.dsm.helpets.listElements;

public class Post {
    private String postId;
    private String postTitulo;
    private String postCategoria;
    private String postDescripcion;
    private String postFoto;

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitulo() {
        return postTitulo;
    }

    public void setPostTitulo(String postTitulo) {
        this.postTitulo = postTitulo;
    }

    public String getPostCategoria() {
        return postCategoria;
    }

    public void setPostCategoria(String postCategoria) {
        this.postCategoria = postCategoria;
    }

    public String getPostDescripcion() {
        return postDescripcion;
    }

    public void setPostDescripcion(String postDescripcion) {
        this.postDescripcion = postDescripcion;
    }

    public String getPostFoto() {
        return postFoto;
    }

    public void setPostFoto(String postFoto) {
        this.postFoto = postFoto;
    }

    @Override
    public String toString() {
        return postTitulo ;
    }
}
