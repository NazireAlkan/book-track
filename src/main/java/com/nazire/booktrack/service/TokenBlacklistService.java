package com.nazire.booktrack.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
        System.out.println("Token blacklist'e eklendi: " + token.substring(0, 20) + "...");
        System.out.println("Toplam blacklisted token sayısı: " + blacklistedTokens.size());
    }
    
    public boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = blacklistedTokens.contains(token);
        System.out.println("Token blacklist kontrolü: " + token.substring(0, 20) + "... -> " + isBlacklisted);
        return isBlacklisted;
    }
} 