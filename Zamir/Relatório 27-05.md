<div align="center">
  <img src="https://github.com/user-attachments/assets/a547579f-f725-4e2e-8bf1-caec1e7137d9" style="width:50%;" alt="Logo WorldSkills"/>
</div>

<br/>

<div align="center">
  <a href="#">
    <img src="https://img.shields.io/badge/Competidor-Vitor_Zamir-blue" alt="Vitor Zamir">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Treinamento-SENAI_Santo_Amaro-red" alt="SENAI Santo Amaro">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Modalidade-Mobile_Robotics_%2323-purple" alt="Mobile Robotics 23">
  </a>
</div>

<br/>

<p align="center">
  Este é o diário oficial de bordo e portfólio técnico de engenharia desenvolvido para a modalidade de <b>Robótica Móvel (#23)</b> da WorldSkills[cite: 1]. Aqui estão documentados os sprints, checklists de segurança e o progresso diário em direção ao padrão de excelência internacional[cite: 1].
</p>

---

## 📅 Planejamento da Semana

<p align="justify">
  O foco desta semana está concentrado na fase <b>P0 - Baseline competitivo e setup</b>[cite: 1]. O objetivo principal é garantir o estabelecimento de um ambiente de trabalho seguro em bancada, mitigação de riscos operacionais com células de energia e estruturação da documentação técnica e divisões de papéis da equipe[cite: 1].
</p>

---

## 📑 Relatório de Atividades Diárias (27/05/2026)

### 📌 Resumo das Atividades do Dia
* **Auditoria Física:** Contabilização e conferência completa de todos os componentes mecânicos, eletrônicos e estruturais pertencentes ao Kit Studica Lyon 2024.
* **Manutenção e Firmware:** Execução dos procedimentos de atualização do processo de boot e firmware da controladora principal VMX-pi e da Titan Quad da Studica.

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
      <td><code>Studica_Kit_Lyon_2024/Mecanica</code></td>
      <td>Verificação de chassis, eixos, acoplamentos e elementos de fixação estrutural.</td>
    </tr>
    <tr>
      <td><code>Studica_Kit_Lyon_2024/Eletronica</code></td>
      <td>Inventário de atuadores, cabos de dados, chicotes elétricos e sensores do kit.</td>
    </tr>
    <tr>
      <td><code>Controladoras/VMX-pi</code></td>
      <td>Atualização do sistema de inicialização (boot) e imagem do Linux embarcado para suporte ao WPILib.</td>
    </tr>
    <tr>
      <td><code>Controladoras/Titan_Quad</code></td>
      <td>Flasheamento de firmware do controlador de motores via barramento de comunicação para garantir barramento CAN/I2C estável.</td>
    </tr>
  </tbody>
</table>

<br/>

### ⏳ Linha do Tempo e Evolução do Dia

<img src="https://private-user-images.githubusercontent.com/100859276/363510327-a0c5800d-fcd0-47f1-990c-3d6951691d1c.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Nzc0OTE1NDksIm5iZiI6MTc3NzQ5MTI0OSwicGF0aCI6Ii8xMDA4NTkyNzYvMzYzNTEwMzI3LWEwYzU4MDBkLWZjZDAtNDdmMS05OTBjLTNkNjk1MTY5MWQxYy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjYwNDI5JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI2MDQyOVQxOTM0MDlaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1jOGQ2NzUxMDgwMzZkZjMxMDc1MzgxNTU2YTBkNGQzOWNhNzU2ZGZiMjYzMzg5ZWI5MGE3NjE4NTE3ZGJmNDViJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZyZXNwb25zZS1jb250ZW50LXR5cGU9aW1hZ2UlMkZwbmcifQ.UwHvrhlFCXPWQa_A-zszZy_3U309LtPTPypqP8U07OA" align="right" width="40%" style="margin-left: 15px; border-radius: 5px;"/>

* **Etapa 1 (Manhã):** Triagem física, contagem e preenchimento da planilha de conformidade de hardware do Kit Lyon 2024, mitigando e prevenindo ativamente a ausência de peças fundamentais durante os testes em bancada.
* **Etapa 2 (Tarde):** Conexão em bancada das controladoras sob protocolo estático, diagnóstico do estado do firmware anterior e execução bem-sucedida do deploy do novo boot loader e sistema operacional na VMX e Titan Quad.

<br clear="right"/>

---

## ✅ Checklist de Validação de Hardware & Casos de Borda

* **Teste Válido (Comunicação):** Inicialização pós-boot bem-sucedida com ping ativo na VMX-pi e reconhecimento imediato e estável da placa Titan Quad via barramento utilitário de comunicação.
* **Teste de Limite (Alimentação):** Monitoramento e teste de oscilação de tensão na Titan Quad sob carga simulada de bancada, validando que as novas diretrizes do firmware mantêm a integridade e segurança lógica dos slots.
* **Teste de Borda (Firmware):** Tentativa de upload crítico de firmware com interrupção forçada do cabo de dados, garantindo que o sistema de recuperação física de boot (recovery mode) não sofra corrupção permanente.

---

## 🚨 Critérios de Parada Crítica (Safe Mode)

De acordo com o plano operacional estabelecido, o treinamento deve ser interrompido imediatamente e uma issue de manutenção deve ser aberta no repositório caso qualquer um dos seguintes cenários seja detectado[cite: 1]:
1. Bateria com avaria física visível ou tensão de circuito aberto abaixo de **12V**[cite: 1].
2. Mecanismo físico de Parada de Emergência (E-Stop) obstruído ou botões de Start/Stop sem identificação clara[cite: 1].
3. Fusíveis de proteção da placa controladora Titan Quad rompidos, ausentes ou substituídos por especificações incorretas[cite: 1].
4. Cabos elétricos ou chicotes de potência sem etiquetas de identificação de origem e destino[cite: 1].

---
<p align="center"><i>Diário Técnico desenvolvido sob as diretrizes de alta performance da Skill #23 - WorldSkills.</i></p>
