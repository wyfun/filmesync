package com.filmesync.controller;

import com.filmesync.model.HistoricoFilme;
import com.filmesync.repository.HistoricoFilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/genero")
public class GeneroController {

    @Autowired
    private HistoricoFilmeRepository historicoRepository;
    
    /**
     * Atualiza o gênero de um filme pelo nome
     */
    @PostMapping("/atualizar")
    public ResponseEntity<Map<String, Object>> atualizarGenero(
            @RequestParam String nomeFilme,
            @RequestParam String genero) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar todos os registros com o nome do filme
            List<HistoricoFilme> filmes = historicoRepository.findAll();
            boolean encontrado = false;
            
            for (HistoricoFilme filme : filmes) {
                if (filme.getNomeFilme().equals(nomeFilme)) {
                    filme.setGenero(genero);
                    historicoRepository.save(filme);
                    encontrado = true;
                }
            }
            
            if (encontrado) {
                response.put("sucesso", true);
                response.put("mensagem", "Gênero atualizado com sucesso para: " + genero);
                response.put("nomeFilme", nomeFilme);
                response.put("genero", genero);
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Filme não encontrado: " + nomeFilme);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao atualizar gênero: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtém o gênero de um filme pelo nome
     */
    @GetMapping("/obter")
    public ResponseEntity<Map<String, Object>> obterGenero(@RequestParam String nomeFilme) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar todos os registros com o nome do filme
            List<HistoricoFilme> filmes = historicoRepository.findAll();
            
            for (HistoricoFilme filme : filmes) {
                if (filme.getNomeFilme().equals(nomeFilme)) {
                    response.put("sucesso", true);
                    response.put("nomeFilme", nomeFilme);
                    response.put("genero", filme.getGenero());
                    return ResponseEntity.ok(response);
                }
            }
            
            response.put("sucesso", false);
            response.put("mensagem", "Filme não encontrado: " + nomeFilme);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao obter gênero: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
