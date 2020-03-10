package io.github.redstoneparadox.cpu.mixin.server.world;

import io.github.redstoneparadox.cpu.scripting.ComputerNetwork;
import io.github.redstoneparadox.cpu.scripting.ComputerNetworkAccessor;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements ComputerNetworkAccessor {
    private final ComputerNetwork network = new ComputerNetwork();

    @Override
    public @NotNull ComputerNetwork getNetwork() {
        return network;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        network.tick();
    }
}
