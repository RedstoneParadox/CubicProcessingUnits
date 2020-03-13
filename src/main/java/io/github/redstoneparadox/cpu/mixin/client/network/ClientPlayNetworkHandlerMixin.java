package io.github.redstoneparadox.cpu.mixin.client.network;

import io.github.redstoneparadox.cpu.item.CpuItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow private MinecraftClient client;

    @Inject(method = "onOpenWrittenBook", at = @At("HEAD"), cancellable = true)
    private void onOpenWrittenBook(OpenWrittenBookS2CPacket packet, CallbackInfo ci) {
        ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
        if (itemStack.getItem() == CpuItems.INSTANCE.getPRINTED_DOCUMENT()) {
            this.client.openScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
        }
    }
}
