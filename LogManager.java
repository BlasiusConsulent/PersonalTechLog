import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Gestisce il ciclo di vita della lista interventi: CRUD in memoria + persistenza su disco.
 *
 * Ho separato questa logica dal Main per rispettare il principio di singola responsabilità:
 * il Main si occupa solo dell'interazione con l'utente, LogManager dei dati.
 *
 * Persistenza tramite serializzazione Java binaria: più robusto del CSV per oggetti complessi,
 * e non richiede dipendenze esterne. Limite noto: non human-readable, ma per un tool personale
 * va benissimo.
 */
public class LogManager {

    private static final String FILE_PATH      = "log_interventi.dat";
    // FIX BUG #5: scrittura su file temporaneo nella stessa directory del target.
    // Se scriviamo e crashiamo a metà, il .dat originale è intatto.
    // Solo quando writeObject() completa con successo, il temp rimpiazza l'originale.
    private static final String FILE_PATH_TEMP = "log_interventi.dat.tmp";

    // La lista è il cuore del sistema. ArrayList perché accediamo spesso per indice
    // e non abbiamo bisogno di inserimenti/cancellazioni frequenti in mezzo alla lista.
    private List<Intervento> interventi;

    public LogManager() {
        this.interventi = new ArrayList<>();
    }

    // --- Operazioni CRUD ---

    public void aggiungi(Intervento i) {
        interventi.add(i);
        System.out.println("  [OK] Intervento [" + i.getId() + "] aggiunto.");
    }

    /**
     * Cerca per ID e lancia l'eccezione custom se non esiste.
     * Stream API per trovare il primo match: più espressivo di un for-loop classico.
     */
    public Intervento cercaPerId(String id) throws InterventoNonTrovatoException {
        // Fail-fast su null: equalsIgnoreCase(null) lancerebbe NPE silenzioso
        // dentro lo stream, rendendo il punto di errore difficile da tracciare.
        Objects.requireNonNull(id, "id di ricerca non può essere null");
        return interventi.stream()
                .filter(i -> i.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new InterventoNonTrovatoException(id));
    }

    public void elimina(String id) throws InterventoNonTrovatoException {
        // cercaPerId lancia l'eccezione se non trovato, quindi remove() è safe.
        // FIX: ora che Intervento.equals() è basato su ID, remove(Object) funziona
        // anche su oggetti ricaricati da file (reference diversa, stesso ID).
        Intervento trovato = cercaPerId(id);
        interventi.remove(trovato);
        System.out.println("  [OK] Intervento [" + id + "] rimosso.");
    }

    /**
     * FIX BUG #4: ritorna una vista non modificabile della lista interna.
     * Il chiamante può iterare e leggere, ma non può fare add/remove direttamente,
     * preservando l'incapsulamento di LogManager.
     */
    public List<Intervento> getTutti() {
        return Collections.unmodifiableList(interventi);
    }

    // --- Persistenza ---

    /**
     * FIX BUG #3 + FIX BUG #5:
     * - Rimosso @SuppressWarnings("unchecked") errato: questa metodo non fa cast generici.
     * - Scrittura atomica: usiamo un file .tmp, lo scriviamo completamente,
     *   poi lo rinominiamo sul target con ATOMIC_MOVE + REPLACE_EXISTING.
     *   Se la JVM crasha a metà scrittura, il file .dat originale è sopravvissuto.
     */
    public void salva() {
        Path tempPath   = Paths.get(FILE_PATH_TEMP);
        Path targetPath = Paths.get(FILE_PATH);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(FILE_PATH_TEMP)))) {
            oos.writeObject(interventi);
        } catch (IOException e) {
            System.err.println("  [ERRORE] Scrittura fallita: " + e.getMessage());
            return; // non rinominare se la scrittura non è andata a buon fine
        }

        // Move atomico: su filesystem locali Linux/macOS è garantito atomico.
        // Su Windows potrebbe non essere atomico ma REPLACE_EXISTING è comunque safe.
        try {
            Files.move(tempPath, targetPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            System.out.println("  [OK] Log salvato in '" + FILE_PATH + "' (" + interventi.size() + " interventi).");
        } catch (IOException e) {
            System.err.println("  [ERRORE] Impossibile finalizzare il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Caricamento all'avvio: se il file non esiste si parte puliti, nessun crash.
     * Il cast unchecked è inevitabile con la serializzazione generica, ma è sicuro
     * perché sappiamo cosa abbiamo scritto noi stessi.
     */
    @SuppressWarnings("unchecked")
    public void carica() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("  [i] Nessun log precedente trovato. Partenza da zero.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            interventi = (List<Intervento>) ois.readObject();
            System.out.println("  [OK] Caricati " + interventi.size() + " interventi dal log.");
        } catch (IOException | ClassNotFoundException e) {
            // Dati non recuperabili: avvisiamo l'utente esplicitamente.
            System.err.println("  [ATTENZIONE] File di log non leggibile o corrotto: " + e.getMessage());
            System.err.println("  [ATTENZIONE] Partenza da zero. I dati precedenti potrebbero essere persi.");
            // interventi rimane l'ArrayList vuoto del costruttore: stato consistente.
        }
    }
}
