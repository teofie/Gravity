package com.example.gravitymod.mixin;

import com.example.gravitymod.GravityManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class, LivingEntity.class, ItemEntity.class})
public abstract class EntityMixin {
    @Inject(method = "getGravity", at = @At("RETURN"), cancellable = true)
    private void gravitymod$scaleGravity(CallbackInfoReturnable<Double> callbackInfo) {
        Entity entity = (Entity) (Object) this;
        callbackInfo.setReturnValue(GravityManager.scale(entity, callbackInfo.getReturnValueD()));
    }
}
