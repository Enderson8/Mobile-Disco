# Plano de Implementação - Organização Inteligente da Biblioteca (F8.0)

Este plano descreve a implementação de ordenação e filtros rápidos na biblioteca do MobileDisco, garantindo persistência e integração com o sistema de busca.

## User Review Required

> [!IMPORTANT]
> A ordenação por "Data de Importação" requer a adição de um novo campo `importDate` no modelo `Song`. Músicas já existentes na biblioteca terão sua data de importação definida como o momento da primeira execução pós-atualização.

## Proposed Changes

### [Componente de Dados]

#### [MODIFY] [Song.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/data/Song.kt)
- Adicionar o campo `val importDate: Long`.

### [Componente de UI - Estado]

#### [MODIFY] [SortOption.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/state/SortOption.kt)
- Adicionar as opções: `IMPORT_DATE`, `MOST_PLAYED`.

#### [NEW] [FilterOption.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/state/FilterOption.kt)
- Criar enum com: `ALL`, `FAVORITES`, `PLAYLISTS`, `ALBUMS`.

### [Componente ViewModel]

#### [MODIFY] [MusicViewModel.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/viewmodel/MusicViewModel.kt)
- Adicionar `_sortOption` e `_filterOption` (StateFlows).
- Persistir estas opções no `SharedPreferences` em cada mudança.
- Atualizar `carregarBiblioteca` e `persistirMusica` para lidar com `importDate`.
- Implementar a lógica de ordenação e filtragem reativa combinando os StateFlows existentes.

### [Componente de UI]

#### [MODIFY] [LibraryPanel.kt](file:///C:/Users/Administrador/AndroidStudioProjects/MobileDisco/app/src/main/java/com/example/mobiledisco/ui/components/LibraryPanel.kt)
- Adicionar a linha de Filtros Rápidos (Chips) logo abaixo da barra de pesquisa.
- Implementar o botão "Ordenar" com as novas opções solicitadas.
- Ajustar a exibição das seções (Favoritos, Playlists, Álbuns) com base no filtro selecionado.

## Verification Plan

### Automated Tests
- Verificar se a ordenação por Nome/Artista/Álbum funciona corretamente no ViewModel.
- Verificar se a filtragem rápida retorna os conjuntos de dados esperados.

### Manual Verification
1. Abrir a Biblioteca.
2. Alterar a ordenação para "Data de Importação" e verificar a ordem.
3. Selecionar o filtro "Favoritos" e verificar se apenas a seção de favoritos (ou músicas favoritas) é exibida.
4. Reiniciar o app e verificar se a opção de ordenação foi mantida.
5. Realizar uma pesquisa e verificar se a filtragem/ordenação ainda é respeitada.
