# Mapa do Front-end — onde fica cada coisa

Guia de referência: cada seção visual do site e o arquivo que a implementa.
O `src/App.vue` é o "maestro": monta as seções na ordem em que aparecem na tela e decide qual página mostrar (`/` ou `/cronograma`).

---

## Página inicial `/` (de cima para baixo)

### 1. Cabeçalho fixo (topo)
**Arquivo:** `src/components/AppHeader.vue`

- Logo "CONNECT / ADVEC MORENO" (ícone é um SVG em CSS mask, ainda placeholder)
- Mini placar central "Level Up VS Os 300" (some no mobile) — clicar rola até o placar do Hero
- Botões de navegação: **Quadro**, **Cronograma** e **Entrar/Sair** (admin)

### 2. Hero — Placar principal (tela cheia)
**Arquivo:** `src/components/HeroSection.vue`

- Banner de fundo com parallax (`public/hero-banner.jpg`) + partículas azuis/vermelhas em canvas
- Tagline: *"Somente um será lendário. O resto será história."*
- **Contagem regressiva** da gincana → `src/components/CountdownTimer.vue`
  - A data final está hardcoded lá: `TARGET_DATE` (linha 4, hoje 30/08/2026)
- Placar dos dois times com logo, nome, pontos animados (contagem sobe suavemente via `useCountUp`), coroa e selos "Liderando"/"Dominando"
- Diamante "VS" central + badge de diferença ("Empatados!", "Disputa acirrada!", "+X à frente")
- **Cabo de guerra**: barra de porcentagem Level Up vs Os 300
- Subtagline: *"Não é sobre quem começa. É sobre quem persiste."*

### 3. Divisor "Cada ponto conta"
**Arquivo:** `src/App.vue` (classe `.section-transition`, no template)

### 4. Últimas Pontuações
**Arquivo:** `src/components/RecentHistorySection.vue`

- Título "Últimas Pontuações" + card "Histórico" com os **10 lançamentos mais recentes**
- Aparece com animação ao rolar (via `useRevealOnScroll`)

### 5. Divisor "Missões ativas"
**Arquivo:** `src/App.vue` (`.section-transition`)

### 6. Sistema de Quests
**Arquivos:** `src/components/QuestSection.vue` (títulos das duas seções) → `src/components/QuestCards.vue` (os 4 cards)

Os textos e valores de pontos dos cards estão **direto no template** de `QuestCards.vue`:
- Recrutar Visitantes (+50 pts / converteu +100)
- Inscrição para Batismo (Quest Épica, +200 pts)
- Arrecadar Alimentos (+5 pts/kg)
- Presença nos Cultos (lista dos cultos da semana)

### 7. Missões de Lançamento
**Arquivos:** `src/components/QuestSection.vue` (título) → `src/components/MissionsList.vue` (lista + barras de progresso)

- A **lista de missões** (títulos/descrições) fica em `src/composables/useMissions.js` (constante `MISSIONS`)
- Admin marca "Completa/Pendente" por time; visitante só vê
- Barras de progresso por time no final

### 8. Placar detalhado (cards com ações de admin)
**Arquivos:** `src/App.vue` (seção `.scoreboard`, layout "card VS card") → `src/components/GroupCard.vue` (cada card)

- Nome do grupo, pontos, selo "Liderando"
- **Só para admin:** botões `+10 / +50 / +100 / −10` e campo de valor personalizado (limite ±10.000, em `MAX_AMOUNT`)

### 9. Histórico completo
**Arquivo:** `src/components/ScoreHistory.vue`

- Últimos **20** lançamentos com data/hora
- Botão **"Zerar placar"** (só admin) — a confirmação (`window.confirm`) fica no `App.vue` (`handleReset`)

### 10. Rodapé
**Arquivos:** `src/App.vue` (o `<footer>`) + `src/components/GlobeLabels.vue` (globo 3D)

- Globo 3D girando (lib `cobe` — atenção: v2 não tem `onRender`, anima via `globe.update()` em rAF próprio)
- Versículo de Marcos 16:15 (texto está no `App.vue`)
- "Connect ADVEC — Departamento de Jovens"

### 11. Modal de login do admin
**Arquivo:** `src/components/AdminLoginDialog.vue`

- Abre pelo botão "Entrar" do cabeçalho; estado `loginOpen` fica no `App.vue`

---

## Página `/cronograma`

**Arquivo:** `src/components/SchedulePage.vue` (tudo em um arquivo só)

O `App.vue` detecta o caminho `/cronograma` (constante `isCronograma`) e troca o conteúdo do meio — cabeçalho e rodapé continuam os mesmos.

- **Calendário mensal** com navegação (‹ mês anterior / próximo ›, botão "Hoje") e dia atual destacado em laranja
- **Cultos semanais** → constante `WEEKLY_SERVICES` (mapa dia-da-semana → culto e cor)
- **Santa Ceia transmitida** → computed `santaCeiaTransmitida` (1º domingo; se cair dia 01, pula para o 2º)
- **Santa Ceia local** → computed `santaCeiaLocal` (2ª quarta-feira)
- **Legenda / Programação semanal** → constante `LEGEND` + bloco `.legend` no template
- No mobile os nomes viram bolinhas coloridas (media query no final do arquivo)

---

## Arquivos de apoio (não são seções visuais)

| Arquivo | O que faz |
|---|---|
| `src/styles/tokens.css` | **Todas as cores, fontes e espaçamentos** (design tokens). Trocar paleta = mexer só aqui |
| `src/styles/base.css` | Reset CSS e estilos globais (body, botões, foco) |
| `src/api/client.js` | Wrapper de `fetch`: CSRF + tratamento de erros |
| `src/composables/useScoreboard.js` | Busca placar/histórico da API (polling de 15s fica no `App.vue`) |
| `src/composables/useAuth.js` | Sessão do admin (login/logout/checagem) |
| `src/composables/useMissions.js` | **Lista de missões** (`MISSIONS`) + estado completa/pendente |
| `src/composables/useCountdown.js` | Lógica da contagem regressiva |
| `src/composables/useCountUp.js` | Animação dos números do placar subindo |
| `src/composables/useRevealOnScroll.js` | Efeito "aparecer ao rolar" das seções |

---

## "Quero mudar X" — atalho rápido

| Quero mudar... | Arquivo |
|---|---|
| Cores do site / dos times | `src/styles/tokens.css` |
| Data final da gincana (contagem regressiva) | `src/components/CountdownTimer.vue` (`TARGET_DATE`) |
| Textos e pontos das quests | `src/components/QuestCards.vue` |
| Missões de lançamento | `src/composables/useMissions.js` (`MISSIONS`) |
| Frases do Hero (taglines) | `src/components/HeroSection.vue` |
| Lemas dos times ("Unidos · Fortes · Fiéis") | `src/components/HeroSection.vue` |
| Horários/cultos do cronograma | `src/components/SchedulePage.vue` (`WEEKLY_SERVICES` e `LEGEND`) |
| Regras da Santa Ceia | `src/components/SchedulePage.vue` (computeds `santaCeia*`) |
| Versículo do rodapé | `src/App.vue` (`<footer>`) |
| Logo do cabeçalho | `src/components/AppHeader.vue` (`.h-icon`, ainda placeholder) |
| Logos dos times | `src/assets/logos/` (mini no header, hero no placar) |
| Banner de fundo do Hero | `public/hero-banner.jpg` |
| Botões de pontos do admin (+10/+50/+100) | `src/components/GroupCard.vue` (`quickActions`) |
