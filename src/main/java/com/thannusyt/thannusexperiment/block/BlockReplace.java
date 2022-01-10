package com.thannusyt.thannusexperiment.block;

import com.thannusyt.thannusexperiment.ThannusExperiment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@Mod.EventBusSubscriber(modid = ThannusExperiment.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockReplace {
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        //TODO
    }
}