package com.wonkglorg;

import com.wonkglorg.grabbers.GrabberPrnt;
import com.wonkglorg.util.console.ConsoleInput;

import java.nio.file.Path;
import java.util.regex.Pattern;

import static com.wonkglorg.util.console.ConsoleUtil.println;

public class Main {
    public static void main(String[] args) {

        println("Prnt downloader");
        println("Downloads images from the prnt website");
        println("------------------------------------------");

        String url = ConsoleInput.of(String.class, "Enter the URL of the image you want to download (default: 6zodqk): ")//
                .matchesPattern(Pattern.compile("[a-zA-Z0-9]{6}"), "Invalid input, max 6 characters a-Z, 0-9")//
                .get("6zodqk");

        Path path = ConsoleInput.of(Path.class, "Enter the path to save the images to (default: Home Directory)")//
                .errorMessage("Invalid Path try again:")//
                .get(Path.of(System.getProperty("user.home")));


        boolean extraInfo = ConsoleInput.of(Boolean.class)//
                .prompt("Display extra debug information Default: false? (y/n):")//
                .get(false);


        new GrabberPrnt(path, url).start(extraInfo);
    }
}
