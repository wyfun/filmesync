package com.filmesync.service;

import com.filmesync.config.SessaoUsuario;
import com.filmesync.model.HistoricoFilme;
import com.filmesync.repository.HistoricoFilmeRepository;
import com.filmesync.util.ImdbApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoFilmeService {

    @Autowired
    private HistoricoFilmeRepository historicoRepository;

    @Autowired
    private SessaoUsuario sessaoUsuario;

    /**
     * Registra a visualização de um filme no histórico
     * Se o filme já existe, atualiza a data de visualização
     */
    public void registrarVisualizacao(String caminhoFilme) {
        // Extrai o nome do filme do caminho
        String nomeFilme = extrairNomeFilme(caminhoFilme);

        // Verifica se o filme já existe no histórico
        Optional<HistoricoFilme> filmeExistente = historicoRepository.findByNomeFilme(nomeFilme);

        // Obter o nome do usuário atual
        String nomeUsuarioAtual = sessaoUsuario.isLogado() ? sessaoUsuario.getUsername() : "Visitante";
        System.out.println("Usuário atual: " + nomeUsuarioAtual + ", Logado: " + sessaoUsuario.isLogado());

        if (filmeExistente.isPresent()) {
            // Atualiza a data de visualização do filme existente
            HistoricoFilme historico = filmeExistente.get();
            historico.setDataVisualizacao(LocalDateTime.now());

            // Adiciona o usuário atual à lista de visualizações
            historico.adicionarUsuario(nomeUsuarioAtual);

            historicoRepository.save(historico);
            System.out.println("Data de visualização atualizada para o filme: " + nomeFilme + " por " + nomeUsuarioAtual);
            System.out.println("Lista de usuários atual: " + historico.getUsuariosVisualizacao());
        } else {
            // Busca o poster do filme
            String posterUrl = ImdbApiUtil.getPosterUrl(caminhoFilme);

            // Cria um novo registro no histórico
            HistoricoFilme historico = new HistoricoFilme(
                    caminhoFilme,
                    nomeFilme,
                    LocalDateTime.now(),
                    posterUrl,
                    nomeUsuarioAtual // Inicializa com o primeiro usuário
            );

            historicoRepository.save(historico);
            System.out.println("Filme adicionado ao histórico: " + nomeFilme + " por " + nomeUsuarioAtual);
            System.out.println("Lista de usuários inicial: " + historico.getUsuariosVisualizacao());
        }
    }

    /**
     * Busca todos os filmes do histórico, ordenados por data (mais recente primeiro)
     */
    public List<HistoricoFilme> buscarHistoricoCompleto() {
        return historicoRepository.findAllByOrderByDataVisualizacaoDesc();
    }

    /**
     * Remove um filme do histórico pelo ID
     */
    public void removerFilmePorId(Long id) {
        if (historicoRepository.existsById(id)) {
            historicoRepository.deleteById(id);
            System.out.println("Filme removido do histórico com ID: " + id);
        } else {
            System.out.println("Filme com ID " + id + " não encontrado");
        }
    }

    /**
     * Remove todos os filmes com um determinado nome
     */
    public void removerFilmesPorNome(String nomeFilme) {
        try {
            historicoRepository.deleteByNomeFilme(nomeFilme);
            System.out.println("Todos os filmes com nome '" + nomeFilme + "' foram removidos");
        } catch (Exception e) {
            System.err.println("Erro ao remover filmes com nome '" + nomeFilme + "': " + e.getMessage());
        }
    }

    /**
     * Limpa todo o histórico de filmes
     */
    public void limparHistorico() {
        historicoRepository.deleteAll();
        System.out.println("Histórico de filmes limpo");
    }

    /**
     * Extrai o nome do filme a partir do caminho do arquivo
     */
    private String extrairNomeFilme(String caminhoFilme) {
        String fileName = caminhoFilme.substring(caminhoFilme.lastIndexOf('/') + 1);
        return fileName.replace(".mp4", "");
    }
}
