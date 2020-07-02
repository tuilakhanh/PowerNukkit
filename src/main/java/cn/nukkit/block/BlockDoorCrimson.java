package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorCrimson;
import cn.nukkit.utils.BlockColor;

public class BlockDoorCrimson extends BlockDoorWood {

    public BlockDoorCrimson() {
        this(0);
    }

    public BlockDoorCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Door Block";
    }

    @Override
    public int getId() {
        return CRIMSON_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemDoorCrimson();
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/
}
