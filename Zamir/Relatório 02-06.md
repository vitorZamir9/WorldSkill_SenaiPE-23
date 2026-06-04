# 🤖 WorldSkills #23 - Mobile Robotics

## 📅 Cronograma Vigente
* **Semana:** 02
* **Período:** 30/05/2026 a 05/06/2026
* **Fase:** P0 - Ambiente oficial e lógica aplicada
* **Projeto Central:** Hello Robot com MockDS e telemetria mínima

---

## 📌 Status dos Entregáveis da Semana
De acordo com os critérios de aceitação estabelecidos para o projeto:

- [x] Projeto Java estruturado e compilando
- [x] Ambiente de simulação MockDS funcionando
- [x] Configuração de ferramentas de telemetria (Shuffleboard)
- [x] Criação de classe Java para logs de transição (Enable/Disable
- [ ] Documentação do README de instalação concluída
- [ ] Gravação do vídeo curto demonstrativo

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Checklist de Segurança e Análise de Riscos)](./Relat%C3%B3rio%2028-05)
* [📑 Relatório Técnico - 29/05/2026 (Fechamento do Baseline e Demonstração)](./Relat%C3%B3rio%2029-05)
* [📑 Relatório Técnico - 01/06/2026 (Ambiente WPILib, Build e Lógica Aplicada)](./Relat%C3%B3rio%2001-06)
* [📑 Relatório Técnico - 02/06/2026 (Desenvolvimento de Classes Lógicas e Integração)](./Relat%C3%B3rio%2002-06)

---

## 📑 Relatório de Atividades Diárias (02/06/2026)

### 📌 Resumo das Atividades do Dia
* **Implementação de Arquitetura POO:** Criação e estruturação da classe Java `SystemHealth` baseada nos conceitos de programação orientada a objetos aplicados à robótica.
* **Injeção de Logs de Estado:** Programação e vinculação dos métodos públicos `.logStartup()` e `.logMode()` para registrar eventos de forma limpa e padronizada.
* **Acoplamento Lógico com a Simulação:** Integração da nova classe ao fluxo do ciclo de execução principal do robô, validando o recebimento de dados no terminal de depuração.

<br/>

### 📁 Sistemas e Componentes Gerenciados

<table width="100%">
  <thead>
    <tr>
      <th align="left">Módulo / Subsistema</th>
      <th align="left">Descrição das Ações Técnicas Executadas</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>Classes/SystemHealth</code></td>
      <td>Desenvolvimento do molde comportamental em Java para isolar a responsabilidade de monitoramento de status do núcleo de movimentação.</td>
    </tr>
    <tr>
      <td><code>Software/Robot_Lifecycle</code></td>
      <td>Instanciação do objeto e mapeamento de chamadas de métodos de telemetria dentro das rotinas estruturais da WPILib.</td>
    </tr>
    <tr>
      <td><code>Logs/Console_Output</code></td>
      <td>Padronização das strings de saída ("health:" e "mode:") para substituir prints genéricos e permitir filtros de busca eficientes.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Escrita do código-fonte da classe `SystemHealth` definindo os atributos privados de controle e os construtores de inicialização. Execução de builds frequentes via GradleRIO para garantir que nenhuma alteração quebrasse a compilação geral.

<div align="center">
  <img src="Evid%C3%AAncias/organiza%C3%A7%C3%A3o%20area%20de%20trabalho.jpg" style="width:30%;" alt="Estruturação de código e build funcional"/>
</div>

<br/>

* **Etapa 2 (Tarde):** Chamada das funções lógicas nos pontos de transição de estado da simulação. Ativação do ambiente e alternância sequencial de modos de controle para capturar a telemetria limpa diretamente na janela do console do desenvolvedor.

<div align="center">
  <img src="Evid%C3%AAncias/ambiente%20de%20simula%C3%A7%C3%A3o.jpg" style="width:30%;" alt="Execução dos logs lógicos em simulação"/>
</div>

<br/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Modularidade):** Chamada bem-sucedida do método construtor injetando o nome da tag do projeto, gerando a saída `health: [Projeto] inicializado` imediatamente após o boot do código.
* **Teste de Limite (Evidência Rastreável):** Validação de que a alternância manual de chaves na interface gráfica gera de forma síncrona os logs de estado sem atrasos ou estouro de memória no terminal.
* **Teste de Borda (Acoplamento Seguro):** Verificação de integridade lógica para garantir que a ausência ou falha pontual de execução em um método de log não interrompa o loop principal do programa ou cause o congelamento da simulação.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**.
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara.
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas.
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino.

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
