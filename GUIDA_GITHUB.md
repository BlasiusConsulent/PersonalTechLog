# GUIDA COMPLETA — Pubblicare PersonalTechLog su GitHub

---

## 1. NOME E DESCRIZIONE PER GITHUB

### Nome del repository (Repository name)
```
PersonalTechLog
```
Semplice, descrittivo, senza spazi (GitHub usa i trattini o il CamelCase per convenzione).

---

### Description (massimo ~150 caratteri, appare sotto il nome)
```
Lightweight CLI tool for IT consultants to log daily technical interventions. Built with Java SE — no dependencies, no server, no cloud.
```

Oppure, se preferisci in italiano:
```
Tool CLI per consulenti IT: registra interventi HW/SW giornalieri in locale. Java SE puro, nessuna dipendenza esterna.
```

---

### Topics / Tag (aiutano a trovare il repo su GitHub)
Nella pagina del repo clicca l'ingranaggio ⚙️ accanto a "About" e aggiungi questi tag:
```
java  cli  tool  freelance  it-consultant  java-se  serialization  oop  open-source  offline
```

---

### Website
Lascia vuoto oppure metti il link al tuo profilo LinkedIn o sito personale.

---

## 2. CREA IL REPOSITORY SU GITHUB

### Passo 1 — Registrati o accedi
Vai su **https://github.com** e accedi al tuo account.
Se non hai un account, crea uno gratuitamente (bastano email e password).

---

### Passo 2 — Crea un nuovo repository

1. Clicca il pulsante verde **"New"** in alto a sinistra (o vai su https://github.com/new)

2. Compila il form:

| Campo | Valore |
|---|---|
| **Repository name** | `PersonalTechLog` |
| **Description** | (copia una delle frasi qui sopra) |
| **Visibility** | ⚪ Public (per open source) |
| **Initialize this repository** | ✅ Spunta "Add a README file" |
| **Add .gitignore** | Scegli `Java` dal menu a tendina |
| **Choose a license** | Scegli `MIT License` |

3. Clicca **"Create repository"**

> Il repository è ora creato e vuoto (con solo README, .gitignore e LICENSE autogenerati).

---

## 3. INSTALLA GIT SUL TUO PC

Prima di caricare i file, verifica che Git sia installato:

```bash
git --version
```

Se il comando non esiste:

- **Windows:** scarica da https://git-scm.com/download/win e installa con le opzioni predefinite
- **macOS:** esegui `xcode-select --install` oppure installa Homebrew e poi `brew install git`
- **Linux (Ubuntu/Debian):** `sudo apt install git`

---

### Configura Git con il tuo nome e email (solo la prima volta)

```bash
git config --global user.name "Il Tuo Nome"
git config --global user.email "tua@email.com"
```

Questi dati appariranno nella cronologia dei commit. Usa la stessa email del tuo account GitHub.

---

## 4. CARICA I FILE SUL REPOSITORY

Hai due opzioni: da **terminale** (consigliata) o tramite **interfaccia web** di GitHub.

---

### OPZIONE A — Da terminale (professionale)

#### 4A.1 — Clona il repository appena creato sul tuo PC

```bash
git clone https://github.com/TUOUSERNAME/PersonalTechLog.git
```

Sostituisci `TUOUSERNAME` con il tuo nome utente GitHub.
Questo crea una cartella `PersonalTechLog` sul tuo PC con i file iniziali.

```bash
cd PersonalTechLog
```

---

#### 4A.2 — Copia i file del progetto nella cartella

Copia manualmente (o con il terminale) i tuoi file sorgente nella cartella appena creata:

```
PersonalTechLog/
├── src/
│   ├── Intervento.java
│   ├── InterventoHardware.java
│   ├── InterventoSoftware.java
│   ├── InterventoNonTrovatoException.java
│   ├── LogManager.java
│   └── Main.java
└── README_IT.md        ← rinominalo in README.md (sostituisce quello autogenerato)
```

Da terminale:

```bash
# Crea la cartella src
mkdir src

# Copia i file .java dentro src/
# (adatta il percorso alla posizione dei tuoi file)
cp /percorso/ai/tuoi/file/*.java src/

# Sostituisci il README autogenerato con il tuo
cp /percorso/README_IT.md README.md
```

---

#### 4A.3 — Aggiorna il .gitignore

Apri il file `.gitignore` (già presente perché abbiamo scelto Java al momento della creazione) e assicurati che contenga queste righe. Se non ci sono, aggiungile:

```gitignore
# Bytecode Java
*.class

# File di dati locale (può contenere dati personali)
log_interventi.dat
log_interventi.dat.tmp

# Eseguibili JAR
*.jar

# IDE
.idea/
*.iml
.vscode/
*.swp
out/
```

---

#### 4A.4 — Controlla cosa sta per essere caricato

```bash
git status
```

Vedrai l'elenco dei file nuovi/modificati in rosso. Verifica che non ci siano `.dat`, `.class` o file privati nell'elenco.

---

#### 4A.5 — Aggiungi tutti i file all'area di staging

```bash
git add .
```

Oppure, per aggiungere file specifici uno per uno:

```bash
git add src/Intervento.java
git add src/InterventoHardware.java
git add src/InterventoSoftware.java
git add src/InterventoNonTrovatoException.java
git add src/LogManager.java
git add src/Main.java
git add README.md
git add .gitignore
```

---

#### 4A.6 — Crea il primo commit

```bash
git commit -m "Initial release: PersonalTechLog v1.0"
```

Il messaggio del commit apparirà nella cronologia su GitHub. Rendilo descrittivo.

---

#### 4A.7 — Carica sul repository GitHub

```bash
git push origin main
```

Se il branch si chiama `master` invece di `main`:

```bash
git push origin master
```

Git chiederà le credenziali GitHub. Inserisci username e **Personal Access Token** (non la password dell'account — vedi nota sotto).

---

> **NOTA — GitHub non accetta più le password normali.**
> Devi usare un **Personal Access Token (PAT)**:
> 1. Vai su GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
> 2. Clicca "Generate new token (classic)"
> 3. Metti una descrizione (es. "push da pc")
> 4. Spunta il permesso **repo**
> 5. Clicca "Generate token" e **copia il token** (lo vedi solo una volta)
> 6. Usalo come "password" quando Git te la chiede

---

#### 4A.8 — Verifica su GitHub

Apri il browser e vai su:
```
https://github.com/TUOUSERNAME/PersonalTechLog
```

Dovresti vedere tutti i file caricati e il README visualizzato automaticamente.

---

### OPZIONE B — Tramite interfaccia web GitHub (più semplice)

Se non vuoi usare il terminale, puoi caricare i file direttamente dal browser.

1. Vai sulla pagina del tuo repository su GitHub
2. Clicca **"Add file"** → **"Upload files"**
3. Trascina i file `.java` dalla cartella `src/` del tuo PC nella finestra del browser
4. Nella sezione "Commit changes" in fondo alla pagina, scrivi un messaggio (es. `"Aggiunti file sorgente"`)
5. Clicca **"Commit changes"**
6. Ripeti per il `README.md` aggiornato

> Nota: con il metodo web non puoi creare sottocartelle facilmente. Per creare `src/`, scrivi il nome del file come `src/Intervento.java` nel campo nome — GitHub creerà automaticamente la cartella.

---

## 5. VERIFICA CHE TUTTO FUNZIONI

Dopo il caricamento, controlla queste cose sulla pagina del repository:

| Cosa verificare | Come |
|---|---|
| README visibile | Scorrendo la pagina del repo, deve apparire formattato |
| Tutti i file .java presenti | Clicca sulla cartella `src/` |
| Licenza riconosciuta | In alto a destra nella pagina del repo deve apparire "MIT license" |
| .gitignore attivo | Il file `.gitignore` deve essere visibile nella root |
| Nessun .dat o .class | Verifica che non siano stati caricati file di dati o bytecode |

---

## 6. AGGIORNAMENTI FUTURI

Ogni volta che modifichi il codice e vuoi aggiornare GitHub:

```bash
# 1. Vai nella cartella del progetto
cd PersonalTechLog

# 2. Controlla cosa è cambiato
git status

# 3. Aggiungi le modifiche
git add .

# 4. Crea un commit con una descrizione chiara
git commit -m "Aggiunta funzione modifica intervento"

# 5. Carica su GitHub
git push origin main
```

---

## 7. STRUTTURA FINALE DEL REPOSITORY SU GITHUB

Dopo il caricamento, il repository apparirà così:

```
PersonalTechLog/
├── src/
│   ├── Intervento.java
│   ├── InterventoHardware.java
│   ├── InterventoSoftware.java
│   ├── InterventoNonTrovatoException.java
│   ├── LogManager.java
│   └── Main.java
├── .gitignore
├── LICENSE
└── README.md
```

---

## 8. RIEPILOGO COMANDI GIT

```bash
git clone URL                    # Scarica il repo sul PC
git status                       # Mostra file modificati/nuovi
git add .                        # Aggiunge tutto all'area di staging
git add nomefile                 # Aggiunge un file specifico
git commit -m "messaggio"        # Salva una versione con descrizione
git push origin main             # Carica su GitHub
git pull origin main             # Scarica aggiornamenti da GitHub
git log --oneline                # Mostra la cronologia dei commit
```

---

*Guida scritta per PersonalTechLog — Java CLI Tool*
