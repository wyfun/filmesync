package com.filmesync.controller;

import com.filmesync.config.SessaoUsuario;
import com.filmesync.model.HistoricoFilme;
import com.filmesync.repository.HistoricoFilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private HistoricoFilmeRepository historicoRepository;

    @Autowired
    private SessaoUsuario sessaoUsuario;

    @GetMapping("/historico")
    public List<HistoricoFilme> listarHistorico() {
        return historicoRepository.findAll();
    }

    @GetMapping("/historico/{id}")
    public Map<String, Object> detalhesHistorico(@PathVariable Long id) {
        Map<String, Object> resultado = new HashMap<>();

        Optional<HistoricoFilme> filme = historicoRepository.findById(id);
        if (filme.isPresent()) {
            HistoricoFilme historico = filme.get();
            resultado.put("id", historico.getId());
            resultado.put("nomeFilme", historico.getNomeFilme());
            resultado.put("dataVisualizacao", historico.getDataVisualizacao().toString());
            resultado.put("usuariosVisualizacao", historico.getUsuariosVisualizacao());
            resultado.put("usuarios", historico.getUsuarios());
        } else {
            resultado.put("erro", "Filme não encontrado");
        }

        return resultado;
    }

    @GetMapping("/historico/atualizar/{id}/{usuarios}")
    public Map<String, Object> atualizarUsuarios(@PathVariable Long id, @PathVariable String usuarios) {
        Map<String, Object> resultado = new HashMap<>();

        Optional<HistoricoFilme> filme = historicoRepository.findById(id);
        if (filme.isPresent()) {
            HistoricoFilme historico = filme.get();
            historico.setUsuariosVisualizacao(usuarios);
            historicoRepository.save(historico);

            resultado.put("mensagem", "Usuários atualizados com sucesso");
            resultado.put("id", historico.getId());
            resultado.put("nomeFilme", historico.getNomeFilme());
            resultado.put("usuariosVisualizacao", historico.getUsuariosVisualizacao());
        } else {
            resultado.put("erro", "Filme não encontrado");
        }

        return resultado;
    }

    @GetMapping("/sessao")
    public Map<String, Object> verificarSessao() {
        Map<String, Object> resultado = new HashMap<>();

        resultado.put("logado", sessaoUsuario.isLogado());
        resultado.put("username", sessaoUsuario.getUsername());
        resultado.put("admin", sessaoUsuario.isAdmin());

        return resultado;
    }

    @GetMapping("/historico/adicionar-usuario-todos/{usuario}")
    public Map<String, Object> adicionarUsuarioATodos(@PathVariable String usuario) {
        Map<String, Object> resultado = new HashMap<>();
        List<HistoricoFilme> todosFilmes = historicoRepository.findAll();

        int atualizados = 0;
        for (HistoricoFilme filme : todosFilmes) {
            filme.adicionarUsuario(usuario);
            atualizados++;
        }

        historicoRepository.saveAll(todosFilmes);

        resultado.put("mensagem", "Usuário '" + usuario + "' adicionado a " + atualizados + " registros");
        resultado.put("totalRegistros", todosFilmes.size());

        return resultado;
    }
}
