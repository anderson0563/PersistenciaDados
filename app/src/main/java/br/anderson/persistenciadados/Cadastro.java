package br.anderson.persistenciadados;

public class Cadastro {
    private String login;

    public Cadastro(){}

    public Cadastro(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
