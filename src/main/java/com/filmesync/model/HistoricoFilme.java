package com.filmesync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_filmes")
public class HistoricoFilme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caminhoFilme;

    @Column(nullable = false)
    private String nomeFilme;

    @Column(nullable = false)
    private LocalDateTime dataVisualizacao;

    @Column(length = 1000)
    private String posterUrl;

    @Column(length = 1000, nullable = true)
    private String usuariosVisualizacao;

    @Column(nullable = true)
    private String genero;

    // Construtores
    public HistoricoFilme() {
    }

    public HistoricoFilme(String caminhoFilme, String nomeFilme, LocalDateTime dataVisualizacao, String posterUrl, String usuariosVisualizacao) {
        this.caminhoFilme = caminhoFilme;
        this.nomeFilme = nomeFilme;
        this.dataVisualizacao = dataVisualizacao;
        this.posterUrl = posterUrl;
        this.usuariosVisualizacao = usuariosVisualizacao;
        this.genero = null; // Inicialmente sem gênero definido
    }

    public HistoricoFilme(String caminhoFilme, String nomeFilme, LocalDateTime dataVisualizacao, String posterUrl, String usuariosVisualizacao, String genero) {
        this.caminhoFilme = caminhoFilme;
        this.nomeFilme = nomeFilme;
        this.dataVisualizacao = dataVisualizacao;
        this.posterUrl = posterUrl;
        this.usuariosVisualizacao = usuariosVisualizacao;
        this.genero = genero;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaminhoFilme() {
        return caminhoFilme;
    }

    public void setCaminhoFilme(String caminhoFilme) {
        this.caminhoFilme = caminhoFilme;
    }

    public String getNomeFilme() {
        return nomeFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public LocalDateTime getDataVisualizacao() {
        return dataVisualizacao;
    }

    public void setDataVisualizacao(LocalDateTime dataVisualizacao) {
        this.dataVisualizacao = dataVisualizacao;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getUsuariosVisualizacao() {
        return usuariosVisualizacao;
    }

    public void setUsuariosVisualizacao(String usuariosVisualizacao) {
        this.usuariosVisualizacao = usuariosVisualizacao;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Adiciona um usuário à lista de visualizações se ele ainda não estiver presente
     */
    public void adicionarUsuario(String nomeUsuario) {
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            return;
        }

        // Normaliza o nome do usuário
        String nomeNormalizado = nomeUsuario.trim();

        // Inicializa a string se for nula ou vazia
        if (this.usuariosVisualizacao == null || this.usuariosVisualizacao.trim().isEmpty()) {
            this.usuariosVisualizacao = nomeNormalizado;
            System.out.println("Inicializando lista de usuários com: " + nomeNormalizado);
            return;
        }

        // Normaliza a lista de usuários atual
        String listaAtual = this.usuariosVisualizacao.trim();

        // Verifica se o usuário já está na lista
        String[] usuarios = listaAtual.split(",");
        for (String usuario : usuarios) {
            if (usuario.trim().equalsIgnoreCase(nomeNormalizado)) {
                System.out.println("Usuário '" + nomeNormalizado + "' já está na lista");
                return; // Usuário já está na lista
            }
        }

        // Adiciona o usuário à lista
        this.usuariosVisualizacao = listaAtual + ", " + nomeNormalizado;
        System.out.println("Adicionado usuário '" + nomeNormalizado + "' à lista. Nova lista: '" + this.usuariosVisualizacao + "'");
    }

    /**
     * Retorna a lista de usuários que visualizaram o filme
     */
    public String[] getUsuarios() {
        if (this.usuariosVisualizacao == null || this.usuariosVisualizacao.trim().isEmpty()) {
            System.out.println("Nenhum usuário encontrado, retornando 'Desconhecido'");
            return new String[]{"Desconhecido"};
        }

        // Normaliza e divide a string de usuários
        String[] usuarios = this.usuariosVisualizacao.split(",");
        for (int i = 0; i < usuarios.length; i++) {
            usuarios[i] = usuarios[i].trim();
        }

        // Remove possíveis entradas vazias
        java.util.List<String> listaFiltrada = new java.util.ArrayList<>();
        for (String usuario : usuarios) {
            if (!usuario.isEmpty()) {
                listaFiltrada.add(usuario);
            }
        }

        // Se a lista estiver vazia após a filtragem, retorna "Desconhecido"
        if (listaFiltrada.isEmpty()) {
            return new String[]{"Desconhecido"};
        }

        // Converte a lista filtrada de volta para array
        String[] resultado = listaFiltrada.toArray(new String[0]);
        System.out.println("Retornando lista de usuários: " + String.join(", ", resultado));
        return resultado;
    }

    @Override
    public String toString() {
        return "HistoricoFilme{" +
                "id=" + id +
                ", caminhoFilme='" + caminhoFilme + '\'' +
                ", nomeFilme='" + nomeFilme + '\'' +
                ", dataVisualizacao=" + dataVisualizacao +
                ", usuariosVisualizacao='" + usuariosVisualizacao + '\'' +
                ", genero='" + genero + '\'' +
                '}';
    }
}
