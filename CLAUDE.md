# Contador de Pontos — Connect ADVEC

Placar de competição entre dois grupos do departamento de jovens (Connect) da igreja ADVEC: **Os 300** vs **Level Up**. Visitantes veem o placar; apenas o admin (login com senha) adiciona/remove pontos e zera o placar. Projeto de uso temporário — manter simples.

## Arquitetura

- **Frontend**: Vue 3 (Composition API) + Vite, JavaScript puro (sem TypeScript, sem Tailwind). Fica na **raiz** do repo.
- **Backend**: Spring Boot 4 (Java 21), pasta `backend/`. Sessão via cookie + CSRF (padrão SPA do Spring Security).
- **Banco**: PostgreSQL no Supabase (conexão via *Session pooler*, porta 5432 — a conexão direta é IPv6-only e falha no Windows).
- **Produção**: front na **Vercel**, backend no **Railway** (Dockerfile), banco no Supabase.
  - O `vercel.json` faz rewrite de `/api/*` para o Railway → navegador só fala com o domínio da Vercel (same-origin, cookies `SameSite=Lax` funcionam).
  - URL: https://gincanaconnect.vercel.app

## Rodar localmente

```bash
# Frontend (raiz) — proxy /api -> localhost:8080 já configurado no vite.config.js
npm install
npm run dev            # http://localhost:5173

# Backend
cd backend
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local   # Windows
```

O backend exige `backend/src/main/resources/application-local.properties` (**gitignorado**, contém credenciais do Supabase e senha do admin). Peça o conteúdo a quem já tem o projeto rodando, ou crie com:

```properties
spring.datasource.url=<JDBC do Supabase Session pooler + ?sslmode=require>
spring.datasource.username=<user do pooler>
spring.datasource.password=<senha do banco>
app.admin-password=<senha do admin local, min. 8 caracteres>
```

## Estrutura do frontend

- `src/styles/tokens.css` — **todos** os design tokens (cores dos grupos, superfícies, espaçamento). Troca de paleta é só aqui.
- `src/api/client.js` — wrapper de fetch: cuida de CSRF (cookie XSRF-TOKEN → header X-XSRF-TOKEN) e erros.
- `src/composables/` — `useAuth` (sessão admin) e `useScoreboard` (placar, com polling de 15s no App.vue).
- `src/components/` — `GroupCard` (card do grupo + ações de admin), `AdminLoginDialog`, `AppHeader` (logo ainda é placeholder), `GlobeLabels` (globo 3D do rodapé, lib `cobe` v2 — **não tem `onRender`**, anima via `globe.update()` em rAF próprio).

## Backend (endpoints)

- `GET /api/scoreboard` — público (grupos + últimos 20 lançamentos)
- `POST /api/auth/login|logout`, `GET /api/auth/me|csrf`
- `POST /api/groups/{id}/points` (body `{"amount": n}`, ±10.000) e `POST /api/scoreboard/reset` — exigem sessão de admin
- Limitador de tentativas de login: 5 falhas → 5 min de bloqueio por IP.
- Grupos são seedados por `GroupSeeder` (ids `os-300` e `level-up`). Cores dos grupos são mapeadas no front (`COLOR_VARS` nos componentes), a API não retorna cor.

## Deploy e pegadinhas conhecidas

- Push na `master` → deploya front (Vercel) e backend (Railway) automaticamente.
- **Vercel Hobby + repo privado**: só commits de autoria do dono do projeto deployam. Colaboradores devem abrir PR e o dono faz merge com "Create a merge commit". O e-mail do `git config user.email` precisa estar cadastrado na conta GitHub do autor (vale usar o e-mail noreply do GitHub).
- **Railway**: Root Directory do serviço = `backend`; variáveis vão no **serviço** (não em Shared Variables): `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `ADMIN_PASSWORD`, `CORS_ORIGIN` (= domínio da Vercel, sem barra final). Sem `CORS_ORIGIN`, POSTs do navegador falham com 403 "Invalid CORS request".
- Cuidado com espaços nos nomes das variáveis no Railway (`DB_URL ` ≠ `DB_URL`).

## Pendências conhecidas

- Logo real no `AppHeader.vue` (placeholder) e favicon.
- Paleta de cores definitiva em `tokens.css`.
