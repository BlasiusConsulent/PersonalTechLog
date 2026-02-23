import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

/**
 * Entry point del tool. Gestisce il loop principale del menu CLI.
 *
 * L'ID viene generato automaticamente con UUID troncato: in un tool personale
 * non ha senso che l'utente se lo inventi a mano, meglio garantire unicità.
 */
public class Main {

    private static final LogManager logManager = new LogManager();

    // Scanner non è più static field ma viene creato e chiuso nel main()
    // per evitare resource leak. Lo passiamo come parametro ai metodi helper.
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   PersonalTechLog — IT Consultant Log   ");
        System.out.println("=========================================");

        logManager.carica();

        // FIX BUG #7: Shutdown hook — garantisce il salvataggio anche in caso di
        // Ctrl+C, SIGTERM, o chiusura della finestra del terminale.
        // Il JVM lo esegue prima di terminare, tranne in caso di SIGKILL o crash della JVM stessa.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n  [Auto-save] Salvataggio dati in corso...");
            logManager.salva();
        }, "shutdown-save-hook"));

        // FIX BUG #6: Scanner aperto in try-with-resources → chiuso automaticamente
        // anche in caso di eccezione, eliminando il resource leak.
        try (Scanner sc = new Scanner(System.in)) {
            scanner = sc;
            runLoop();
        }
    }

    private static void runLoop() {
        boolean running = true;
        while (running) {
            stampaMenu();

            // FIX BUG #8: gestione EOF/pipe — se lo stdin si chiude inaspettatamente
            // (es. piping di input da file, terminale chiuso) catturiamo NoSuchElementException
            // e terminiamo in modo pulito invece di mostrare uno stack trace all'utente.
            String scelta;
            try {
                scelta = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                System.out.println("\n  [i] Input terminato, uscita.");
                break;
            }

            switch (scelta) {
                case "1" -> aggiungiHardware();
                case "2" -> aggiungiSoftware();
                case "3" -> visualizzaTutti();
                case "4" -> cercaIntervento();
                case "5" -> eliminaIntervento();
                case "6" -> {
                    // Salvataggio esplicito prima di uscire; lo shutdown hook lo farebbe
                    // comunque, ma è più rapido e fornisce feedback immediato all'utente.
                    logManager.salva();
                    System.out.println("\n  Arrivederci.\n");
                    running = false;
                }
                default -> System.out.println("  Opzione non valida. Riprova.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    private static void stampaMenu() {
        System.out.println("\n--- MENU ----------------------------------------");
        System.out.println("  1. Aggiungi Intervento Hardware");
        System.out.println("  2. Aggiungi Intervento Software");
        System.out.println("  3. Visualizza tutti gli interventi");
        System.out.println("  4. Cerca intervento per ID");
        System.out.println("  5. Elimina intervento per ID");
        System.out.println("  6. Salva ed Esci");
        System.out.print("  Scelta: ");
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    private static void aggiungiHardware() {
        System.out.println("\n-- Nuovo Intervento Hardware --");
        String cliente     = leggiStringa("  Cliente: ");
        LocalDate data     = leggiData("  Data (YYYY-MM-DD, invio per oggi): ");
        String descrizione = leggiStringa("  Descrizione: ");
        String pezzo       = leggiStringa("  Pezzo di ricambio: ");

        String id = generaId();
        InterventoHardware hw = new InterventoHardware(id, cliente, data, descrizione, pezzo);
        logManager.aggiungi(hw);
    }

    private static void aggiungiSoftware() {
        System.out.println("\n-- Nuovo Intervento Software --");
        String cliente     = leggiStringa("  Cliente: ");
        LocalDate data     = leggiData("  Data (YYYY-MM-DD, invio per oggi): ");
        String descrizione = leggiStringa("  Descrizione: ");
        String so          = leggiStringa("  Sistema Operativo: ");

        String id = generaId();
        InterventoSoftware sw = new InterventoSoftware(id, cliente, data, descrizione, so);
        logManager.aggiungi(sw);
    }

    private static void visualizzaTutti() {
        List<Intervento> lista = logManager.getTutti();
        if (lista.isEmpty()) {
            System.out.println("\n  Nessun intervento registrato.");
            return;
        }
        System.out.println("\n-- Interventi registrati (" + lista.size() + ") ----------------");
        // Polimorfismo: toString() chiama la versione giusta (HW o SW) automaticamente.
        // lista è ora una unmodifiableList, ma forEach è read-only quindi nessun problema.
        lista.forEach(i -> System.out.println("  " + i));

        double totale = lista.stream().mapToDouble(Intervento::calcolaTariffa).sum();
        System.out.printf("%n  Totale fatturabile stimato: %.2f EUR%n", totale);
    }

    private static void cercaIntervento() {
        String id = leggiStringa("\n  ID da cercare: ");
        try {
            Intervento trovato = logManager.cercaPerId(id);
            System.out.println("  Trovato --> " + trovato);
        } catch (InterventoNonTrovatoException e) {
            System.out.println("  [X] " + e.getMessage());
        }
    }

    private static void eliminaIntervento() {
        String id = leggiStringa("\n  ID da eliminare: ");
        try {
            logManager.elimina(id);
        } catch (InterventoNonTrovatoException e) {
            System.out.println("  [X] " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Utility input
    // -------------------------------------------------------------------------

    private static String leggiStringa(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            while (input.isEmpty()) {
                System.out.print("  (campo obbligatorio) " + prompt);
                input = scanner.nextLine().trim();
            }
            return input;
        } catch (NoSuchElementException e) {
            // EOF durante inserimento: torniamo un placeholder per non bloccare il flusso
            return "(n/a)";
        }
    }

    /**
     * Se l'utente preme invio senza digitare nulla, usa la data di oggi.
     * Gestione dell'eccezione di parsing inline: nessun crash per un formato sbagliato.
     */
    private static LocalDate leggiData(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return LocalDate.now();
            }
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("  Formato non valido, uso la data di oggi.");
            return LocalDate.now();
        } catch (NoSuchElementException e) {
            return LocalDate.now();
        }
    }

    /**
     * UUID troncato a 8 caratteri: abbastanza unico per un log personale.
     * Con ~1000 voci la probabilità di collisione è circa 0.012%, accettabile.
     */
    private static String generaId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
