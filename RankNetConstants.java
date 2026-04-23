package com.unifiedmc.bedrock;

public class RakNetConstants {
    
    // MAGIC de RakNet - Secuencia obligatoria para handshake
    public static final byte[] MAGIC = {
        (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0x00,
        (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
        (byte) 0xFD, (byte) 0xFD, (byte) 0xFD, (byte) 0xFD,
        (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
    };
    
    // Packet IDs de RakNet
    public static final byte ID_UNCONNECTED_PING = 0x01;
    public static final byte ID_UNCONNECTED_PONG = 0x1C;
    public static final byte ID_OPEN_CONNECTION_REQUEST_1 = 0x05;
    public static final byte ID_OPEN_CONNECTION_REPLY_1 = 0x06;
    public static final byte ID_OPEN_CONNECTION_REQUEST_2 = 0x07;
    public static final byte ID_OPEN_CONNECTION_REPLY_2 = 0x08;
    
    // Server GUID fijo para UnifiedMC
    public static final long SERVER_GUID = 0x554E49464945444DCL; // "UNIFIEDMC" en hex
    
    // Protocolo Bedrock para v26.13 (1.21.70)
    public static final int BEDROCK_PROTOCOL = 786;
    public static final String BEDROCK_VERSION = "1.21.70";
    
    // Configuración del servidor
    public static final int MAX_PLAYERS = 20;
    public static final String MOTD = "§aUnifiedMC §7| §fJava + Bedrock";
}
