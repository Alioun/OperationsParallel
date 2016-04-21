/* (C) 2016, R. Schiedermeier, rs@cs.hm.edu
 * Oracle Corporation Java 1.8.0_72, Linux i386 4.3.0
 * violet (Intel Core i7 CPU 920/2668 MHz, 8 Cores, 12032 MB RAM)
 **/
package edu.hm.cs.rs.arch.a02_waitnotify;

/** Die Operationen.
 * @author R. Schiedermeier, rs@cs.hm.edu
 * @version 2016-04-02
 */
public enum Operation {
    A1, A2, A3, B1, B2, B3, C1, C2, C3;

    /** Dauer der Operation in Millisekunden. */
    private int duration;

    /** Initialisiert die Operationen mit Laufzeiten.
     * Diese Methoden muss vor dem Start der Operationen genau einmal aufgerufen werden.
     * @param args Strings mit Laufzeiten in Millisekunden, einer fuer jede Operation.
     * Fehlende Strings ergaenzt die Methode durch den Letztgenannten.
     * Ueberzaehlige Strings verfallen.
     * Wenn alle Strings fehlen, gilt eine Laufzeit von 1 Sekunde.
     */
    public static void init(String... args) {
        if(args.length == 0)
            init("1000");
        else {
            for(Operation operation: values())
                operation.duration = Integer.parseInt(args[operation.ordinal() < args.length
                        ? operation.ordinal()
                        : args.length - 1]);
        }
    }

    /** Fuehrt diese Operation aus.
     */
    public void exec() {
        if(duration < 1)
            throw new IllegalStateException(this + ": not initialized");
        try {
            Thread.sleep(duration);
        }
        catch(InterruptedException e) {
            throw new RuntimeException("unexpected interrupt");
        }
    }

}