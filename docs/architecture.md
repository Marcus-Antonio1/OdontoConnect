# Decisões iniciais de arquitetura

## Estilo arquitetural
Monólito modular no backend. A separação por domínio será mantida dentro de uma única aplicação Spring Boot nesta fase.

## Domínios previstos
- identity: autenticação, usuários, perfis e permissões
- scheduling: turnos, escalas e disponibilidade
- patient: cadastro e identificação de pacientes
- conversation: conversas, mensagens e atribuições
- attendance: ciclo de vida dos atendimentos
- ombudsman: manifestações, protocolos e respostas
- integration: adaptadores dos canais externos
- reporting: indicadores e relatórios

## Princípios
- PostgreSQL será a fonte persistente dos dados.
- Mensagens externas deverão ser idempotentes.
- Eventos importantes terão trilha de auditoria.
- Integrações externas ficarão isoladas dos módulos de negócio.
- Dados de demonstração serão fictícios.
- Não utilizar dados reais de pacientes neste protótipo.
