package com.unifiedmc.bedrock;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RakNetServer extends Thread {
    
    private final JavaPlugin plugin;
    private final int port;
    private DatagramSocket socket;
    private volatile boolean running = true;
    
    public RakNetServer(JavaPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
        setName("UnifiedMC-RakNet");
    }
    
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(1000);
            
            byte[] buffer = new byte[4096];
            
            plugin.getLogger().info("🎧 Escuchando conexiones Bedrock en 0.0.0.0:" + port);
            
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
                    handlePacket(data, packet.getAddress(), packet.getPort());
                    
                } catch (java.net.SocketTimeoutException e) {
                    // Timeout normal
                } catch (IOException e) {
                    if (running) {
                        plugin.getLogger().warning("Error recibiendo paquete: " + e.getMessage());
                    }
                }
            }
        } catch (SocketException e) {
            plugin.getLogger().severe("Error creando socket UDP: " + e.getMessage());
        }
    }
    
    private void handlePacket(byte[] data, InetAddress address, int port) {
        if (data.length < 1) return;
        
        int packetId = data[0] & 0xFF;
        
        switch (packetId) {
            case RakNetConstants.ID_UNCONNECTED_PING:
                handleUnconnectedPing(data, address, port);
                break;
            case RakNetConstants.ID_OPEN_CONNECTION_REQUEST_1:
                handleOpenConnectionRequest1(data, address, port);
                break;
            case RakNetConstants.ID_OPEN_CONNECTION_REQUEST_2:
                handleOpenConnectionRequest2(data, address, port);
                break;
            default:
                if (plugin.getConfig().getBoolean("debug", false)) {
                    plugin.getLogger().info("📦 Paquete RakNet ID: 0x" + 
                        Integer.toHexString(packetId) + " desde " + address);
                }
        }
    }
    
    private void handleUnconnectedPing(byte[] data, InetAddress address, int port) {
        ByteBuffer in = ByteBuffer.wrap(data);
        in.get(); // Saltar ID
        
        long clientTimestamp = in.getLong();
        byte[] magic = new byte[16];
        in.get(magic);
        long clientGuid = in.getLong();
        
        plugin.getLogger().info("🏓 Ping desde Bedrock: " + address.getHostAddress());
        
        // Obtener jugadores online del servidor Java
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        
        ByteBuffer out = ByteBuffer.allocate(512);
        out.put(RakNetConstants.ID_UNCONNECTED_PONG);
        out.putLong(clientTimestamp);
        out.putLong(RakNetConstants.SERVER_GUID);
        out.put(RakNetConstants.MAGIC);
        
        // Formato MOTD: "MCPE;MOTD;PROTOCOL;VERSION;PLAYERS;MAX;GUID;LEVEL;MODE;..."
        String motd = "MCPE;" +
                      RakNetConstants.MOTD + ";" +
                      RakNetConstants.BEDROCK_PROTOCOL + ";" +
                      RakNetConstants.BEDROCK_VERSION + ";" +
                      onlinePlayers + ";" +
                      maxPlayers + ";" +
                      RakNetConstants.SERVER_GUID + ";" +
                      "UnifiedMC;Survival;1;" + port + ";" + (port + 1) + ";";
        
        byte[] motdBytes = motd.getBytes(StandardCharsets.UTF_8);
        out.putShort((short) motdBytes.length);
        out.put(motdBytes);
        
        try {
            byte[] response = Arrays.copyOf(out.array(), out.position());
            DatagramPacket packet = new DatagramPacket(response, response.length, address, port);
            socket.send(packet);
            plugin.getLogger().info("✅ Servidor visible para Bedrock en " + address.getHostAddress());
        } catch (IOException e) {
            plugin.getLogger().warning("Error enviando Pong: " + e.getMessage());
        }
    }
    
    private void handleOpenConnectionRequest1(byte[] data, InetAddress address, int port) {
        ByteBuffer in = ByteBuffer.wrap(data);
        in.get(); // Saltar ID
        in.get(RakNetConstants.MAGIC); // Saltar MAGIC
        byte protocolVersion = in.get();
        
        short mtuSize = (short) (data.length + 46);
        
        plugin.getLogger().info("🔓 OpenConnectionRequest1 - Protocolo: " + protocolVersion);
        
        ByteBuffer out = ByteBuffer.allocate(32);
        out.put(RakNetConstants.ID_OPEN_CONNECTION_REPLY_1);
        out.put(RakNetConstants.MAGIC);
        out.putLong(RakNetConstants.SERVER_GUID);
        out.put((byte) 0x00); // useSecurity = false
        out.putShort(mtuSize);
        
        try {
            byte[] response = Arrays.copyOf(out.array(), out.position());
            DatagramPacket packet = new DatagramPacket(response, response.length, address, port);
            socket.send(packet);
            plugin.getLogger().info("✅ OpenConnectionReply1 enviado");
        } catch (IOException e) {
            plugin.getLogger().warning("Error enviando Reply1: " + e.getMessage());
        }
    }
    
    private void handleOpenConnectionRequest2(byte[] data, InetAddress address, int port) {
        plugin.getLogger().info("🔓 OpenConnectionRequest2 recibido - Estableciendo conexión...");
        
        ByteBuffer out = ByteBuffer.allocate(64);
        out.put(RakNetConstants.ID_OPEN_CONNECTION_REPLY_2);
        out.put(RakNetConstants.MAGIC);
        out.putLong(RakNetConstants.SERVER_GUID);
        out.putShort((short) address.getPort()); // Client port
        out.putShort((short) 19132); // Server port
        out.put((byte) 0x00); // Use encryption
        
        try {
            byte[] response = Arrays.copyOf(out.array(), out.position());
            DatagramPacket packet = new DatagramPacket(response, response.length, address, port);
            socket.send(packet);
            plugin.getLogger().info("✅ Conexión RakNet establecida con " + address.getHostAddress());
            plugin.getLogger().info("🎮 ¡Un jugador de Bedrock está entrando!");
        } catch (IOException e) {
            plugin.getLogger().warning("Error enviando Reply2: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        interrupt();
        plugin.getLogger().info("Servidor RakNet detenido");
    }
}
