package com.filmesync.repository;

import com.filmesync.model.HistoricoFilme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoFilmeRepository extends JpaRepository<HistoricoFilme, Long> {

    /**
     * Busca o histórico de um filme específico pelo caminho
     */
    Optional<HistoricoFilme> findByCaminhoFilme(String caminhoFilme);

    /**
     * Verifica se um filme já foi visto hoje
     */
    boolean existsByCaminhoFilmeAndDataVisualizacaoBetween(String caminhoFilme, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Verifica se um filme com o mesmo nome já existe no histórico
     */
    boolean existsByNomeFilme(String nomeFilme);

    /**
     * Encontra todos os filmes com um determinado nome
     */
    List<HistoricoFilme> findAllByNomeFilme(String nomeFilme);

    /**
     * Remove todos os filmes com um determinado nome
     */
    void deleteByNomeFilme(String nomeFilme);

    /**
     * Busca um filme pelo nome
     */
    Optional<HistoricoFilme> findByNomeFilme(String nomeFilme);

    /**
     * Busca todos os filmes vistos, ordenados pela data de visualização (mais recente primeiro)
     */
    List<HistoricoFilme> findAllByOrderByDataVisualizacaoDesc();

    /**
     * Busca os últimos N filmes vistos
     */
    @Query(value = "SELECT h FROM HistoricoFilme h ORDER BY h.dataVisualizacao DESC")
    List<HistoricoFilme> findRecentFilmes();
}
