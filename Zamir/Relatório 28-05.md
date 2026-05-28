# 🤖 WorldSkills #23 - Mobile Robotics

## 📅 Cronograma Vigente
* **Semana:** 01
* **Período:** 23/05/2026 a 29/05/2026
* **Fase:** P0 - Baseline competitivo e setup
* **Projeto Central:** Baseline de bancada e inventário competitivo

---

## 📌 Status dos Entregáveis da Semana
De acordo com os critérios de aceitação estabelecidos para o projeto:

- [x] Repositório Git estruturado
- [x] Inventário físico do Kit Studica Lyon
- [x] Checklist elétrico e de segurança inicial
- [ ] Diários técnicos individuais updated

---

## 📁 Histórico de Relatórios Diários
Acesse abaixo os registros detalhados de cada sessão de treinamento executada em bancada:

* [📑 Relatório Técnico - 27/05/2026 (Auditoria Física e Firmware)](./Relat%C3%B3rio%2027-05)

---

## 📑 Relatório de Atividades Diárias (28/05/2026)

### 📌 Resumo das Atividades do Dia

* **Protocolo de Bancada:** Execução e validação do checklist estático de segurança e organização das ferramentas do posto de trabalho antes de qualquer acionamento elétrico.
  > ℹ️ *Nota: Este protocolo específico não foi requisitado para as atividades executadas no dia de hoje devido ao foco exclusivo em firmware de bancada isolada.*

* **Manutenção e Atualização da VMX-pi:** Realização do procedimento de flash e atualização do firmware da controladora principal VMX-pi para garantir total estabilidade com o ambiente de desenvolvimento.

<div align="center">
  <img src="Evid%C3%AAncias/Vmx_foto.jpg" style="width:30%;" alt="Atualizando a VMX-pi"/>
</div>

<br/>

* **Firmware da Titan Quad:** Processo de deploy e atualização de firmware concluído com sucesso na controladora de motores Titan Quad da Studica, assegurando uma comunicação limpa via barramento CAN/I2C.

<div align="center">
  <img src="Evid%C3%AAncias/titan.jpg" style="width:30%;" alt="Atualização de firmware da Titan Quad"/>
</div>

<br/>

* **Análise de Riscos Elétricos e Organização de Fusíveis:** Mapeamento preventivo dos fusíveis de proteção da Titan Quad[cite: 1]. Eles atuam como a primeira linha de defesa contra sobrecorrente e curtos-circuitos elétricos, protegendo os circuitos lógicos internos da controladora e os canais dos motores caso ocorra um pico abrupto de corrente (como em cenários de stall).

<div align="center">
  <img src="Evid%C3%AAncias/titan_fusiveis.jpg" style="width:30%;" alt="Organização dos fusíveis da Titan"/>
</div>

<br/>

* **Identificação de Baixa Tensão na Bateria:** Monitoramento em tempo real através do software de telemetria para identificar quedas de tensão críticas (abaixo de 12V)[cite: 1]. Essa medição previne danos permanentes às células da bateria e previne comportamentos imprevisíveis (brownouts) no robô durante a execução das tarefas.

<div align="center">
  <img src="Evid%C3%AAncias/baixa%20tens%C3%A3o%20bateria.jpg" style="width:20%;" alt="Identificação de baixa tensão na bateria"/>
</div>

<br/>

* **Gestão Competitiva e Evidências:** Distribuição das responsabilidades de desenvolvimento e preenchimento sistemático do diário técnico para mitigação de falhas em prova.

<div align="center">
  <img src="Evid%C3%AAncias/9ca0af9e-5590-4db3-b9c3-4c85361a6070.jpg" style="width:30%;" alt="Registro das evidências no diário de bordo"/>
</div>

<br/>
---

## ⚠️ Critérios de Parada Crítica
A execução em bancada deve ser interrompida imediatamente caso ocorra qualquer uma das seguintes anomalias antes ou durante a energização:
> 1. Bateria suspeita ou com tensão abaixo de 12 V.
> 2. Dispositivo de E-Stop (Parada de Emergência) não localizado ou inacessível.
> 3. Fusíveis de proteção da controladora Titan Quad ausentes ou danificados.
> 4. Presença de cabos elétricos sem etiqueta de identificação de origem/destino.
