# CBJJB — Carteirinha Digital

Aplicativo Android oficial para consulta e gerenciamento da carteirinha digital de atletas da CBJJB. O projeto compartilha o contrato de dados do Firebase com o aplicativo Flutter IBCJJ.

## Funcionalidades

- autenticação de atletas com Firebase Authentication;
- perfil e carteirinha digital;
- validade anual e acesso vitalício para perfis autorizados;
- autorização de carteira para CBJJB, IBCJJ ou ambos;
- foto de perfil e certificados armazenados em Base64 no Firestore;
- múltiplos certificados e solicitação de ativação;
- compatibilidade com campos opcionais e cadastros legados.

## Tecnologias

- Kotlin e Coroutines;
- Jetpack Compose e Material 3;
- arquitetura MVVM com Repository;
- Hilt para injeção de dependências;
- Firebase Authentication e Cloud Firestore;
- Android SDK 36, Gradle 8.13 e AGP 8.13.2.

## Segurança e dados

O esquema compartilhado está documentado em [`FIRESTORE_DATA_CONTRACT.md`](FIRESTORE_DATA_CONTRACT.md). As regras e os índices ficam em `firestore.rules` e `firestore.indexes.json`.

Arquivos de assinatura (`.jks`, `.keystore`, `.pem` e `keystore.properties`) não são versionados. Para gerar uma release, cada ambiente deve fornecer seu próprio arquivo local `keystore.properties`.

## Validação

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Para uma release assinada, após configurar localmente a chave de upload:

```powershell
.\gradlew.bat bundleRelease
```
