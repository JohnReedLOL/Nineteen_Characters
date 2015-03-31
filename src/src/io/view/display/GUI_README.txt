The GUI file PotentialNineteenCharactersGUI.java is machine generated.

The file PotentialNineteenCharactersGUI.form is necessary to modify the private variables as well as the function initComponents().

Note        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PotentialNineteenCharactersGUI().setVisible(true);
            }
        });

In the above code, the GUI thread is created by the main thread. The GUI thread is the only thread that is allowed to modify the new PotentialNineteenCharactersGUI. 
Tasks are passed to the GUI thread using java.awt.EventQueue.invokeLater(new Runnable() { public void run(){...} }); 


