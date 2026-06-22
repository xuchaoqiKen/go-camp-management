package com.gocamp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.FileWriter;

public class GenHash {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "admin123";
        String hash = encoder.encode(raw);
        System.out.println("HASH:" + hash);
        System.out.println("VERIFY:" + encoder.matches(raw, hash));
        
        // Write to file
        FileWriter fw = new FileWriter("d:/code/cursor/go-camp-management/generated_hash.txt");
        fw.write(hash);
        fw.close();
        System.out.println("Hash written to file");
    }
}