package com.unifiedmc;

import com.unifiedmc.bedrock.RakNetServer;
import org.bukkit.plugin.java.JavaPlugin;

public class UnifiedMC extends JavaPlugin {
    
    private RakNetServer rakNetServer;
    
    @Override
    public void onEnable() {
        try {
            rakNetServer = new RakNetServer(this, 19132);
            rakNetServer.start();
            getLogger().info("✅ UnifiedMC activo en puerto UDP 19132");
            getLogger().info("🌐 Jugadores de Bedrock ya pueden conectarse");
        } catch (Exception e) {
            getLogger().severe("❌ Error al iniciar UnifiedMC: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        if (rakNetServer != null) {
            rakNetServer.shutdown();
        }
        getLogger().info("UnifiedMC detenido");
    }
}
