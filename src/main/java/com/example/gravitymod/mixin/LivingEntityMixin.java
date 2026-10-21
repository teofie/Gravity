package com.example.gravitymod.mixin;

import com.example.gravitymod.GravityManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private double gravitymod$verticalVelocity;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void gravitymod$rememberZeroGravityVelocity(CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        gravitymod$verticalVelocity = GravityManager.isZero() ? entity.getVelocity().y : 0.0D;
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void gravitymod$maintainNearZeroGravityGroundContact(CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Box bounds = entity.getBoundingBox();
        Box supportCheck = new Box(
                bounds.minX,
                bounds.minY - 1.0E-4D,
                bounds.minZ,
                bounds.maxX,
                bounds.minY + 1.0E-5D,
                bounds.maxZ
        );
        if (GravityManager.isNearZero()
                && entity.getVelocity().y <= 1.0E-5D
                && entity.getEntityWorld().getBlockOrFluidCollisions(entity, supportCheck).iterator().hasNext()) {
            entity.setOnGround(true);
        }

        if (GravityManager.isZero()
                && !entity.isOnGround()
                && Math.abs(gravitymod$verticalVelocity) > 1.0E-5D) {
            entity.setVelocity(entity.getVelocity().x, gravitymod$verticalVelocity, entity.getVelocity().z);
        }
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void gravitymod$ensureNearZeroGravityJump(CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (GravityManager.isNearZero() && entity.getVelocity().y <= 1.0E-5D) {
            entity.setVelocity(entity.getVelocity().x, 0.42D, entity.getVelocity().z);
        }
    }

    @ModifyArg(
            method = "handleFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;computeFallDamage(DF)I"
            ),
            index = 0
    )
    private double gravitymod$scaleFallDistance(double fallDistance) {
        return GravityManager.scaleFallDistance(fallDistance);
    }
}
