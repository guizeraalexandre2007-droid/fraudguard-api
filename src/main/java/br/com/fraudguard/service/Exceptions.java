package br.com.fraudguard.service;

public final class Exceptions {
    private Exceptions() {}
    public static class NaoEncontradoException extends RuntimeException {
        public NaoEncontradoException(String mensagem) { super(mensagem); }
    }
    public static class ConflitoException extends RuntimeException {
        public ConflitoException(String mensagem) { super(mensagem); }
    }
}
