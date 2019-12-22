package io.github.redstoneparadox.cpu.mixin.server.world;

import io.github.redstoneparadox.cpu.scripting.ConnectionManager;
import io.github.redstoneparadox.cpu.scripting.ConnectionManagerAccessor;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements ConnectionManagerAccessor {
    private final ConnectionManager manager = new ConnectionManager();

    @NotNull
    @Override
    public ConnectionManager getManager() {
        return manager;
    }
}
