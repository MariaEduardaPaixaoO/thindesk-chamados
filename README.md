# Thindesk — Controle de Chamados Técnicos

Sistema web para registro e acompanhamento de chamados de suporte técnico, desenvolvido a partir do projeto Thindesk apresentado na disciplina **Projeto e Desenvolvimento de Software Corporativo**.

As regras de negócio de chamados (validações, ciclo de status, registro de solução e bloqueio de alteração após o encerramento) foram incorporadas do microsserviço de chamados desenvolvido na prática anterior da disciplina.

| | |
|---|---|
| **Versão atual** | `1.0.0` |
| **Aplicação publicada** | _adicionar o link do deploy_ |
| **Usuário de demonstração** | `demo` |
| **Senha de demonstração** | `demo123` |

Também é possível criar uma conta pela opção **Cadastre-se** na tela de login.

![Dashboard do Thindesk](docs/dashboard.png)

## Funcionalidades

- **Autenticação**: login e logout com Spring Security, senhas armazenadas com hash BCrypt e cadastro de novos usuários.
- **Chamados**: abertura, consulta, edição, cancelamento, filtro por status e página de detalhes.
- **Atribuição de responsável**: cada chamado pode receber um técnico responsável.
- **Dashboard**: total de chamados por situação, gráficos por status, tipo, técnico e período, além da lista dos chamados mais recentes. Todos os valores são calculados a partir dos registros do banco.
- **Clientes**: cadastro, edição e exclusão dos solicitantes.
- **Configurações**: técnicos, tipos de chamado e horários de atendimento por setor.
- **API REST** para chamados e clientes, com respostas de erro padronizadas.

### Ciclo de vida do chamado

```
ABERTO ──► EM_ANDAMENTO ──► CONCLUIDO
   │             │
   └─────────────┴──────► CANCELADO
```

- Todo chamado é aberto com status `ABERTO`; o status enviado na abertura é ignorado.
- Ao receber um técnico responsável, um chamado aberto passa para `EM_ANDAMENTO`.
- Para concluir é obrigatório informar o técnico responsável e a solução (mínimo de 10 caracteres).
- Chamados `CONCLUIDO` ou `CANCELADO` não podem mais ser alterados.
- Título: 5 a 100 caracteres. Descrição: 10 a 2000 caracteres.
- Um cliente ou tipo de chamado que já possui chamados não pode ser excluído.

## Tecnologias

- Java 17
- Spring Boot 3.4 (Spring MVC, Spring Security, Spring Data MongoDB, Bean Validation)
- Thymeleaf
- MongoDB (MongoDB Atlas em produção)
- Bootstrap 5.3 e Bootstrap Icons
- Chart.js 4
- JUnit 5 e Mockito
- Docker e Render (deploy)

## Estrutura do projeto

```
src/main/java/com/pfc
├── ThindeskApplication.java     classe principal
├── SecurityConfig.java          regras de autenticação e acesso
├── MongoInitConfig.java         criação das coleções
├── CargaInicialConfig.java      usuário de demonstração e dados iniciais
└── thindesk
    ├── controller               páginas (Thymeleaf) e endpoints REST
    ├── dto                      formulários e indicadores do dashboard
    ├── entity                   documentos do MongoDB e enums
    ├── exception                exceções de negócio e tratamento de erros da API
    ├── repository               acesso ao MongoDB
    └── service                  regras de negócio
src/main/resources
├── templates                    páginas Thymeleaf
├── static                       CSS, JavaScript e imagens
└── application.properties
```

## Como executar localmente

### Requisitos

- JDK 17 ou superior
- MongoDB: instalado localmente, em um contêiner Docker ou no MongoDB Atlas

O Maven não precisa ser instalado; o projeto usa o Maven Wrapper (`mvnw`).

### 1. Subir o MongoDB

Com Docker:

```bash
docker run -d --name thindesk-mongo -p 27017:27017 mongo:7
```

Sem configuração adicional, a aplicação conecta em `mongodb://localhost:27017`. Para usar o MongoDB Atlas, defina a variável `MONGODB_URI` (veja a seção seguinte).

### 2. Executar a aplicação

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Acessar

Abra `http://localhost:8080` e entre com `demo` / `demo123`.

Na primeira execução com o banco vazio, a aplicação cria o usuário de demonstração e uma carga inicial de clientes, técnicos, tipos, horários e chamados, para que o dashboard já exiba informações.

## Variáveis de ambiente

| Variável | Descrição | Valor padrão |
|---|---|---|
| `MONGODB_URI` | String de conexão com o MongoDB | `mongodb://localhost:27017` |
| `MONGODB_DATABASE` | Nome do banco de dados | `thindesk` |
| `PORT` | Porta HTTP da aplicação | `8080` |
| `DEMO_USERNAME` | Usuário de demonstração criado na inicialização | `demo` |
| `DEMO_PASSWORD` | Senha do usuário de demonstração e dos técnicos da carga inicial | `demo123` |
| `SEED_DEMO_DATA` | Cria a carga inicial de dados quando as coleções estão vazias | `true` |

Para desenvolvimento local, as variáveis podem ficar em um arquivo `.env` na raiz do projeto, lido automaticamente pela aplicação. Use o modelo `.env.example`:

```bash
cp .env.example .env      # Windows: copy .env.example .env
```

Os valores devem ser escritos sem aspas. O `.env` está no `.gitignore` e não deve ser enviado ao repositório. Em produção, as variáveis são configuradas diretamente no Render.

Também é possível defini-las no terminal. Exemplo com MongoDB Atlas (Linux/macOS):

```bash
export MONGODB_URI="mongodb+srv://usuario:senha@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority"
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
$env:MONGODB_URI="mongodb+srv://usuario:senha@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority"
.\mvnw.cmd spring-boot:run
```

## Testes

```bash
./mvnw test
```

Os testes unitários cobrem as regras de chamados (abertura, atribuição de técnico, conclusão, cancelamento e bloqueio de alteração) e o cadastro de usuários. Eles não dependem do MongoDB.

## API REST

Os endpoints exigem autenticação HTTP Basic com um usuário cadastrado.

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/chamados?status=` | Lista os chamados, com filtro opcional por status |
| `GET` | `/api/chamados/{id}` | Consulta um chamado |
| `POST` | `/api/chamados` | Abre um chamado |
| `PUT` | `/api/chamados/{id}` | Atualiza um chamado |
| `PUT` | `/api/chamados/{id}/cancelar` | Cancela um chamado |
| `GET` | `/api/clientes` | Lista os clientes |
| `GET` | `/api/clientes/{id}` | Consulta um cliente |
| `POST` | `/api/clientes` | Cadastra um cliente |

Exemplo de abertura de chamado:

```bash
curl -u demo:demo123 -X POST http://localhost:8080/api/chamados \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Computador não liga","descricao":"O computador da recepção não liga desde hoje cedo.","tipo":"Hardware","usuario":"Carla Mendes"}'
```

Respostas de erro: `400` para dados inválidos (com a mensagem de cada campo), `401` sem autenticação, `404` para registro inexistente e `409` ao alterar um chamado já encerrado.

## Deploy

A aplicação é publicada no **Render** a partir do `Dockerfile`, com o banco no **MongoDB Atlas**.

1. **MongoDB Atlas**: criar um cluster gratuito (M0), um usuário de banco de dados e liberar o acesso de rede para `0.0.0.0/0`. Copiar a string de conexão.
2. **Render**: criar um *Blueprint* apontando para este repositório (o arquivo `render.yaml` já descreve o serviço) ou um *Web Service* do tipo Docker.
3. Informar a variável `MONGODB_URI` com a string do Atlas. As demais já possuem valores definidos.
4. Após o deploy, acessar a URL gerada pelo Render e entrar com a credencial de demonstração.

No plano gratuito do Render, o serviço é suspenso após um período sem acessos; o primeiro acesso seguinte pode levar cerca de um minuto.

## Versionamento

O projeto segue o **Versionamento Semântico** ([SemVer 2.0.0](https://semver.org/lang/pt-BR/)), no formato `MAJOR.MINOR.PATCH`:

| Parte | Quando é incrementada | Exemplo no projeto |
|---|---|---|
| **MAJOR** | Mudanças incompatíveis com a versão anterior, como alteração no formato da API ou na estrutura dos dados | Trocar o formato do JSON de `/api/chamados` → `2.0.0` |
| **MINOR** | Novas funcionalidades compatíveis com o que já existe | Adicionar anexos aos chamados → `1.1.0` |
| **PATCH** | Correções de erros sem mudança de funcionalidade | Corrigir uma validação do formulário → `1.0.1` |

Ao incrementar uma parte, as partes à direita voltam para zero (`1.4.2` → `1.5.0` → `2.0.0`). Versões anteriores à primeira estável podem usar um identificador de pré-lançamento, como `-alpha` ou `-beta`.

A versão é definida no `pom.xml` (`<version>`) e cada versão publicada é marcada no Git com uma tag no formato `vMAJOR.MINOR.PATCH`:

```bash
git tag -a v1.0.0 -m "Versão 1.0.0"
git push origin v1.0.0
```

## Histórico de versões

### 1.0.0

Primeira versão estável.

- Autenticação com usuários armazenados no MongoDB, cadastro de contas e logout.
- Abertura, edição, cancelamento, detalhes e filtro de chamados, com atribuição de técnico responsável.
- Regras de negócio e validações incorporadas do microsserviço de chamados.
- Dashboard com indicadores e gráficos calculados a partir dos dados do sistema.
- Cadastro completo de clientes; telas de técnicos, tipos de chamado e horários de atendimento.
- Tratamento de erros nas páginas e na API REST.
- Configuração por variáveis de ambiente, carga inicial de dados e deploy com Docker.

### 1.0-alpha

Versão de referência apresentada em aula: login com usuário fixo, listagem de chamados e clientes e API REST de chamados.
