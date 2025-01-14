/*
 * Copyright (c) 2019-2021 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector.dump;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.geysermc.connector.RoryConnector;
import org.geysermc.connector.common.serializer.AsteriskSerializer;
import org.geysermc.connector.configuration.RoryConfiguration;
import org.geysermc.connector.network.BedrockProtocol;
import org.geysermc.connector.network.session.RorySession;
import org.geysermc.connector.utils.DockerCheck;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.floodgate.util.DeviceOS;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

@Getter
public class DumpInfo {

    @JsonIgnore
    private static final long MEGABYTE = 1024L * 1024L;

    private final DumpInfo.VersionInfo versionInfo;
    private Properties gitInfo;
    private final RoryConfiguration config;
    private final HashInfo hashInfo;
    private final Object2IntMap<DeviceOS> userPlatforms;
    private final RamInfo ramInfo;
    private final BootstrapDumpInfo bootstrapInfo;

    public DumpInfo() {
        this.versionInfo = new DumpInfo.VersionInfo();

        try {
            this.gitInfo = new Properties();
            this.gitInfo.load(FileUtils.getResource("git.properties"));
        } catch (IOException ignored) { }

        this.config = RoryConnector.getInstance().getConfig();

        String md5Hash = "unknown";
        String sha256Hash = "unknown";
        try {
            // https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
            // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
            File file = new File(DumpInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            ByteSource byteSource = Files.asByteSource(file);
            // Jenkins uses MD5 for its hash
            //noinspection UnstableApiUsage
            md5Hash = byteSource.hash(Hashing.md5()).toString();
            //noinspection UnstableApiUsage
            sha256Hash = byteSource.hash(Hashing.sha256()).toString();
        } catch (Exception e) {
            if (RoryConnector.getInstance().getConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }

        this.hashInfo = new HashInfo(md5Hash, sha256Hash);

        this.ramInfo = new DumpInfo.RamInfo();

        this.userPlatforms = new Object2IntOpenHashMap<>();
        for (RorySession session : RoryConnector.getInstance().getPlayers()) {
            DeviceOS device = session.getClientData().getDeviceOS();
            userPlatforms.put(device, userPlatforms.getOrDefault(device, 0) + 1);
        }

        this.bootstrapInfo = RoryConnector.getInstance().getBootstrap().getDumpInfo();
    }

    @Getter
    public static class VersionInfo {

        private final String name;
        private final String version;
        private final String javaVersion;
        private final String architecture;
        private final String operatingSystem;
        private final String operatingSystemVersion;

        private final NetworkInfo network;
        private final MCInfo mcInfo;

        VersionInfo() {
            this.name = RoryConnector.NAME;
            this.version = RoryConnector.VERSION;
            this.javaVersion = System.getProperty("java.version");
            this.architecture = System.getProperty("os.arch"); // Usually gives Java architecture but still may be helpful.
            this.operatingSystem = System.getProperty("os.name");
            this.operatingSystemVersion = System.getProperty("os.version");

            this.network = new NetworkInfo();
            this.mcInfo = new MCInfo();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class HashInfo {
        private final String md5Hash;
        private final String sha256Hash;
    }

    @Getter
    public static class NetworkInfo {

        private String internalIP;
        private final boolean dockerCheck;

        NetworkInfo() {
            if (AsteriskSerializer.showSensitive) {
                try {
                    // This is the most reliable for getting the main local IP
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("geysermc.org", 80));
                    this.internalIP = socket.getLocalAddress().getHostAddress();
                } catch (IOException e1) {
                    try {
                        // Fallback to the normal way of getting the local IP
                        this.internalIP = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException ignored) { }
                }
            } else {
                // Sometimes the internal IP is the external IP...
                this.internalIP = "***";
            }

            this.dockerCheck = DockerCheck.checkBasic();
        }
    }

    @Getter
    public static class MCInfo {

        private final String bedrockVersion;
        private final int bedrockProtocol;
        private final String javaVersion;
        private final int javaProtocol;

        MCInfo() {
            this.bedrockVersion = BedrockProtocol.DEFAULT_BEDROCK_CODEC.getMinecraftVersion();
            this.bedrockProtocol = BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion();
            this.javaVersion = MinecraftConstants.GAME_VERSION;
            this.javaProtocol = MinecraftConstants.PROTOCOL_VERSION;
        }
    }

    @Getter
    public static class RamInfo {

        private final long free;
        private final long total;
        private final long max;

        RamInfo() {
            this.free = Runtime.getRuntime().freeMemory() / MEGABYTE;
            this.total = Runtime.getRuntime().totalMemory() / MEGABYTE;
            this.max = Runtime.getRuntime().maxMemory() / MEGABYTE;
        }
    }
}
