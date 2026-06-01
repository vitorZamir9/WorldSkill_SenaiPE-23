# 🤖 WorldSkills #23 - Mobile Robotics

## 📅 Cronograma Vigente
* **Semana:** 02[cite: 2]
* **Período:** 30/05/2026 a 05/06/2026[cite: 2]
* **Fase:** P0 - Ambiente oficial e lógica aplicada[cite: 2]
* **Projeto Central:** Hello Robot com MockDS e telemetria mínima[cite: 2]

---

## 📌 Status dos Entregáveis da Semana
De acordo com os critérios de aceitação estabelecidos para o projeto[cite: 2]:

- [x] Projeto Java estruturado e compilando[cite: 2]
- [x] Ambiente de simulação MockDS funcionando[cite: 2]
- [x] Injeção de logs de transição de Enable/Disable[cite: 2]
- [x] Criação do primeiro subsistema lógico (POO)[cite: 2]
- [ ] Documentação do README de instalação concluída[cite: 2]
- [ ] Vídeo curto demonstrativo do fluxo de simulação[cite: 2]

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Checklist de Segurança e Análise de Riscos)](./Relat%C3%B3rio%2028-05)
* [📑 Relatório Técnico - 01/06/2026 (Ambiente WPILib, Build e Lógica Aplicada)](./Relat%C3%B3rio%2001-06)

---

## 📑 Relatório de Atividades Diárias (01/06/2026)

### 📌 Resumo das Atividades do Dia
* **Auditoria de Ambiente e Build:** Verificação da pasta raiz do projeto no VS Code, validação das extensões da WPILib e execução do primeiro build de compilação via GradleRIO[cite: 2].
* **Lógica Aplicada e Simulação:** Configuração e execução do ecossistema de testes com o simulador MockDS, validando as transições de estado lógico do robô e a geração de telemetria[cite: 2].
* **Modularização em POO:** Desenvolvimento da primeira classe Java de subsistema estruturado (`SystemHealth`), isolando as chamadas e a responsabilidade dos logs lógicos centrais do programa[cite: 2].

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
      <td><code>Ambiente/VS_Code_WPILib</code></td>
      <td>Abertura do projeto pelo diretório raiz, inspeção dos arquivos <code>build.gradle</code> e execução de build limpo para depuração de erros de sintaxe Java[cite: 2].</td>
    </tr>
    <tr>
      <td><code>Simulação/MockDS</code></td>
      <td>Integração da lógica de controle com a interface do MockDS, permitindo o acionamento remoto dos estados de operação[cite: 2].</td>
    </tr>
    <tr>
      <td><code>Classes/SystemHealth</code></td>
      <td>Instanciação do primeiro objeto de subsistema para centralizar o rastreamento do estado de saúde física e modos lógicos da controladora[cite: 2].</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Resolução de falhas de ambiente onde as tarefas do GradleRIO não eram reconhecidas por abertura de subpastas incorretas (`src/`)[cite: 2]. Ajuste do diretório raiz no VS Code, preenchimento do checklist de ambiente e execução de build nominal bem-sucedido[cite: 2].
* **Etapa 2 (Tarde):** Implementação dos métodos públicos `.logStartup()` e `.logMode()` no subsistema Java[cite: 2]. Execução do código de simulação em tempo real alternando os modos no painel do MockDS para capturar de forma limpa as mensagens geradas no console[cite: 2].

<br/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Compilação):** Execução nominal do build do GradleRIO gerando status `BUILD SUCCESSFUL` sem avisos ou erros de sintaxe pendentes no terminal do VS Code[cite: 2].
* **Teste de Limite (Transição de Estado):** Ciclo sequencial repetido 3 vezes (Inicializar -> Desabilitar -> Habilitar -> Desabilitar) via MockDS, assegurando a precisão temporal dos logs de mudança de modo do robô[cite: 2].
* **Teste de Borda (Diagnóstico de Sintaxe):** Simulação de falha induzida por omissão de terminação de código (ponto e vírgula) e alteração de escopo de métodos lógicos para certificar a capacidade de identificar rapidamente arquivo e linha exatos no terminal sem recorrer a tentativas aleatórias[cite: 2].

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado[cite: 2]:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**[cite: 2].
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara[cite: 2].
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas[cite: 2].
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino[cite: 2].

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
