package io.github.redstoneparadox.cpu.mixin.server.network;

import com.mojang.authlib.GameProfile;
import io.github.redstoneparadox.cpu.item.CpuItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "openEditBookScreen", at = @At("HEAD"), cancellable = true)
    private void openBookEditScreen(ItemStack book, Hand hand, CallbackInfo ci) {
        Item item = book.getItem();
        if (item == CpuItems.INSTANCE.getPRINTED_DOCUMENT()) {
            if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
                this.container.sendContentUpdates();
            }

            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
            ci.cancel();
        }
    }
}
