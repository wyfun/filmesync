package com.filmesync.controller;

import com.filmesync.config.SessaoUsuario;
import com.filmesync.model.HistoricoFilme;
import com.filmesync.service.HistoricoFilmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/historico")
public class HistoricoController {

    @Autowired
    private HistoricoFilmeService historicoService;

    @Autowired
    private SessaoUsuario sessaoUsuario;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping
    public String exibirHistorico(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false, defaultValue = "data") String ordenarPor,
            Model model) {

        // Buscar todo o histórico
        List<HistoricoFilme> historico = historicoService.buscarHistoricoCompleto();

        // Coletar todos os gêneros disponíveis
        List<String> generosDisponiveis = new ArrayList<>();
        for (HistoricoFilme filme : historico) {
            String generoFilme = filme.getGenero();
            if (generoFilme != null && !generoFilme.isEmpty() && !generosDisponiveis.contains(generoFilme)) {
                generosDisponiveis.add(generoFilme);
            }
        }

        // Ordenar a lista de gêneros
        Collections.sort(generosDisponiveis);

        // Filtrar por gênero, se especificado
        if (genero != null && !genero.isEmpty()) {
            if (genero.equals("sem-genero")) {
                historico = historico.stream()
                        .filter(filme -> filme.getGenero() == null || filme.getGenero().isEmpty())
                        .collect(Collectors.toList());
            } else {
                historico = historico.stream()
                        .filter(filme -> genero.equals(filme.getGenero()))
                        .collect(Collectors.toList());
            }
        }

        // Ordenar conforme solicitado
        if ("nome".equals(ordenarPor)) {
            historico.sort(Comparator.comparing(HistoricoFilme::getNomeFilme));
        } else if ("genero".equals(ordenarPor)) {
            historico.sort((f1, f2) -> {
                String genero1 = f1.getGenero() != null ? f1.getGenero() : "ZZZ";
                String genero2 = f2.getGenero() != null ? f2.getGenero() : "ZZZ";
                return genero1.compareToIgnoreCase(genero2);
            });
        } else { // data (padrão)
            historico.sort(Comparator.comparing(HistoricoFilme::getDataVisualizacao).reversed());
        }

        model.addAttribute("historico", historico);
        model.addAttribute("generosDisponiveis", generosDisponiveis);
        model.addAttribute("generoSelecionado", genero);
        model.addAttribute("ordenarPor", ordenarPor);
        model.addAttribute("formatter", FORMATTER);
        return "historico";
    }

    @GetMapping("/remover/{id}")
    public String removerFilme(@PathVariable Long id) {
        historicoService.removerFilmePorId(id);
        return "redirect:/historico";
    }

    @GetMapping("/remover-nome/{nome}")
    public String removerFilmesPorNome(@PathVariable String nome) {
        historicoService.removerFilmesPorNome(nome);
        return "redirect:/historico";
    }

    @GetMapping("/limpar")
    public String limparHistorico() {
        historicoService.limparHistorico();
        return "redirect:/historico";
    }
}
