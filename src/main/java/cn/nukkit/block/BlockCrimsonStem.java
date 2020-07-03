package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

/**
 * BlockCrimsonStem.java was made by using BlockWood.java
 */
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockCrimsonStem extends BlockSolid {

    public BlockCrimsonStem() {
    }
    
    @Override
    public int getId() {
        return CRIMSON_STEM;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public String getName() {
        return "Crimson Stem";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        short[] faces = new short[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100
        };
        
        this.setDamage(faces[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isAxe()) {
            Block strippedBlock = Block.get(STRIPPED_CRIMSON_STEM);
            item.useOn(this);
            this.level.setBlock(this, strippedBlock, true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public Item toItem() {
            return new ItemBlock(this);
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
