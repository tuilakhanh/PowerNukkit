package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class WarpedPlanks extends BlockSolid {

    public WarpedPlanks() {
        this(0);
    }

    public WarpedPlanks(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_PLANKS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/

}
