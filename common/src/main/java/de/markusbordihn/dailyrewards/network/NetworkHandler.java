/*
 * Copyright 2022 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.dailyrewards.network;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.network.message.MessageOpenRewardScreen;
import de.markusbordihn.dailyrewards.network.message.ModMessage;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkHandler {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final NetworkChannel INSTANCE =
      NetworkChannel.create(new ResourceLocation(Constants.MOD_ID, "network"));

  public static void registerNetworkHandler() {
    log.info("{} Network Handler...", Constants.LOG_REGISTER_PREFIX);

    if (Platform.getEnvironment() == Env.CLIENT) {
      Client.register();
    } else {
      Server.register();
    }
  }

  public static <R extends PacketListener, T extends ModMessage<R>> void register(
      Class<T> type,
      Supplier<T> packetSupplier,
      Function<NetworkManager.PacketContext, R> contextMapper) {
    INSTANCE.register(
        type,
        ModMessage::write,
        packetByteBuf -> {
          T packet = packetSupplier.get();
          packet.read(packetByteBuf);
          return packet;
        },
        (packet, contextSupplier) -> {
          if (contextMapper != null) {
            packet.handle(contextMapper.apply(contextSupplier.get()));
          }
        });
  }

  public static <M> void sendToServer(M message) {
    try {
      INSTANCE.sendToServer(message);
    } catch (Exception e) {
      log.error("Failed to send {} to server, got error: {}", message, e.getMessage());
    }
  }

  public static <M> void sendToPlayer(M message, ServerPlayer serverPlayer) {
    try {
      INSTANCE.sendToPlayer(serverPlayer, message);
    } catch (Exception e) {
      log.error(
          "Failed to send {} to player {}, got error: {}",
          message,
          serverPlayer.getName().getString(),
          e.getMessage());
    }
  }

  public static class Client {
    public static final Function<NetworkManager.PacketContext, ClientPacketListener> CLIENT_PLAY =
        context -> Minecraft.getInstance().getConnection();
    public static final Function<NetworkManager.PacketContext, ServerGamePacketListenerImpl>
        SERVER_PLAY = context -> ((ServerPlayer) context.getPlayer()).connection;

    public static void register() {
      NetworkHandler.register(
          MessageOpenRewardScreen.class, MessageOpenRewardScreen::new, SERVER_PLAY);
    }
  }

  public static class Server {
    public static final Function<NetworkManager.PacketContext, ServerGamePacketListenerImpl>
        SERVER_PLAY = context -> ((ServerPlayer) context.getPlayer()).connection;

    public static void register() {
      NetworkHandler.register(
          MessageOpenRewardScreen.class, MessageOpenRewardScreen::new, SERVER_PLAY);
    }
  }
}
