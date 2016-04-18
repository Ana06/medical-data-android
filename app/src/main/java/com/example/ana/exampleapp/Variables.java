package com.example.ana.exampleapp;

/**
 * Class with static final variables used in the program.
 *
 * @author Ana María Martínez Gómez
 */
public final class Variables {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     *  Final class: to prevent someone from accidentally instantiating the class, we give it an
     *  empty constructor.
     */
    public Variables() {}
}
