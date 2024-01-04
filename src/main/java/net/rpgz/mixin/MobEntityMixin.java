package net.rpgz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.network.chat.Component;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Mth;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rpgz.access.InventoryAccess;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;
import net.rpgz.screen.MobEntityContainerMenu;
import net.rpgz.util.RpgHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.StreamSupport;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements InventoryAccess {

    @Unique
    private final SimpleContainer rpgz$inventory = new SimpleContainer(9);

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (this.deathTime > 19) {
            AABB box = this.getBoundingBox();
            BlockPos blockPos = BlockPos.containing(box.getCenter().x(), box.minY, box.getCenter().z());
            if (this.level().getBlockState(blockPos).isAir()) {
                if ((Object) this instanceof FlyingMob) {
                    this.setPos(this.getX(), this.getY() - 0.25D, this.getZ());
                } else if (this.position().y > 0) {
                    this.setPos(this.getX(), this.getY() - (Math.min(this.position().y, 0.8D)), this.getZ());
                } else if (this.position().y < 0) {
                    this.setPos(this.getX(), this.getY() + (Math.max(this.position().y, -0.8D)) + (this.position().y > -0.2D ? -0.4D : 0.0D), this.getZ());
                } else {
                    this.setPos(this.getX(), this.getY() - 0.1D, this.getZ());
                }
            } else
                // Water floating
                if (this.level().containsAnyLiquid(box.contract(0.0D, box.getYsize(), 0.0D))) {
                    if (ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    if (this.canStandOnFluid(this.level().getFluidState(this.blockPosition())))
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    else if (this.level().containsAnyLiquid(box.contract(0.0D, -box.getYsize() + (box.getYsize() / 5), 0.0D)) && !ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() - 0.05D, this.getZ());
                }
        } else {
            super.tick();
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
            AABB newBoundingBox = new AABB(this.getX() - (this.getBbWidth() / 3.0F), this.getY() - (this.getBbWidth() / 3.0F), this.getZ() - (this.getBbWidth() / 3.0F),
                    this.getX() + (this.getBbWidth() / 1.5F), this.getY() + (this.getBbWidth() / 1.5F), this.getZ() + (this.getBbWidth() / 1.5F));
            if ((this.getDimensions(Pose.STANDING).height < 1.0F && this.getDimensions(Pose.STANDING).width < 1.0F)
                    || (this.getDimensions(Pose.STANDING).width / this.getDimensions(Pose.STANDING).height) > 1.395F) {
                this.setBoundingBox(newBoundingBox);
            } else {
                this.setBoundingBox(newBoundingBox.move(this.getForward().yRot(-30.0F)));
                // this.setBoundingBox(newBoundingBox.offset(this.getRotationVecClient().rotateY(-30.0F)));
                // acceptable solution
            }
            // Chicken always has trouble - not fixable
            // Shulker has trouble
            // this.checkBlockCollision(); //Doesnt solve problem
            // if (this.isInsideWall()) {} // Doenst work
            if (!this.level().isClientSide()) {
                AABB box = this.getBoundingBox();
                BlockPos blockPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).above();
                BlockPos blockPos2 = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

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
                if (this.level().isLoaded(blockPos) && this.level().isLoaded(blockPos2)) {
                    if (!this.rpgz$inventory.isEmpty()
                            && (((!StreamSupport.stream(this.level().getBlockCollisions(this, checkBox).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.level().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty))
                            && (!StreamSupport.stream(this.level().getBlockCollisions(this, checkBoxTwo).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.level().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty)))
                            || this.isBaby() || (ConfigInit.CONFIG.drop_unlooted && this.deathTime > ConfigInit.CONFIG.drop_after_ticks))
                            || this.getType().is(TagInit.EXCLUDED_ENTITIES) || ConfigInit.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", "").replace(".", ":"))) {

                        this.rpgz$inventory.removeAllItems().forEach(this::dropStack);
                    }
                }
            }
            // world.getClosestPlayer(this,// 1.0D)// !=// null// || Testing purpose
        }

        if ((!this.level().isClientSide() && this.deathTime >= 20 && this.rpgz$inventory.isEmpty() && ConfigInit.CONFIG.despawn_immediately_when_empty)
                || (this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
            if (!this.level().isClientSide()) // Make sure only on server particle
                this.rpgz$despawnParticlesServer();

            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.getPassengers().isEmpty()) {
            for (int i = 0; i < this.getPassengers().size(); i++) {
                this.getPassengers().get(i).removeVehicle();
            }
        }
        super.die(damageSource);
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        if (this.isDeadOrDying()) {
            rpgz$addInventoryItem(stack);
            return null;
        } else {
            return super.spawnAtLocation(stack);
        }
    }

    @Unique
    private void rpgz$despawnParticlesServer() {
        for (int i = 0; i < 20; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = Mth.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
            double y = Mth.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
            double z = Mth.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
        }
    }

    // Stop turning after death
    @Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
    public void turnHead(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
        if (this.deathTime > 0) {
            info.setReturnValue(0.0F);
        }
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void isAffectedByDaylightMixin(CallbackInfoReturnable<Boolean> info) {
        if (this.isDeadOrDying()) {
            info.setReturnValue(false);
        }
    }

    @Override
    public void rpgz$addInventoryItem(ItemStack stack) {
        RpgHelper.addStackToInventory((Mob) (Object) this, stack, this.level());
    }

    @Override
    public SimpleContainer getInventory() {
        return this.rpgz$inventory;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (this.deathTime > 20) {
            if (!this.level().isClientSide()) {
                if (player.getItemInHand(hand).getItem() instanceof ShovelItem) {
                    if (!this.rpgz$inventory.isEmpty()) {
                        for (int i = 0; i < this.rpgz$inventory.getContainerSize(); i++)
                            player.getInventory().placeItemBackInInventory(this.rpgz$inventory.getItem(i));
                    }
                    this.rpgz$inventory.clearContent();
                    if (!ConfigInit.CONFIG.despawn_immediately_when_empty) {
                        this.rpgz$despawnParticlesServer();
                        this.remove(RemovalReason.KILLED);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (!this.rpgz$inventory.isEmpty()) {
                    if (player.isShiftKeyDown()) {
                        for (int i = 0; i < this.rpgz$inventory.getContainerSize(); i++) {
                            player.getInventory().placeItemBackInInventory(this.rpgz$inventory.getItem(i));
                        }
                        this.rpgz$inventory.clearContent();
                    } else {
                        player.openMenu(new SimpleMenuProvider((syncId, inv, p) -> new MobEntityContainerMenu(syncId, p.getInventory(), this.rpgz$inventory), Component.literal("")));
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if ((Object) this instanceof Player) {
                return super.interactAt(player, hitPos, hand);
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, hitPos, hand);
    }

}