# CBJJB — Carteirinha Digital

Aplicativo Android da CBJJB para cadastro de atletas e consulta da carteirinha digital. O app oferece autenticação, perfil do atleta, controle de validade e acesso por aplicativo, além de envio de foto e certificados. Está publicado na Google Play.

> **Versão configurada neste repositório:** 5.0.1 (`versionCode` 6). A disponibilidade na loja pode variar por país e canal de distribuição.

## Funcionalidades

- Cadastro e login com Firebase Authentication.
- Perfil do atleta integrado ao Cloud Firestore.
- Exibição de acesso à carteirinha por validade anual ou acesso vitalício.
- Verificação do aplicativo autorizado para a carteira (CBJJB e/ou IBCJJ), com compatibilidade com cadastros legados.
- Atualização da foto de perfil e gerenciamento de múltiplos certificados.
- Solicitação de ativação da carteira com protocolo de confirmação.
- Regras do Firestore para separar permissões de atletas e funções administrativas.

## Tecnologias

| Área | Tecnologias |
|---|---|
| Linguagem e plataforma | Kotlin, Android SDK 36, Java 17 |
| Interface | Jetpack Compose, Material 3, Navigation Compose |
| Arquitetura | MVVM, Repository, StateFlow, Coroutines |
| Injeção de dependências | Hilt |
| Backend | Firebase Authentication, Cloud Firestore |
| Build | Gradle 8.13, Android Gradle Plugin 8.13.2 |
| Testes | JUnit 4, Compose UI Test, AndroidJUnitRunner |

## Arquitetura

O código Android está em `app/src/main/java/com/app/cbjjbapp`, organizado por responsabilidade:

- `ui/`: ViewModels, estados de tela e fluxos da interface.
- `data/`: perfil do atleta e repositórios de autenticação, usuários e mídia.
- `domain/`: política de acesso à carteirinha.
- `util/`: conversão e processamento de imagens.
- `di/`: configuração de injeção de dependências do Firebase.

A interface Compose observa estados expostos pelos ViewModels. Os repositórios isolam o acesso ao Firebase e a política de domínio avalia a permissão da carteirinha independentemente das telas.

## Dados e segurança

O app compartilha o contrato do Firestore com o aplicativo Flutter IBCJJ. As alterações devem manter compatibilidade com cadastros antigos e campos opcionais.

- [`FIRESTORE_DATA_CONTRACT.md`](FIRESTORE_DATA_CONTRACT.md): campos e regras de compatibilidade do esquema compartilhado.
- [`firestore.rules`](firestore.rules): autorização de acesso aos documentos.
- [`firestore.indexes.json`](firestore.indexes.json): índices do Firestore.

As regras devem ser validadas no emulador antes de serem publicadas no projeto Firebase compartilhado. Não use dados reais de atletas em testes. Arquivos de assinatura e credenciais locais não devem ser incluídos no repositório.

## Testes

- Testes unitários da política de acesso: validade, limites de data e fuso horário, autorização por app, acesso vitalício e compatibilidade legada.
- Testes unitários do mapeamento do perfil para o contrato do Firestore.
- Testes instrumentados Compose para tela de login, validação de campos vazios e navegação/validação de cadastro.

Execute os testes locais e gere o APK de depuração:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Para executar também os testes de interface, inicie um emulador ou conecte um dispositivo Android:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Executar localmente

1. Instale o JDK 17 e o Android SDK com a plataforma 36.
2. Configure `ANDROID_HOME` ou `ANDROID_SDK_ROOT` para apontar para o SDK.
3. Configure a integração Firebase do projeto no ambiente local.
4. Abra o projeto no Android Studio ou execute:

```powershell
.\gradlew.bat assembleDebug
```

Para gerar um bundle de release, configure localmente `keystore.properties` e a chave de assinatura do ambiente:

```powershell
.\gradlew.bat bundleRelease
```

Não versionamos chaves de assinatura nem senhas. Mantenha credenciais de cada ambiente fora do repositório.

## Resumo profissional

Este projeto demonstra desenvolvimento Android nativo com Kotlin e Jetpack Compose, integração com Firebase, aplicação de regras de negócio de acesso, compatibilidade de dados entre aplicativos e testes unitários e instrumentados. Há sugestões de apresentação para currículo e LinkedIn em [`docs/portfolio.md`](docs/portfolio.md).
