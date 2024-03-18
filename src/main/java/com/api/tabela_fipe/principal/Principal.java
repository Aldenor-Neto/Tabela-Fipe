package com.api.tabela_fipe.principal;

import com.api.tabela_fipe.model.Dados;
import com.api.tabela_fipe.model.Modelos;
import com.api.tabela_fipe.model.Veiculo;
import com.api.tabela_fipe.service.ConsumoApi;
import com.api.tabela_fipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String urlBase = "https://parallelum.com.br/fipe/api/v1/";
    private String endereco;

    public void exibirMenu() {
        System.out.println("qual o tipo de veiculo deseja consultar? \n  Carro, Moto ou Caminhão");
        var opcao = input.nextLine();

        if (opcao.toLowerCase().contains("carr")) {
            endereco = urlBase + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = urlBase + "motos/marcas";
        } else if (opcao.toLowerCase().contains("cam")) {
            endereco = urlBase + "caminao/marcas";
        } else {
            System.out.println("opção inválida!");
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = converte.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);

        System.out.println("informe o código da marca");
        var codigoMarca = input.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);

        var modelosMarcas = converte.obterDados(json, Modelos.class);
        System.out.println("Modelos da marca informada");
        modelosMarcas.modelos().stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);

        System.out.println("\n informe o nome do modelo ");
        var nomeVeiculo = input.nextLine();

        List<Dados> modelosFiltrados = modelosMarcas.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\n Modelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\n digite o código do modelo para ver a avaliação");
        var codigoModelo = input.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = converte.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = converte.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("todos os veiculos filtrados com suas respectivas informações ");
        veiculos.forEach(System.out::println);
    }
}
