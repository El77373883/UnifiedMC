# UnifiedMC

**Puente nativo Java-Bedrock sin dependencias externas**

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21.3-blue)](https://papermc.io/)
[![Bedrock](https://img.shields.io/badge/Bedrock-1.21.70-green)](https://minecraft.net/)

## 🎯 Objetivo

UnifiedMC permite que jugadores de **Minecraft Bedrock** (iPhone, Android, Windows 10, Xbox, PS4/PS5, Switch) se conecten a un servidor **Java Edition** sin usar Geyser ni Floodgate.

**100% código propio - 0% dependencias externas**

## ✨ Estado actual

- [x] Servidor RakNet básico
- [x] Respuesta a Unconnected Ping (servidor visible en lista)
- [x] Handshake RakNet completo (OpenConnectionRequest 1 y 2)
- [ ] Traducción de paquetes Bedrock → Java
- [ ] Inyección de jugadores Bedrock en Spigot
- [ ] Sincronización de chunks

## 🚀 Instalación

1. Descarga el último `.jar` de [Releases](https://github.com/TuUsuario/UnifiedMC/releases)
2. Colócalo en la carpeta `plugins/` de tu servidor Paper/Spigot
3. Asegúrate de que el puerto **19132 UDP** esté abierto en tu firewall
4. Reinicia el servidor

## 📱 Conexión desde Bedrock

1. Abre Minecraft en tu dispositivo Bedrock
2. Ve a **Jugar → Servidores → Añadir servidor**
3. Nombre: `UnifiedMC`
4. Dirección: `IP_DE_TU_SERVIDOR`
5. Puerto: `19132`

## 🛠️ Desarrollo

```bash
git clone https://github.com/TuUsuario/UnifiedMC.git
cd UnifiedMC
mvn clean package
