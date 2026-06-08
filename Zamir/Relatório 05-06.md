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
- [x] Implementação do fluxo de Enable/Disable com comandos e logs lógicos
- [x] Código de movimentação simulada e telemetria mínima dos motores
- [ ] Montagem elétrica final do robô (Pendente / Próxima Semana)
- [ ] Validação física com o Control Panel (Pendente / Próxima Semana)

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Checklist de Segurança e Análise de Riscos)](./Relat%C3%B3rio%2028-05)
* [📑 Relatório Técnico - 29/05/2026 (Fechamento do Baseline e Demonstração)](./Relat%C3%B3rio%2029-05)
* [📑 Relatório Técnico - 01/06/2026 (Ambiente WPILib, Build e Lógica Aplicada)](./Relat%C3%B3rio%2001-06)
* [📑 Relatório Técnico - 02/06/2026 (Estruturação de Arquitetura e Subsistemas Java)](./Relat%C3%B3rio%2002-06)
* [📑 Relatório Técnico - 03/06/2026 (Controle de Estados, Logs e Simulação de Fluxo)](./Relat%C3%B3rio%2003-06)
* [📑 Relatório Técnico - 04/06/2026 (Validação de Build, Depuração e Documentação)](./Relat%C3%B3rio%2004-06)
* [📑 Relatório Técnico - 05/06/2026 (Lógica de Motores, Telemetria Mínima e Simulação)](./Relat%C3%B3rio%2005-06)

---

## 📑 Relatório de Atividades Diárias (05/06/2026)

### 📌 Resumo das Atividades do Dia
* **Desenvolvimento da Lógica de Tração:** Escrita e acoplamento do código de controle dos motores no ecossistema WPILib, permitindo que os atuadores virtuais respondessem aos comandos de aceleração.
* **Validação de Telemetria Mínima:** Configuração gráfica de dashboards para ler e exibir os dados de velocidade e status lógicos dos motores em tempo real durante os ciclos de teste.
* **Simulação Integrada em Equipe (Alejandro e Carol):** Trabalho colaborativo focado na validação dinâmica do fluxo lógico utilizando o simulador MockDS, garantindo a integridade do código do robô.
* **Mapeamento de Pendências da Elétrica:** Inspeção do hardware físico e postergação controlada da montagem elétrica e integração com o Control Panel para a próxima janela de cronograma.

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
      <td><code>Motores/Drive_Train_Logic</code></td>
      <td>Implementação das diretrizes lógicas para atuação dos motores dentro do ciclo de execução do código Java.</td>
    </tr>
    <tr>
      <td><code>Simulação/MockDS_Integration</code></td>
      <td>Ativação e acionamento dos comandos via MockDS para validar se o robô responde perfeitamente ao comportamento de andar.</td>
    </tr>
    <tr>
      <td><code>Hardware/Planejamento_Elétrico</code></td>
      <td>Análise de posicionamento de componentes (bateria, controladoras, barramentos) e formalização do plano de montagem física para a Semana 03.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Desenvolvimento conjunto em bancada de desenvolvimento. Criação dos objetos de controle de motor e vinculação da lógica de movimentação básica. Inicialização do projeto no ambiente WPILib para testar a injeção correta da telemetria mínima de resposta dos atuadores.

<div align="center">
  <img src="Evid%C3%AAncias/organiza%C3%A7%C3%A3o%20area%20de%20trabalho.jpg" style="width:30%;" alt="Programação em equipe da lógica dos motores"/>
</div>

<br/>

* **Etapa 2 (Tarde):** Execução de baterias de testes com o MockDS no modo Habilitado (Enable). Monitoramento dos gráficos de resposta no Shuffleboard, confirmando que o robô executou o fluxo lógico de andar com sucesso na simulação. Mapeamento final da área física do robô para organizar a transição e a execução da fiação elétrica na semana seguinte.

<div align="center">
  <img src="Evid%C3%AAncias/ambiente%20de%20simula%C3%A7%C3%A3o.jpg" style="width:30%;" alt="Validação do fluxo dos motores via MockDS"/>
</div>

<br/>

---

## 🗒️ Nota de Operação Técnica: Bloqueio de Hardware Física
> ⚠️ **Ponto de Controle de Cronograma:** O fluxo lógico de software e a simulação matemática estão 100% validados e homologados. No entanto, por restrição de tempo de bancada para preenchimento de checklists estruturais, a montagem da infraestrutura elétrica física do chassi não foi realizada nesta sessão. Esta atividade foi postergada estrategicamente para a **Semana 03**, onde será priorizado o cabeamento, fixação e comissionamento de sinais utilizando o utilitário **Control Panel** diretamente no robô real.

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Telemetria Dinâmica):** Acionamento dos comandos de movimentação no painel simulador com leitura e atualização instantânea dos eixos de velocidade nos gráficos analíticos de monitoramento.
* **Teste de Limite (Chaveamento Virtual):** Teste contínuo de rotação lógica dos motores por 2 minutos seguidos no simulador para avaliar se ocorria alguma anomalia de transbordamento de dados ou travamento de thread.
* **Teste de Borda (Interrupção por Segurança):** Validação de que, ao cortar a comunicação ou forçar o modo Disable via MockDS, o envio de potência para os motores cai para zero de forma imediata e síncrona, mesmo que o código continue requisitando movimentação.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**.
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara.
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas.
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino.

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
