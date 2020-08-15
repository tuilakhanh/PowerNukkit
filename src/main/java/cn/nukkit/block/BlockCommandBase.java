package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by good777LUCKY
 */
public abstract class BlockCommandBase extends BlockSolidMeta implements Faceable {

    public BlockCommandBase() {
        this(0);
    }
    
    public BlockCommandBase(int meta) {
        super(meta);
    }
    
    @Override
    public double getHardness() {
        return -1;
    }
    
    @Override
    public double getResistance() {
        return 18000000;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                // TODO: Add UI
            }
        }
        return true;
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        // This code was made with BlockDispenser.java
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                    double y = player.y + player.getEyeHeight();
                    
                    if (y - this.y > 2) {
                        this.setDamage(BlockFace.UP.getIndex());
                    } else if (this.y - y > 0) {
                        this.setDamage(BlockFace.DOWN.getIndex());
                    } else {
                        this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
                    }
                } else {
                    this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
                }
            }
        }
        this.getLevel().setBlock(block, this, true);
        // TODO: Add Block Entity
        return true;
    }
    
    /* TODO: Add Redstone Update
    @Override
    public int onUpdate(int type) {
        
    }*/
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x7);
    }
    
    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride() {
        // TODO: Add Comparator Functionality
        return 0;
    }
    
    // TODO: Add Functionality
}
