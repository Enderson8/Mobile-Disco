# Plano de Implementação — Sprint P1.1 (Compatibilidade com Barra de Navegação)

Este plano visa corrigir a sobreposição da interface pelos botões de navegação do Android, garantindo que o Mobile Disco seja totalmente utilizável em dispositivos com navegação por gestos ou três botões.

## User Review Required

> [!IMPORTANT]
> A solução foca em aplicar insets de janela (`navigationBarsPadding`) de forma cirúrgica nos componentes que ficam na parte inferior da tela, permitindo que o fundo (background) continue preenchendo toda a tela para manter a estética Hi-Fi.

## Proposed Changes

### [Navegação e Root]

#### [MODIFY] [MobileDiscoScreen.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/MobileDiscoScreen.kt)
- Corrigir a omissão do `modifier` recebido: envolver o `Crossfade` em um `Box` que aplica o modificador, garantindo que o `innerPadding` do `MainActivity` seja respeitado ou que a tela se comporte como esperado pelo sistema de insets.

### [Telas e Componentes]

#### [MODIFY] [MiniPlayer.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/components/MiniPlayer.kt)
- Adicionar `navigationBarsPadding()` ao `Surface` do MiniPlayer. Isso é crítico pois o MiniPlayer fica fixo na parte inferior da `HomeScreen`.

#### [MODIFY] [NowPlayingScreen.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/NowPlayingScreen.kt)
- Adicionar `navigationBarsPadding()` à `Column` principal. Isso garantirá que os controles de reprodução (Play/Next/Prev) fiquem acima dos botões do sistema.

#### [MODIFY] [PlaylistScreen.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/PlaylistScreen.kt)
- Adicionar `navigationBarsPadding()` à `Column` raiz.
- Garantir que a `LazyColumn` tenha um conteúdo que possa ser scrollado para além da barra de navegação.

#### [MODIFY] [StatisticsScreen.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/StatisticsScreen.kt)
- Adicionar `navigationBarsPadding()` à `Column` principal para proteger as estatísticas de destaque no final da lista.

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/SettingsScreen.kt)
- Adicionar `navigationBarsPadding()` à `Column` principal para que a seção "Sobre" e o espaçamento final não sejam cortados.

## Verification Plan

### Automated Tests
- Verificar compilação do projeto.

### Manual Verification
1. **Teste de Três Botões**: Emular um dispositivo com navegação por 3 botões (ex: Motorola G24) e verificar se o MiniPlayer e os controles da NowPlaying estão totalmente clicáveis.
2. **Teste de Gestos**: Verificar se não há um espaço em branco excessivo na navegação por gestos.
3. **Scroll**: Validar se o final das listas em Playlists, Estatísticas e Configurações é alcançável e visível.
