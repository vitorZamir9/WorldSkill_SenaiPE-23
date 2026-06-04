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

---

## 📑 Relatório de Atividades Diárias (02/06/2026)

### 📌 Resumo das Atividades do Dia
* **Arquitetura Inicial do Hello Robot:** Criação e parametrização do esqueleto estrutural do projeto base, organizando a árvore de diretórios oficial orientada a objetos.
* **Modularização por Pacotes:** Segmentação do escopo de desenvolvimento em pacotes e classes específicas, isolando as responsabilidades de controle no padrão Java.
* **Organização Mínima de Subsistemas:** Mapeamento conceitual e instanciação de objetos para controle independente do robô, garantindo total compatibilidade com a WPILib.

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
      <td><code>Projeto/Hello_Robot_Java</code></td>
      <td>Instalação e verificação da árvore de diretórios raiz para recepção das classes de controle do ecossistema.</td>
    </tr>
    <tr>
      <td><code>Arquitetura/Pacotes_Modulares</code></td>
      <td>Organização de diretórios estruturados em pacotes Java para mitigar códigos acoplados ou arquivos únicos inflados.</td>
    </tr>
    <tr>
      <td><code>Subsistemas/Robot_Health</code></td>
      <td>Construção do primeiro molde de comportamento (classe) focado no tratamento isolado de rotinas lógicas e telemetria básica.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

* **Etapa 1 (Manhã):** Criação e validação do template oficial do robô através da extensão WPILib no VS Code. Estruturação dos pacotes internos e verificação visual do arquivo de configurações gerais para certificar que as dependências do GradleRIO apontassem para o ambiente modularizado de forma correta.

<div align="center">
  <img src="Evid%C3%AAncias/organiza%C3%A7%C3%A3o%20area%20de%20trabalho.jpg" style="width:30%;" alt="Estruturação de pacotes no VS Code"/>
</div>

<br/>

* **Etapa 2 (Tarde):** Criação de classes lógicas com nomenclatura estrita de engenharia para modelar o comportamento do robô. Instanciação dos primeiros objetos lógicos dentro do arquivo principal de loop e execução de ciclos de simulação para monitorar o correto fluxo de carregamento da arquitetura na bancada de desenvolvimento.

<div align="center">
  <img src="Evid%C3%AAncias/ambiente%20de%20simula%C3%A7%C3%A3o.jpg" style="width:30%;" alt="Validação da estrutura Hello Robot no simulador"/>
</div>

<br/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Conformidade de Nomenclatura):** Verificação de que 100% das classes e arquivos criados respeitam estritamente as regras de sintaxe da linguagem Java e as convenções estruturais adotadas por equipes de alta performance na WPILib.
* **Teste de Limite (Compilação Estrutural):** Execução de build limpo na árvore de pacotes modularizada, validando que o compilador consegue rastrear e indexar todas as dependências internas e importações de classes sem erros de escopo.
* **Teste de Borda (Prevenção de Monolitos):** Isolamento de métodos de diagnóstico de inicialização dentro de sua respectiva classe de subsistema, assegurando por testes estáticos que uma falha de carregamento ou modificação neste arquivo não interfira nas rotinas lógicas nativas do ciclo principal.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**.
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara.
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas.
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino.

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
