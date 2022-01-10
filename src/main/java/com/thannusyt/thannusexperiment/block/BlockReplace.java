package com.thannusyt.thannusexperiment.block;

import com.google.common.base.Preconditions;
import com.thannusyt.thannusexperiment.ThannusExperiment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

import static net.minecraftforge.eventbus.api.Event.Result.DEFAULT;
import static net.minecraftforge.eventbus.api.Event.Result.DENY;

@Mod.EventBusSubscriber(modid = ThannusExperiment.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerInteractEvent extends PlayerEvent
{
    private final InteractionHand hand;
    private final BlockPos pos;
    @Nullable
    private final Direction face;
    private InteractionResult cancellationResult = InteractionResult.PASS;

    private PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face)
    {
        super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
        this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
        this.face = face;
    }
    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block. <br>
     * This event controls which of {@link Item#onItemUseFirst}, {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)},
     * and {@link Item#useOn(UseOnContext)} will be called. <br>
     * Canceling the event will cause none of the above three to be called. <br>
     * <br>
     * Let result be the first non-pass return value of the above three methods, or pass, if they all pass. <br>
     * Or {@link #cancellationResult} if the event is cancelled. <br>
     * If result equals {@link InteractionResult#PASS}, we proceed to {@link net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem}.  <br>
     * <br>
     * There are various results to this event, see the getters below.  <br>
     * Note that handling things differently on the client vs server may cause desynchronizations!
     */
    @Cancelable
    public static class RightClickBlock extends net.minecraftforge.event.entity.player.PlayerInteractEvent
    {
        private Result useBlock = DEFAULT;
        private Result useItem = DEFAULT;
        private BlockHitResult hitVec;

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
            super(player, hand, pos, hitVec.getDirection());
            this.hitVec = hitVec;
        }

        /**
         * @return If {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} should be called
         */
        public Result getUseBlock()
        {
            return useBlock;
        }

        /**
         * @return If {@link Item#onItemUseFirst} and {@link Item#useOn(UseOnContext)} should be called
         */
        public Result getUseItem()
        {
            return useItem;
        }

        /**
         * @return The ray trace result targeting the block.
         */
        public BlockHitResult getHitVec()
        {
            return hitVec;
        }

        /**
         * DENY: {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} will never be called. <br>
         * DEFAULT: {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} will be called if {@link Item#onItemUseFirst} passes. <br>
         * Note that default activation can be blocked if the user is sneaking and holding an item that does not return true to {@link Item#doesSneakBypassUse}. <br>
         * ALLOW: {@link Block#updateOrDestroy(BlockState, BlockState, LevelAccessor, BlockPos, int, int)} will always be called, unless {@link Item#onItemUseFirst} does not pass. <br>
         */
        public void setUseBlock(Result triggerBlock)
        {
            this.useBlock = triggerBlock;
        }

        /**
         * DENY: Neither {@link Item#useOn(UseOnContext)} or {@link Item#onItemUseFirst} will be called. <br>
         * DEFAULT: {@link Item#onItemUseFirst} will always be called, and {@link Item#useOn(UseOnContext)} will be called if the block passes. <br>
         * ALLOW: {@link Item#onItemUseFirst} will always be called, and {@link Item#useOn(UseOnContext)} will be called if the block passes, regardless of cooldowns or emptiness. <br>
         */
        public void setUseItem(Result triggerItem)
        {
            this.useItem = triggerItem;
        }

        @Override
        public void setCanceled(boolean canceled)
        {
            super.setCanceled(canceled);
            if (canceled)
            {
                useBlock = DENY;
                useItem = DENY;
            }
        }
    }
}