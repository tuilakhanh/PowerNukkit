package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.ObjectNyliumVegetation;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockNylium extends BlockSolid {
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !up().isTransparent()) {
            level.setBlock(this, Block.get(NETHERRACK), false);
            return type;
        }
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        Block up = up();
        if (item.isNull() || item.getId() != ItemID.DYE || item.getDamage() != DyeColor.WHITE.getDyeData() || up.getId() != AIR) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        
        grow();

        level.addParticle(new BoneMealParticle(up));
        
        return true;
    }

    public boolean grow() {
        ObjectNyliumVegetation.growVegetation(level, this, new NukkitRandom());
        return true;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{ Item.get(NETHERRACK) };
        }
        return new Item[0];
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
