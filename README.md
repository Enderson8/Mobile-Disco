Mobile Disco

Um player de música offline para Android inspirado em equipamentos Hi-Fi clássicos.

O Mobile Disco foi desenvolvido em Kotlin utilizando Jetpack Compose e tem como objetivo oferecer uma experiência elegante, rápida e focada na coleção de discos do usuário, sem anúncios, sem streaming e sem distrações.

---

## ✨ Características

- 🎵 Reprodução de arquivos locais
- 💿 Importação de álbuns completos
- 🖼️ Capas de álbuns incorporadas (Embedded Cover Art)
- 📚 Biblioteca organizada por artista e álbum
- 🔎 Busca rápida por músicas
- ▶️ Reprodução contínua
- ⏭️ Próxima / Anterior
- 🔀 Shuffle
- 🔁 Repeat (Faixa / Álbum)
- 🎚️ Barra de progresso
- 📱 Controles na tela bloqueada
- 🎧 Reprodução em segundo plano
- 💾 Persistência da sessão
- ⚡ Interface desenvolvida em Jetpack Compose
- 🎨 Design original inspirado em equipamentos Hi-Fi

---

## 📸 Capturas de tela

*(Adicionar screenshots futuramente)*

---

## 🛠️ Tecnologias

- Kotlin
- Jetpack Compose
- Media3 / ExoPlayer
- MediaSession
- Android SDK
- Material Design 3

---

## 🏗️ Arquitetura

O projeto utiliza uma arquitetura baseada em MVVM.

```
UI
│
├── Screens
├── Components
│
ViewModel
│
Player
│
Repository (metadados)
│
Storage
```

---

## 📂 Estrutura

```
app/

├── data/
├── importer/
├── player/
├── ui/
│   ├── components/
│   ├── screens/
│   └── theme/
├── viewmodel/
└── MainActivity.kt
```

---

## 🚀 Funcionalidades

### Biblioteca

- Importação de músicas
- Importação de álbuns
- Organização automática
- Ordenação por faixa
- Proteção contra duplicatas

### Reprodução

- Play
- Pause
- Stop
- Próxima
- Anterior
- Shuffle
- Repeat

### Interface

- Tema Hi-Fi exclusivo
- Microinterações
- Tela "Now Playing"
- Biblioteca organizada
- Capa dos álbuns

---

## 📋 Roadmap

### ✔ Concluído

- Biblioteca
- Importação de álbuns
- Capas
- Pesquisa
- Auto Next
- Now Playing
- Media Session
- Controles da tela bloqueada
- Reprodução inteligente
- Shuffle
- Repeat

### 🔨 Em desenvolvimento

- Playlists
- Favoritos
- Estatísticas
- Otimização de desempenho

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT.

---

## 👨‍💻 Autor

Desenvolvido por **Enderson Carrara**.

GitHub:
https://github.com/Enderson8
