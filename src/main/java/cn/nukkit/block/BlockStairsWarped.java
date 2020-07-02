package cn.nukkit.block;


import cn.nukkit.utils.BlockColor;

public class BlockStairsWarped extends BlockStairsWood {

    public BlockStairsWarped() {
        this(0);
    }

    public BlockStairsWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Wood Stairs";
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/

}
