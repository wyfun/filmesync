package com.filmesync.controller;

import com.filmesync.config.SessaoUsuario;
import com.filmesync.model.HistoricoFilme;
import com.filmesync.service.HistoricoFilmeService;
import com.filmesync.util.ImdbApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class FilmeController {

    private String filmeSelecionado;

    @Autowired
    private HistoricoFilmeService historicoService;

    @Autowired
    private SessaoUsuario sessaoUsuario;

    // Página de seleção de filmes
    @GetMapping("/selecionar-filme")
    public String listarFilmes(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false, defaultValue = "nome") String ordenarPor,
            Model model) {

        List<String> filmes = new java.util.ArrayList<>();
        Map<String, String> posterUrls = new HashMap<>();

        try {
            // Tenta acessar a pasta de vídeos
            File pastaVideos = new ClassPathResource("static/videos").getFile();

            if (pastaVideos.exists() && pastaVideos.isDirectory()) {
                try (Stream<Path> paths = Files.walk(pastaVideos.toPath())) {
                    filmes = paths
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".mp4"))
                            .map(path -> {
                                Path relativo = pastaVideos.toPath().relativize(path);
                                return "/videos/" + relativo.toString().replace(File.separator, "/");
                            })
                            .collect(Collectors.toList());

                    // Criar um mapa de filmes para suas imagens de poster
                    for (String filme : filmes) {
                        try {
                            // Buscar o poster do filme da API do IMDb
                            String posterUrl = ImdbApiUtil.getPosterUrl(filme);
                            posterUrls.put(filme, posterUrl);
                        } catch (Exception e) {
                            System.err.println("Erro ao buscar poster para " + filme + ": " + e.getMessage());
                            posterUrls.put(filme, "https://via.placeholder.com/200x300.png?text=Sem+Imagem");
                        }
                    }
                }
            } else {
                System.err.println("A pasta de vídeos não existe ou não é um diretório: " + pastaVideos.getAbsolutePath());
                model.addAttribute("erro", "A pasta de vídeos não foi encontrada. Por favor, crie a pasta 'static/videos' e adicione seus filmes.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao acessar a pasta de vídeos: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao acessar a pasta de vídeos: " + e.getMessage());
        }

        // Obter informações de gênero para cada filme
        Map<String, String> generosFilmes = new HashMap<>();
        List<String> generosDisponiveis = new ArrayList<>();

        // Buscar gêneros de todos os filmes no histórico
        List<HistoricoFilme> historicos = historicoService.buscarHistoricoCompleto();
        for (HistoricoFilme historico : historicos) {
            String nomeFilme = historico.getNomeFilme();
            String generoFilme = historico.getGenero();

            if (generoFilme != null && !generoFilme.isEmpty()) {
                for (String filme : filmes) {
                    String nomeFilmeAtual = filme.substring(filme.lastIndexOf('/') + 1).replace(".mp4", "");
                    if (nomeFilmeAtual.equals(nomeFilme)) {
                        generosFilmes.put(filme, generoFilme);
                        if (!generosDisponiveis.contains(generoFilme)) {
                            generosDisponiveis.add(generoFilme);
                        }
                        break;
                    }
                }
            }
        }

        // Ordenar a lista de gêneros disponíveis
        Collections.sort(generosDisponiveis);

        // Filtrar filmes por gênero, se especificado
        if (genero != null && !genero.isEmpty()) {
            filmes = filmes.stream()
                    .filter(filme -> {
                        String generoFilme = generosFilmes.get(filme);
                        return genero.equals("sem-genero") ?
                                (generoFilme == null || generoFilme.isEmpty()) :
                                genero.equals(generoFilme);
                    })
                    .collect(Collectors.toList());
        }

        // Ordenar filmes conforme solicitado
        if ("genero".equals(ordenarPor)) {
            filmes.sort((f1, f2) -> {
                String genero1 = generosFilmes.getOrDefault(f1, "ZZZ"); // Filmes sem gênero ficam por último
                String genero2 = generosFilmes.getOrDefault(f2, "ZZZ");
                return genero1.compareToIgnoreCase(genero2);
            });
        } else if ("nome".equals(ordenarPor)) {
            filmes.sort((f1, f2) -> {
                String nome1 = f1.substring(f1.lastIndexOf('/') + 1).replace(".mp4", "");
                String nome2 = f2.substring(f2.lastIndexOf('/') + 1).replace(".mp4", "");
                return nome1.compareToIgnoreCase(nome2);
            });
        }

        // Enviar a lista de filmes e os posters para o template
        model.addAttribute("filmes", filmes);
        model.addAttribute("posterUrls", posterUrls);
        model.addAttribute("generosFilmes", generosFilmes);
        model.addAttribute("generosDisponiveis", generosDisponiveis);
        model.addAttribute("generoSelecionado", genero);
        model.addAttribute("ordenarPor", ordenarPor);
        model.addAttribute("imagemPlaceholder", "https://via.placeholder.com/200x300.png?text=Sem+Imagem");

        return "selecionar-filme";
    }

    // Seleção de filme pelo usuário
    // Alterando a rota para redirecionar para /player
    @PostMapping("/selecionar-filme")
    public String selecionarFilme(@RequestParam String caminho, Model model) {
        this.filmeSelecionado = caminho;
        model.addAttribute("filmeSelecionado", filmeSelecionado);

        // Não registra a visualização aqui - será registrada quando o usuário assistir metade do filme

        return "redirect:/player";  // Redirecionando para /player
    }

    // Página do player para mostrar o filme selecionado
    @GetMapping("/player")
    public String exibirPlayer(Model model) {
        if (filmeSelecionado != null) {
            model.addAttribute("filmeSelecionado", filmeSelecionado);
        } else {
            model.addAttribute("aviso", "Nenhum filme selecionado.");
        }
        return "player";  // A página do player
    }

    // Rota para retornar o caminho do filme selecionado
    @GetMapping("/filme-selecionado")
    @ResponseBody
    public String getFilmeSelecionado() {
        return filmeSelecionado != null ? filmeSelecionado : "";
    }

    // Rota para registrar a visualização quando o filme passar da metade
    @PostMapping("/registrar-visualizacao")
    @ResponseBody
    public Map<String, Object> registrarVisualizacao() {
        Map<String, Object> resultado = new HashMap<>();

        if (filmeSelecionado != null) {
            // Registrar a visualização do filme no histórico
            historicoService.registrarVisualizacao(filmeSelecionado);

            resultado.put("sucesso", true);
            resultado.put("mensagem", "Visualização registrada com sucesso");
            resultado.put("filme", extrairNomeFilme(filmeSelecionado));
            resultado.put("usuario", sessaoUsuario.getUsername());
        } else {
            resultado.put("sucesso", false);
            resultado.put("mensagem", "Nenhum filme selecionado");
        }

        return resultado;
    }

    // Método auxiliar para extrair o nome do filme
    private String extrairNomeFilme(String caminhoFilme) {
        String fileName = caminhoFilme.substring(caminhoFilme.lastIndexOf('/') + 1);
        return fileName.replace(".mp4", "");
    }
}
