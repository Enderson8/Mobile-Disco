# Plano de Implementação — Sprint P1.0 (Hardening e Revisão Geral)

Este plano visa estabilizar e otimizar o Mobile Disco, removendo redundâncias, melhorando o desempenho do Compose e garantindo um gerenciamento de memória eficiente.

## User Review Required

> [!IMPORTANT]
> Nenhuma funcionalidade nova será adicionada. O foco é estritamente técnico: performance, estabilidade e limpeza de código.

## Open Questions

- Existe algum limite de tamanho de biblioteca que devemos considerar para as otimizações de memória? (Assumirei ~500 músicas para as otimizações de ByteArray).

## Proposed Changes

### [Infraestrutura e Player]

#### [MODIFY] [MusicPlayer.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/player/MusicPlayer.kt)
- Corrigir avisos de estilo (quebras de linha, parâmetros booleanos sem nome).
- Usar extensões KTX (`toUri`).
- Simplificar lógica de `createMediaItem` com `if-then` dobrável.

#### [MODIFY] [PlaybackService.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/player/PlaybackService.kt)
- Revisar se o `mediaSession` está sendo liberado corretamente em todos os fluxos.

### [Lógica de Negócio (ViewModel)]

#### [MODIFY] [MusicViewModel.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/viewmodel/MusicViewModel.kt)
- **Remoção de Código Morto**: Excluir `filaReproducao`, `playCounts`, `filteredFavoritos` e `anteriorMusica` que não possuem uso externo direto ou são redundantes.
- **Otimização de Performance**:
    - Usar `Sequence` em cadeias de filtragem complexas (como `filteredMaisTocadas`).
    - Substituir `Enum.values()` por `Enum.entries` (Kotlin 1.9+).
    - Usar KTX `SharedPreferences.edit` e `toUri`.
- **Refatoração de Busca**: Centralizar a lógica de filtragem de texto (`contains` ignore case) para evitar duplicação.
- **Gerenciamento de Metadados**: Otimizar `atualizarMetadadosEmSegundoPlano` para evitar notificações excessivas na UI durante a atualização da biblioteca.

### [Interface do Usuário (Compose)]

#### [MODIFY] [LibraryPanel.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/components/LibraryPanel.kt)
- **Otimização de Recomposição**:
    - Usar `remember` ou `derivedStateOf` para flags de visibilidade (`showHistory`, `showFavorites`, etc).
    - Limpar imports não utilizados (`fadeIn`).
- **Design System**: Corrigir vírgulas finais e quebras de linha para consistência com o guia de estilo.

#### [MODIFY] [AlbumCover.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/components/AlbumCover.kt)
- Implementar um cache simples de `Bitmap` para evitar `decodeByteArray` repetitivos para a mesma música.

#### [MODIFY] [HomeScreenContent.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/screens/HomeScreenContent.kt)
- Corrigir alinhamento e estilo de botões conforme o Design System Hi-Fi.

## Verification Plan

### Automated Tests
- Executar build completo para garantir que não há erros de compilação.
- Verificar se todos os avisos críticos do analyzer foram resolvidos.

### Manual Verification
1.  **Navegação**: Percorrer todas as telas (Library -> Now Playing -> Playlist -> Statistics -> Settings) e voltar.
2.  **Performance**: Verificar a fluidez do scroll na biblioteca enquanto metadados são atualizados.
3.  **Memória**: Monitorar o uso de memória durante a troca rápida de músicas (decodificação de capas).
4.  **Media Session**: Validar se os controles de mídia no sistema continuam funcionando corretamente após a refatoração do player.
