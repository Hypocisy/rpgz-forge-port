package net.rpgz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.rpgz.access.InventoryAccess;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;
import net.rpgz.screen.MobEntityScreenHandler;
import net.rpgz.util.RpgHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Level;
import java.util.stream.StreamSupport;

@Mixin(net.minecraft.world.entity.Mob.class)
public abstract class Mob extends LivingEntity implements InventoryAccess {

    private SimpleContainer inventory = new SimpleContainer(9);

    public Mob(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tickMovement() {
        if (this.deathTime > 19) {
            AABB box = this.getBoundingBox();
            BlockPos blockPos = BlockPos.containing(box.getCenter().x, box.minY, box.getCenter().z);
            if (this.level().getBlockState(blockPos).isAir()) {
                if ((Object) this instanceof FlyingMob) {
                    this.setPos(this.getX(), this.getY() - 0.25D, this.getZ());
                } else if (this.getDeltaMovement().y > 0) {
                    this.setPos(this.getX(), this.getY() - (Math.min(this.getDeltaMovement().y, 0.8D)), this.getZ());
                } else if (this.getDeltaMovement().y < 0) {
                    this.setPos(this.getX(), this.getY() + (Math.max(this.getDeltaMovement().y, -0.8D)) + (this.getDeltaMovement().y > -0.2D ? -0.4D : 0.0D), this.getZ());
                } else {
                    this.setPos(this.getX(), this.getY() - 0.1D, this.getZ());
                }
            } else
                // Water floating
                if (this.wasTouchingWater) {
                    if (ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    if (this.(this.level().getFluidState(this.getBlockPosBelowThatAffectsMyMovement())))
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    else if (this.level().containsFluid(box.offset(0.0D, -box.getYLength() + (box.getYLength() / 5), 0.0D)) && !ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() - 0.05D, this.getZ());
                }
        } else {
            super.tickMovement();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 1) {
            if (this.isOnFire())
                this.extinguishFire();
            if (this.getVehicle() != null)
                this.stopRiding();
        }

        if (this.deathTime >= 20) {
            // Has to get set on server and client
            AABB newBoundingBox = new AABB(this.getX() - (this.getBbWidth() / 3.0F), this.getY() - (this.getBbWidth() / 3.0F), this.getZ() - (this.getWidth() / 3.0F),
                    this.getX() + (this.getBbWidth() / 1.5F), this.getY() + (this.getBbWidth() / 1.5F), this.getZ() + (this.getWidth() / 1.5F));
            if ((this.getDimensions(EntityPose.STANDING).height < 1.0F && this.getDimensions(EntityPose.STANDING).width < 1.0F)
                    || (this.getDimensions(EntityPose.STANDING).width / this.getDimensions(EntityPose.STANDING).height) > 1.395F) {
                this.setBoundingBox(newBoundingBox);
            } else {
                this.setBoundingBox(newBoundingBox.offset(this.getRotationVector(0F, this.bodyYaw).rotateY(-30.0F)));
                // this.setBoundingBox(newBoundingBox.offset(this.getRotationVecClient().rotateY(-30.0F)));
                // acceptable solution
            }
            // Chicken always has trouble - not fixable
            // Shulker has trouble
            // this.checkBlockCollision(); //Doesnt solve problem
            // if (this.isInsideWall()) {} // Doenst work
            if (!this.level().isClientSide()) {
                AABB box = this.getBoundingBox();
                BlockPos blockPos = BlockPos.ofFloored(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
                BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

                // Older method, might be better?
                // if (this.getWorld().isRegionLoaded(blockPos, blockPos2)) {
                // if (!world.isClient && !this.inventory.isEmpty()
                // && (world.getBlockState(blockPos).isFullCube(world, blockPos)
                // || world.getBlockState(blockPos2).isFullCube(world, blockPos2) ||
                // this.isBaby()
                // || (Config.CONFIG.drop_unlooted && this.deathTime >
                // Config.CONFIG.drop_after_ticks))
                // || this.getType().isIn(Tags.EXCLUDED_ENTITIES)
                // ||
                // Config.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.",
                // ""))) {
                // this.inventory.clearToList().forEach(this::dropStack);
                // }
                // }

                // New method to check if inside block
                AABB checkBox = new AABB(box.maxX, box.maxY, box.maxZ, box.maxX + 0.001D, box.maxY + 0.001D, box.maxZ + 0.001D);
                AABB checkBoxTwo = new AABB(box.minX, box.maxY, box.minZ, box.minX + 0.001D, box.maxY + 0.001D, box.minZ + 0.001D);
                AABB checkBoxThree = new AABB(box.maxX - (box.getXsize() / 3D), box.maxY, box.maxZ - (box.getZsize() / 3D), box.maxX + 0.001D - (box.getXsize() / 3D), box.maxY + 0.001D,
                        box.maxZ + 0.001D - (box.getZsize() / 3D));
                if (this.level().(blockPos, blockPos2)) {
                    if (!this.inventory.isEmpty()
                            && (((!StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBox).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty))
                            && (!StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxTwo).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty)))
                            || this.isBaby() || (ConfigInit.CONFIG.drop_unlooted && this.deathTime > ConfigInit.CONFIG.drop_after_ticks))
                            || this.getType().isIn(TagInit.EXCLUDED_ENTITIES) || ConfigInit.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", "").replace(".", ":"))) {

                        this.inventory.clearToList().forEach(this::dropStack);
                    }
                }
            }
            // world.getClosestPlayer(this,// 1.0D)// !=// null// || Testing purpose
        }

        if ((!this.getWorld().isClient() && this.deathTime >= 20 && this.inventory.isEmpty() && ConfigInit.CONFIG.despawn_immediately_when_empty)
                || (this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
            if (!this.getWorld().isClient()) // Make sure only on server particle
                this.despawnParticlesServer();

            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.hasPassengers()) {
            for (int i = 0; i < this.getPassengerList().size(); i++) {
                this.getPassengerList().get(i).dismountVehicle();
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public ItemEntity dropStack(ItemStack stack) {
        if (this.isDead()) {
            addInventoryItem(stack);
            return null;
        } else {
            return super.dropStack(stack);
        }
    }

    private void despawnParticlesServer() {
        for (int i = 0; i < 20; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = MathHelper.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
            double y = MathHelper.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
            double z = MathHelper.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
            ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
        }
    }

    // Stop turning after death
    @Inject(method = "turnHead", at = @At("HEAD"), cancellable = true)
    public void turnHead(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
        if (this.deathTime > 0) {
            info.setReturnValue(0.0F);
        }
    }

    @Inject(method = "Lnet/minecraft/entity/mob/MobEntity;isAffectedByDaylight()Z", at = @At("HEAD"), cancellable = true)
    private void isAffectedByDaylightMixin(CallbackInfoReturnable<Boolean> info) {
        if (this.isDeadOrDying()) {
            info.setReturnValue(false);
        }
    }

    @Override
    public void addInventoryItem(ItemStack stack) {
        RpgHelper.addStackToInventory(() (Object) this, stack, this.level());
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (this.deathTime > 20) {
            if (!this.getWorld().isClient()) {
                if (player.getStackInHand(hand).getItem() instanceof ShovelItem) {
                    if (!this.inventory.isEmpty()) {
                        for (int i = 0; i < this.inventory.size(); i++)
                            player.getInventory().offerOrDrop(this.inventory.getStack(i));
                    }
                    this.inventory.clear();
                    if (!ConfigInit.CONFIG.despawn_immediately_when_empty) {
                        this.despawnParticlesServer();
                        this.remove(RemovalReason.KILLED);
                    }
                    return ActionResult.SUCCESS;
                }
                if (!this.inventory.isEmpty()) {
                    if (player.isSneaking()) {
                        for (int i = 0; i < this.inventory.size(); i++) {
                            player.getInventory().offerOrDrop(this.inventory.getStack(i));
                        }
                        this.inventory.clear();
                    } else {
                        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> new MobEntityScreenHandler(syncId, p.getInventory(), this.inventory), Text.literal("")));
                    }
                    return ActionResult.SUCCESS;
                }
            } else if ((Object) this instanceof PlayerEntity) {
                return super.interactAt(player, hitPos, hand);
            }
            return ActionResult.SUCCESS;
        }
        return super.interactAt(player, hitPos, hand);
    }

}