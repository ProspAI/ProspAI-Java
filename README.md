

# ProspAI

**ProspAI** é uma aplicação de inteligência artificial desenvolvida para otimizar estratégias de vendas através da análise de dados de clientes. A aplicação oferece uma API RESTful robusta e uma interface MVC baseada em Spring Boot, que facilita a navegação e a utilização dos recursos disponíveis.

#segue o link do video explicativo: [Sprint3 - java advanced](https://www.youtube.com/watch?v=3Y9TP-nfKTk)

#A aplicação já está hospedada na nuvem e pode ser acessada através do link base: [ProspAI na Azure](https://prospai.azurewebsites.net/clientes).

## Índice

1. [Visão Geral do Projeto](#visão-geral-do-projeto)
2. [Arquitetura](#arquitetura)
3. [Recursos Principais](#recursos-principais)
4. [Tecnologias Utilizadas](#tecnologias-utilizadas)
5. [Padrões de Projeto Implementados](#padrões-de-projeto-implementados)
6. [Endpoints da API](#endpoints-da-api)
7. [Funcionalidades da Interface MVC](#funcionalidades-da-interface-mvc)
8. [Instalação e Configuração](#instalação-e-configuração)
9. [Como Contribuir](#como-contribuir)
10. [Licença](#licença)

## Visão Geral do Projeto

**ProspAI** é uma plataforma que combina inteligência artificial e análise de dados para melhorar a tomada de decisão em vendas. Através da integração com fontes de dados externas, como Reclame Aqui e Procon, a aplicação consegue analisar feedbacks de clientes e oferecer predições e relatórios detalhados para otimização de estratégias de vendas.

## Arquitetura

A aplicação ProspAI é composta de duas partes principais:

1. **API RESTful**: Construída utilizando Spring Boot, oferecendo endpoints para operações CRUD sobre clientes, feedbacks, predições, relatórios, estratégias de vendas, e usuários.
   
2. **Interface MVC**: Baseada em Thymeleaf, a interface MVC proporciona uma experiência de usuário intuitiva e permite a visualização e manipulação dos dados de forma amigável.

A aplicação utiliza HATEOAS (Hypermedia as the Engine of Application State) para melhorar a navegabilidade e autodescoberta de recursos na API.

## Recursos Principais

- **Gestão de Clientes**: Criação, edição, visualização, e remoção de clientes.
- **Gestão de Feedbacks**: Manipulação de feedbacks associados a clientes.
- **Análise e Predição**: Geração de predições com base nos dados dos clientes.
- **Relatórios Personalizados**: Criação e gerenciamento de relatórios de vendas e desempenho.
- **Automação de Estratégias de Vendas**: Definição e implementação de estratégias automáticas para otimização de vendas.
- **Gestão de Usuários**: Controle de acesso e gerenciamento de usuários da plataforma.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA com integração ao SQL Server
- Thymeleaf para a camada de visualização (MVC)
- HATEOAS para hiperlinks de recursos na API REST
- OpenAPI/Swagger para documentação da API
- JavaScript e Bootstrap para interatividade e design responsivo

## Padrões de Projeto Implementados

- **DTO (Data Transfer Object)**: Utilizados para transferir dados entre a camada de serviço e a camada de apresentação.
- **HATEOAS**: Implementado para melhorar a navegabilidade da API, fornecendo links para operações relacionadas nos objetos de resposta.
- **Validação de Dados**: Utilização de Bean Validation para garantir a integridade dos dados recebidos e processados pela API.

## Endpoints da API

Abaixo está a lista de principais endpoints da API RESTful disponíveis:

### Clientes (/api/clientes)
- `GET /api/clientes` - Retorna todos os clientes.
- `GET /api/clientes/{id}` - Retorna um cliente específico por ID.
- `POST /api/clientes` - Cria um novo cliente.
- `PUT /api/clientes/{id}` - Atualiza um cliente existente.
- `DELETE /api/clientes/{id}` - Deleta um cliente.

### Feedbacks (/api/feedbacks)
- `GET /api/feedbacks` - Retorna todos os feedbacks.
- `GET /api/feedbacks/{id}` - Retorna um feedback específico por ID.
- `POST /api/feedbacks/cliente/{clienteId}` - Cria um feedback para um cliente específico.
- `PUT /api/feedbacks/{id}` - Atualiza um feedback existente.
- `DELETE /api/feedbacks/{id}` - Deleta um feedback.

### Predições (/api/predictions)
- `GET /api/predictions` - Retorna todas as predições.
- `GET /api/predictions/{id}` - Retorna uma predição específica por ID.
- `POST /api/predictions/cliente/{clienteId}` - Cria uma predição para um cliente específico.
- `PUT /api/predictions/{id}` - Atualiza uma predição existente.
- `DELETE /api/predictions/{id}` - Deleta uma predição.

### Relatórios (/api/reports)
- `GET /api/reports` - Retorna todos os relatórios.
- `GET /api/reports/{id}` - Retorna um relatório específico por ID.
- `POST /api/reports` - Cria um novo relatório.
- `PUT /api/reports/{id}` - Atualiza um relatório existente.
- `DELETE /api/reports/{id}` - Deleta um relatório.

### Estratégias de Vendas (/api/sales-strategies)
- `GET /api/sales-strategies` - Retorna todas as estratégias de vendas.
- `GET /api/sales-strategies/{id}` - Retorna uma estratégia de vendas específica por ID.
- `POST /api/sales-strategies` - Cria uma nova estratégia de vendas.
- `PUT /api/sales-strategies/{id}` - Atualiza uma estratégia de vendas existente.
- `DELETE /api/sales-strategies/{id}` - Deleta uma estratégia de vendas.

### Usuários (/api/usuarios)
- `GET /api/usuarios` - Retorna todos os usuários.
- `GET /api/usuarios/{id}` - Retorna um usuário específico por ID.
- `POST /api/usuarios` - Cria um novo usuário.
- `PUT /api/usuarios/{id}` - Atualiza um usuário existente.
- `DELETE /api/usuarios/{id}` - Deleta um usuário.

### Documentação Swagger
Para testar e visualizar a documentação dos endpoints da API, acesse:

- **Ambiente Local:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Ambiente na Nuvem (Azure):** [https://prospai.azurewebsites.net/swagger-ui.html](https://prospai.azurewebsites.net/swagger-ui.html)

## Funcionalidades da Interface MVC

A interface MVC oferece funcionalidades interativas para a gestão de todas as entidades mencionadas acima:

- **Clientes:** `/clientes` - Permite visualizar, adicionar, editar, e deletar clientes.
- **Feedbacks:** `/feedbacks` - Gerenciamento de feedbacks dos clientes.
- **Predições:** `/predictions` - Criação e edição de predições baseadas em dados de clientes.
- **Relatórios:** `/reports` - Geração e visualização de relatórios de desempenho.
- **Estratégias de Vendas:** `/sales-strategies` - Implementação e gerenciamento de estratégias de vendas.
- **Usuários:** `/usuarios` - Gestão de usuários com controle de acesso e permissões.

## Instalação e Configuração

1. **Clone o Repositório:**
   ```bash
   git clone https://github.com/seu-usuario/prospai.git
   cd prospai
   ```

2. **Configure o Banco de Dados:**
   - A aplicação utiliza SQL Server. Atualize as configurações do banco de dados no arquivo `application.properties` conforme necessário.

3. **Execute a Aplicação:**
   - Utilize o Maven ou qualquer IDE Java para compilar e executar o projeto:
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a Aplicação:**
   - Acesse a aplicação através do navegador: [http://localhost:8080/clientes](http://localhost:8080/clientes) ou use o link da nuvem: [ProspAI na Azure](https://prospai.azurewebsites.net/clientes).

## Como Contribuir

1. **Fork o Projeto**
2. **Crie um Branch de Funcionalidade**
   ```bash
   git checkout -b nova-funcionalidade
   ```
3. **Commit suas Alterações**
   ```bash
   git commit -m 'Adiciona nova funcionalidade'
   ```
4. **Envie o Branch**
   ```bash
   git push origin nova-funcionalidade
   ```
5. **Abra um Pull Request**

## Licença

Este projeto é licenciado sob os termos da MIT License.

---

## Autores

Este projeto foi desenvolvido por:

- **AGATHA PIRES** – RM552247 – (2TDSPH)
- **DAVID BRYAN VIANA** – RM551236 – (2TDSPM)
- **GABRIEL LIMA** – RM99743 – (2TDSPM)
- **GIOVANNA ALVAREZ** – RM98892 – (2TDSPM)
- **MURILO MATOS** – RM552525 – (2TDSPM)

---

Sinta-se à vontade para contribuir com melhorias e novas funcionalidades para o ProspAI!
