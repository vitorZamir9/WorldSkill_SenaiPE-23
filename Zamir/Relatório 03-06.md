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
- [x] Estruturação inicial do projeto Hello Robot com pacotes e subsistemas
- [x] Implementação do fluxo de Enable/Disable com comandos e logs lógicos
- [ ] Documentação do README de instalação concluída
- [ ] Gravação do vídeo curto demonstrativo

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Checklist de Segurança e Análise de Riscos)](./Relat%C3%B3rio%2028-05)
* [📑 Relatório Técnico - 29/05/2026 (Fechamento do Baseline e Demonstração)](./Relat%C3%B3rio%2029-05)
* [📑 Relatório Técnico - 01/06/2026 (Ambiente WPILib, Build e Lógica Aplicada)](./Relat%C3%B3rio%2001-06)
* [📑 Relatório Técnico - 02/06/2026 (Estruturação de Arquitetura e Subsistemas Java)](./Relat%C3%B3rio%2002-06)
* [📑 Relatório Técnico - 03/06/2026 (Controle de Estados, Logs e Simulação de Fluxo)](./Relat%C3%B3rio%2003-06)

---

## 📑 Relatório de Atividades Diárias (03/06/2026)

### 📌 Resumo das Atividades do Dia
* **Controle de Estados via MockDS:** Implementação e validação prática do ciclo de vida básico do robô, manipulando as transições entre os estados lógicos de Enable e Disable.
* **Programação de Comandos e Logs Iniciais:** Desenvolvimento e injeção de rotinas lógicas para registrar de forma sistemática e visível os eventos de chaveamento diretamente no console técnico.
* **Rastreabilidade e Versionamento Funcional:** Criação de commits estruturados por etapa para registrar o primeiro fluxo comportamental dinâmico do robô no repositório Git.

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
      <td><code>Simulação/MockDS_Core</code></td>
      <td>Parametrização da interface de simulação para interceptar e testar de forma segura as rotinas de alteração de modos do robô.</td>
    </tr>
    <tr>
      <td><code>Software/Robot_Lifecycle_Logs</code></td>
      <td>Escrita e estruturação de chamadas lógicas e mensagens descritivas padronizadas ativadas por eventos de transição do sistema.</td>
    </tr>
    <tr>
      <td><code>Controle/Git_Versionamento</code></td>
      <td>Execução de testes, validação de integridade local e deploy de commits técnicos rastreáveis utilizando mensagens no padrão imperativo.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Programação dos métodos específicos do ciclo do robô para capturar a transição instantânea de estado. Vinculação dos comandos de logs iniciais e execução de builds limpos via GradleRIO para garantir a estabilidade do código.

<div align="center">
  <img src="Evid%C3%AAncias/organiza%C3%A7%C3%A3o%20area%20de%20trabalho.jpg" style="width:30%;" alt="Escrita e compilação do fluxo de enable e disable"/>
</div>

<br/>

* **Etapa 2 (Tarde):** Execução do código-fonte integrado à interface gráfica do painel MockDS. Alternância sistemática de chaves operacionais e monitoramento concorrente do terminal, registrando com sucesso as mensagens visíveis no console, seguido pelo commit funcional do primeiro fluxo dinâmico.

<div align="center">
  <img src="Evid%C3%AAncias/ambiente%20de%20simula%C3%A7%C3%A3o.jpg" style="width:30%;" alt="Logs de transição visíveis e simulação ativa"/>
</div>

<br/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Chaveamento em Simulação):** Demonstração bem-sucedida do fluxo, onde a ativação e desativação no painel do MockDS gera a exibição síncrona dos respectivos logs de controle de modo sem omissão de dados.
* **Teste de Limite (Rastreabilidade Git):** Execução do comando de histórico do Git gerando commits técnicos pequenos, isolados e com descrições normatizadas para documentar cada passo funcional do fluxo de lógica implementado.
* **Teste de Borda (Segurança de Estado Inicial):** Validação estática para garantir que o software inicie obrigatoriamente retido no modo de segurança (`disabled`) padrão e impeça a execução forçada de qualquer linha de comando antes da liberação explícita via painel de controle.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado[cite: 1]:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**.
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara.
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas.
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino.

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
