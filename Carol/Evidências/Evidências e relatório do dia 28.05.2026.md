<p align="center">
  <img src="https://vectorseek.com/wp-content/uploads/2023/08/WorldSkills-Logo-Vector.svg-.png" alt="WorldSkills Logo" width="250"/>
</p>

<h1 align="center"> WorldsSkills - #23 Robótica Móvel </h1>

<p align="center">
  Repositório dedicado ao projeto P0 - Baseline competitivo e setup.
</p>

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

## 📁 Registro Histórico de Atividades
Consulte a documentação técnica das sessões anteriores por meio dos links abaixo:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)
* [📑 Relatório Técnico - 28/05/2026 (Segurança de Bancada e Riscos Elétricos)](./Relat%C3%B3rio%2028-05)

---

## 📑 Relatório de Atividades em Bancada (28/05/2026)

### 📊 Metodologia Operacional do Dia
As atividades deste período focaram na validação das condições de segurança do laboratório e no mapeamento preventivo de falhas em sistemas de potência. O objetivo foi assegurar que o espaço físico e a distribuição de tarefas minimizem erros operacionais durante a preparação para a competição.

---

### ⏳ Linha do Tempo e Evolução Técnica

* **Período da Manhã: Inspeção de Segurança e Organização Estática**
  Foco na aplicação do checklist de conformidade do posto de trabalho. Realizamos a triagem das ferramentas de precisão e a verificação estrutural da bancada antes de efetuar qualquer conexão ou ligação física nos componentes do robô.
  
* **Período da Tarde: Mapeamento de Riscos e Divisão de Funções**
  Análise de pontos críticos na distribuição de energia da controladora. Efetuamos vistorias preventivas nas conexões de potência para evitar curtos-circuitos e organizamos o plano de ação individual para o cumprimento do diário de bordo técnico.

---

### ✅ Checklist de Validação e Segurança de Hardware

* **Verificação de Bancada (Segurança Geral):** Inspeção visual para certificar o aperto de fixadores mecânicos e garantir o isolamento completo de emendas e fiações.
* **Análise de Sinais (Mapeamento de Potência):** Checagem física da continuidade dos cabos elétricos principais, prevenindo aquecimentos anômalos no circuito da controladora Titan Quad.
* **Teste de Proteção (Comportamento de Emergência):** Simulação de desligamento forçado do sistema para atestar a resposta imediata de interrupção de energia dos atuadores.

---

### 📸 Evidências Fotográficas

-------------------------------------------------------------------------
📷 **Protocolo de Bancada:** Organização do espaço físico e triagem de segurança.
-------------------------------------------------------------------------
<div align="center">
  <img src="Evid%C3%AAncias/0e023ffc-a647-4455-b2f1-36d37572942a.jpg" style="width:30%; transform: rotate(180deg);" alt="Inspeção da bancada de trabalho"/>
</div>

<br/>

-------------------------------------------------------------------------
📷 **Análise de Riscos Elétricos:** Monitoramento preventivo de barramentos de força.
-------------------------------------------------------------------------
<div align="center">
  <img src="Evid%C3%AAncias/5ee36cf4-0873-4a47-b1c1-a8ade23191a5.jpg" style="width:30%;" alt="Medição e checagem de sistemas elétricos"/>
</div>

<br/>

-------------------------------------------------------------------------
📷 **Gestão Coletiva:** Divisão de tarefas diárias e preenchimento de evidências.
-------------------------------------------------------------------------
<div align="center">
  <img src="Evid%C3%AAncias/9ca0af9e-5590-4db3-b9c3-4c85361a6070.jpg" style="width:30%;" alt="Registro das evidências no diário de bordo"/>
</div>

<br/>

---

## ⚠️ Condições para Interrupção Imediata (Parada Crítica)
O acionamento elétrico ou teste de bancada deve ser suspenso de imediato caso seja detectada qualquer uma das seguintes irregularidades:
> 1. Presença de baterias com deformações físicas (estufadas) ou com carga residual abaixo do limite nominal de 12 V.
> 2. Botão de Parada de Emergência (E-Stop) obstruído ou posicionado fora do raio de alcance rápido do operador.
> 3. Ausência, quebra ou sinais de oxidação nos fusíveis críticos instalados na placa de controle Titan Quad.
> 4. Falta de padronização ou ausência de anilhas/etiquetas de identificação nos cabos condutores do circuito do robô.
