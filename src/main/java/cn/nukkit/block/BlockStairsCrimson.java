package cn.nukkit.block;


import cn.nukkit.utils.BlockColor;

public class BlockStairsCrimson extends BlockStairsWood {

    public BlockStairsCrimson() {
        this(0);
    }

    public BlockStairsCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Crimson Wood Stairs";
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/

}
