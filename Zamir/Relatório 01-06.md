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
- [x] Injeção de logs de transição de Enable/Disable
- [ ] Documentação do README de instalação concluída
- [ ] Resolução da pendência de firmware da Titan Quad

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Checklist de Segurança e Análise de Riscos)](./Relat%C3%B3rio%2028-05)
* [📑 Relatório Técnico - 01/06/2026 (Ambiente WPILib, Build e Lógica Aplicada)](./Relat%C3%B3rio%2001-06)

---

## 📑 Relatório de Atividades Diárias (01/06/2026)

### 📌 Resumo das Atividades do Dia
* **Rollback e Compatibilização de Software:** Atualização e calibração do ecossistema de desenvolvimento WPILib para a versão estável de 2020, alinhando as dependências do GradleRIO às exigências do controlador técnico.
* **Organização do Portfólio de Firmware:** Estruturação, categorização e limpeza dos softwares utilitários de programação e gerenciamento das controladoras lógicas centrais VMX-pi e Titan Quad.
* **Diagnóstico de Comunicação e Telemetria:** Configuração das interfaces de instrumentação em tempo real, realizando a integração de dados lógicos entre a simulação via MockDS e os dashboards do Shuffleboard.

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
      <td><code>Ambiente/WPILib_2020</code></td>
      <td>Configuração do deploy de software do projeto e fixação do ecossistema na versão 2020 para total compatibilidade de bibliotecas.</td>
    </tr>
    <tr>
      <td><code>Controladoras/Gerenciamento_Bancada</code></td>
      <td>Organização lógica e estrutural dos caminhos de execução dos programas de firmware da VMX-pi e ferramentas utilitárias da Titan Quad.</td>
    </tr>
    <tr>
      <td><code>Interface/Shuffleboard_MockDS</code></td>
      <td>Mapeamento do fluxo de rede e canais de dados de telemetria utilizando a ferramenta gráfica Shuffleboard conectada ao ambiente MockDS.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Limpeza geral e classificação dos programas do ambiente de desenvolvimento. Atualização sistemática do stack WPILib para o padrão funcional de 2020 e verificação da estabilidade estática dos diretórios de controle.

<div align="center">
  <img src="https://github.com/vitorZamir9/WorldSkill_Senai-23/blob/03c902be67f7e7522d6adad7db5e376ffef7fd39/Zamir/Evid%C3%AAncias/organiza%C3%A7%C3%A3o%20area%20de%20trabalho.png" style="width:30%;" alt="Organização da área de trabalho"/>
</div>

<br/>

* **Etapa 2 (Tarde):** Execução do protocolo de atualização de firmware na controladora física Titan Quad. O sistema apresentou uma falha no barramento de escrita, fazendo com que o driver permanecesse retido na versão legada `1.0.0`. O erro foi isolado para análise de hardware e abertura de relatório de falhas.

<div align="center">
  <img src="https://github.com/vitorZamir9/WorldSkill_Senai-23/blob/0febf456ed64ecb9d08bd3b72176602c33b22bb7/Zamir/Evid%C3%AAncias/problema%20atualizar%20titan.png" style="width:30%;" alt="Problema na atualização da Titan"/>
</div>

<br/>

* **Etapa 3 (Final):** Inicialização do ecossistema simulado utilizando a interface básica do MockDS para forçar chaveamento lógico, acompanhando de forma concorrente a plotagem e leitura de dados variáveis nos grafos de telemetria do Shuffleboard.

<div align="center">
  <img src="https://github.com/vitorZamir9/WorldSkill_Senai-23/blob/605eb33c4295273dd9786b92f411022760504bb2/Zamir/Evid%C3%AAncias/ambiente%20de%20simula%C3%A7%C3%A3o.png" style="width:70%;" alt="Ambiente de simulação"/>
</div>

<br/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Integração Lógica):** Reconhecimento imediato do tráfego de dados e inicialização nominal das variáveis de estado no ambiente Shuffleboard sob comandos ativos enviados via interface gráfica do MockDS.
* **Teste de Limite (Versionamento):** Compilação completa do código-fonte utilizando a suite GradleRIO na versão configurada de 2020, mitigando erros de compatibilidade de API de desenvolvimento antigo.
* **Teste de Borda (Falha de Firmware Bloqueado):** Tentativa sistemática de deploy e injeção do novo firmware na Titan Quad com interrupção forçada por erro de barramento, validando que, apesar de não atualizar e reter a versão `1.0.0`, o sistema mantém sua integridade de boot sem inutilizar os canais periféricos.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**.
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara.
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas.
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino.

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
