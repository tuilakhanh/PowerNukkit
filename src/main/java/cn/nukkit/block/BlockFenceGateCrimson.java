package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * BlockFenceGateCrimson.java was made by using BlockFenceGate(Some Wood type).java
 */
/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateCrimson extends BlockFenceGate {
    public BlockFenceGateCrimson() {
        this(0);
    }

    public BlockFenceGateCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_CRIMSON;
    }

    @Override
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.FENCE_GATE_CRIMSON, 0, 1);
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
