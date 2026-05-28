<p align="center">
  <img src="https://vectorseek.com/wp-content/uploads/2023/08/WorldSkills-Logo-Vector.svg-.png" alt="WorldSkills Logo" width="250"/>
</p>

<h1 align="center"> WorldsSkills - #23 Robótica Móvel </h1>

## Cronograma Semanal 
* **Semana de Trabalho:** 01
* **Intervalo de Dias:** 23/05/2026 a 29/05/2026
* **Status da Fase:** P0 - Alinhamento de hardware e checagem inicial
* **Foco Operacional:** Configuração do posto de trabalho e controle de insumos

---

## 📌 Alinhamento de Metas da Semana
Acompanhamento dos requisitos obrigatórios estabelecidos para o desenvolvimento em bancada:

- [x] Repositório Git configurado e operacional
- [x] Levantamento físico e contagem do Kit Studica Lyon
- [x] Aplicação do protocolo de proteção elétrica e segurança
- [x] Atualização dos diários de bordo individuais

---

## 📑 Relatório de Atividades em Bancada (28/05/2026)

### 📊 Metodologia Operacional do Dia
As atividades deste período focaram na validação das condições de segurança do laboratório e no mapeamento preventivo de falhas em sistemas de potência. O objetivo foi assegurar que o espaço físico e a distribuição de tarefas minimizem erros operacionais durante a preparação para a competição.

---

### ⏳ Linha do Tempo e Evolução Técnica

* **Período da Manhã: Inspeção de Segurança e Organização Estática**
  Foco na aplicação do checklist de conformidade do posto de trabalho. Realizamos a triagem das ferramentas de precisão e a verificação estrutural da bancada antes de efetuar qualquer conexão ou ligação física nos componentes de processamento do robô.
  
* **Período da Tarde: Mapeamento de Riscos e Divisão de Funções**
  Análise de pontos críticos na distribuição de energia da controladora. Efetuamos vistorias preventivas nas conexões de potência e na medição das baterias com multímetro para evitar curtos-circuitos, além de organizar o plano de ação individual para o diário técnico.

---

### ✅ Checklist de Validação e Segurança de Hardware

* **Verificação de Bancada (Segurança Geral):** Inspeção visual para certificar o correto acoplamento dos cabos de comunicação e alimentação nas interfaces lógicas essenciais.
* **Análise de Sinais (Mapeamento de Potência):** Checagem física da continuidade dos cabos elétricos principais e medição direta da tensão de operação para evitar sobrecarga elétrica no circuito.
* **Teste de Proteção (Comportamento de Emergência):** Monitoramento visual da integridade física dos fusíveis instalados no painel lateral de distribuição de potência para assegurar o corte correto em caso de picos de corrente.

---

### 📸 Evidências Fotograficas

-------------------------------------------------------------------------
📷 **Protocolo de Bancada:** Posicionamento e inspeção física estrutural do controlador lógico e fiação de dados.
-------------------------------------------------------------------------
<br/>
<div align="center">
  <img src="Evidências/EvidenciaControladorVMX28.05.jpg" style="width:45%;" alt="Módulo Controlador VMX Studica Robotics"/>
</div>
<br/>

-------------------------------------------------------------------------
📷 **Análise de Riscos Elétricos:** Medição direta com multímetro para validação da tensão nominal estável na linha de alimentação.
-------------------------------------------------------------------------
<div align="center">
  <img src="Evidências/evidenciamultimetro28.05.jpg" style="width:45%;" alt="Medição de tensão de 12.45V com alicate amperímetro e multímetro"/>
</div>
<br/>

-------------------------------------------------------------------------
📷 **Gestão Coletiva e Controle de Potência:** Inspeção visual direta do alinhamento físico e pinagem dos fusíveis na placa Titan Quad.
-------------------------------------------------------------------------
<div align="center">
  <img src="Evidências/evidenciaTitan Quad28.05.jpg" style="width:45%;" alt="Verificação de fusíveis na Titan Quad Motor Controller"/>
</div>
<br/>

## ⚠️ Condições para Interrupção Imediata (Parada Crítica)
O acionamento elétrico ou teste de bancada deve ser suspenso de imediato caso seja detectada qualquer uma das seguintes irregularidades:
> 1. Presença de baterias com deformações físicas (estufadas) ou com carga residual abaixo do limite seguro de 12 V.
> 2. Botão de Parada de Emergência (E-Stop) obstruído ou posicionado fora do raio de alcance rápido do operador.
> 3. Ausência, quebra ou sinais de oxidação nos fusíveis críticos instalados na placa de controle Titan Quad.
> 4. Falta de padronização ou ausência de anilhas/etiquetas de identificação nos cabos condutores do circuito do robô.
