# PersonalTechLog

> Strumento a riga di comando leggero per consulenti informatici che vogliono registrare i propri interventi tecnici giornalieri senza la complessità di un gestionale, un database o una connessione internet.

[![Licenza: MIT](https://img.shields.io/badge/Licenza-MIT-yellow.svg)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-14%2B-blue.svg)](https://www.oracle.com/java/)
[![Stato](https://img.shields.io/badge/stato-stabile-brightgreen.svg)]()
[![Dipendenze](https://img.shields.io/badge/dipendenze-nessuna-brightgreen.svg)]()

---

## Indice

- [Descrizione](#descrizione)
- [Demo rapida](#demo-rapida)
- [Funzionalità](#funzionalità)
- [Architettura](#architettura)
- [Struttura del progetto](#struttura-del-progetto)
- [Requisiti di sistema](#requisiti-di-sistema)
- [Installazione e avvio](#installazione-e-avvio)
- [Utilizzo dettagliato](#utilizzo-dettagliato)
- [Logica delle tariffe](#logica-delle-tariffe)
- [Persistenza dei dati](#persistenza-dei-dati)
- [Concetti OOP implementati](#concetti-oop-implementati)
- [Scelte tecniche e trade-off](#scelte-tecniche-e-trade-off)
- [Limitazioni note](#limitazioni-note)
- [Roadmap futura](#roadmap-futura)
- [Tecnologie utilizzate](#tecnologie-utilizzate)
- [Contribuire al progetto](#contribuire-al-progetto)
- [Licenza](#licenza)
- [Note legali, esclusione di responsabilità e privacy](#note-legali-esclusione-di-responsabilità-e-privacy)

---

## Descrizione

Come consulente informatico freelance, ogni giornata è un mix di interventi hardware, reinstallazioni di sistemi operativi, configurazioni di rete e ticket vari. Tenere traccia di tutto in un foglio Excel è scomodo, affidarsi a un CRM online richiede connessione e abbonamento, e la carta è la carta.

**PersonalTechLog** è uno strumento da terminale minimal che:

- gira interamente **offline** sul proprio laptop o workstation
- salva tutto in un **singolo file locale**, senza server né cloud
- non richiede installazioni aggiuntive, server o dipendenze esterne
- si avvia in meno di un secondo e non presenta form inutili

Si aggiunge il cliente, si descrive l'intervento, si preme invio. Fine.

---

## Demo rapida

```
=========================================
   PersonalTechLog — IT Consultant Log
=========================================
  [OK] Caricati 3 interventi dal log.

--- MENU ----------------------------------------
  1. Aggiungi Intervento Hardware
  2. Aggiungi Intervento Software
  3. Visualizza tutti gli interventi
  4. Cerca intervento per ID
  5. Elimina intervento per ID
  6. Salva ed Esci
  Scelta: 3

-- Interventi registrati (3) ----------------
  [HW] A3F1C2D9 | Cliente: Rossi Srl           | Data: 2025-06-10 | Sostituzione RAM   | Ricambio: Crucial 2x16GB DDR5   | Tariffa: 90.00€
  [SW] 7B2E4F1A | Cliente: Studio Bianchi       | Data: 2025-06-11 | Reinstall OS       | OS: Windows 11 Pro              | Tariffa: 55.00€
  [SW] 9C3D5E2B | Cliente: Comune di Verona     | Data: 2025-06-12 | Migrazione AD      | OS: Windows Server 2022         | Tariffa: 77.00€

  Totale fatturabile stimato: 222.00 EUR
```

---

## Funzionalità

| Funzione | Descrizione |
|---|---|
| **Aggiungi HW** | Registra un intervento hardware con cliente, data, descrizione e pezzo di ricambio |
| **Aggiungi SW** | Registra un intervento software con cliente, data, descrizione e sistema operativo |
| **Visualizza tutti** | Mostra la lista completa con il totale fatturabile stimato |
| **Cerca per ID** | Ricerca puntuale per ID univoco, case-insensitive |
| **Elimina per ID** | Rimozione di un singolo intervento dal log |
| **Salva ed Esci** | Persiste i dati su disco e termina il programma |
| **Auto-save** | In caso di Ctrl+C o chiusura improvvisa, i dati vengono salvati automaticamente tramite shutdown hook JVM |

---

## Architettura

Il progetto è strutturato attorno a tre layer distinti che non si sovrappongono:

```
┌──────────────────────────────────────────────────────┐
│                   LAYER UI  (Main.java)               │
│   Menu CLI, lettura input utente, routing scelte      │
├──────────────────────────────────────────────────────┤
│             LAYER DATI  (LogManager.java)             │
│   CRUD in memoria, persistenza file, eccezioni        │
├─────────────────────────┬────────────────────────────┤
│  InterventoHardware     │  InterventoSoftware         │
│  + pezzoRicambio        │  + sistemaOperativo         │
│  tariffa = 90.00 €      │  tariffa = 55 / 77 €        │
└─────────────────────────┴────────────────────────────┘
            ↑ entrambi estendono
   ┌──────────────────────────────┐
   │     Intervento  (astratta)   │
   │  id, cliente, data, desc     │
   │  calcolaTariffa()  abstract  │
   └──────────────────────────────┘
```

Ogni layer conosce solo quello immediatamente sottostante. `Main` non sa come funziona la serializzazione; `LogManager` non sa come viene visualizzato il menu.

---

## Struttura del progetto

```
PersonalTechLog/
├── src/
│   ├── Intervento.java                     # Classe base astratta
│   ├── InterventoHardware.java             # Sottoclasse HW
│   ├── InterventoSoftware.java             # Sottoclasse SW
│   ├── InterventoNonTrovatoException.java  # Eccezione checked personalizzata
│   ├── LogManager.java                     # Gestione lista + I/O su disco
│   └── Main.java                           # Entry point CLI
├── log_interventi.dat                      # Generato automaticamente (non committare)
├── .gitignore
├── LICENSE
└── README.md
```

> `log_interventi.dat` è il file di persistenza binario. Viene creato nella directory di esecuzione. Non va incluso nel repository Git in quanto può contenere dati personali di terzi.

---

## Requisiti di sistema

| Requisito | Dettaglio |
|---|---|
| **Java JDK** | Versione 14 o superiore (consigliato 17 o 21 LTS) |
| **Sistema operativo** | Linux, macOS, Windows (qualsiasi versione con JDK supportato) |
| **Spazio su disco** | Inferiore a 1 MB per il software; il file `.dat` cresce di circa 500 byte per ogni intervento |
| **RAM** | Trascurabile (< 50 MB a runtime) |
| **Dipendenze esterne** | **Nessuna** |
| **Connessione internet** | **Non richiesta** |

Per verificare la versione Java installata:

```bash
java -version
javac -version
```

---

## Installazione e avvio

### 1 — Clona il repository

```bash
git clone https://github.com/tuousername/PersonalTechLog.git
cd PersonalTechLog
```

### 2 — Compila il sorgente

```bash
cd src
javac *.java
```

### 3 — Avvia il programma

```bash
java Main
```

Il file `log_interventi.dat` viene creato nella directory corrente al primo salvataggio.

---

### Alternativa — JAR eseguibile

Per ottenere un singolo file `.jar` trasportabile:

```bash
cd src
javac *.java
jar cfe ../PersonalTechLog.jar Main *.class
cd ..
java -jar PersonalTechLog.jar
```

Il `.jar` può essere spostato in qualsiasi directory. I dati vengono salvati nella cartella da cui viene lanciato.

---

### Configurazione .gitignore consigliata

```gitignore
# Bytecode compilato
*.class

# File di dati locale (può contenere dati personali di clienti)
log_interventi.dat
log_interventi.dat.tmp

# Ambienti IDE
.idea/
*.iml
.vscode/
*.swp
```

---

## Utilizzo dettagliato

### Aggiungere un intervento Hardware

```
Scelta: 1

-- Nuovo Intervento Hardware --
  Cliente: Farmacia Centrale
  Data (YYYY-MM-DD, invio per oggi): 2025-06-15
  Descrizione: Sostituzione alimentatore danneggiato
  Pezzo di ricambio: Seasonic Focus GX-650W

  [OK] Intervento [F3A8C1D2] aggiunto.
```

L'ID viene generato automaticamente come UUID troncato a 8 caratteri esadecimali maiuscoli.

### Aggiungere un intervento Software

```
Scelta: 2

-- Nuovo Intervento Software --
  Cliente: Studio Legale Mori
  Data (YYYY-MM-DD, invio per oggi):
  Descrizione: Migrazione profili utente su nuovo dominio
  Sistema Operativo: Windows Server 2019

  [OK] Intervento [9E2B4F7A] aggiunto.
```

Premendo invio sul campo data senza digitare nulla, viene usata automaticamente la data odierna.

### Visualizzare tutti gli interventi

```
Scelta: 3

-- Interventi registrati (2) ----------------
  [HW] F3A8C1D2 | Cliente: Farmacia Centrale   | Data: 2025-06-15 | Sostituzione alimentatore | Ricambio: Seasonic GX-650W  | Tariffa: 90.00€
  [SW] 9E2B4F7A | Cliente: Studio Legale Mori  | Data: 2025-06-16 | Migrazione profili        | OS: Windows Server 2019     | Tariffa: 77.00€

  Totale fatturabile stimato: 167.00 EUR
```

### Cercare un intervento per ID

```
Scelta: 4

  ID da cercare: f3a8c1d2
  Trovato --> [HW] F3A8C1D2 | Cliente: Farmacia Centrale ...
```

La ricerca è **case-insensitive**: `f3a8c1d2`, `F3A8C1D2` e `F3a8C1d2` trovano lo stesso risultato.

### Eliminare un intervento

```
Scelta: 5

  ID da eliminare: 9E2B4F7A
  [OK] Intervento [9E2B4F7A] rimosso.
```

Se l'ID non esiste, il programma mostra un messaggio e non crasha:

```
  [X] Nessun intervento trovato con ID: XXXXXXXX
```

### Salvare ed uscire

```
Scelta: 6

  [OK] Log salvato in 'log_interventi.dat' (1 interventi).

  Arrivederci.
```

---

## Logica delle tariffe

Le tariffe sono costanti nel codice sorgente. Per modificarle è sufficiente aggiornare le seguenti righe e ricompilare:

```java
// InterventoHardware.java — riga ~12
private static final double TARIFFA_ORARIA_HW = 65.0;

// InterventoSoftware.java — riga ~12
private static final double TARIFFA_BASE_SW = 55.0;
```

| Tipo intervento | Base | Extra | Totale |
|---|---|---|---|
| **Hardware** | 65,00 €/h | + 25,00 € trasferta fissa | **90,00 €** |
| **Software** — OS client | 55,00 €/h | nessuno | **55,00 €** |
| **Software** — OS server | 55,00 €/h | × 1,4 (ambienti server) | **77,00 €** |

**Quando si applica il moltiplicatore server?**
Il campo `sistemaOperativo` viene analizzato (case-insensitive): se contiene la parola `server`, viene applicato il coefficiente 1,4. Esempi che lo attivano: `Windows Server 2019`, `Ubuntu Server 22.04`, `CentOS 7 Server`.

> **Nota importante:** i totali visualizzati sono **stime orientative** a uso personale del consulente. Non costituiscono preventivi, offerte vincolanti, fatture o documentazione fiscale di alcun tipo. Si veda la sezione [Note legali](#note-legali-esclusione-di-responsabilità-e-privacy).

---

## Persistenza dei dati

I dati vengono salvati tramite **Java Object Serialization** nel file binario `log_interventi.dat`.

### Salvataggio atomico

Il salvataggio avviene in due fasi per proteggere i dati esistenti in caso di interruzione improvvisa:

```
Fase 1 → Scrittura completa su log_interventi.dat.tmp
          (se fallisce, il file .dat originale rimane intatto)

Fase 2 → Rinomina atomica: .dat.tmp diventa .dat
          (o riesce completamente, o non cambia nulla)
```

### Auto-save su uscita anomala

Uno **shutdown hook JVM** garantisce il salvataggio automatico in caso di:
- `Ctrl+C` da terminale
- `SIGTERM` dal sistema operativo
- Chiusura della finestra del terminale

L'unico scenario in cui i dati non vengono salvati automaticamente è un `SIGKILL` (`kill -9`) o un crash hardware, non verificabili nell'uso normale.

### Compatibilità tra versioni

Ogni classe serializzata dichiara un `serialVersionUID` esplicito. I file `.dat` restano leggibili tra versioni successive del software finché tali identificatori non vengono modificati deliberatamente.

---

## Concetti OOP implementati

### Ereditarietà e Polimorfismo

`Intervento` è una **classe astratta** con due metodi astratti obbligatori per le sottoclassi:

```java
public abstract double calcolaTariffa();
protected abstract String getTipo();
```

`InterventoHardware` e `InterventoSoftware` implementano entrambi in modo diverso. Nel `LogManager` e nel `Main` il codice opera sull'interfaccia comune senza conoscere il tipo concreto:

```java
// Dispatch automatico: HW o SW, la chiamata è identica
lista.stream().mapToDouble(Intervento::calcolaTariffa).sum();
```

### Incapsulamento

Tutti i campi sono `private`. L'accesso avviene esclusivamente tramite getter e setter con validazione. `LogManager` espone la lista interna solo tramite `Collections.unmodifiableList()`: il chiamante può leggere ma non modificare direttamente la struttura dati interna.

### Gestione eccezioni

`InterventoNonTrovatoException` è una **checked exception** (estende `Exception`): ricercare o eliminare un ID inesistente è un input errato dell'utente, non un bug del programma. Usare una checked exception costringe il chiamante a gestire esplicitamente il caso.

### Collezioni e Stream API

`ArrayList<Intervento>` come struttura dati principale. Ricerca tramite Stream API con `filter().findFirst().orElseThrow()`. Aggregazione delle tariffe con `mapToDouble().sum()`.

---

## Scelte tecniche e trade-off

| Scelta effettuata | Alternativa considerata | Motivazione |
|---|---|---|
| Java Object Serialization | JSON con Gson/Jackson | Zero dipendenze esterne |
| `ArrayList` | `LinkedList` | Accesso per indice O(1), iterazione sequenziale prevalente |
| Checked exception | RuntimeException | Forza la gestione esplicita nel chiamante |
| Atomic write (tmp + rename) | Scrittura diretta | Protegge i dati da corruzione in caso di crash |
| Shutdown hook | Solo salvataggio manuale | Persistenza garantita anche su Ctrl+C e SIGTERM |
| `Collections.unmodifiableList` | Ritornare la lista diretta | Preserva l'incapsulamento di LogManager |
| `Objects.requireNonNull` | Controllo manuale `if null` | Fail-fast con messaggio descrittivo, idioma Java standard |
| UUID troncato a 8 hex | Contatore numerico | Nessuna gestione di stato globale del contatore |

---

## Limitazioni note

**Formato non human-readable.** Il file `.dat` è binario e non può essere letto o modificato con un editor di testo.

**Nessuna modifica degli interventi.** Una volta aggiunto, un intervento non è modificabile. Per correggere un dato occorre eliminarlo e reinserirlo.

**Tariffe fisse nel sorgente.** Per cambiare le tariffe è necessario modificare le costanti nel codice e ricompilare.

**Probabilità di collisione ID.** Con 1.000 interventi la probabilità di generare due ID identici è circa 0,012%. Per uso quotidiano è trascurabile.

**Single-user.** Non è prevista gestione multi-utente, locking su file o sincronizzazione concorrente.

---

## Roadmap futura

- [ ] Modifica di un intervento esistente
- [ ] Filtro per data o per cliente
- [ ] Export in formato CSV per importazione in Excel / Google Sheets
- [ ] File di configurazione esterno per le tariffe (`.properties`)
- [ ] Statistiche mensili: numero interventi, media tariffa, cliente più frequente
- [ ] Ricerca per nome cliente
- [ ] Migrazione opzionale a JSON per portabilità del file dati

---

## Tecnologie utilizzate

| Tecnologia | Versione | Utilizzo |
|---|---|---|
| **Java SE** | 14+ (testato su 21) | Linguaggio principale |
| **Java Object Serialization** | Built-in (`java.io`) | Persistenza dati su file binario |
| **Java NIO** | Built-in (`java.nio.file`) | Scrittura atomica con `Files.move()` |
| **Java Time API** | Built-in (`java.time`) | Gestione date con `LocalDate` |
| **Java Collections** | Built-in (`java.util`) | `ArrayList`, `Collections.unmodifiableList` |
| **Java Stream API** | Built-in (`java.util.stream`) | Ricerca, aggregazione, totale tariffe |
| **Dipendenze esterne** | — | **Nessuna** |

---

## Contribuire al progetto

Il progetto è aperto a contribuzioni. Per proporre modifiche:

1. Eseguire un fork del repository
2. Creare un branch descrittivo (`git checkout -b feature/nome-funzione`)
3. Effettuare le modifiche con commit chiari e atomici
4. Aprire una Pull Request descrivendo le modifiche apportate e la motivazione

Per segnalare bug o proporre funzionalità, aprire una **Issue** su GitHub con una descrizione dettagliata del comportamento osservato o desiderato.

---

## Licenza

Questo software è distribuito sotto **Licenza MIT**.

```
MIT License

Copyright (c) 2025 — Autore del progetto

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

Il testo completo della licenza è disponibile nel file [`LICENSE`](./LICENSE) incluso nel repository.

---

## Note legali, esclusione di responsabilità e privacy

> Leggere attentamente questa sezione prima di utilizzare, distribuire o modificare il software.

---

### 1. Natura del software e utilizzo consentito

PersonalTechLog è un software open source distribuito a titolo **gratuito** e **senza scopo di lucro** ai sensi della Licenza MIT. È progettato come strumento di supporto personale per la registrazione informale di interventi tecnici. Non è un software gestionale, fiscale, contabile o professionale certificato.

L'utilizzo del software è consentito a chiunque nel rispetto della Licenza MIT e della normativa vigente nel paese dell'utente. È fatto divieto di utilizzare il software per attività illecite, fraudolente o contrarie all'ordine pubblico.

---

### 2. Esclusione totale di garanzia

Ai sensi dell'art. 1229 del Codice Civile italiano, nei limiti consentiti dalla legge, e in conformità con la sezione **"AS IS"** della Licenza MIT, il software viene fornito **senza alcuna garanzia di alcun tipo**, espressa o implicita, incluse a titolo esemplificativo e non esaustivo:

- garanzia di **commerciabilità** o idoneità per uno scopo specifico;
- garanzia di **assenza di difetti**, errori, interruzioni o perdita di dati;
- garanzia di **accuratezza, completezza o aggiornamento** delle funzionalità;
- garanzia di **compatibilità** con qualsiasi sistema operativo, hardware o configurazione software;
- garanzia di **continuità del servizio** o disponibilità nel tempo.

L'autore non fornisce alcun supporto tecnico, assistenza o manutenzione obbligatoria.

---

### 3. Esclusione totale di responsabilità

Nella misura massima consentita dalla legge applicabile, l'autore, i contributori e i distributori del software **declinano ogni e qualsiasi responsabilità** per danni diretti, indiretti, incidentali, speciali, consequenziali, punitivi o di qualsiasi altra natura, derivanti da o connessi con:

- l'uso, l'impossibilità di uso, il malfunzionamento o la messa fuori servizio del software;
- la **perdita di dati**, corruzione di file o indisponibilità del file di persistenza `.dat`;
- **errori nei calcoli** delle tariffe o nei totali stimati visualizzati dal software;
- decisioni aziendali, fiscali, contabili o legali prese sulla base dei dati inseriti o visualizzati nel software;
- accesso non autorizzato ai dati salvati localmente;
- incompatibilità con versioni future di Java o del sistema operativo;
- qualsiasi danno patrimoniale, economico o non patrimoniale subito dall'utente o da terzi.

Tale esclusione si applica indipendentemente dalla teoria giuridica invocata (contratto, illecito extracontrattuale, responsabilità oggettiva o altra) e anche nel caso in cui l'autore sia stato informato della possibilità di tali danni.

**I calcoli delle tariffe visualizzati dal software sono esclusivamente stime orientative a uso personale.** Non costituiscono in alcun modo preventivi ufficiali, offerte contrattuali, fatture, documenti fiscali o probatori. Qualsiasi utilizzo di tali dati a fini contabili, fiscali o legali è esclusiva responsabilità dell'utente.

---

### 4. Responsabilità dell'utente

L'utente è l'unico responsabile:

- della **correttezza e completezza** dei dati inseriti nel software;
- della **sicurezza** del file `.dat` e del dispositivo su cui è installato il software;
- del rispetto della normativa applicabile in materia di trattamento dei dati personali (si veda la sezione Privacy);
- dell'adeguatezza del software rispetto alle proprie esigenze professionali;
- di qualsiasi **backup** dei dati: il software non include funzionalità di backup automatico su supporti esterni o cloud.

---

### 5. Privacy e trattamento dei dati personali (GDPR)

Il software salva localmente, nel file `log_interventi.dat`, i dati inseriti dall'utente, che possono includere **dati personali di terzi** (ad esempio: ragioni sociali, nomi di clienti, descrizioni di interventi).

In conformità con il **Regolamento (UE) 2016/679 (GDPR)** e con il **D.Lgs. 196/2003** (Codice in materia di protezione dei dati personali), come modificato dal D.Lgs. 101/2018, si precisa quanto segue:

- Il software **non trasmette alcun dato** a server esterni, servizi cloud, terze parti o all'autore. Tutti i dati rimangono esclusivamente sul dispositivo locale dell'utente.
- L'autore del software **non è titolare del trattamento** dei dati inseriti dall'utente. Il titolare del trattamento è esclusivamente l'utente o il soggetto che utilizza il software nell'esercizio della propria attività.
- L'utente che inserisce nel software dati personali di terzi (clienti, collaboratori, ecc.) è tenuto a farlo nel rispetto della normativa privacy applicabile, in particolare verificando di disporre di una **base giuridica** idonea al trattamento (art. 6 GDPR) e, ove necessario, di aver reso apposita **informativa** agli interessati.
- Si raccomanda di non inserire nel software **categorie particolari di dati** ai sensi dell'art. 9 GDPR (dati sanitari, biometrici, giudiziari, ecc.).
- Il file `.dat` non è cifrato. L'utente è responsabile della **protezione fisica e logica** del dispositivo su cui il software è installato, nonché dell'adozione di misure di sicurezza adeguate (art. 32 GDPR).
- In caso di perdita, furto o accesso non autorizzato al dispositivo, l'utente dovrà valutare autonomamente l'eventuale obbligo di notifica della violazione al Garante per la protezione dei dati personali entro 72 ore (art. 33 GDPR) e agli interessati (art. 34 GDPR).

---

### 6. Proprietà intellettuale

Il codice sorgente di PersonalTechLog è protetto dal diritto d'autore ai sensi della **Legge 22 aprile 1941 n. 633** (Legge sul diritto d'autore) e successive modifiche, nonché dalla normativa comunitaria applicabile.

La distribuzione, modifica e utilizzo del software sono consentiti nei limiti e alle condizioni della **Licenza MIT** riportata nella sezione precedente e nel file `LICENSE` allegato.

Marchi commerciali, nomi di prodotti e loghi di terze parti eventualmente citati nel software o nella documentazione (es. Windows, Ubuntu, ecc.) sono di proprietà dei rispettivi titolari. Il loro utilizzo è puramente descrittivo e non implica alcuna affiliazione, sponsorizzazione o approvazione da parte dei relativi titolari nei confronti di questo progetto.

---

### 7. Legge applicabile e foro competente

Il presente software e la relativa documentazione sono regolati dalla **legge italiana**. Per qualsiasi controversia connessa all'utilizzo del software, nei limiti in cui sia applicabile, è competente il **Foro di [indicare la città dell'autore]**, salva diversa disposizione inderogabile di legge.

Nei rapporti con utenti qualificati come **consumatori** ai sensi del D.Lgs. 206/2005 (Codice del Consumo), si applicano le disposizioni inderogabili di legge a tutela del consumatore, incluse le norme sulla risoluzione alternativa delle controversie (ODR/ADR) ai sensi del D.Lgs. 130/2015 e del Regolamento UE 524/2013.

---

### 8. Modifiche al software e alla documentazione

L'autore si riserva il diritto di modificare, aggiornare, sospendere o interrompere lo sviluppo del software in qualsiasi momento e senza preavviso. Le versioni future potrebbero non essere compatibili con i file `.dat` generati da versioni precedenti. L'utente è responsabile di verificare la compatibilità prima di aggiornare.

---

### 9. Separabilità delle clausole

Qualora una o più disposizioni delle presenti note legali risultino invalide, nulle o inefficaci ai sensi della legge applicabile, le restanti disposizioni rimarranno pienamente in vigore. La clausola invalida verrà sostituita dalla disposizione valida che più si avvicini all'intento originario.

---

*Ultimo aggiornamento delle note legali: 2025*

*Per segnalazioni, contatti o richieste relative alla privacy: aprire una Issue su GitHub oppure contattare l'autore tramite i recapiti indicati sul profilo GitHub.*
